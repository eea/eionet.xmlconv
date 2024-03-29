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
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.QueryMetadataService;
import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkerJobInfoRabbitMQResponseMessage;
import eionet.gdem.rabbitMQ.service.CdrResponseMessageFactoryService;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestrator;
import eionet.gdem.utils.StatusUtils;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkersJobsResultsMessageReceiver implements MessageListener {

    @Autowired
    private JobService jobService;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkersJobsResultsMessageReceiver.class);

    @Autowired
    private ContainersRancherApiOrchestrator containersOrchestrator;

    @Autowired
    private WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;

    @Autowired
    private JobExecutorService jobExecutorService;

    @Autowired
    private QueryMetadataService queryMetadataService;

    @Autowired
    private CdrResponseMessageFactoryService cdrResponseMessageFactoryService;

    @Override
    public void onMessage(Message message) {
        String messageBody = new String(message.getBody());
        XQScript script = null;
        try {
            Long fmeJobId = null;
            ObjectMapper mapper =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            WorkerJobInfoRabbitMQResponseMessage response = mapper.readValue(messageBody, WorkerJobInfoRabbitMQResponseMessage.class);

            String containerId="";
            if (Properties.enableJobExecRancherScheduledTask) {
                try {
                    containerId = containersOrchestrator.getContainerId(response.getJobExecutorName());
                } catch (RancherApiException e) {
                    //rancher occasionally might get unresponsive
                    LOGGER.error("Error during retrieval of jobExecutor " + response.getJobExecutorName() + " containerId");
                }
            }

            script = response.getScript();
            JobEntry jobEntry = jobService.findById(Integer.parseInt(script.getJobId()));

            if(jobEntry.getnStatus() == Constants.XQ_READY || jobEntry.getnStatus() == Constants.XQ_FATAL_ERR){
                LOGGER.error("Job with id " + jobEntry.getId() + " is already finished with status " + StatusUtils.getStatusNameByNumber(jobEntry.getnStatus()));
                return;
            }

            JobExecutor jobExecutor = new JobExecutor(response.getJobExecutorName(), response.getJobExecutorStatus(), Integer.parseInt(script.getJobId()), containerId, response.getHeartBeatQueue()).setJobExecutorType(response.getJobExecutorType());
            JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(response.getJobExecutorName(), containerId, response.getJobExecutorStatus(), Integer.parseInt(script.getJobId()), new Timestamp(new Date().getTime()), response.getHeartBeatQueue());
            jobExecutorHistory.setJobExecutorType(response.getJobExecutorType());
            InternalSchedulingStatus internalStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_PROCESSING);
            jobEntry.setJobExecutorName(response.getJobExecutorName());
            if(!Utils.isNullStr(script.getFmeJobId())){
                fmeJobId = Long.parseLong(script.getFmeJobId());
                jobEntry.setFmeJobId(fmeJobId);
                jobExecutor.setFmeJobId(fmeJobId);
                jobExecutorHistory.setFmeJobId(fmeJobId);
            }

            Long durationOfJob = Utils.getDifferenceBetweenTwoTimestampsInMs(new Timestamp(new Date().getTime()), jobEntry.getTimestamp());
            if (response.isErrorExists()) {
                LOGGER.info("Job with id " + script.getJobId() + " failed with error: " + response.getErrorMessage());
                jobEntry.setnStatus(Constants.XQ_FATAL_ERR).setIntSchedulingStatus(internalStatus).setTimestamp(new Timestamp(new Date().getTime()));
                workerAndJobStatusHandlerService.updateJobAndJobHistoryEntries(jobEntry);
                saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
                queryMetadataService.storeScriptInformation(jobEntry.getQueryId(), jobEntry.getFile(), jobEntry.getScriptType(), durationOfJob, Constants.XQ_FATAL_ERR, jobEntry.getId(), fmeJobId, script.getOrigFileUrl(), jobEntry.getXmlSize());
            } else if (response.getJobExecutorStatus() == SchedulingConstants.WORKER_RECEIVED) {
                LOGGER.info("Job with id=" + script.getJobId() + " received by worker with container name " + response.getJobExecutorName());
                findIfJobIsHeavyBasedOnWorkerType(response, jobEntry, jobExecutor, jobExecutorHistory);
                jobEntry.setnStatus(Constants.XQ_PROCESSING).setIntSchedulingStatus(internalStatus).setTimestamp(new Timestamp(new Date().getTime()));
                workerAndJobStatusHandlerService.updateJobAndJobHistoryEntries(jobEntry);
                saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
            } else if (response.getJobExecutorStatus() == SchedulingConstants.WORKER_READY) {
                LOGGER.info("### Job with id=" + script.getJobId() + " status is READY. Executing time in nanoseconds = " + response.getExecutionTime() + ".");
                jobEntry.setnStatus(Constants.XQ_READY).setIntSchedulingStatus(internalStatus).setTimestamp(new Timestamp(new Date().getTime()));
                workerAndJobStatusHandlerService.updateJobAndJobHistoryEntries(jobEntry);
                saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
                queryMetadataService.storeScriptInformation(jobEntry.getQueryId(), jobEntry.getFile(), jobEntry.getScriptType(), durationOfJob, Constants.XQ_READY, jobEntry.getId(), fmeJobId, script.getOrigFileUrl(), jobEntry.getXmlSize());
            }
            else if (response.getJobExecutorStatus() == SchedulingConstants.WORKER_RECEIVED_FME_JOB_ID) {
                LOGGER.info("Job with id=" + script.getJobId() + " received FME job id " + fmeJobId + " by worker with container name " + response.getJobExecutorName());
                findIfJobIsHeavyBasedOnWorkerType(response, jobEntry, jobExecutor, jobExecutorHistory);
                jobEntry.setnStatus(Constants.XQ_PROCESSING).setIntSchedulingStatus(internalStatus).setTimestamp(new Timestamp(new Date().getTime()));
                workerAndJobStatusHandlerService.updateJobAndJobHistoryEntries(jobEntry);
                saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
            }
            if(jobEntry.getAddedFromQueue() != null && jobEntry.getAddedFromQueue()) {
                cdrResponseMessageFactoryService.createCdrResponseMessageAndSendToQueueOrPendingJobsTable(jobEntry);
            }
        } catch (Exception e) {
            LOGGER.info("Error during jobExecutor message processing: ", e);
        }
    }

    private void saveOrUpdateJobExecutor(JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException {
        try {
            workerAndJobStatusHandlerService.saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
        } catch (DatabaseException e) {
            LOGGER.error("Database exception when updating jobExecutor or jobExecutorHistory with name " + jobExecutor.getName() + " and jobId " + jobExecutor.getJobId() + ", " + e.toString());
        }
    }

    /**
     * find if a job is light or heavy based on the type of worker (light or heavy) that handles the job
     * @param response
     * @param jobEntry
     * @param jobExecutor
     * @param jobExecutorHistory
     * @throws DatabaseException
     */
    private void findIfJobIsHeavyBasedOnWorkerType(WorkerJobInfoRabbitMQResponseMessage response, JobEntry jobEntry, JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException {
        if (response.getJobExecutorType()!=null && response.getJobExecutorType().equals(JobExecutorType.Heavy)) {  //the response came from a heavy worker, so the job is heavy
            jobEntry.setHeavy(true);
            List<JobExecutor> jobExecutors = jobExecutorService.findExecutorsByJobId(jobEntry.getId());
            //find light workers that may have grabbed the job before the job was sent to heavy queue and set their status to failed in order for them to be deleted later by the responsible scheduled Task.
            jobExecutors = jobExecutors.stream().filter(j -> j.getJobExecutorType() == JobExecutorType.Light).collect(Collectors.toList());
            if (jobExecutors.size()!=0) {
                for (JobExecutor lightJobExecutor : jobExecutors) {
                    if (lightJobExecutor.getStatus().equals(SchedulingConstants.WORKER_FAILED)) {
                        return;
                    }
                    lightJobExecutor.setStatus(SchedulingConstants.WORKER_FAILED);
                    JobExecutorHistory lightJobExecutorHistory = new JobExecutorHistory(lightJobExecutor.getName(), lightJobExecutor.getContainerId(), SchedulingConstants.WORKER_FAILED, lightJobExecutor.getJobId(), new Timestamp(new Date().getTime()), lightJobExecutor.getHeartBeatQueue());
                    lightJobExecutorHistory.setJobExecutorType(lightJobExecutor.getJobExecutorType());
                    saveOrUpdateJobExecutor(lightJobExecutor, lightJobExecutorHistory);
                }
            }
        } else {  //the response came from a light worker. However, maybe the job has already been handled by a heavy worker but there was an inconsistency in the order the messages were processed
            if (jobEntry.isHeavy()) {
                jobExecutor.setStatus(SchedulingConstants.WORKER_FAILED);
                jobExecutorHistory.setStatus(SchedulingConstants.WORKER_FAILED);
                saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
            }
        }
    }

}













