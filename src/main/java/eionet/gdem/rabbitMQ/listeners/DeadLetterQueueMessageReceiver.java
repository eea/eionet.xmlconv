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
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import eionet.gdem.rabbitMQ.service.HandleHeavyJobsService;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageSender;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestrator;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    @Qualifier("heartBeatRabbitMessageSenderImpl")
    RabbitMQMessageSender rabbitMQMessageSender;

    @Autowired
    HandleHeavyJobsService handleHeavyJobsService;

    /**
     * time in milliseconds
     */
    private static final Integer RETRY_DELAY = 20000;

    @Override
    public void onMessage(Message message) {
        String messageBody = new String(message.getBody());
        try {
            ObjectMapper mapper =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
            WorkerJobRabbitMQRequestMessage deadLetterMessage = mapper.readValue(messageBody, WorkerJobRabbitMQRequestMessage.class);

            LOGGER.info("Received error message in DEAD LETTER QUEUE: " + deadLetterMessage.getErrorMessage());
            XQScript script = deadLetterMessage.getScript();

            if (deadLetterMessage.getErrorStatus()==null) {
                //We assume that a message arriving in Dead Letter queue, without error Status, has come from a worker that
                //exploded due to memory exceptions.
                LOGGER.info("Job Message didn't contain ErrorStatus, therefore, " + script.getJobId() + " was detected as heavy");
                handleHeavyJobsService.handle(deadLetterMessage);
                return;
            }

            String containerId="";
            if (Properties.enableJobExecRancherScheduledTask) {
                containerId = containersOrchestrator.getContainerId(deadLetterMessage.getJobExecutorName());
            }

            JobEntry jobEntry = jobService.findById(Integer.parseInt(script.getJobId()));

            if(deadLetterMessage.getErrorStatus()!=null && deadLetterMessage.getErrorStatus() == Constants.CANCELLED_BY_USER){
                LOGGER.info("Job " + script.getJobId() + " was cancelled by user");
            } else if (deadLetterMessage.getErrorStatus()!=null && deadLetterMessage.getErrorStatus() == Constants.XQ_INTERRUPTED) {
                LOGGER.info("Job " + script.getJobId() + " was interrupted by interruptLongRunningJobs task because duration exceed schema's maxExecution time");
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
            } else if(deadLetterMessage.getErrorStatus()!=null && deadLetterMessage.getErrorStatus() == Constants.XQ_READY) {
                LOGGER.info("Job " + script.getJobId() + " has already been executed");
            } else{
                LOGGER.info("Received message in DEAD LETTER QUEUE with unknown status: " + deadLetterMessage.getErrorStatus());

                Integer retriesCnt = deadLetterMessage.getJobExecutionRetries();
                if(retriesCnt < Constants.MAX_SCRIPT_EXECUTION_RETRIES){
                    deadLetterMessage.setJobExecutionRetries(retriesCnt + 1);
                    InternalSchedulingStatus internalStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_QUEUED);
                    jobEntry.setJobExecutorName(null).setWorkerRetries(retriesCnt+1);
                    JobExecutor jobExecutor = new JobExecutor(deadLetterMessage.getJobExecutorName(), SchedulingConstants.WORKER_READY, Integer.parseInt(script.getJobId()), containerId, deadLetterMessage.getHeartBeatQueue());
                    JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(deadLetterMessage.getJobExecutorName(), containerId, SchedulingConstants.WORKER_READY, Integer.parseInt(script.getJobId()), new Timestamp(new Date().getTime()), deadLetterMessage.getHeartBeatQueue());
                    Thread.sleep(RETRY_DELAY);
                    workerAndJobStatusHandlerService.resendMessageToWorker(retriesCnt+1, Constants.XQ_RECEIVED, internalStatus, jobEntry, deadLetterMessage, jobExecutor, jobExecutorHistory);
                }
                else{
                    //message will be discarded
                    LOGGER.info("Reached maximum retries of job execution for job: " + script.getJobId());
                    InternalSchedulingStatus internalStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
                    jobEntry.setJobExecutorName(deadLetterMessage.getJobExecutorName()).setWorkerRetries(Constants.MAX_SCRIPT_EXECUTION_RETRIES);
                    JobExecutor jobExecutor = new JobExecutor(deadLetterMessage.getJobExecutorName(), SchedulingConstants.WORKER_READY, Integer.parseInt(script.getJobId()), containerId, deadLetterMessage.getHeartBeatQueue());
                    JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(deadLetterMessage.getJobExecutorName(), containerId, SchedulingConstants.WORKER_READY, Integer.parseInt(script.getJobId()), new Timestamp(new Date().getTime()), deadLetterMessage.getHeartBeatQueue());
                    Thread.sleep(RETRY_DELAY);
                    workerAndJobStatusHandlerService.updateJobAndJobExecTables(Constants.XQ_FATAL_ERR, internalStatus, jobEntry, jobExecutor, jobExecutorHistory);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error during dead letter queue message processing: ", e);
        }
    }
}

















