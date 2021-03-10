package eionet.gdem.rabbitMQ;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
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

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkersJobsResultsMessageReceiver.class);

    @Autowired
    private ContainersRancherApiOrchestrator containersOrchestrator;

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

            JobEntry jobEntry = null;
            script = response.getScript();
            if (script != null) {
                jobEntry = jobService.findById(Integer.parseInt(script.getJobId()));
            }

            if (script == null) {
                JobExecutor jobExecutor = new JobExecutor(response.getContainerName(), containerId, response.getJobExecutorStatus());
                jobExecutorService.saveJobExecutor(jobExecutor);
                JobExecutorHistory entry = new JobExecutorHistory(response.getContainerName(), containerId, response.getJobExecutorStatus(), new Timestamp(new Date().getTime()));
                jobExecutorHistoryService.saveJobExecutorHistoryEntry(entry);
            } else if (response.isErrorExists()) {
                LOGGER.info("Job with id " + script.getJobId() + " failed with error: " + response.getErrorMessage());
                updateJobAndJobExecTables(Constants.XQ_FATAL_ERR, SchedulingConstants.INTERNAL_STATUS_PROCESSING, response, containerId, jobEntry);
            } else if (response.getJobExecutorStatus() == SchedulingConstants.WORKER_RECEIVED) {
                LOGGER.info("Job with id=" + script.getJobId() + " received by worker with container name " + response.getContainerName());
                updateJobAndJobExecTables(Constants.XQ_PROCESSING, SchedulingConstants.INTERNAL_STATUS_PROCESSING, response, containerId, jobEntry);
            } else if (response.getJobExecutorStatus() == SchedulingConstants.WORKER_READY) {
                LOGGER.info("### Job with id=" + script.getJobId() + " status is READY. Executing time in nanoseconds = " + response.getExecutionTime() + ".");
                updateJobAndJobExecTables(Constants.XQ_READY, SchedulingConstants.INTERNAL_STATUS_PROCESSING, response, containerId, jobEntry);
            }
        } catch (Exception e) {
            LOGGER.info("Error during jobExecutor message processing: ", e.getMessage());
            return;
        }
    }

    void updateJobAndJobExecTables(Integer nStatus, Integer internalStatus, WorkersRabbitMQResponse response, String containerId, JobEntry jobEntry) {
        XQScript script = response.getScript();
        jobService.changeNStatus(Integer.parseInt(script.getJobId()), nStatus);
        InternalSchedulingStatus intStatus = new InternalSchedulingStatus().setId(internalStatus);
        jobService.changeIntStatusAndJobExecutorName(intStatus, response.getContainerName(), new Timestamp(new Date().getTime()), Integer.parseInt(script.getJobId()));
        jobHistoryService.updateStatusesAndJobExecutorName(script, nStatus, internalStatus, response.getContainerName(), jobEntry.getJobType());
        jobExecutorService.updateJobExecutor(response.getJobExecutorStatus(), Integer.parseInt(script.getJobId()), response.getContainerName(), containerId);
        JobExecutorHistory entry = new JobExecutorHistory(response.getContainerName(), containerId, response.getJobExecutorStatus(), Integer.parseInt(script.getJobId()), new Timestamp(new Date().getTime()));
        jobExecutorHistoryService.saveJobExecutorHistoryEntry(entry);
    }
}













