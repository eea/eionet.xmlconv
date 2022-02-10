package eionet.gdem.infrastructure.scheduling;

import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ContainerApiResponse;
import eionet.gdem.rancher.model.ContainerData;
import eionet.gdem.rancher.model.ServiceApiRequestBody;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestrator;
import eionet.gdem.rancher.service.ServicesRancherApiOrchestrator;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkersOrchestrationSharedServiceImpl implements WorkersOrchestrationSharedService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkersOrchestrationSharedServiceImpl.class);

    private ServicesRancherApiOrchestrator servicesRancherApiOrchestrator;
    private ContainersRancherApiOrchestrator containersRancherApiOrchestrator;
    private JobExecutorService jobExecutorService;
    private JobService jobService;
    private RabbitAdmin rabbitAdmin;
    private WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;
    private CircuitBreaker circuitBreaker;

    @Autowired
    public WorkersOrchestrationSharedServiceImpl(ServicesRancherApiOrchestrator servicesRancherApiOrchestrator, ContainersRancherApiOrchestrator containersRancherApiOrchestrator,
                    JobExecutorService jobExecutorService, JobService jobService, RabbitAdmin rabbitAdmin, WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService, CircuitBreaker circuitBreaker) {
        this.servicesRancherApiOrchestrator = servicesRancherApiOrchestrator;
        this.containersRancherApiOrchestrator = containersRancherApiOrchestrator;
        this.jobExecutorService = jobExecutorService;
        this.jobService = jobService;
        this.rabbitAdmin = rabbitAdmin;
        this.workerAndJobStatusHandlerService = workerAndJobStatusHandlerService;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public void createWorkers(String serviceId, Integer newWorkers, Integer maxJobExecutorsAllowed) throws RancherApiException {
        List<String> instances = servicesRancherApiOrchestrator.getContainerInstances(serviceId);
        Integer scale = newWorkers;
        if (instances.size() >= maxJobExecutorsAllowed) {
            return;
        } else if (instances.size() + newWorkers > maxJobExecutorsAllowed) {
            scale = maxJobExecutorsAllowed - instances.size();
        }
        ServiceApiRequestBody serviceApiRequestBody = new ServiceApiRequestBody().setScale(instances.size() + scale);
        Runnable decorateRunnable = circuitBreaker.decorateRunnable(() -> {
            try {
                servicesRancherApiOrchestrator.scaleUpOrDownContainerInstances(serviceId, serviceApiRequestBody);
            } catch (RancherApiException e) {
                LOGGER.error("Error during rancher functionality: " + e.getMessage());
            }
        });
        decorateRunnable.run();
        LOGGER.info("Created " + scale + " new worker(s)");
    }

    @Override
    public void deleteFailedWorkers(String serviceId, JobExecutorType jobExecutorType) throws RancherApiException {
        List<JobExecutor> totalFailedWorkers = jobExecutorService.findByStatus(SchedulingConstants.WORKER_FAILED);
        //find failed workers by jobExecutorType
        List<JobExecutor> failedWorkersToBeDeleted = totalFailedWorkers.stream().filter(jobExecutor -> jobExecutor.getJobExecutorType().equals(jobExecutorType)).collect(Collectors.toList());

        //find jobExecutorType for workers with unknown jobExecutorType and add them in failedWorkersToBeDeleted list if they belong to rancher service with specific serviceId
        List<JobExecutor> jobExecutorsWithUnknownType = totalFailedWorkers.stream().filter(jobExecutor -> jobExecutor.getJobExecutorType().equals(JobExecutorType.Uknown)).collect(Collectors.toList());
        for (JobExecutor jobExec : jobExecutorsWithUnknownType) {
            ContainerApiResponse containerInfo = containersRancherApiOrchestrator.getContainerInfo(jobExec.getName());
            if (containerInfo.getData().size()>0) {
                String jobExecServiceId = containerInfo.getData().get(0).getServiceIds().get(0);
                if (jobExecServiceId.equals(serviceId)) {
                    failedWorkersToBeDeleted.add(jobExec);
                }
            }
        }

        for (JobExecutor worker : failedWorkersToBeDeleted) {
            try {
                deleteFromRancherAndDatabase(worker);
            } catch (DatabaseException e) {
                LOGGER.error("Error during deletion of failed worker " + worker.getName());
            }
        }
        List<String> instances = servicesRancherApiOrchestrator.getContainerInstances(serviceId);
        if (instances.size()==0) {
            ServiceApiRequestBody serviceApiRequestBody = new ServiceApiRequestBody().setScale(1);
            Runnable decorateRunnable = circuitBreaker.decorateRunnable(() -> {
                try {
                    servicesRancherApiOrchestrator.scaleUpOrDownContainerInstances(serviceId, serviceApiRequestBody);
                } catch (RancherApiException e) {
                    LOGGER.error("Error during rancher functionality: " + e.getMessage());
                }
            });
            decorateRunnable.run();
        }
    }

    @Override
    public void deleteFromRancherAndDatabase(JobExecutor worker) throws DatabaseException {
        Runnable decorateRunnable = circuitBreaker.decorateRunnable(() -> {
            try {
                containersRancherApiOrchestrator.deleteContainer(worker.getName());
            } catch (RancherApiException e) {
                LOGGER.error("Error during rancher functionality: " + e.getMessage());
            }
        });
        decorateRunnable.run();

        jobExecutorService.deleteByName(worker.getName());
        deleteWorkerHeartBeatQueue(worker.getHeartBeatQueue());
    }

    @Override
    public void scheduleWorkersOrchestration(String serviceId, boolean isHeavy, JobExecutorType jobExecutorType, Integer maxJobExecutorsAllowed) {
        try {
            this.deleteFailedWorkers(serviceId, jobExecutorType);
        } catch (RancherApiException e) {
            LOGGER.error("Error during deletion of failed workers");
            return;
        }

        InternalSchedulingStatus internalStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_QUEUED);
        List<JobEntry> jobs = jobService.findByIntSchedulingStatusAndIsHeavy(internalStatus, isHeavy);
        List<JobExecutor> readyWorkers = jobExecutorService.findByStatus(SchedulingConstants.WORKER_READY);
        readyWorkers = readyWorkers.stream().filter(jobExecutor -> jobExecutor.getJobExecutorType().equals(jobExecutorType)).collect(Collectors.toList());
        if (jobs.size() > readyWorkers.size()) {
            Integer newWorkers = jobs.size() - readyWorkers.size();
            try {
                this.createWorkers(serviceId, newWorkers, maxJobExecutorsAllowed);
            } catch (RancherApiException e) {
                LOGGER.error("Worker creation failed.");
                return;
            }
        } else if (jobs.size() < readyWorkers.size()) {
            List<String> instances = null;
            try {
                instances = servicesRancherApiOrchestrator.getContainerInstances(serviceId);
            } catch (RancherApiException e) {
                LOGGER.error("Cannot get container instances to proceed with scale down");
                return;
            }
            if (instances.size() == 1) {
                return;
            }
            Integer workersToDelete = readyWorkers.size() - jobs.size();
            Integer workersDeleted = 1;
            for (JobExecutor worker : readyWorkers) {
                while (workersDeleted <= workersToDelete) {
                    try {
                        instances = servicesRancherApiOrchestrator.getContainerInstances(serviceId);
                    } catch (RancherApiException e) {
                        LOGGER.error("cannot get instances in order to delete them later, " + e);
                        return;
                    }
                    if (instances.size() == 1) {
                        LOGGER.info("Only one worker instance found. No deletion required. Task Exiting.");
                        return;
                    }
                    try {
                        this.deleteFromRancherAndDatabase(worker);
                    } catch (DatabaseException e) {
                        LOGGER.error("Error Deleting worker " + worker.getName() + ", " + e);
                    }
                    workersDeleted++;
                    break;
                }
            }
        }
    }

    @Override
    public void updateDbContainersHealthStatusFromRancher(List<String> instances, boolean isHeavy) throws RancherApiException {
        for (String containerId : instances) {
            //for each instance find status
            ContainerData data = containersRancherApiOrchestrator.getContainerInfoById(containerId);
            String healthState = data.getHealthState();
            String state = data.getState();
            if (healthState==null || (healthState!=null && healthState.equals(SchedulingConstants.CONTAINER_HEALTH_STATE_ENUM.UNHEALTHY.getValue()))
                    || state.equals(SchedulingConstants.CONTAINER_STATE_ENUM.STOPPED.getValue()) || state.equals(SchedulingConstants.CONTAINER_STATE_ENUM.ERROR.getValue())) {
                //update table JOB_EXECUTOR insert row with status failed and add history entry to JOB_EXECUTOR_HISTORY.
                String containerName = data.getName();
                String heartBeatQueue = containerName + "-queue";
                JobExecutor jobExecutor = new JobExecutor(containerName, containerId, SchedulingConstants.WORKER_FAILED, heartBeatQueue);
                try {
                    if (isHeavy) LOGGER.info("Task synchronizeRancherHeavyContainersAndDbEntriesByExistenceAndStatus: setting status of container with name " + containerName + " to WORKER_FAILED");
                    else LOGGER.info("Task synchronizeRancherLightContainersAndDbEntriesByExistenceAndStatus: setting status of container with name " + containerName + " to WORKER_FAILED");
                    JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(containerName, containerId, SchedulingConstants.WORKER_FAILED, new Timestamp(new Date().getTime()), heartBeatQueue);
                    workerAndJobStatusHandlerService.saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
                } catch (DatabaseException e) {
                    LOGGER.error("Task failed for jobExecutor with containerId " + containerId);
                }
            }
        }
    }

    @Override
    public void synchronizeRancherContainersWithDbEntries(List<JobExecutor> jobExecutors, List<String> instances) {
        for (JobExecutor jobExecutor : jobExecutors) {
            if (!instances.contains(jobExecutor.getContainerId())) {
                LOGGER.info("Container retrieved form Database  with ID:" + jobExecutor.getContainerId() + " and name:" + jobExecutor.getName() +
                        " doesn't exist on rancher.Proceeding with deletion from Database");
                try {
                    jobExecutorService.deleteByContainerId(jobExecutor.getContainerId());
                    deleteWorkerHeartBeatQueue(jobExecutor.getHeartBeatQueue());
                } catch (DatabaseException e) {
                    LOGGER.error("Task synchronizeRancherContainersAndDbEntriesByExistenceAndStatus failed for jobExecutor with name " + jobExecutor.getName());
                }
            }
        }
    }

    @Override
    public void deleteWorkerHeartBeatQueue(String queueName) {
        boolean queueDeleted = rabbitAdmin.deleteQueue(queueName);
        if (!queueDeleted) {
            LOGGER.error("Worker Heartbeat queue  could not be deleted:" + queueName);
        }
    }
}
