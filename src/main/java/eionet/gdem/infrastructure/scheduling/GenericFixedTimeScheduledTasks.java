package eionet.gdem.infrastructure.scheduling;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.XMLConvException;
import eionet.gdem.cache.CacheManagerUtil;
import eionet.gdem.datadict.DDServiceClient;
import eionet.gdem.dto.DDDatasetTable;
import eionet.gdem.dto.WorkqueueJob;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.jpa.Entities.*;
import eionet.gdem.jpa.enums.AlertSeverity;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.jpa.service.*;
import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.notifications.UNSEventSender;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessage;
import eionet.gdem.rabbitMQ.service.HeartBeatMsgHandlerService;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.service.ServicesRancherApiOrchestrator;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.InputAnalyser;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import eionet.gdem.web.spring.workqueue.WorkqueueManager;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent;
import io.github.resilience4j.consumer.CircularEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GenericFixedTimeScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericFixedTimeScheduledTasks.class);

    @Autowired
    private IXQJobDao xqJobDao;
    @Qualifier("jobHistoryRepository")
    @Autowired
    private JobHistoryRepository repository;
    @Autowired
    private WorkerHeartBeatMsgService workerHeartBeatMsgService;
    @Autowired
    private JobService jobService;
    @Autowired
    private HeartBeatMsgHandlerService heartBeatMsgHandlerService;
    @Autowired
    private WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;
    @Autowired
    private QueryMetadataService queryMetadataService;
    @Autowired
    private ServicesRancherApiOrchestrator servicesRancherApiOrchestrator;
    @Autowired
    private JobExecutorService jobExecutorService;
    @Autowired
    private WorkersOrchestrationSharedService workersOrchestrationSharedService;
    @Autowired
    private AlertService alertService;
    @Autowired
    private CircularEventConsumer circularEventConsumer;
    @Autowired
    private CircuitBreaker circuitBreaker;

    /**
     * Dao for getting job data.
     */
    private WorkqueueManager jobsManager = new WorkqueueManager();

    private static final Integer MIN_UNANSWERED_REQUESTS = 10;

    @Autowired
    public GenericFixedTimeScheduledTasks() {
    }

    @Transactional
    @Scheduled(cron = "0 */5 * * * *") //Every 5 minutes
    public void schedulePeriodicUpdateOfDurationOfJobsInProcessingStatus() throws SQLException, GeneralSecurityException {
        //Retrieve jobs from T_XQJOBS with status PROCESSING (XQ_PROCESSING = 2,  INTERNAL_STATUS_ID=3)
        Map<String, Timestamp> jobsInfo = xqJobDao.getJobsWithTimestamps(Constants.XQ_PROCESSING, SchedulingConstants.INTERNAL_STATUS_PROCESSING);
        //Create new map with the duration for each job
        Map<String, Long> jobDurations = new HashMap<>();
        for (Map.Entry<String, Timestamp> entry : jobsInfo.entrySet()) {
            Long currentMs = new Timestamp(new Date().getTime()).getTime();
            long diffInMs = Math.abs(currentMs - entry.getValue().getTime());
            jobDurations.put(entry.getKey(), diffInMs);
            //Update time spent in status in table JOB_HISTORY
            repository.setDurationForJobHistory(diffInMs, entry.getKey(), Constants.XQ_PROCESSING, SchedulingConstants.INTERNAL_STATUS_PROCESSING);
        }
        //Update time spent in status in table T_XQJOBS
        xqJobDao.updateXQJobsDuration(jobDurations);
        LOGGER.info("Updated duration of jobs in PROCESSING status.");
    }

    @Transactional
    @Scheduled(cron = "0 0 */4 * * *") //Every 4 hours
    public void schedulePeriodicNotificationsForLongRunningJobs() throws Exception {
        //Retrieve jobs from T_XQJOBS with status PROCESSING (XQ_PROCESSING = 2, INTERNAL_STATUS_ID=3) and duration more than Properties.LONG_RUNNING_JOBS_EVENT
        String[] jobsIds = xqJobDao.getLongRunningJobs(Properties.longRunningJobThreshold, Constants.XQ_PROCESSING, SchedulingConstants.INTERNAL_STATUS_PROCESSING);
        if (jobsIds == null || jobsIds.length == 0) {
            return;
        }
        List<String> longRunningJobIds = Arrays.asList(jobsIds);
        if (longRunningJobIds.size() > 0) {
            LOGGER.info("Found long running jobs with ids " + longRunningJobIds);
            //send notifications to users via UNS
            new UNSEventSender().longRunningJobsNotifications(longRunningJobIds, Properties.LONG_RUNNING_JOBS_EVENT);
            LOGGER.info("Sent notifications for long running jobs");
        }
    }

    /**
     * The task runs every minute, finds jobs that have n_status=2 (processing) and internal_status_id=3 (the job has been received by a worker)
     * and sends messages to workers asking whether they are executing a specific job. The task also saves a record with the message request in the
     * table WORKER_HEART_BEAT_MSG
     */
    @Scheduled(cron = "0 */1 * * * *")  //every minute
    public void sendPeriodicHeartBeatMessages() {
        List<JobEntry> processingJobs = jobService.findProcessingJobs();
        for (JobEntry jobEntry : processingJobs) {
            try {
                WorkerHeartBeatMessage heartBeatMsgInfo = new WorkerHeartBeatMessage(jobEntry.getJobExecutorName(), jobEntry.getId(), new Timestamp(new Date().getTime()));
                WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry = new WorkerHeartBeatMsgEntry(jobEntry.getId(), jobEntry.getJobExecutorName(), heartBeatMsgInfo.getRequestTimestamp());
                heartBeatMsgHandlerService.saveMsgAndSendToRabbitMQ(heartBeatMsgInfo, workerHeartBeatMsgEntry);
            } catch (Exception e) {
                LOGGER.error("Error while setting heart beat message for job with id " + jobEntry.getId());
            }
        }
    }

    /**
     * The task runs every 5 minutes, finds jobs that have n_status=2 (processing) and internal_status_id=3 (the job has been received by a worker)
     * and checks whether there are unanswered heart beat messages in table WORKER_HEART_BEAT_MSG, meaning messages with null RESPONSE_TIMESTAMP.
     * If it finds at least 5 records for a job, then the job gets n_status=FATAL_ERROR and internal_status=cancelled.
     */
    @Scheduled(cron = "0 */5 * * * *")  //every 5 minutes
    public void checkHeartBeatMessagesForProcessingJobs() {
        List<JobEntry> processingJobs = jobService.findProcessingJobs();
        for (JobEntry jobEntry : processingJobs) {
            try {
                List<WorkerHeartBeatMsgEntry> heartBeatMsgList = workerHeartBeatMsgService.findUnAnsweredHeartBeatMessages(jobEntry.getId());
                if (heartBeatMsgList.size() >= MIN_UNANSWERED_REQUESTS) {
                    LOGGER.info("Setting the status of job " + jobEntry.getId() + " to " + Constants.XQ_FATAL_ERR + ", because of " + heartBeatMsgList.size() + " records with null response timestamp");
                    InternalSchedulingStatus internalStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
                    jobEntry.setnStatus(Constants.XQ_FATAL_ERR).setIntSchedulingStatus(internalStatus).setTimestamp(new Timestamp(new Date().getTime()));
                    workerAndJobStatusHandlerService.updateJobAndJobHistoryEntries(jobEntry);
                    Long durationOfJob = Utils.getDifferenceBetweenTwoTimestampsInMs(new Timestamp(new Date().getTime()), jobEntry.getTimestamp());
                    queryMetadataService.storeScriptInformation(jobEntry.getQueryId(), jobEntry.getFile(), jobEntry.getScriptType(), durationOfJob, Constants.XQ_FATAL_ERR, jobEntry.getId(), null);
                }
            } catch (Exception e) {
                LOGGER.error("Error while checking heart beat messages for job with id " + jobEntry.getId());
            }
        }
    }

    /**
     * The task runs every 30 minutes, finds all jobs with n_status=2 and internal_status=3 and interrupts a job if its duration is longer than
     * corresponding schema's maxExecutionTime, meaning the task sets job's n_status=7 (status for interrupted) and internal_status=4 (cancelled).
     * The task also changes worker's status to 2 (failed).
     * Actual Job Interruption happens in 2 ways:
     * - If a jobExecutor as already picked a job that should be interrupted, we mark this JobExecutor-worker as 'Failed',
     * So as to be deleted by converters.
     * - If a jobExecutor picks a job whose status has changed to interrupted, before executing this job, the jobExecutor will ask converters
     * and learn about this INterrupted status, and will then gradually reject the rabbitmq message for this job, so no other jobExecutors
     * pick it.
     */
    @Scheduled(cron = "0 */30 * * * *")  //every 30 minutes
    public void interruptLongRunningJobs() {
        SchemaManager schemaManager = new SchemaManager();
        List<JobEntry> jobEntries = null;
        try {
            jobEntries = jobService.findProcessingJobs();
        } catch (Exception e) {
            LOGGER.error("Error while fetching long running jobs.");
            return;
        }
        if (jobEntries != null && jobEntries.size() > 0) {
            for (JobEntry jobEntry : jobEntries) {
                if (jobEntry.getDuration() == null) {
                    continue;
                }
                String schemaUrl = null;
                try {
                    schemaUrl = findSchemaFromXml(jobEntry.getUrl());
                    Long schemaMaxExecTime = schemaManager.getSchemaMaxExecutionTime(schemaUrl);
                    if (jobEntry.getDuration().compareTo(BigInteger.valueOf(schemaMaxExecTime)) > 0) {
                        LOGGER.info("Interrupting job " + jobEntry.getId() + " because exceeded schema's max execution time.");
                        InternalSchedulingStatus internalStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
                        jobEntry.setnStatus(Constants.XQ_INTERRUPTED).setIntSchedulingStatus(internalStatus).setTimestamp(new Timestamp(new Date().getTime()));
                        workerAndJobStatusHandlerService.handleCancelledJob(jobEntry, SchedulingConstants.WORKER_FAILED);
                    }
                } catch (Exception e) {
                    LOGGER.error("Error while running interruptLongRunningJobs task for job with id " + jobEntry.getId() + ", " + e);
                }
            }
        }
    }

    /**
     * Finds schema from XML
     *
     * @param xml XML
     * @return Result
     */
    String findSchemaFromXml(String xml) throws XMLConvException {
        InputAnalyser analyser = new InputAnalyser();
        try {
            analyser.parseXML(xml);
            String schemaOrDTD = analyser.getSchemaOrDTD();
            return schemaOrDTD;
        } catch (Exception e) {
            throw new XMLConvException("Could not extract schema");
        }
    }

    /**
     * Deletes expired finished jobs from workqueue
     **/
    @Scheduled(cron = "0 0 */3 * * *") //Every 3 hours
    public void schedulePeriodicCleanupOfFinishedWorkqueueJobs() {
        LOGGER.info("Cleanup of finished workqueue jobs.");
        try {
            List<WorkqueueJob> jobs = jobsManager.getFinishedJobs();

            if (jobs != null) {
                for (WorkqueueJob job : jobs) {
                    if (canDeleteJob(job)) {
                        jobsManager.endXQJob(job);
                    }
                }
            }
        } catch (DCMException e) {
            LOGGER.error("Error when running work-queue clearner job: ", e);
        }
    }

    /**
     * Check the job's age and return true if it is possible to delete it.
     *
     * @param job Workqueue job object
     * @return true if job can be deleted.
     */
    public static boolean canDeleteJob(WorkqueueJob job) {
        boolean canDelete = false;
        if (job != null && job.getJobTimestamp() != null && job.getStatus() >= Constants.XQ_READY) {
            Calendar now = Calendar.getInstance();
            int maxAge = Properties.wqJobMaxAge == 0 ? -1 : -Properties.wqJobMaxAge;
            now.add(Calendar.HOUR, maxAge);
            Calendar jobCal = Calendar.getInstance();
            jobCal.setTime(job.getJobTimestamp());
            if (now.after(jobCal)) {
                canDelete = true;
            }
        }
        return canDelete;
    }

    /**
     * Updates Data Dictionary tables cache.
     **/
    @Scheduled(cron = "0 0 */1 * * *") //Every 1 hour
    public void schedulePeriodicDDTablesCacheUpdate() {
        LOGGER.info("Updating DD tables chache.");
        try {
            List<DDDatasetTable> ddTables = DDServiceClient.getDDTablesFromDD();

            CacheManagerUtil.updateDDTablesCache(ddTables);
            LOGGER.info("DD tables cache updated");
        } catch (Exception e) {
            LOGGER.error("Error when updating DD tables cache: ", e);
        }
    }

    /**
     * Runs every 2 minutes and checks if a worker with unknown type exists in light and heavy rancher services and if not, deletes it from database
     */
    @Scheduled(cron = "0 */2 * * * *")  //every 2 minutes
    public void synchronizeWorkersWithUnknownTypeDbEntriesAndRancher() throws DatabaseException, RancherApiException {
        if (!Properties.enableJobExecRancherScheduledTask) {
            return;
        }
        try {
            List<String> lightInstances = servicesRancherApiOrchestrator.getContainerInstances(Properties.rancherLightJobExecServiceId);
            List<String> heavyInstances = servicesRancherApiOrchestrator.getContainerInstances(Properties.rancherHeavyJobExecServiceId);
            List<JobExecutor> jobExecutors = jobExecutorService.listJobExecutor();
            List<JobExecutor> jobExecutorsWithUnknownStatus = jobExecutors.stream().filter(jobExecutor -> jobExecutor.getJobExecutorType().equals(JobExecutorType.Uknown)).collect(Collectors.toList());
            for (JobExecutor jobExecutor : jobExecutorsWithUnknownStatus) {
                if (!lightInstances.contains(jobExecutor.getContainerId()) && !heavyInstances.contains(jobExecutor.getContainerId())) {
                    LOGGER.info("Container retrieved form Database  with ID:" + jobExecutor.getContainerId() + " and name:" + jobExecutor.getName() +
                            " doesn't exist on rancher.Proceeding with deletion from Database");
                    try {
                        jobExecutorService.deleteByContainerId(jobExecutor.getContainerId());
                        workersOrchestrationSharedService.deleteWorkerHeartBeatQueue(jobExecutor.getHeartBeatQueue());
                    } catch (DatabaseException e) {
                        LOGGER.error("Task synchronizeRancherContainersAndDbEntriesByExistenceAndStatus failed for jobExecutor with name " + jobExecutor.getName());
                    }
                }
            }
        } catch (RancherApiException e) {
            LOGGER.error("RancherApiException: Could not retrieve job Executor instances info from Rancher");
            throw e;
        }
    }

    /**
     * The task runs every minute, finds if there are events created by circuit breaker and saves them as alerts in ALERTS table.
     * Only not_permitted events are buffered in circularEventConsumer.
     */
    @Scheduled(cron = "0 */1 * * * *")  //every minute
    public void sendRancherCircuitBreakerEventsToUns() {
        LOGGER.info("Task for finding creating and sending alerts is running");
        io.vavr.collection.List<CircuitBreakerEvent> bufferedEvents = circularEventConsumer.getBufferedEvents();
        bufferedEvents.forEach(event -> {
            AlertEntry alertEntry =  new AlertEntry().setSeverity(AlertSeverity.CRITICAL.getId()).setDescription("JobExecutor rancher orchestration malfunctions, circuit breaker is open")
                    .setOccurrenceDate(Timestamp.valueOf(event.getCreationTime().toLocalDateTime()));
            try {
                new UNSEventSender().rancherCircuitBreakerOpenNotification(event.getCreationTime() + ", Time exceeded for rancher proper functionality", Properties.RANCHER_CIRCUIT_BREAKER_EVENT);
                alertEntry.setNotificationSentToUns(true);
            } catch (Exception e) {
                LOGGER.error("Error sending rancher circuit breaker event to uns");
                alertEntry.setNotificationSentToUns(false);
            } finally {
                alertService.save(alertEntry);
            }
        });
        if (!bufferedEvents.isEmpty()) {
            circularEventConsumer = new CircularEventConsumer(10);
            circuitBreaker.getEventPublisher().onCallNotPermitted(circularEventConsumer);
        }
    }

}






















