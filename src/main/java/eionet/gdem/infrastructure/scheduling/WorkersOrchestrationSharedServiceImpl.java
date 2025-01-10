package eionet.gdem.infrastructure.scheduling;

import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.*;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.QueryJpaService;
import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
import eionet.gdem.rancher.service.RancherApiService;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkersOrchestrationSharedServiceImpl implements WorkersOrchestrationSharedService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkersOrchestrationSharedServiceImpl.class);

    private RancherApiService rancherApiService;
    private JobExecutorService jobExecutorService;
    private JobService jobService;
    private RabbitAdmin rabbitAdmin;
    private WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;
    private CircuitBreaker circuitBreaker;
    private QueryJpaService queryJpaService;

    @Autowired
    public WorkersOrchestrationSharedServiceImpl(RancherApiService rancherApiService,
                                                 JobExecutorService jobExecutorService,
                                                 JobService jobService,
                                                 RabbitAdmin rabbitAdmin,
                                                 WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService,
                                                 CircuitBreaker circuitBreaker,
                                                 QueryJpaService queryJpaService) {
        this.rancherApiService = rancherApiService;
        this.jobExecutorService = jobExecutorService;
        this.jobService = jobService;
        this.rabbitAdmin = rabbitAdmin;
        this.workerAndJobStatusHandlerService = workerAndJobStatusHandlerService;
        this.circuitBreaker = circuitBreaker;
        this.queryJpaService = queryJpaService;
    }

    @Override
    public void createWorkers(String deploymentName, Integer newWorkers, Integer maxJobExecutorsAllowed) {
        Integer runningPods = rancherApiService.getRunningPods(deploymentName);
        if (runningPods >= maxJobExecutorsAllowed) {
            LOGGER.info("No new workers will be created since max allowed ({}) exist.", maxJobExecutorsAllowed);
            return;
        }

        final int replicas = (runningPods + newWorkers > maxJobExecutorsAllowed) ?
                maxJobExecutorsAllowed : runningPods + newWorkers;

        Runnable decorateRunnable = circuitBreaker.decorateRunnable(() -> {
            rancherApiService.scaleDeployment(deploymentName, replicas);
        });
        decorateRunnable.run();
        int scale = replicas - runningPods;
        LOGGER.info("Created {} new worker(s)", scale);
    }

    @Override
    public void deleteFailedWorkers(String deploymentName, JobExecutorType jobExecutorType) {
        List<JobExecutor> totalFailedWorkers = jobExecutorService.findByStatus(SchedulingConstants.WORKER_FAILED);

        // find failed workers by jobExecutorType
        List<JobExecutor> failedWorkersToBeDeleted = totalFailedWorkers.stream()
                .filter(jobExecutor -> jobExecutor.getJobExecutorType().equals(jobExecutorType)).collect(Collectors.toList());

        // find jobExecutorType for workers with unknown jobExecutorType and add them in failedWorkersToBeDeleted list
        // if they belong to rancher deployment with the specific deployment name
        List<JobExecutor> jobExecutorsWithUnknownType = totalFailedWorkers.stream()
                .filter(jobExecutor -> jobExecutor.getJobExecutorType().equals(JobExecutorType.Unknown)).collect(Collectors.toList());

        Map<String, String> deploymentLabels = rancherApiService.getDeploymentByName(deploymentName).getSpec().getSelector().getMatchLabels();
        for (JobExecutor jobExec : jobExecutorsWithUnknownType) {
            Map<String, String> podLabels = rancherApiService.getPod(jobExec.getName()).getMetadata().getLabels();
            if (deploymentLabels.equals(podLabels)) {
                failedWorkersToBeDeleted.add(jobExec);
            }
        }

        for (JobExecutor worker : failedWorkersToBeDeleted) {
            try {
                deleteFromRancherAndDatabase(worker, deploymentName);
            } catch (DatabaseException e) {
                LOGGER.error("Error during deletion of failed worker {}", worker.getName());
            }
        }

        Integer runningPods = rancherApiService.getRunningPods(deploymentName);
        if (runningPods == 0) {
            Runnable decorateRunnable = circuitBreaker.decorateRunnable(() -> {
                rancherApiService.scaleDeployment(deploymentName, 1);
            });
            decorateRunnable.run();
        }
    }

    @Override
    public void deleteFromRancherAndDatabase(JobExecutor worker, String deploymentName) throws DatabaseException {
        Runnable decorateRunnable = circuitBreaker.decorateRunnable(() -> {
            // scale down the deployment (reduce replicas) so that Kubernetes does not recreate the pods
            int currentReplicas = rancherApiService.getDeploymentByName(deploymentName).getSpec().getReplicas();
            int newReplicas = currentReplicas <= 1 ? 1 : currentReplicas - 1;
            rancherApiService.scaleDeployment(deploymentName, newReplicas);

            // delete the pod from Rancher
            rancherApiService.deletePod(worker.getName());
            // delete the heart beat queue
            deleteWorkerHeartBeatQueue(worker.getHeartBeatQueue());
        });
        decorateRunnable.run();

        jobExecutorService.deleteByName(worker.getName());
        LOGGER.info("Deleted worker {} from Rancher and database", worker.getName());
    }

    @Override
    public void scheduleWorkersOrchestration(String deploymentName, boolean isHeavy, JobExecutorType jobExecutorType, Integer maxJobExecutorsAllowed) {
        this.deleteFailedWorkers(deploymentName, jobExecutorType);

        InternalSchedulingStatus internalStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_QUEUED);
        List<JobEntry> jobs = jobService.findByIntSchedulingStatusAndIsHeavy(internalStatus, isHeavy);
        List<JobEntry> finalJobs = new ArrayList<>();
        if (!isHeavy) {
            switch (jobExecutorType) {
                case Sync_fme:
                    jobs = jobs.stream().filter(jobEntry -> jobEntry.getScriptType().equals(XQScript.SCRIPT_LANG_FME)).collect(Collectors.toList());
                    for (JobEntry jobEntry : jobs) {
                        QueryEntry queryEntry = queryJpaService.findByQueryId(jobEntry.getQueryId());
                        if (!queryEntry.isAsynchronousExecution()) {
                            finalJobs.add(jobEntry);
                        }
                    }
                    break;
                case Async_fme:
                    jobs = jobs.stream().filter(jobEntry -> jobEntry.getScriptType().equals(XQScript.SCRIPT_LANG_FME)).collect(Collectors.toList());
                    for (JobEntry jobEntry : jobs) {
                        QueryEntry queryEntry = queryJpaService.findByQueryId(jobEntry.getQueryId());
                        if (queryEntry.isAsynchronousExecution()) {
                            finalJobs.add(jobEntry);
                        }
                    }
                    break;
                case Light:
                    finalJobs = jobs.stream().filter(jobEntry -> !jobEntry.getScriptType().equals(XQScript.SCRIPT_LANG_FME)).collect(Collectors.toList());
                    break;
            }
        } else {
            finalJobs = jobs;
        }
        List<JobExecutor> readyWorkers = jobExecutorService.findByStatus(SchedulingConstants.WORKER_READY);
        readyWorkers = readyWorkers.stream().filter(jobExecutor -> jobExecutor.getJobExecutorType().equals(jobExecutorType)).collect(Collectors.toList());
        LOGGER.info("{} {} ready workers for {} jobs", readyWorkers.size(), jobExecutorType, finalJobs.size());
        if (finalJobs.size() > readyWorkers.size()) {
            Integer newWorkers = finalJobs.size() - readyWorkers.size();
            this.createWorkers(deploymentName, newWorkers, maxJobExecutorsAllowed);
        } else if (finalJobs.size() < readyWorkers.size()) {
            Integer runningPods = rancherApiService.getRunningPods(deploymentName);
            if (runningPods == 1) {
                return;
            }

            int workersToDelete = readyWorkers.size() - finalJobs.size();
            LOGGER.info("Preparing to delete {} {} workers", workersToDelete, jobExecutorType);
            int workersDeleted = 1;
            for (JobExecutor worker : readyWorkers) {
                while (workersDeleted <= workersToDelete) {
                    runningPods = rancherApiService.getRunningPods(deploymentName);
                    if (runningPods == 1) {
                        LOGGER.info("Only one worker instance found. No deletion required. Task Exiting.");
                        return;
                    }
                    try {
                        this.deleteFromRancherAndDatabase(worker, deploymentName);
                    } catch (DatabaseException e) {
                        LOGGER.error("Error Deleting worker {}. Exception: {}", worker.getName(), e);
                    }
                    workersDeleted++;
                    break;
                }
            }
            LOGGER.info("Deleted {} {} workers", workersDeleted, jobExecutorType);
        }
    }

    @Override
    public void updateDbStatusForFailedPods(List<Pod> failedPods, boolean isHeavy) {
        for (Pod pod : failedPods) {
            // update table JOB_EXECUTOR insert row with status failed and add history entry to JOB_EXECUTOR_HISTORY.
            String podName = pod.getMetadata().getName();
            String heartBeatQueue = podName + "-queue";
            JobExecutor jobExecutor = new JobExecutor(podName, SchedulingConstants.WORKER_FAILED, heartBeatQueue);
            try {
                if (isHeavy) {
                    LOGGER.info("Task synchronizeRancherHeavyContainersAndDbEntriesByExistenceAndStatus: setting status of pod with name {} to WORKER_FAILED", podName);
                } else {
                    LOGGER.info("Task synchronizeRancherLightContainersAndDbEntriesByExistenceAndStatus: setting status of pod with name {} to WORKER_FAILED", podName);
                }
                JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(podName, SchedulingConstants.WORKER_FAILED, new Timestamp(new Date().getTime()), heartBeatQueue);
                workerAndJobStatusHandlerService.saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
            } catch (DatabaseException e) {
                LOGGER.error("Task failed for jobExecutor with name {}", podName);
            }
        }
    }

    @Override
    public void synchronizeRancherPodsWithDbEntries(List<JobExecutor> jobExecutors, List<String> podNames) {
        for (JobExecutor jobExecutor : jobExecutors) {
            if (!podNames.contains(jobExecutor.getName())) {
                LOGGER.info("Worker retrieved form database with name: {} doesn't exist on rancher. " +
                        "Proceeding with deletion from database.", jobExecutor.getName());
                try {
                    jobExecutorService.deleteByName(jobExecutor.getName());
                    deleteWorkerHeartBeatQueue(jobExecutor.getHeartBeatQueue());
                } catch (DatabaseException e) {
                    LOGGER.error("Task synchronizeRancherContainersAndDbEntriesByExistenceAndStatus failed for jobExecutor with name {}", jobExecutor.getName());
                }
            }
        }
    }

    @Override
    public void deleteWorkerHeartBeatQueue(String queueName) {
        boolean queueDeleted = rabbitAdmin.deleteQueue(queueName);
        if (!queueDeleted) {
            LOGGER.error("Worker Heartbeat {} queue could not be deleted", queueName);
        }
    }
}
