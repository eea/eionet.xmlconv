package eionet.gdem.rabbitMQ.listeners;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkerJobInfoRabbitMQResponse;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageSender;
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
public class DeadLetterQueueMessageReceiver implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeadLetterQueueMessageReceiver.class);

    @Autowired
    private RabbitMQMessageSender jobMessageSender;

    @Autowired
    JobService jobService;

    @Autowired
    JobExecutorService jobExecutorService;

    @Autowired
    JobExecutorHistoryService jobExecutorHistoryService;

    @Autowired
    JobHistoryService jobHistoryService;

    @Autowired
    private ContainersRancherApiOrchestrator containersOrchestrator;


    @Override
    public void onMessage(Message message) {
        String messageBody = new String(message.getBody());
        try {
            ObjectMapper mapper =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
            WorkerJobRabbitMQRequest deadLetterMessage = mapper.readValue(messageBody, WorkerJobRabbitMQRequest.class);

            LOGGER.info("Received error message in DEAD LETTER QUEUE: " + deadLetterMessage.getErrorMessage());
            XQScript script = deadLetterMessage.getScript();

            String containerId="";
            if (Properties.enableJobExecRancherScheduledTask) {
                containerId = containersOrchestrator.getContainerId(deadLetterMessage.getJobExecutorName());
            }

            JobEntry jobEntry = jobService.findById(Integer.parseInt(script.getJobId()));

            if(deadLetterMessage.getErrorStatus() == Constants.CANCELLED_BY_USER){
                LOGGER.info("Job was cancelled by user");
            }
            else if(deadLetterMessage.getErrorStatus() == Constants.JOB_EXCEPTION_ERROR){
                //TODO maybe other status
                updateJobAndJobExecTables(Constants.XQ_FATAL_ERR, SchedulingConstants.INTERNAL_STATUS_PROCESSING, deadLetterMessage, containerId, jobEntry);
            }
            else if(deadLetterMessage.getErrorStatus() == Constants.XQ_CANCELLED){
                //TODO update db
            }
            else{
                LOGGER.info("Received message in DEAD LETTER QUEUE with unknown status: " + deadLetterMessage.getErrorStatus());

                Integer retriesCnt = deadLetterMessage.getJobExecutionRetries();
                if(retriesCnt < Constants.MAX_SCRIPT_EXECUTION_RETRIES){
                    deadLetterMessage.setJobExecutionRetries(retriesCnt + 1);
                    jobMessageSender.sendJobInfoToRabbitMQ(deadLetterMessage);
                }
                else{
                    //message will be discarded
                    LOGGER.info("Reached maximum retries of job execution for job: " + script.getJobId());
                    //TODO maybe other status
                    updateJobAndJobExecTables(Constants.XQ_FATAL_ERR, SchedulingConstants.INTERNAL_STATUS_PROCESSING, deadLetterMessage, containerId, jobEntry);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error during dead letter queue message processing: ", e.getMessage());
        }
    }

    void updateJobAndJobExecTables(Integer nStatus, Integer internalStatus, WorkerJobRabbitMQRequest request, String containerId, JobEntry jobEntry) {
        XQScript script = request.getScript();
        jobService.changeNStatus(Integer.parseInt(script.getJobId()), nStatus);
        InternalSchedulingStatus intStatus = new InternalSchedulingStatus().setId(internalStatus);
        jobService.changeIntStatusAndJobExecutorName(intStatus, request.getJobExecutorName(), new Timestamp(new Date().getTime()), Integer.parseInt(script.getJobId()));
        jobHistoryService.updateStatusesAndJobExecutorName(script, nStatus, internalStatus, request.getJobExecutorName(), jobEntry.getJobType());
        jobExecutorService.updateJobExecutor(request.getJobExecutorStatus(), Integer.parseInt(script.getJobId()), request.getJobExecutorName(), containerId, request.getHeartBeatQueue());
        JobExecutorHistory entry = new JobExecutorHistory(request.getJobExecutorName(), containerId, request.getJobExecutorStatus(), Integer.parseInt(script.getJobId()), new Timestamp(new Date().getTime()), request.getHeartBeatQueue());
        jobExecutorHistoryService.saveJobExecutorHistoryEntry(entry);
    }
}
