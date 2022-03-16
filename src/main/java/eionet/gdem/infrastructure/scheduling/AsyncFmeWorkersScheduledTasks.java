package eionet.gdem.infrastructure.scheduling;

import eionet.gdem.Properties;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.PropertiesService;
import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.service.ServicesRancherApiOrchestratorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AsyncFmeWorkersScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncFmeWorkersScheduledTasks.class);
    private static final String MAX_SYNC_FME_JOB_EXECUTORS_ALLOWED = "maxAsyncFmeJobExecutorContainersAllowed";

    private WorkersOrchestrationSharedServiceImpl workersOrchestrationSharedService;
    private ServicesRancherApiOrchestratorImpl servicesRancherApiOrchestrator;
    private JobExecutorService jobExecutorService;
    private JobService jobService;
    private PropertiesService propertiesService;

    @Autowired
    public AsyncFmeWorkersScheduledTasks(WorkersOrchestrationSharedServiceImpl workersOrchestrationSharedService, ServicesRancherApiOrchestratorImpl servicesRancherApiOrchestrator, JobExecutorService jobExecutorService,
                                         JobService jobService, PropertiesService propertiesService) {
        this.workersOrchestrationSharedService = workersOrchestrationSharedService;
        this.servicesRancherApiOrchestrator = servicesRancherApiOrchestrator;
        this.jobExecutorService = jobExecutorService;
        this.propertiesService = propertiesService;
        this.jobService = jobService;
    }

    /**
     * The task runs every minute and checks how many asynchronous fme jobs have internalSchedulingStatus=2 (meaning the job has been added to rabbitmq queue and is waiting
     * for a worker to grab it) and field IS_HEAVY=false and how many asynchronous fme workers have status=1 (meaning they are ready to receive a job) and creates or
     * deletes workers accordingly.
     *
     * @throws RancherApiException
     */
    @Transactional
    @Scheduled(cron = "0 */1 * * * *")  //every minute
    public void scheduleAsyncFmeWorkersOrchestration() {
        if (!Properties.enableJobExecRancherScheduledTask) {
            return;
        }
        Integer asyncFmeJobExecutorsAllowed = Properties.maxAsyncFmeJobExecutorContainersAllowed;
        try {
            Integer value = (Integer) propertiesService.getValue(MAX_SYNC_FME_JOB_EXECUTORS_ALLOWED);
            if (value != null) asyncFmeJobExecutorsAllowed=value;
            LOGGER.info("Max asynchronous fme jobExecutors parameter set to " + asyncFmeJobExecutorsAllowed);
        } catch (DatabaseException e) {
            LOGGER.error("Max asynchronous fme jobExecutors parameter set to " + asyncFmeJobExecutorsAllowed + ", because of database error");
        }
        workersOrchestrationSharedService.scheduleWorkersOrchestration(Properties.rancherAsyncFmeJobExecServiceId, false, JobExecutorType.Async_fme, asyncFmeJobExecutorsAllowed);
    }

    /**
     * Finds asynchronous fme jobExecutor instances in rancher that have failed to run correctly (unhealthy state) and updates their status in database
     * with status=2 (FAILED). The task also finds asynchronous fme jobExecutors in database that don't exist in rancher and deletes them from database.
     *
     * @throws RancherApiException
     * @throws DatabaseException
     */
    @Scheduled(cron = "0 */2 * * * *") //Every 2 minutes
    public void synchronizeRancherAsyncFmeContainersAndDbEntriesByExistenceAndStatus() throws RancherApiException, DatabaseException {
        if (!Properties.enableJobExecRancherScheduledTask) {
            return;
        }
        try {
            //Retrieve jobExecutor instances names from Rancher
            List<String> instances = servicesRancherApiOrchestrator.getContainerInstances(Properties.rancherAsyncFmeJobExecServiceId);
            workersOrchestrationSharedService.updateDbContainersHealthStatusFromRancher(instances, true);

            List<JobExecutor> jobExecutors = jobExecutorService.listJobExecutor();
            List<JobExecutor> asyncFmeJobExecutors = jobExecutors.stream().filter(jobExecutor -> jobExecutor.getJobExecutorType().equals(JobExecutorType.Async_fme)).collect(Collectors.toList());
            workersOrchestrationSharedService.synchronizeRancherContainersWithDbEntries(asyncFmeJobExecutors, instances);
        } catch (RancherApiException rae) {
            LOGGER.error("RancherApiException: Could not retrieve job Executor instances info from Rancher");
            throw rae;
        }
    }
}
