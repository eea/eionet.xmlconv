package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.QueryJpaService;
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
    private QueryAndQueryHistoryService queryAndQueryHistoryService;
    private QueryJpaService queryJpaService;

    private CdrResponseMessageFactoryService cdrResponseMessageFactoryService;

    public static final String CONVERTERS_NAME = "converters";
    private static final Logger LOGGER = LoggerFactory.getLogger(HandleHeavyJobsServiceImpl.class);

    @Autowired
    public HandleHeavyJobsServiceImpl(JobService jobService, JobHistoryService jobHistoryService, WorkerHeartBeatMsgService workerHeartBeatMsgService,
                                      @Qualifier("heavyJobRabbitMessageSenderImpl") RabbitMQMessageSender rabbitMQMessageSender, QueryAndQueryHistoryService queryAndQueryHistoryService,
                                      QueryJpaService queryJpaService, CdrResponseMessageFactoryService cdrResponseMessageFactoryService) {
        this.jobService = jobService;
        this.jobHistoryService = jobHistoryService;
        this.workerHeartBeatMsgService = workerHeartBeatMsgService;
        this.rabbitMQMessageSender = rabbitMQMessageSender;
        this.queryAndQueryHistoryService = queryAndQueryHistoryService;
        this.queryJpaService = queryJpaService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handle(WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage, JobEntry jobEntry, JobHistoryEntry jobHistoryEntry) throws DatabaseException {
        LOGGER.info("Handling heavy job " + workerJobRabbitMQRequestMessage.getScript().getJobId());
        jobEntry.setTimestamp(new Timestamp(new Date().getTime()));
        jobService.saveOrUpdate(jobEntry);
        jobHistoryService.save(jobHistoryEntry);
        rabbitMQMessageSender.sendMessageToRabbitMQ(workerJobRabbitMQRequestMessage);
        if (jobEntry.getHeavyRetriesOnFailure()==1) {
            clearUnansweredLightWorkerHeartBeatMessages(jobEntry.getId());
        }
        if(jobEntry.getAddedFromQueue() != null && jobEntry.getAddedFromQueue()) {
            cdrResponseMessageFactoryService.createCdrResponseMessageAndSendToQueueOrPendingJobsTable(jobEntry);
        }
    }

    void clearUnansweredLightWorkerHeartBeatMessages(Integer jobId) throws DatabaseException {
        List<WorkerHeartBeatMsgEntry> messages = workerHeartBeatMsgService.findUnAnsweredHeartBeatMessages(jobId);
        if (messages.size()>0) {
            messages.stream().forEach(workerHeartBeatMsgEntry -> workerHeartBeatMsgService.delete(workerHeartBeatMsgEntry.getId()));
        }
    }
}
