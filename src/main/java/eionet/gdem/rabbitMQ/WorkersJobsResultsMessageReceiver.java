package eionet.gdem.rabbitMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Constants;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.service.JobExecutorService;
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

    @Autowired
    JobExecutorService jobExecutorService;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkersJobsResultsMessageReceiver.class);

    @Override
    public void onMessage(Message message) {
        String messageBody = new String(message.getBody());
        XQScript script = null;
        try {
            ObjectMapper mapper =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
            WorkersRabbitMQResponse response = mapper.readValue(messageBody, WorkersRabbitMQResponse.class);

            script = response.getScript();
            if (script == null) {
                JobExecutor jobExecutor = new JobExecutor(response.getContainerName(), response.getJobExecutorStatus());
                jobExecutorService.saveJobExecutor(jobExecutor);
            } else if (response.isErrorExists()) {
                jobService.changeNStatus(script, Constants.XQ_FATAL_ERR);
                LOGGER.info("Error: " + response.getErrorMessage());
                jobExecutorService.updateJobExecutor(SchedulingConstants.WORKER_READY, Integer.parseInt(script.getJobId()), response.getContainerName());
            } else if (response.getJobStatus() == SchedulingConstants.WORKER_RECEIVED) {
                LOGGER.info("Job with id=" + script.getJobId() + " received by worker with container name " + response.getContainerName());
                jobService.changeNStatus(script, Constants.XQ_WORKER_RECEIVED);
                InternalSchedulingStatus intStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_PROCESSING);
                jobService.changeInternalStatus(intStatus, Integer.parseInt(script.getJobId()));
                jobExecutorService.updateJobExecutor(SchedulingConstants.WORKER_RECEIVED, Integer.parseInt(script.getJobId()), response.getContainerName());
            } else if (response.getJobStatus() == SchedulingConstants.WORKER_READY) {
                jobService.changeNStatus(script, Constants.XQ_READY);
                LOGGER.info("### Job with id=" + script.getJobId() + " status is READY. Executing time in nanoseconds = " + response.getExecutionTime() + ".");
                jobExecutorService.updateJobExecutor(SchedulingConstants.WORKER_READY, Integer.parseInt(script.getJobId()), response.getContainerName());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e1) {
            LOGGER.info(e1.getMessage());
            throw new RuntimeException(e1);
        }
    }
}













