package eionet.gdem.infrastructure.scheduling;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.PropertiesService;
import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
import eionet.gdem.rancher.service.RancherApiService;
import io.fabric8.kubernetes.api.model.Pod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HeavyWorkersScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeavyWorkersScheduledTasks.class);
    private static final String MAX_HEAVY_JOB_EXECUTORS_ALLOWED = "maxHeavyJobExecutorContainersAllowed";

    private final WorkersOrchestrationSharedService workersOrchestrationSharedService;
    private final RancherApiService rancherApiService;
    private final JobExecutorService jobExecutorService;
    private final WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;
    private final JobService jobService;
    private final PropertiesService propertiesService;

    @Autowired
    public HeavyWorkersScheduledTasks(WorkersOrchestrationSharedService workersOrchestrationSharedService,
                                      RancherApiService rancherApiService,
                                      JobExecutorService jobExecutorService,
                                      WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService,
                                      JobService jobService,
                                      PropertiesService propertiesService) {
        this.workersOrchestrationSharedService = workersOrchestrationSharedService;
        this.rancherApiService = rancherApiService;
        this.jobExecutorService = jobExecutorService;
        this.workerAndJobStatusHandlerService = workerAndJobStatusHandlerService;
        this.propertiesService = propertiesService;
        this.jobService = jobService;
    }

    /**
     * The task runs every minute and checks how many jobs have internalSchedulingStatus=2 (meaning the job has been added to rabbitmq queue and is waiting
     * for a worker to grab it) and field IS_HEAVY=true and how many heavy workers have status=1 (meaning they are ready to receive a job) and creates or
     * deletes workers accordingly.
     */
    @Transactional
    @Scheduled(cron = "0 */1 * * * *")  // every minute
    public void scheduleHeavyWorkersOrchestration() {
        if (!Properties.enableJobExecRancherScheduledTask) {
            return;
        }
        Integer heavyJobExecutorsAllowed = Properties.maxHeavyJobExecutorContainersAllowed;
        try {
            Integer value = (Integer) propertiesService.getValue(MAX_HEAVY_JOB_EXECUTORS_ALLOWED);
            if (value != null) heavyJobExecutorsAllowed = value;
            LOGGER.info("Max heavy jobExecutors parameter set to {}", heavyJobExecutorsAllowed);
        } catch (DatabaseException e) {
            LOGGER.error("Max heavy jobExecutors parameter set to {}, because of database error", heavyJobExecutorsAllowed);
        }
        workersOrchestrationSharedService.scheduleWorkersOrchestration(Properties.RANCHER_HEAVY_JOBEXEC_DEPLOYMENT_NAME, true, JobExecutorType.Heavy, heavyJobExecutorsAllowed);
    }

    /**
     * Finds heavy jobExecutor pods in rancher that have failed to run correctly (unhealthy state) and updates their status in database
     * with status=2 (FAILED). The task also finds heavy jobExecutors in database that don't exist in rancher and deletes them from database.
     *
     * @throws DatabaseException
     */
    @Transactional
    @Scheduled(cron = "0 */2 * * * *") // every 2 minutes
    public void synchronizeRancherHeavyContainersAndDbEntriesByExistenceAndStatus() throws DatabaseException {
        if (!Properties.enableJobExecRancherScheduledTask) {
            return;
        }
        // Retrieve jobExecutor failed pods from Rancher
        List<Pod> failedPods = rancherApiService.getPods(Properties.RANCHER_HEAVY_JOBEXEC_DEPLOYMENT_NAME);
        workersOrchestrationSharedService.updateDbStatusForFailedPods(failedPods, true);

        List<JobExecutor> jobExecutors = jobExecutorService.listJobExecutor();
        List<JobExecutor> heavyJobExecutors = jobExecutors.stream().filter(jobExecutor -> jobExecutor.getJobExecutorType().equals(JobExecutorType.Heavy)).collect(Collectors.toList());
        List<String> podNames = rancherApiService.getPodNames(Properties.RANCHER_HEAVY_JOBEXEC_DEPLOYMENT_NAME);
        workersOrchestrationSharedService.synchronizeRancherPodsWithDbEntries(heavyJobExecutors, podNames);
    }

    /**
     * Finds heavy jobs that their heavyRetriesOnFailure have exceeded maxHeavyRetries (meaning the heavy worker has run out of memory maxHeavyRetries times) and marks
     * the jobs as fatal_error (n_status=4 and internal_status=4) and the workers that have been executing them as failed (status=2)
     */
    @Scheduled(cron = "0 */1 * * * *")  // every minute
    public void checkProcessingHeavyJobs() {
        List<JobEntry> heavyProcessingJobs = jobService.findProcessingJobs().stream().filter(JobEntry::isHeavy).collect(Collectors.toList());
        for (JobEntry jobEntry : heavyProcessingJobs) {
            try {
                if (jobEntry.getHeavyRetriesOnFailure() != null && jobEntry.getHeavyRetriesOnFailure() == Properties.maxHeavyRetries.intValue()) {
                    LOGGER.info("Setting the status of job {} to " + Constants.XQ_FATAL_ERR + ", because heavy worker {} reached maximum heavy retries", jobEntry.getId(), jobEntry.getJobExecutorName());
                    InternalSchedulingStatus internalStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
                    jobEntry.setnStatus(Constants.XQ_FATAL_ERR).setIntSchedulingStatus(internalStatus).setTimestamp(new Timestamp(new Date().getTime()));
                    workerAndJobStatusHandlerService.handleCancelledJob(jobEntry, SchedulingConstants.WORKER_FAILED);
                }
            } catch (DatabaseException e) {
                LOGGER.error("Error while checking processing heavy job with id {}", jobEntry.getId());
            }
        }
    }
}
