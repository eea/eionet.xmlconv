package eionet.gdem.rabbitMQ.listeners;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Properties;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.rabbitMQ.model.WorkerStateRabbitMQResponseMessage;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestrator;
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
    JobExecutorService jobExecutorService;

    @Autowired
    JobExecutorHistoryService jobExecutorHistoryService;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkersStatusMessageReceiver.class);

    @Autowired
    private ContainersRancherApiOrchestrator containersOrchestrator;

    @Autowired
    private WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;

    @Override
    public void onMessage(Message message) {
        String messageBody = new String(message.getBody());
        try {
            ObjectMapper mapper =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            WorkerStateRabbitMQResponseMessage response = mapper.readValue(messageBody, WorkerStateRabbitMQResponseMessage.class);

            String containerId="";
            if (Properties.enableJobExecRancherScheduledTask) {
                try {
                    containerId = containersOrchestrator.getContainerId(response.getJobExecutorName());
                } catch (RancherApiException e) {
                    //rancher occasionally might get unresponsive
                    LOGGER.error("Error during retrieval of jobExecutor " + response.getJobExecutorName() + " containerId");
                }
            }

            JobExecutor jobExecutor = new JobExecutor(response.getJobExecutorName(), containerId, response.getJobExecutorStatus(), response.getHeartBeatQueue());
            jobExecutor.setJobExecutorType(response.getJobExecutorType());
            JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(response.getJobExecutorName(), containerId, response.getJobExecutorStatus(), new Timestamp(new Date().getTime()), response.getHeartBeatQueue());
            jobExecutorHistory.setJobExecutorType(response.getJobExecutorType());
            workerAndJobStatusHandlerService.saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
        } catch (Exception e) {
            LOGGER.info("Error during jobExecutor message processing: ", e);
        }
    }

}













