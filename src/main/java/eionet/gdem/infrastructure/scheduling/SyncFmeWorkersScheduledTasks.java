package eionet.gdem.infrastructure.scheduling;

import eionet.gdem.Properties;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.PropertiesService;
import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.rancher.service.RancherApiService;
import io.fabric8.kubernetes.api.model.Pod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SyncFmeWorkersScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncFmeWorkersScheduledTasks.class);
    private static final String MAX_SYNC_FME_JOB_EXECUTORS_ALLOWED = "maxSyncFmeJobExecutorContainersAllowed";

    private final WorkersOrchestrationSharedService workersOrchestrationSharedService;
    private final RancherApiService rancherApiService;
    private final JobExecutorService jobExecutorService;
    private final PropertiesService propertiesService;

    @Autowired
    public SyncFmeWorkersScheduledTasks(WorkersOrchestrationSharedService workersOrchestrationSharedService,
                                        RancherApiService rancherApiService,
                                        JobExecutorService jobExecutorService,
                                        PropertiesService propertiesService) {
        this.workersOrchestrationSharedService = workersOrchestrationSharedService;
        this.rancherApiService = rancherApiService;
        this.jobExecutorService = jobExecutorService;
        this.propertiesService = propertiesService;
    }

    /**
     * The task runs every minute and checks how many synchronous fme jobs have internalSchedulingStatus=2 (meaning the job has been added to rabbitmq queue and is waiting
     * for a worker to grab it) and field IS_HEAVY=false and how many synchronous fme workers have status=1 (meaning they are ready to receive a job) and creates or
     * deletes workers accordingly.
     */
    @Transactional
    @Scheduled(cron = "0 */1 * * * *")  // every minute
    public void scheduleSyncFmeWorkersOrchestration() {
        if (!Properties.enableJobExecRancherScheduledTask) {
            return;
        }
        Integer syncFmeJobExecutorsAllowed = Properties.maxSyncFmeJobExecutorContainersAllowed;
        try {
            Integer value = (Integer) propertiesService.getValue(MAX_SYNC_FME_JOB_EXECUTORS_ALLOWED);
            if (value != null) syncFmeJobExecutorsAllowed = value;
            LOGGER.info("Max synchronous fme jobExecutors parameter set to {}", syncFmeJobExecutorsAllowed);
        } catch (DatabaseException e) {
            LOGGER.error("Max synchronous fme jobExecutors parameter set to {}, because of database error", syncFmeJobExecutorsAllowed);
        }
        workersOrchestrationSharedService.scheduleWorkersOrchestration(Properties.RANCHER_SYNC_FME_JOBEXEC_DEPLOYMENT_NAME, false, JobExecutorType.Sync_fme, syncFmeJobExecutorsAllowed);
    }

    /**
     * Finds synchronous fme jobExecutor pods in rancher that have failed to run correctly (unhealthy state) and updates their status in database
     * with status=2 (FAILED). The task also finds synchronous fme jobExecutors in database that don't exist in rancher and deletes them from database.
     *
     * @throws DatabaseException
     */
    @Scheduled(cron = "0 */2 * * * *") // every 2 minutes
    public void synchronizeRancherSyncFmeContainersAndDbEntriesByExistenceAndStatus() throws DatabaseException {
        if (!Properties.enableJobExecRancherScheduledTask) {
            return;
        }
        // Retrieve jobExecutor failed pods from Rancher
        List<Pod> failedPods = rancherApiService.getPods(Properties.RANCHER_SYNC_FME_JOBEXEC_DEPLOYMENT_NAME);
        workersOrchestrationSharedService.updateDbStatusForFailedPods(failedPods, true);

        List<JobExecutor> jobExecutors = jobExecutorService.listJobExecutor();
        List<JobExecutor> syncFmeJobExecutors = jobExecutors.stream().filter(jobExecutor -> jobExecutor.getJobExecutorType().equals(JobExecutorType.Sync_fme)).collect(Collectors.toList());
        List<String> podNames = rancherApiService.getPodNames(Properties.RANCHER_SYNC_FME_JOBEXEC_DEPLOYMENT_NAME);
        workersOrchestrationSharedService.synchronizeRancherPodsWithDbEntries(syncFmeJobExecutors, podNames);
    }
}
