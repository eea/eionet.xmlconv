package eionet.gdem.infrastructure.scheduling;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.repositories.JobExecutorRepository;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.jpa.repositories.JobRepository;
import eionet.gdem.notifications.UNSEventSender;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ServiceApiRequestBody;
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
        while (ContainersRancherApiOrchestratorImpl.lock) {
            LOGGER.info("Waiting for rancher to complete other tasks");
        }
        String serviceId = Properties.rancherJobExecServiceId;
        InternalSchedulingStatus internalStatus = new InternalSchedulingStatus().setId(2);
        List<JobEntry> jobs = jobRepository.findByIntSchedulingStatus(internalStatus);
        List<JobExecutor> readyWorkers = jobExecutorRepository.findByStatus(SchedulingConstants.WORKER_READY);
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
                }
                containersOrchestrator.deleteContainer(worker.getName());
                jobExecutorRepository.deleteByName(worker.getName());
                count++;
            }
        }
    }

    void createWorkers(String serviceId, Integer newWorkers) throws RancherApiException {
        List<String> instances = servicesOrchestrator.getContainerInstances(serviceId);
        ServiceApiRequestBody serviceApiRequestBody = new ServiceApiRequestBody().setScale(instances.size()+newWorkers);
        servicesOrchestrator.scaleUpOrDownContainerInstances(serviceId, serviceApiRequestBody);
        LOGGER.info("Created " + newWorkers + " new worker(s)");
    }
}

















