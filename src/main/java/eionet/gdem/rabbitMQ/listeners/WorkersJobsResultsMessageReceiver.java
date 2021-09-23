package eionet.gdem.rabbitMQ.listeners;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkerJobInfoRabbitMQResponse;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestrator;
import eionet.gdem.services.QueryMetadataService;
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

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkersJobsResultsMessageReceiver.class);

    @Autowired
    private ContainersRancherApiOrchestrator containersOrchestrator;

    @Autowired
    private WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;

    @Autowired
    private QueryMetadataService queryMetadataService;

    @Override
    public void onMessage(Message message) {
        String messageBody = new String(message.getBody());
        XQScript script = null;
        try {
            ObjectMapper mapper =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
            WorkerJobInfoRabbitMQResponse response = mapper.readValue(messageBody, WorkerJobInfoRabbitMQResponse.class);

            String containerId="";
            if (Properties.enableJobExecRancherScheduledTask) {
                containerId = containersOrchestrator.getContainerId(response.getJobExecutorName());
            }

            script = response.getScript();
            JobEntry jobEntry = jobService.findById(Integer.parseInt(script.getJobId()));

            JobExecutor jobExecutor = new JobExecutor(response.getJobExecutorName(), response.getJobExecutorStatus(), Integer.parseInt(script.getJobId()), containerId, response.getHeartBeatQueue());
            JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(response.getJobExecutorName(), containerId, response.getJobExecutorStatus(), Integer.parseInt(script.getJobId()), new Timestamp(new Date().getTime()), response.getHeartBeatQueue());
            InternalSchedulingStatus internalStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_PROCESSING);
            jobEntry.setJobExecutorName(response.getJobExecutorName());
            if (response.isErrorExists()) {
                LOGGER.info("Job with id " + script.getJobId() + " failed with error: " + response.getErrorMessage());
                workerAndJobStatusHandlerService.updateJobAndJobExecTables(Constants.XQ_FATAL_ERR, internalStatus, jobEntry, jobExecutor, jobExecutorHistory);
                queryMetadataService.storeScriptInformation(jobEntry.getQueryId(), jobEntry.getFile(), jobEntry.getScriptType(), jobEntry.getDuration().longValue(), Constants.XQ_FATAL_ERR);
            } else if (response.getJobExecutorStatus() == SchedulingConstants.WORKER_RECEIVED) {
                LOGGER.info("Job with id=" + script.getJobId() + " received by worker with container name " + response.getJobExecutorName());
                workerAndJobStatusHandlerService.updateJobAndJobExecTables(Constants.XQ_PROCESSING, internalStatus, jobEntry, jobExecutor, jobExecutorHistory);
            } else if (response.getJobExecutorStatus() == SchedulingConstants.WORKER_READY) {
                LOGGER.info("### Job with id=" + script.getJobId() + " status is READY. Executing time in nanoseconds = " + response.getExecutionTime() + ".");
                workerAndJobStatusHandlerService.updateJobAndJobExecTables(Constants.XQ_READY, internalStatus, jobEntry, jobExecutor, jobExecutorHistory);
                queryMetadataService.storeScriptInformation(jobEntry.getQueryId(), jobEntry.getFile(), jobEntry.getScriptType(), jobEntry.getDuration().longValue(), Constants.XQ_READY);
            }
        } catch (Exception e) {
            LOGGER.info("Error during jobExecutor message processing: ", e);
        }
    }

}













