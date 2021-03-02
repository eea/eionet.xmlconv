package eionet.gdem.rabbitMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkersRabbitMQResponse;
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
public class WorkersJobsResultsMessageReceiver implements MessageListener {

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

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkersJobsResultsMessageReceiver.class);

    @Override
    public void onMessage(Message message) {
        String messageBody = new String(message.getBody());
        XQScript script = null;
        try {
            ObjectMapper mapper =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
            WorkersRabbitMQResponse response = mapper.readValue(messageBody, WorkersRabbitMQResponse.class);
            String containerId="";
            if (Properties.enableJobExecRancherScheduledTask) {
                containerId = containersOrchestrator.getContainerId(response.getContainerName());
            }

            script = response.getScript();
            if (script == null) {
                JobExecutor jobExecutor = new JobExecutor(response.getContainerName(), containerId, response.getJobExecutorStatus());
                jobExecutorService.saveJobExecutor(jobExecutor);
                JobExecutorHistory entry = new JobExecutorHistory(response.getContainerName(), containerId, response.getJobExecutorStatus(), new Timestamp(new Date().getTime()));
                jobExecutorHistoryService.saveJobExecutorHistoryEntry(entry);
            } else if (response.isErrorExists()) {
                jobService.changeNStatus(script, Constants.XQ_FATAL_ERR);
                jobHistoryService.updateStatusesAndJobExecutorName(script, Constants.XQ_FATAL_ERR, response.getContainerName());
                LOGGER.info("Job with id " + response.getScript().getJobId() + " failed with error: " + response.getErrorMessage());
                jobExecutorService.updateJobExecutor(SchedulingConstants.WORKER_READY, Integer.parseInt(script.getJobId()), response.getContainerName(), containerId);
                JobExecutorHistory entry = new JobExecutorHistory(response.getContainerName(), containerId, SchedulingConstants.WORKER_READY, new Timestamp(new Date().getTime()));
                jobExecutorHistoryService.saveJobExecutorHistoryEntry(entry);
            } else if (response.getJobStatus() == SchedulingConstants.WORKER_RECEIVED) {
                LOGGER.info("Job with id=" + script.getJobId() + " received by worker with container name " + response.getContainerName());
                jobService.changeNStatus(script, Constants.XQ_WORKER_RECEIVED);
                InternalSchedulingStatus intStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_PROCESSING);
                jobService.changeIntStatusAndJobExecutorName(intStatus, response.getContainerName(), new Timestamp(new Date().getTime()), Integer.parseInt(script.getJobId()));
                jobHistoryService.updateStatusesAndJobExecutorName(script, Constants.XQ_WORKER_RECEIVED, response.getContainerName());
                jobExecutorService.updateJobExecutor(SchedulingConstants.WORKER_RECEIVED, Integer.parseInt(script.getJobId()), response.getContainerName(), containerId);
                JobExecutorHistory entry = new JobExecutorHistory(response.getContainerName(), containerId, SchedulingConstants.WORKER_RECEIVED, new Timestamp(new Date().getTime()));
                jobExecutorHistoryService.saveJobExecutorHistoryEntry(entry);
            } else if (response.getJobStatus() == SchedulingConstants.WORKER_READY) {
                jobService.changeNStatus(script, Constants.XQ_READY);
                jobHistoryService.updateStatusesAndJobExecutorName(script, Constants.XQ_READY, response.getContainerName());
                LOGGER.info("### Job with id=" + script.getJobId() + " status is READY. Executing time in nanoseconds = " + response.getExecutionTime() + ".");
                jobExecutorService.updateJobExecutor(SchedulingConstants.WORKER_READY, Integer.parseInt(script.getJobId()), response.getContainerName(), containerId);
                JobExecutorHistory entry = new JobExecutorHistory(response.getContainerName(), containerId, SchedulingConstants.WORKER_READY, new Timestamp(new Date().getTime()));
                jobExecutorHistoryService.saveJobExecutorHistoryEntry(entry);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e1) {
            LOGGER.info(e1.getMessage());
            throw new RuntimeException(e1);
        }
    }
}













