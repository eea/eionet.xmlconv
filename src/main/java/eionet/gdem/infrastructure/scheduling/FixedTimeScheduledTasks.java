package eionet.gdem.infrastructure.scheduling;

import eionet.gdem.Constants;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Service
public class FixedTimeScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedTimeScheduledTasks.class);

    @Autowired
    private IXQJobDao xqJobDao;

    @Qualifier("jobHistoryRepository")
    @Autowired
    JobHistoryRepository repository;

    @Autowired
    public FixedTimeScheduledTasks() {
    }

    @Transactional
    @Scheduled(cron = "0 */5 * * * *") //Every 5 minutes
    public void schedulePeriodicUpdateOfDurationOfJobsInProcessingStatus() throws SQLException {
        //Retrieve jobs from T_XQJOBS with status PROCESSING (XQ_PROCESSING = 2)
        Map<String, Timestamp> jobsInfo = xqJobDao.getJobsWithTimestamps(Constants.XQ_PROCESSING);
        //Create new map with the duration for each job
        Map<String, Long> jobDurations = new HashMap<>();
        for (Map.Entry<String,Timestamp> entry : jobsInfo.entrySet()) {
            long diffInMs = Math.abs(new java.util.Date().getTime() - entry.getValue().getTime());
            jobDurations.put(entry.getKey(), diffInMs);

            //Update time spent in status in table JOB_HISTORY
            repository.setDurationForJobHistory(diffInMs, entry.getKey(), Constants.XQ_PROCESSING);
        }
        //Update time spent in status in table T_XQJOBS
        xqJobDao.updateXQJobsDuration(jobDurations);
        LOGGER.info("Updated duration of jobs in PROCESSING status.");
    }
}
