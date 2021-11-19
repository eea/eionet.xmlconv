package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Constants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.WorkerHeartBeatMsgService;
import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessage;
import eionet.gdem.services.JobHistoryService;
import eionet.gdem.jpa.service.QueryMetadataService;
import eionet.gdem.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private QueryMetadataService queryMetadataService;

    @Autowired
    public HeartBeatMsgHandlerServiceImpl(WorkerHeartBeatMsgService workerHeartBeatMsgService, @Qualifier("heartBeatRabbitMessageSenderImpl") RabbitMQMessageSender rabbitMQMessageSender,
                                          JobService jobService, JobHistoryService jobHistoryService, QueryMetadataService queryMetadataService) {
        this.workerHeartBeatMsgService = workerHeartBeatMsgService;
        this.rabbitMQMessageSender = rabbitMQMessageSender;
        this.jobService = jobService;
        this.jobHistoryService = jobHistoryService;
        this.queryMetadataService = queryMetadataService;
    }

    @Transactional
    @Override
    public void saveMsgAndSendToRabbitMQ(WorkerHeartBeatMessage heartBeatMsgInfo, WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry) {
        workerHeartBeatMsgEntry = workerHeartBeatMsgService.save(workerHeartBeatMsgEntry);
        heartBeatMsgInfo.setId(workerHeartBeatMsgEntry.getId());
        rabbitMQMessageSender.sendMessageToRabbitMQ(heartBeatMsgInfo);
    }

    @Transactional
    @Override
    public void updateHeartBeatJobAndQueryTables(WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry, WorkerHeartBeatMessage response, Integer nStatus, InternalSchedulingStatus internalStatus) throws DatabaseException {
        JobEntry jobEntry = jobService.findById(response.getJobId());

        if (jobEntry.getnStatus()== Constants.XQ_PROCESSING && response.getJobStatus().equals(Constants.JOB_NOT_FOUND_IN_WORKER) &&
                jobEntry.isHeavy() && response.getJobExecutorType().equals(JobExecutorType.Light)) {
            //heavy job is waiting in heavy queue to be grabbed by a heavy worker, so ignore light worker's response
            workerHeartBeatMsgEntry.setJobStatus(Constants.XQ_PROCESSING);
            workerHeartBeatMsgService.save(workerHeartBeatMsgEntry);
            return;
        }
        workerHeartBeatMsgService.save(workerHeartBeatMsgEntry);
        if (jobEntry.getnStatus()== Constants.XQ_PROCESSING && response.getJobStatus().equals(Constants.JOB_NOT_FOUND_IN_WORKER)) {
            jobService.updateJob(nStatus, internalStatus, jobEntry.getJobExecutorName(), new Timestamp(new Date().getTime()), jobEntry);
            JobHistoryEntry jobHistoryEntry = new JobHistoryEntry(jobEntry.getId().toString(), nStatus, new Timestamp(new Date().getTime()), jobEntry.getUrl(), jobEntry.getFile(), jobEntry.getResultFile(), jobEntry.getScriptType())
                    .setIntSchedulingStatus(internalStatus.getId()).setJobExecutorName(jobEntry.getJobExecutorName()).setWorkerRetries(jobEntry.getWorkerRetries()).setJobType(jobEntry.getJobType())
                    .setDuration(jobEntry.getDuration()!=null ? jobEntry.getDuration().longValue() : null).setHeavy(jobEntry.isHeavy()).setHeavyRetries(jobEntry.getHeavyRetries());
            jobHistoryService.save(jobHistoryEntry);
            Long durationOfJob = Utils.getDifferenceBetweenTwoTimestampsInMs(new Timestamp(new Date().getTime()), jobEntry.getTimestamp());
            queryMetadataService.storeScriptInformation(jobEntry.getQueryId(), jobEntry.getFile(), jobEntry.getScriptType(), durationOfJob, Constants.XQ_FATAL_ERR);
        }
    }
}
