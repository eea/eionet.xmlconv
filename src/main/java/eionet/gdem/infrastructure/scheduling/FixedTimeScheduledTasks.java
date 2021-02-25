package eionet.gdem.infrastructure.scheduling;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.repositories.JobExecutorRepository;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.jpa.repositories.JobRepository;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.notifications.UNSEventSender;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ContainerApiResponse;
import eionet.gdem.rancher.model.ContainerData;
import eionet.gdem.rancher.model.ServiceApiRequestBody;
import eionet.gdem.rancher.model.ServiceApiResponse;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestrator;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestratorImpl;
import eionet.gdem.rancher.service.ServicesRancherApiOrchestrator;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Qualifier("jobRepository")
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobExecutorRepository jobExecutorRepository;
    @Autowired
    private ServicesRancherApiOrchestrator servicesOrchestrator;
    @Autowired
    private ContainersRancherApiOrchestrator containersOrchestrator;

    @Autowired
    private JobExecutorService jobExecutorService;

    @Autowired
    private JobExecutorHistoryService jobExecutorHistoryService;

    @Autowired
    public FixedTimeScheduledTasks() {
    }

    @Transactional
    @Scheduled(cron = "0 */5 * * * *") //Every 5 minutes
    public void schedulePeriodicUpdateOfDurationOfJobsInProcessingStatus() throws SQLException, GeneralSecurityException {
        //Retrieve jobs from T_XQJOBS with status PROCESSING (XQ_PROCESSING = 2)
        Map<String, Timestamp> jobsInfo = xqJobDao.getJobsWithTimestamps(Constants.XQ_PROCESSING);
        //Create new map with the duration for each job
        Map<String, Long> jobDurations = new HashMap<>();
        for (Map.Entry<String,Timestamp> entry : jobsInfo.entrySet()) {
            Long currentMs = new Timestamp(new Date().getTime()).getTime();
            long diffInMs = Math.abs(currentMs - entry.getValue().getTime());
            jobDurations.put(entry.getKey(), diffInMs);
            //Update time spent in status in table JOB_HISTORY
            repository.setDurationForJobHistory(diffInMs, entry.getKey(), Constants.XQ_PROCESSING);
        }
        //Update time spent in status in table T_XQJOBS
        xqJobDao.updateXQJobsDuration(jobDurations);
        LOGGER.info("Updated duration of jobs in PROCESSING status.");
    }

    @Transactional
    @Scheduled(cron = "0 0 */4 * * *") //Every 4 hours
    public void schedulePeriodicNotificationsForLongRunningJobs() throws SQLException, GeneralSecurityException {
        //Retrieve jobs from T_XQJOBS with status PROCESSING (XQ_PROCESSING = 2) and duration more than Properties.LONG_RUNNING_JOBS_EVENT
        String[] jobsIds = xqJobDao.getLongRunningJobs(Properties.longRunningJobThreshold, Constants.XQ_PROCESSING);
        if (jobsIds == null || jobsIds.length == 0){
            return;
        }
        List<String> longRunningJobIds = Arrays.asList(jobsIds);
        if(longRunningJobIds.size() > 0){
            LOGGER.info("Found long running jobs with ids " + longRunningJobIds);
            //send notifications to users via UNS
            new UNSEventSender().longRunningJobsNotifications(longRunningJobIds, Properties.LONG_RUNNING_JOBS_EVENT);
            LOGGER.info("Sent notifications for long running jobs");
        }
    }

    @Transactional
    @Scheduled(cron= "0 */2 * * * *")  //every 2 minutes
    public void scheduleWorkersOrchestration() throws RancherApiException {
        if (!Properties.enableJobExecRancherScheduledTask) {
            return;
        }
        String serviceId = Properties.rancherJobExecServiceId;
        InternalSchedulingStatus internalStatus = new InternalSchedulingStatus().setId(2);
        List<JobEntry> jobs = jobRepository.findByIntSchedulingStatus(internalStatus);
        List<JobExecutor> readyWorkers = jobExecutorRepository.findByStatus(SchedulingConstants.WORKER_READY);
        try {
            if (jobs.size()>readyWorkers.size()) {
                Integer newWorkers = jobs.size() - readyWorkers.size();
                try {
                    createWorkers(serviceId, newWorkers);
                } catch (RancherApiException e) {
                    LOGGER.info("JobExecutor scaling failed. Trying again.");
                    createWorkers(serviceId, newWorkers);
                }
            } else {
                List<String> instances = servicesOrchestrator.getContainerInstances(serviceId);
                if (instances.size()==1) {
                    return;
                }
                Integer workersToDelete = readyWorkers.size() - jobs.size();
                Integer count = 1;
                for (JobExecutor worker : readyWorkers) {
                    if (count == workersToDelete) {
                        instances = servicesOrchestrator.getContainerInstances(serviceId);
                        if (instances.size()==1) {
                            return;
                        }
                        deleteFromRancherAndDatabase(worker);
                        return;
                    }
                    deleteFromRancherAndDatabase(worker);
                    count++;
                }
            }
            deleteFailedWorkers();
        } catch (RancherApiException e) {
            LOGGER.info("Error occurred during workers orchestration, " + e.getMessage());
            ServiceApiResponse serviceInfo = servicesOrchestrator.getServiceInfo(serviceId);
            List<String> instances = servicesOrchestrator.getContainerInstances(serviceId);
            if (serviceInfo.getScale() < instances.size()) {
                Integer newScale = instances.size() - serviceInfo.getScale();
                ServiceApiRequestBody serviceApiRequestBody = new ServiceApiRequestBody().setScale(serviceInfo.getScale() + newScale);
                LOGGER.info("Scaling up again because of error");
                servicesOrchestrator.scaleUpOrDownContainerInstances(serviceId, serviceApiRequestBody);
            }
        }
    }

    /**
     * deletes workers that have failed to run correctly
     * @throws RancherApiException
     */
    void deleteFailedWorkers() throws RancherApiException {
        List<JobExecutor> failedWorkers = jobExecutorRepository.findByStatus(SchedulingConstants.WORKER_FAILED);
        for (JobExecutor worker : failedWorkers) {
            deleteFromRancherAndDatabase(worker);
        }
    }


    /**
     * deletes worker from rancher and JOB_EXECUTOR table
     * @param worker
     * @throws RancherApiException
     */
    synchronized void deleteFromRancherAndDatabase(JobExecutor worker) throws RancherApiException {
        containersOrchestrator.deleteContainer(worker.getName());
        jobExecutorRepository.deleteByName(worker.getName());
    }

    /**
     * increase workers so that maxJobExecutorContainersAllowed is not exceeded for JobExecutor containers
     * @param serviceId
     * @param newWorkers
     * @throws RancherApiException
     */
    void createWorkers(String serviceId, Integer newWorkers) throws RancherApiException {
        List<String> instances = servicesOrchestrator.getContainerInstances(serviceId);
        Integer maxJobExecutorsAllowed = Properties.maxJobExecutorContainersAllowed;
        Integer scale = newWorkers;
        if (instances.size()==maxJobExecutorsAllowed) {
            return;
        } else if (instances.size()+newWorkers>maxJobExecutorsAllowed) {
            scale = maxJobExecutorsAllowed - instances.size();
        }
        ServiceApiRequestBody serviceApiRequestBody = new ServiceApiRequestBody().setScale(instances.size()+scale);
        servicesOrchestrator.scaleUpOrDownContainerInstances(serviceId, serviceApiRequestBody);
        LOGGER.info("Created " + scale + " new worker(s)");
    }

    @Transactional
    @Scheduled(cron = "0 */2 * * * *") //Every 2 minutes
    public void checkWorkersStatusInRancherAndUpdateDB() throws RancherApiException {
        try {
            //Retrieve jobExecutor instances names from Rancher
            List<String> instances = servicesOrchestrator.getContainerInstances(Properties.rancherJobExecServiceId);
            for (String containerId: instances) {
                //for each instance find status
                ContainerData data = containersOrchestrator.getContainerInfoById(containerId);
                String healthState = data.getHealthState();
                if(healthState.equals(SchedulingConstants.CONTAINER_HEALTH_STATE_ENUM.UNHEALTHY.getValue())) {
                    //update table JOB_EXECUTOR insert row with status failed and add history entry to JOB_EXECUTOR_HISTORY.
                    String containerName = data.getName();
                    jobExecutorService.updateJobExecutor(SchedulingConstants.WORKER_FAILED, null, containerName);
                    JobExecutorHistory entry = new JobExecutorHistory(containerName, containerId, SchedulingConstants.WORKER_FAILED, new Timestamp(new Date().getTime()));
                    jobExecutorHistoryService.saveJobExecutorHistoryEntry(entry);
                }
            }
        }
        catch(RancherApiException rae){
            LOGGER.error("RancherApiException: Could not retrieve job Executor instances info from Rancher");
            throw rae;
        }
    }
}

















