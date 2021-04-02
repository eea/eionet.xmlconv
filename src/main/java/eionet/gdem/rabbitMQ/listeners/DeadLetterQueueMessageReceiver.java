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
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkerJobInfoRabbitMQResponse;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageSender;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestrator;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.JobHistoryService;
import eionet.gdem.utils.Utils;
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
                updateJobAndJobExecTables(Constants.XQ_FATAL_ERR, SchedulingConstants.INTERNAL_STATUS_PROCESSING, deadLetterMessage, containerId, jobEntry);
            }
            else if(deadLetterMessage.getErrorStatus() == Constants.DELETED){
                //delete temp files and entry from T_XQJOBS table
                String jobId = script.getJobId();
                // delete also result files from file system tmp folder
                try {
                    Utils.deleteFile(script.getStrResultFile());
                } catch (Exception e) {
                    LOGGER.error("Could not delete job result file: " + script.getStrResultFile() + "." + e.getMessage());
                }
                // delete xquery files, if they are stored in tmp folder
                String xqFile = script.getOrigFileUrl();
                try {
                    // Important!!!: delete only, when the file is stored in tmp folder
                    if (xqFile.startsWith(Properties.tmpFolder)) {
                        Utils.deleteFile(xqFile);
                    }
                } catch (Exception e) {
                    LOGGER.error("Could not delete XQuery script file: " + xqFile + "." + e.getMessage());
                }

                GDEMServices.getDaoService().getXQJobDao().endXQJob(jobId);
                LOGGER.info("Deleted job: " + jobId + " from the database");
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
                    updateJobAndJobExecTables(Constants.XQ_FATAL_ERR, SchedulingConstants.INTERNAL_STATUS_PROCESSING, deadLetterMessage, containerId, jobEntry);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error during dead letter queue message processing: ", e.getMessage());
        }
    }

    void updateJobAndJobExecTables(Integer nStatus, Integer internalStatus, WorkerJobRabbitMQRequest request, String containerId, JobEntry jobEntry) throws DatabaseException {
        XQScript script = request.getScript();
        jobService.changeNStatus(Integer.parseInt(script.getJobId()), nStatus);
        InternalSchedulingStatus intStatus = new InternalSchedulingStatus().setId(internalStatus);
        jobService.changeIntStatusAndJobExecutorName(intStatus, request.getJobExecutorName(), new Timestamp(new Date().getTime()), Integer.parseInt(script.getJobId()));
        jobHistoryService.updateStatusesAndJobExecutorName(script, nStatus, internalStatus, request.getJobExecutorName(), jobEntry.getJobType());
        JobExecutor jobExecutor = new JobExecutor(request.getJobExecutorName(), request.getJobExecutorStatus(), Integer.parseInt(script.getJobId()), containerId, request.getHeartBeatQueue());
        jobExecutorService.saveOrUpdateJobExecutor(jobExecutor);
        JobExecutorHistory entry = new JobExecutorHistory(request.getJobExecutorName(), containerId, request.getJobExecutorStatus(), Integer.parseInt(script.getJobId()), new Timestamp(new Date().getTime()), request.getHeartBeatQueue());
        jobExecutorHistoryService.saveJobExecutorHistoryEntry(entry);
    }
}
