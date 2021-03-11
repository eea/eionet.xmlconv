package eionet.gdem.rabbitMQ;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Properties;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.rabbitMQ.model.WorkerStateRabbitMQResponse;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestrator;
import eionet.gdem.services.JobHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class WorkersStatusMessageReceiver implements MessageListener {

    @Autowired
    JobService jobService;

    @Autowired
    JobExecutorService jobExecutorService;

    @Autowired
    JobExecutorHistoryService jobExecutorHistoryService;

    @Autowired
    JobHistoryService jobHistoryService;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkersStatusMessageReceiver.class);

    @Autowired
    private ContainersRancherApiOrchestrator containersOrchestrator;

    @Override
    public void onMessage(Message message) {
        String messageBody = new String(message.getBody());
        try {
            ObjectMapper mapper =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
            WorkerStateRabbitMQResponse response = mapper.readValue(messageBody, WorkerStateRabbitMQResponse.class);

            String containerId="";
            if (Properties.enableJobExecRancherScheduledTask) {
                containerId = containersOrchestrator.getContainerId(response.getJobExecutorName());
            }

            JobExecutor jobExecutor = new JobExecutor(response.getJobExecutorName(), containerId, response.getJobExecutorStatus());
            jobExecutorService.saveJobExecutor(jobExecutor);
            JobExecutorHistory entry = new JobExecutorHistory(response.getJobExecutorName(), containerId, response.getJobExecutorStatus(), new Timestamp(new Date().getTime()));
            jobExecutorHistoryService.saveJobExecutorHistoryEntry(entry);
        } catch (Exception e) {
            LOGGER.info("Error during jobExecutor message processing: ", e.getMessage());
            return;
        }
    }

}













