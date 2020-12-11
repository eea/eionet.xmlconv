package eionet.gdem.rabbitMQ;

import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class WorkersJobsResultsMessageReceiver {

    @Autowired
    private IXQJobDao xqJobDao;

    @Qualifier("jobHistoryRepository")
    @Autowired
    private JobHistoryRepository jobHistoryRepository;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkersJobsResultsMessageReceiver.class);

    public void handleMessage(String message) {
        LOGGER.info(message);
     //   xqJobDao.changeJobStatus(jobId, status);

//        jobHistoryRepository.save(new JobHistoryEntry(jobId, status, new Timestamp(new Date().getTime()), url, scriptFile, resultFile, scriptType));
//        LOGGER.info("Job with id=" + jobId + " has been inserted in table JOB_HISTORY ");

     //   LOGGER.info("### Job with id=" + jobId + " status is READY. Executing time in nanoseconds = " + (stopTimeEnd - startTimeSta)+ ".");
    }
}
