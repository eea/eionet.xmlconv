package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.service.JobService;
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

@Service
public class HandleHeavyJobsServiceImpl implements HandleHeavyJobsService {

    private JobService jobService;
    private JobHistoryService jobHistoryService;
    private WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;
    private RabbitMQMessageSender rabbitMQMessageSender;

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleHeavyJobsServiceImpl.class);

    @Autowired
    public HandleHeavyJobsServiceImpl(JobService jobService, JobHistoryService jobHistoryService, WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService,
                                      @Qualifier("heavyJobRabbitMessageSenderImpl") RabbitMQMessageSender rabbitMQMessageSender) {
        this.jobService = jobService;
        this.jobHistoryService = jobHistoryService;
        this.workerAndJobStatusHandlerService = workerAndJobStatusHandlerService;
        this.rabbitMQMessageSender = rabbitMQMessageSender;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handle(WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage, JobEntry jobEntry, JobHistoryEntry jobHistoryEntry) {
        LOGGER.info("Handling heavy job " + workerJobRabbitMQRequestMessage.getScript().getJobId());
        jobService.updateIsHeavyAndHeavyRetries(jobEntry.isHeavy(), jobEntry.getHeavyRetriesOnFailure(), new Timestamp(new Date().getTime()), jobEntry.getId());
        jobHistoryService.save(jobHistoryEntry);
        rabbitMQMessageSender.sendMessageToRabbitMQ(workerJobRabbitMQRequestMessage);
    }
}
