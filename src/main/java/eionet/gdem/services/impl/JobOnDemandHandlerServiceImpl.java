package eionet.gdem.services.impl;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.XMLConvException;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.QueryJpaService;
import eionet.gdem.qa.QaScriptView;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageSender;
import eionet.gdem.services.JobHistoryService;
import eionet.gdem.services.JobOnDemandHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

@Service("jobOnDemandHandlerService")
public class JobOnDemandHandlerServiceImpl implements JobOnDemandHandlerService {

    private JobService jobService;
    private JobHistoryService jobHistoryService;
    private RabbitMQMessageSender jobMessageLightSender;
    private RabbitMQMessageSender jobMessageHeavySender;
    private QueryJpaService queryJpaService;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobOnDemandHandlerServiceImpl.class);

    @Autowired
    public JobOnDemandHandlerServiceImpl(JobService jobService, JobHistoryService jobHistoryService,
                                         @Qualifier("lightJobRabbitMessageSenderImpl") RabbitMQMessageSender jobMessageLightSender,
                                         @Qualifier("heavyJobRabbitMessageSenderImpl") RabbitMQMessageSender jobMessageHeavySender,
                                         QueryJpaService queryJpaService) {
        this.jobService = jobService;
        this.jobHistoryService = jobHistoryService;
        this.jobMessageLightSender = jobMessageLightSender;
        this.jobMessageHeavySender = jobMessageHeavySender;
        this.queryJpaService = queryJpaService;
    }

    @Transactional
    @Override
    public JobEntry createJobAndSendToRabbitMQ(XQScript script, Integer scriptId, boolean isApi) throws XMLConvException {
        JobEntry jobEntry = new JobEntry();
        try {
            InternalSchedulingStatus internalSchedulingStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_RECEIVED);
            QueryEntry query = queryJpaService.findByQueryId(scriptId);
            boolean isHeavy = false;
            if (query != null && query.getMarkedHeavy()) isHeavy = true;
            jobEntry = new JobEntry(script.getSrcFileUrl(), script.getScriptFileName(), script.getStrResultFile(), Constants.XQ_RECEIVED, scriptId, new Timestamp(new Date().getTime()), script.getScriptType(), internalSchedulingStatus)
                    .setRetryCounter(0).setJobType(Constants.ON_DEMAND_TYPE);
            if (isHeavy) jobEntry.setHeavy(true);
            jobEntry = jobService.save(jobEntry);
            LOGGER.info("Job with id " + jobEntry.getId() + " has been inserted in table T_XQJOBS");
            saveJobHistory(jobEntry.getId().toString(), script, Constants.XQ_RECEIVED, SchedulingConstants.INTERNAL_STATUS_RECEIVED, isHeavy);
            script.setJobId(jobEntry.getId().toString());

            WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage = new WorkerJobRabbitMQRequestMessage(script);
            workerJobRabbitMQRequestMessage.setJobType(Constants.ON_DEMAND_TYPE);

            if (isApi) workerJobRabbitMQRequestMessage.setApi(true);

            if (isHeavy) {
                jobMessageHeavySender.sendMessageToRabbitMQ(workerJobRabbitMQRequestMessage);
            } else {
                jobMessageLightSender.sendMessageToRabbitMQ(workerJobRabbitMQRequestMessage);
            }

            Integer retryCounter = jobService.getRetryCounter(jobEntry.getId());
            jobService.updateJobInfo(Constants.XQ_PROCESSING, Properties.getHostname(), new Timestamp(new Date().getTime()), retryCounter + 1, jobEntry.getId());
            internalSchedulingStatus.setId(SchedulingConstants.INTERNAL_STATUS_QUEUED);
            jobService.updateJob(Constants.XQ_PROCESSING, internalSchedulingStatus, null, new Timestamp(new Date().getTime()), jobEntry);
            saveJobHistory(jobEntry.getId().toString(), script, Constants.XQ_PROCESSING, SchedulingConstants.INTERNAL_STATUS_QUEUED, isHeavy);
        } catch (Exception e) {
            throw new XMLConvException(e.getMessage());
        }
        return jobEntry;
    }

    void saveJobHistory(String jobId, XQScript script, Integer nStatus, Integer internalStatus, boolean isHeavy) {
        JobHistoryEntry jobHistoryEntry = new JobHistoryEntry(jobId, nStatus, new Timestamp(new Date().getTime()), script.getSrcFileUrl(), script.getScriptFileName(), script.getStrResultFile(), script.getScriptType());
        jobHistoryEntry.setIntSchedulingStatus(internalStatus);
        jobHistoryEntry.setJobType(Constants.ON_DEMAND_TYPE);
        if (isHeavy) jobHistoryEntry.setHeavy(true);
        jobHistoryService.save(jobHistoryEntry);
        LOGGER.info("Job with id #" + jobId + " has been inserted in table JOB_HISTORY ");
    }
}























