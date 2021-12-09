package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.WorkerHeartBeatMsgService;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import eionet.gdem.services.JobHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class HandleHeavyJobsServiceImpl implements HandleHeavyJobsService {

    private JobService jobService;
    private JobHistoryService jobHistoryService;
    private WorkerHeartBeatMsgService workerHeartBeatMsgService;
    private RabbitMQMessageSender rabbitMQMessageSender;

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleHeavyJobsServiceImpl.class);

    @Autowired
    public HandleHeavyJobsServiceImpl(JobService jobService, JobHistoryService jobHistoryService, WorkerHeartBeatMsgService workerHeartBeatMsgService,
                                      @Qualifier("heavyJobRabbitMessageSenderImpl") RabbitMQMessageSender rabbitMQMessageSender) {
        this.jobService = jobService;
        this.jobHistoryService = jobHistoryService;
        this.workerHeartBeatMsgService = workerHeartBeatMsgService;
        this.rabbitMQMessageSender = rabbitMQMessageSender;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handle(WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage, JobEntry jobEntry, JobHistoryEntry jobHistoryEntry) throws DatabaseException {
        LOGGER.info("Handling heavy job " + workerJobRabbitMQRequestMessage.getScript().getJobId());
        jobService.updateJob(jobEntry.getnStatus(), jobEntry.getIntSchedulingStatus(), jobEntry.getJobExecutorName(), new Timestamp(new Date().getTime()), jobEntry);
        jobService.updateHeavyRetriesOnFailure(jobEntry.getHeavyRetriesOnFailure(), new Timestamp(new Date().getTime()), jobEntry.getId());
        jobHistoryService.save(jobHistoryEntry);
        rabbitMQMessageSender.sendMessageToRabbitMQ(workerJobRabbitMQRequestMessage);
        if (jobEntry.getHeavyRetriesOnFailure()==1) {
            clearUnansweredLightWorkerHeartBeatMessages(jobEntry.getId());
        }
    }
    
    void clearUnansweredLightWorkerHeartBeatMessages(Integer jobId) throws DatabaseException {
        List<WorkerHeartBeatMsgEntry> messages = workerHeartBeatMsgService.findUnAnsweredHeartBeatMessages(jobId);
        if (messages.size()>0) {
            messages.stream().forEach(workerHeartBeatMsgEntry -> workerHeartBeatMsgService.delete(workerHeartBeatMsgEntry.getId()));
        }
    }
}
