package eionet.gdem.rabbitMQ.listeners;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestrator;
import eionet.gdem.services.GDEMServices;
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
    JobService jobService;

    @Autowired
    WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;

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

            if(deadLetterMessage.getErrorStatus()!=null && deadLetterMessage.getErrorStatus() == Constants.CANCELLED_BY_USER){
                LOGGER.info("Job was cancelled by user");
            } else if (deadLetterMessage.getErrorStatus()!=null && deadLetterMessage.getErrorStatus() == Constants.XQ_INTERRUPTED) {
                LOGGER.info("Job was interrupted by interruptLongRunningJobs task because duration exceed schema's maxExecution time");
            } else if(deadLetterMessage.getErrorStatus()!=null && deadLetterMessage.getErrorStatus() == Constants.DELETED){
                //delete temp files and entry from T_XQJOBS table
                String jobId = script.getJobId();
                // delete also result files from file system tmp folder
                try {
                    Utils.deleteFile(script.getStrResultFile());
                } catch (Exception e) {
                    LOGGER.error("Could not delete job result file: " + script.getStrResultFile() + "." + e.getMessage());
                }
                // delete xquery files, if they are stored in tmp folder
                String xqFile = script.getScriptFileName();
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
            } else{
                LOGGER.info("Reached maximum retries of job execution for job: " + jobEntry.getId());
                JobHistoryEntry jobHistoryEntry = new JobHistoryEntry(jobEntry.getId().toString(), jobEntry.getnStatus(), new Timestamp(new Date().getTime()), jobEntry.getUrl(), jobEntry.getFile(), jobEntry.getResultFile(), jobEntry.getScriptType())
                    .setIntSchedulingStatus(jobEntry.getIntSchedulingStatus().getId()).setJobExecutorName(jobEntry.getJobExecutorName()).setJobType(jobEntry.getJobType()).setWorkerRetries(Constants.MAX_SCRIPT_EXECUTION_RETRIES);
                JobExecutor jobExecutor = new JobExecutor(jobEntry.getJobExecutorName(), SchedulingConstants.WORKER_READY, jobEntry.getId());
                JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(jobEntry.getJobExecutorName(), containerId, SchedulingConstants.WORKER_READY, jobEntry.getId(), new Timestamp(new Date().getTime()), jobEntry.getJobExecutorName()+"-queue");
                workerAndJobStatusHandlerService.updateWorkerRetriesAndWorkerStatus(Constants.MAX_SCRIPT_EXECUTION_RETRIES, jobHistoryEntry, jobExecutor, jobExecutorHistory);
            }
        } catch (Exception e) {
            LOGGER.error("Error during dead letter queue message processing: ", e.getMessage());
        }
    }
}

















