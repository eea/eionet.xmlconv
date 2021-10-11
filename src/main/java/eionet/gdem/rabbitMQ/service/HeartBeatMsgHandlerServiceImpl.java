package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Constants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.WorkerHeartBeatMsgService;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessageInfo;
import eionet.gdem.services.JobHistoryService;
import eionet.gdem.jpa.service.QueryMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class HeartBeatMsgHandlerServiceImpl implements HeartBeatMsgHandlerService {

    private WorkerHeartBeatMsgService workerHeartBeatMsgService;
    private RabbitMQMessageSender rabbitMQMessageSender;
    private JobService jobService;
    private JobHistoryService jobHistoryService;


    @Autowired
    QueryMetadataService queryMetadataService;

    @Autowired
    public HeartBeatMsgHandlerServiceImpl(WorkerHeartBeatMsgService workerHeartBeatMsgService, RabbitMQMessageSender rabbitMQMessageSender, JobService jobService, JobHistoryService jobHistoryService) {
        this.workerHeartBeatMsgService = workerHeartBeatMsgService;
        this.rabbitMQMessageSender = rabbitMQMessageSender;
        this.jobService = jobService;
        this.jobHistoryService = jobHistoryService;
    }

    @Transactional
    @Override
    public void saveMsgAndSendToRabbitMQ(WorkerHeartBeatMessageInfo heartBeatMsgInfo, WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry) {
        workerHeartBeatMsgEntry = workerHeartBeatMsgService.save(workerHeartBeatMsgEntry);
        heartBeatMsgInfo.setId(workerHeartBeatMsgEntry.getId());
        rabbitMQMessageSender.sendHeartBeatMessage(heartBeatMsgInfo);
    }

    @Transactional
    @Override
    public void updateHeartBeatJobAndQueryTables(WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry, Integer jobId, Integer jobStatus, Integer nStatus, InternalSchedulingStatus internalStatus) throws DatabaseException {
        workerHeartBeatMsgService.save(workerHeartBeatMsgEntry);

        JobEntry jobEntry = jobService.findById(jobId);
        if (jobEntry.getnStatus()== Constants.XQ_PROCESSING && jobStatus.equals(Constants.JOB_NOT_FOUND_IN_WORKER)) {
            jobService.changeStatusesAndJobExecutorName(nStatus, internalStatus, jobEntry.getJobExecutorName(), new Timestamp(new Date().getTime()), jobEntry.getId());
            JobHistoryEntry jobHistoryEntry = new JobHistoryEntry(jobEntry.getId().toString(), nStatus, new Timestamp(new Date().getTime()), jobEntry.getUrl(), jobEntry.getFile(), jobEntry.getResultFile(), jobEntry.getScriptType())
                    .setIntSchedulingStatus(internalStatus.getId()).setJobExecutorName(jobEntry.getJobExecutorName()).setWorkerRetries(jobEntry.getWorkerRetries()).setJobType(jobEntry.getJobType())
                    .setDuration(jobEntry.getDuration()!=null ? jobEntry.getDuration().longValue() : null);
            jobHistoryService.save(jobHistoryEntry);
            Long currentMs = new Timestamp(new Date().getTime()).getTime();
            Long durationOfJob = Math.abs(currentMs - jobEntry.getTimestamp().getTime());
            queryMetadataService.storeScriptInformation(jobEntry.getQueryId(), jobEntry.getFile(), jobEntry.getScriptType(), durationOfJob, Constants.XQ_FATAL_ERR);
        }
    }
}
