package eionet.gdem.rabbitMQ.service;

import eionet.gdem.data.scripts.HeavyScriptReasonEnum;
import eionet.gdem.jpa.Entities.*;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.QueryJpaService;
import eionet.gdem.jpa.service.WorkerHeartBeatMsgService;
import eionet.gdem.qa.utils.ScriptUtils;
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

    public static final String CONVERTERS_NAME = "converters";
    private static final Logger LOGGER = LoggerFactory.getLogger(HandleHeavyJobsServiceImpl.class);

    @Autowired
    public HandleHeavyJobsServiceImpl(JobService jobService, JobHistoryService jobHistoryService, WorkerHeartBeatMsgService workerHeartBeatMsgService,
                                      @Qualifier("heavyJobRabbitMessageSenderImpl") RabbitMQMessageSender rabbitMQMessageSender, QueryAndQueryHistoryService queryAndQueryHistoryService,
                                      QueryJpaService queryJpaService) {
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
        markScriptHeavy(jobEntry.getQueryId());
        rabbitMQMessageSender.sendMessageToRabbitMQ(workerJobRabbitMQRequestMessage);
        if (jobEntry.getHeavyRetriesOnFailure()==1) {
            clearUnansweredLightWorkerHeartBeatMessages(jobEntry.getId());
        }
    }

    private void markScriptHeavy(Integer queryId) {
        QueryEntry queryEntry = queryJpaService.findByQueryId(queryId);
        if (queryEntry!=null) {
            queryEntry.setMarkedHeavy(true);
            queryEntry.setMarkedHeavyReason(HeavyScriptReasonEnum.OUT_OF_MEMORY.getCode());
            queryEntry.setVersion(queryEntry.getVersion() + 1);
            QueryHistoryEntry queryHistoryEntry = ScriptUtils.createQueryHistoryEntry(CONVERTERS_NAME, queryEntry.getShortName(), queryEntry.getSchemaId().toString(), queryEntry.getResultType(), queryEntry.getDescription(), queryEntry.getScriptType(), queryEntry.getUpperLimit().toString(), queryEntry.getUrl(),
                    queryEntry.isAsynchronousExecution(), queryEntry.isActive(), queryEntry.getQueryFileName(), queryEntry.getVersion(), true, HeavyScriptReasonEnum.OUT_OF_MEMORY.getCode(), null, queryEntry.getRuleMatch());
            queryHistoryEntry.setQueryEntry(queryEntry);
            queryAndQueryHistoryService.saveQueryAndQueryHistoryEntries(queryEntry, queryHistoryEntry);
            LOGGER.info("Marked script with id " + queryEntry.getQueryId() + " as heavy because of Out of memory error");
            LOGGER.info("Marked script history of script with id " + queryEntry.getQueryId() + " as heavy because of Out of memory error");
        }
    }

    void clearUnansweredLightWorkerHeartBeatMessages(Integer jobId) throws DatabaseException {
        List<WorkerHeartBeatMsgEntry> messages = workerHeartBeatMsgService.findUnAnsweredHeartBeatMessages(jobId);
        if (messages.size()>0) {
            messages.stream().forEach(workerHeartBeatMsgEntry -> workerHeartBeatMsgService.delete(workerHeartBeatMsgEntry.getId()));
        }
    }
}
