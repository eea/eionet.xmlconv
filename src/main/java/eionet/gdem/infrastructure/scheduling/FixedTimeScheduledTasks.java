package eionet.gdem.infrastructure.scheduling;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.XMLConvException;
import eionet.gdem.jpa.Entities.*;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.WorkerHeartBeatMsgService;
import eionet.gdem.notifications.UNSEventSender;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessageInfo;
import eionet.gdem.rabbitMQ.service.HeartBeatMsgHandlerService;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ContainerData;
import eionet.gdem.rancher.model.ServiceApiRequestBody;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestrator;
import eionet.gdem.rancher.service.ServicesRancherApiOrchestrator;
import eionet.gdem.validation.InputAnalyser;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
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

@Service
public class FixedTimeScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedTimeScheduledTasks.class);

    @Autowired
    private IXQJobDao xqJobDao;
    @Qualifier("jobHistoryRepository")
    @Autowired
    JobHistoryRepository repository;
    @Autowired
    private ServicesRancherApiOrchestrator servicesOrchestrator;
    @Autowired
    private ContainersRancherApiOrchestrator containersOrchestrator;
    @Autowired
    private JobExecutorService jobExecutorService;
    @Autowired
    private WorkerHeartBeatMsgService workerHeartBeatMsgService;
    @Autowired
    private JobService jobService;
    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Autowired
    HeartBeatMsgHandlerService heartBeatMsgHandlerService;
    @Autowired
    WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;

    private static final Integer MIN_UNANSWERED_REQUESTS = 5;

    @Autowired
    public FixedTimeScheduledTasks() {
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
    public void schedulePeriodicNotificationsForLongRunningJobs() throws SQLException, GeneralSecurityException {
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
     * The task runs every minute and checks how many jobs have internalScedulingStatus=2 (meaning the job has been added to rabbitmq queue and is waiting
     * for a worker to grab it) and how many workers have status=1 (meaning they are ready to receive a job) and creates or deletes workers accordingly.
     *
     * @throws RancherApiException
     */
    @Transactional
    @Scheduled(cron = "0 */1 * * * *")  //every minute
    public void scheduleWorkersOrchestration() {
        if (!Properties.enableJobExecRancherScheduledTask) {
            return;
        }
        String serviceId = Properties.rancherJobExecServiceId;

        try {
            deleteFailedWorkers(serviceId);
        } catch (RancherApiException | DatabaseException e) {
            LOGGER.error("Error during deletion of failed workers");
            return;
        }

        InternalSchedulingStatus internalStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_QUEUED);
        List<JobEntry> jobs = jobService.findByIntSchedulingStatus(internalStatus);
        List<JobExecutor> readyWorkers = jobExecutorService.findByStatus(SchedulingConstants.WORKER_READY);
        if (jobs.size() > readyWorkers.size()) {
            Integer newWorkers = jobs.size() - readyWorkers.size();
            try {
                createWorkers(serviceId, newWorkers);
            } catch (RancherApiException e) {
                LOGGER.error("Worker creation failed.");
                return;
            }
        } else if (jobs.size() < readyWorkers.size()) {
            List<String> instances = null;
            try {
                instances = servicesOrchestrator.getContainerInstances(serviceId);
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
                        instances = servicesOrchestrator.getContainerInstances(serviceId);
                    } catch (RancherApiException e) {
                        LOGGER.error("cannot get instances in order to delete them later, " + e);
                        return;
                    }
                    if (instances.size() == 1) {
                        LOGGER.info("Only one worker instance found. No deletion required. Task Exiting.");
                        return;
                    }
                    try {
                        deleteFromRancherAndDatabase(worker);
                    } catch (RancherApiException | DatabaseException e) {
                        LOGGER.error("Error Deleting worker " + worker.getName() + ", " + e);
                    }
                    workersDeleted++;
                    break;
                }
            }
        }

    }

    /**
     * deletes workers that have failed to run correctly
     *
     * @throws RancherApiException
     */
    void deleteFailedWorkers(String serviceId) throws RancherApiException, DatabaseException {
        List<JobExecutor> failedWorkers = jobExecutorService.findByStatus(SchedulingConstants.WORKER_FAILED);
        List<String> instances = servicesOrchestrator.getContainerInstances(serviceId);
        if (failedWorkers.size()==1 && instances.size()==1) {
            LOGGER.info("Restarting failed worker because found only one worker instance.");
            containersOrchestrator.restartContainer(failedWorkers.get(0).getName());
            return;
        }
        for (JobExecutor worker : failedWorkers) {
            deleteFromRancherAndDatabase(worker);
        }
    }

    /**
     * deletes worker from rancher and JOB_EXECUTOR table
     *
     * @param worker
     * @throws RancherApiException
     */
    synchronized void deleteFromRancherAndDatabase(JobExecutor worker) throws RancherApiException, DatabaseException {
        containersOrchestrator.deleteContainer(worker.getName());
        jobExecutorService.deleteByName(worker.getName());
        boolean queueDeleted = rabbitAdmin.deleteQueue(worker.getHeartBeatQueue());
        if (!queueDeleted) {
            LOGGER.error("Worker Heartbeat queue  could not be deleted:" + worker.getHeartBeatQueue());
        }
    }

    /**
     * increase workers so that maxJobExecutorContainersAllowed is not exceeded for JobExecutor containers
     *
     * @param serviceId
     * @param newWorkers
     * @throws RancherApiException
     */
    void createWorkers(String serviceId, Integer newWorkers) throws RancherApiException {
        List<String> instances = servicesOrchestrator.getContainerInstances(serviceId);
        Integer maxJobExecutorsAllowed = Properties.maxJobExecutorContainersAllowed;
        Integer scale = newWorkers;
        if (instances.size() == maxJobExecutorsAllowed) {
            return;
        } else if (instances.size() + newWorkers > maxJobExecutorsAllowed) {
            scale = maxJobExecutorsAllowed - instances.size();
        }
        ServiceApiRequestBody serviceApiRequestBody = new ServiceApiRequestBody().setScale(instances.size() + scale);
        servicesOrchestrator.scaleUpOrDownContainerInstances(serviceId, serviceApiRequestBody);
        LOGGER.info("Created " + scale + " new worker(s)");
    }

    /**
     * Finds jobExecutor instances in rancher that have failed to run correctly (unhealthy state) and updates their status in database
     * with status=2 (FAILED). The task also finds jobExecutors in database that don't exist in rancher and deletes them from database.
     *
     * @throws RancherApiException
     * @throws DatabaseException
     */
    @Scheduled(cron = "0 */2 * * * *") //Every 2 minutes
    public void synchronizeRancherContainersAndDbEntriesByExistenceAndStatus() throws RancherApiException, DatabaseException {
        if (!Properties.enableJobExecRancherScheduledTask) {
            return;
        }
        try {
            //Retrieve jobExecutor instances names from Rancher
            List<String> instances = servicesOrchestrator.getContainerInstances(Properties.rancherJobExecServiceId);
            this.updateDbContainersHealthStatusFromRancher(instances);

            List<JobExecutor> jobExecutors = jobExecutorService.listJobExecutor();
            this.synchronizeRancherContainersWithDbEntries(jobExecutors, instances);
        } catch (RancherApiException rae) {
            LOGGER.error("RancherApiException: Could not retrieve job Executor instances info from Rancher");
            throw rae;
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
                WorkerHeartBeatMessageInfo heartBeatMsgInfo = new WorkerHeartBeatMessageInfo(jobEntry.getJobExecutorName(), jobEntry.getId(), new Timestamp(new Date().getTime()));
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
                    InternalSchedulingStatus internalStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
                    workerAndJobStatusHandlerService.updateJobAndJobHistoryEntries(Constants.XQ_FATAL_ERR, internalStatus, jobEntry);
                }
            } catch (Exception e) {
                LOGGER.error("Error while checking heart beat messages for job with id " + jobEntry.getId());
            }
        }
    }

    protected void updateDbContainersHealthStatusFromRancher(List<String> instances) throws RancherApiException {
        for (String containerId : instances) {
            //for each instance find status
            ContainerData data = containersOrchestrator.getContainerInfoById(containerId);
            String healthState = data.getHealthState();
            if (healthState.equals(SchedulingConstants.CONTAINER_HEALTH_STATE_ENUM.UNHEALTHY.getValue())) {
                //update table JOB_EXECUTOR insert row with status failed and add history entry to JOB_EXECUTOR_HISTORY.
                String containerName = data.getName();
                String heartBeatQueue = containerName + "-queue";
                JobExecutor jobExecutor = new JobExecutor(containerName, containerId, SchedulingConstants.WORKER_FAILED, heartBeatQueue);
                try {
                    JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(containerName, containerId, SchedulingConstants.WORKER_FAILED, new Timestamp(new Date().getTime()), heartBeatQueue);
                    workerAndJobStatusHandlerService.saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
                } catch (DatabaseException e) {
                    LOGGER.error("Task failed for jobExecutor with containerId " + containerId);
                }
            }
        }
    }

    protected void synchronizeRancherContainersWithDbEntries(List<JobExecutor> jobExecutors, List<String> instances) {
        for (JobExecutor jobExecutor : jobExecutors) {
            if (!instances.contains(jobExecutor.getContainerId())) {
                LOGGER.info("Container retrieved form Database  with ID:" + jobExecutor.getContainerId() + " and name:" + jobExecutor.getName() +
                        " doesn't exist on rancher.Proceeding with deletion from Database");
                try {
                    jobExecutorService.deleteByContainerId(jobExecutor.getContainerId());
                    boolean queueDeleted = rabbitAdmin.deleteQueue(jobExecutor.getHeartBeatQueue());
                    if (!queueDeleted) {
                        LOGGER.error("Worker Heartbeat queue  could not be deleted:" + jobExecutor.getHeartBeatQueue());
                    }
                } catch (DatabaseException e) {
                    LOGGER.error("Task failed for jobExecutor with name " + jobExecutor.getName());
                }
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
                        InternalSchedulingStatus internalStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
                        workerAndJobStatusHandlerService.handleCancelledJob(jobEntry, SchedulingConstants.WORKER_FAILED, Constants.XQ_INTERRUPTED, internalStatus);
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

}






















