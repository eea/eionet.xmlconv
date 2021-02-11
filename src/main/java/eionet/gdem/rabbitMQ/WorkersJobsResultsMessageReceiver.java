package eionet.gdem.rabbitMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Constants;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkersRabbitMQResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkersJobsResultsMessageReceiver implements MessageListener {

    @Autowired
    JobService jobService;

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
            if (response.isErrorExists()) {
                jobService.changeNStatus(xqScript, Constants.XQ_FATAL_ERR);
                LOGGER.info("Error: " + response.getErrorMessage());
                return;
            }

            if (response.getJobStatus() == SchedulingConstants.WORKER_RECEIVED) {
                LOGGER.info("Job with id=" + xqScript.getJobId() + " received by worker with container name " + response.getContainerName());
                jobService.changeNStatus(xqScript, Constants.XQ_WORKER_RECEIVED);
                InternalSchedulingStatus intStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_PROCESSING);
                jobService.changeInternalStatus(intStatus, Integer.parseInt(xqScript.getJobId()));
            } else if (response.getJobStatus() == SchedulingConstants.WORKER_SUCCESS) {
                jobService.changeNStatus(xqScript, Constants.XQ_READY);
                LOGGER.info("### Job with id=" + xqScript.getJobId() + " status is READY. Executing time in nanoseconds = " + response.getExecutionTime() + ".");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e1) {
            LOGGER.info(e1.getMessage());
            throw new RuntimeException(e1);
        }
    }
}













