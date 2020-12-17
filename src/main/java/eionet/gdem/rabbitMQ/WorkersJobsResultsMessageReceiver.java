package eionet.gdem.rabbitMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Constants;
import eionet.gdem.WorkerConstants;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkersRabbitMQResponse;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class WorkersJobsResultsMessageReceiver implements MessageListener {

    @Autowired
    private IXQJobDao xqJobDao;

    @Qualifier("jobHistoryRepository")
    @Autowired
    private JobHistoryRepository jobHistoryRepository;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkersJobsResultsMessageReceiver.class);

    @Override
    public void onMessage(Message message) {
        String messageBody = new String(message.getBody());
        XQScript xqScript = null;
        try {
            ObjectMapper mapper =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
            WorkersRabbitMQResponse response = mapper.readValue(messageBody, WorkersRabbitMQResponse.class);

            xqScript = response.getXqScript();
            if (response.hasError()) {
                changeStatus(xqScript, Constants.XQ_FATAL_ERR);
                LOGGER.info("Error: " + response.getErrorMessage());
            }

            if (response.getJobStatus() == WorkerConstants.XQ_WORKER_RECEIVED) {
                LOGGER.info("Job with id=" + xqScript.getJobId() + " received by worker with container name " + response.getContainerName());
                changeStatus(xqScript, Constants.XQ_WORKER_RECEIVED);
            } else if (response.getJobStatus() == WorkerConstants.XQ_WORKER_SUCCESS) {
                changeStatus(xqScript, Constants.XQ_READY);
                LOGGER.info("### Job with id=" + xqScript.getJobId() + " status is READY. Executing time in nanoseconds = " + response.getExecutionTime() + ".");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (SQLException e1) {
            LOGGER.error("Database exception when changing status of job with id " + xqScript.getJobId() + e1.toString());
            throw new RuntimeException(e1);
        }
    }

    void changeStatus(XQScript xqScript, Integer status) throws SQLException {
        xqJobDao.changeJobStatus(xqScript.getJobId(), status);
        if (status == 3)
            LOGGER.info("### Job with id=" + xqScript.getJobId() + " has changed status to " + Constants.JOB_READY + ".");
        else if (status == 7)
            LOGGER.info("### Job with id=" + xqScript.getJobId() + " has changed status to " + Constants.XQ_INTERRUPTED + ".");
        else if (status == 9)
            LOGGER.info("### Job with id=" + xqScript.getJobId() + " has changed status to " + Constants.XQ_WORKER_RECEIVED + ".");
        else
            LOGGER.info("### Job with id=" + xqScript.getJobId() + " has changed status to " + Constants.XQ_FATAL_ERR + ".");
        jobHistoryRepository.save(new JobHistoryEntry(xqScript.getJobId(), status, new Timestamp(new Date().getTime()), xqScript.getSrcFileUrl(), xqScript.getScriptFileName(), xqScript.getStrResultFile(), xqScript.getScriptType()));
        LOGGER.info("Job with id=" + xqScript.getJobId() + " has been inserted in table JOB_HISTORY ");
    }
}













