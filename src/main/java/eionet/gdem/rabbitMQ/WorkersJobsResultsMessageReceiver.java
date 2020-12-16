package eionet.gdem.rabbitMQ;

import eionet.gdem.Constants;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkersRabbitMQResponse;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class WorkersJobsResultsMessageReceiver {

    @Autowired
    private IXQJobDao xqJobDao;

    @Qualifier("jobHistoryRepository")
    @Autowired
    private JobHistoryRepository jobHistoryRepository;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkersJobsResultsMessageReceiver.class);

    public void handleMessage(WorkersRabbitMQResponse response) throws SQLException {
        XQScript xqScript = response.getXqScript();
        LOGGER.info("Job with id " + xqScript.getJobId() + " executed by worker with container name " + response.getContainerName());
        if (response.isHasError()) {
            xqJobDao.changeJobStatus(xqScript.getJobId(), response.getJobStatus());
            LOGGER.info("### Job with id=" + xqScript.getJobId() + " has changed status to " + Constants.XQ_FATAL_ERR + ".");
            LOGGER.info("Error: " + response.getErrorMessage());
        }
        try {
            xqJobDao.changeJobStatus(xqScript.getJobId(), response.getJobStatus());
            LOGGER.info("### Job with id=" + xqScript.getJobId() + " has changed status to " + Constants.JOB_READY + ".");
        } catch (SQLException e) {
            LOGGER.error("Database exception when changing status of job with id " + xqScript.getJobId() + e.toString());
            throw e;
        }
        jobHistoryRepository.save(new JobHistoryEntry(xqScript.getJobId(), response.getJobStatus(), new Timestamp(new Date().getTime()), xqScript.getSrcFileUrl(), xqScript.getScriptFileName(), xqScript.getStrResultFile(), xqScript.getScriptType()));
        LOGGER.info("Job with id=" + xqScript.getJobId() + " has been inserted in table JOB_HISTORY ");
        LOGGER.info("### Job with id=" + xqScript.getJobId() + " status is READY. Executing time in nanoseconds = " + response.getExecutionTime()+ ".");
    }

}
