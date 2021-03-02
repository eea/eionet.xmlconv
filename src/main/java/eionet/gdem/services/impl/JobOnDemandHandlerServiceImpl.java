package eionet.gdem.services.impl;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.jpa.repositories.JobRepository;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.service.WorkersJobMessageSender;
import eionet.gdem.services.JobOnDemandHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

@Service("jobOnDemandHandlerService")
public class JobOnDemandHandlerServiceImpl implements JobOnDemandHandlerService {

    private JobRepository jobRepository;
    private JobHistoryRepository jobHistoryRepository;
    private WorkersJobMessageSender jobMessageSender;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobOnDemandHandlerServiceImpl.class);

    @Autowired
    public JobOnDemandHandlerServiceImpl(@Qualifier("jobRepository") JobRepository jobRepository, @Qualifier("jobHistoryRepository") JobHistoryRepository jobHistoryRepository,
                                         WorkersJobMessageSender jobMessageSender) {
        this.jobRepository = jobRepository;
        this.jobHistoryRepository = jobHistoryRepository;
        this.jobMessageSender = jobMessageSender;
    }

    @Transactional
    @Override
    public JobEntry createJobAndSendToRabbitMQ(XQScript script, Integer scriptId) throws SQLException {
        JobEntry jobEntry = new JobEntry();
        try {
            InternalSchedulingStatus internalSchedulingStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_RECEIVED);
            jobEntry = new JobEntry(script.getSrcFileUrl(), script.getScriptFileName(), script.getStrResultFile(), Constants.XQ_RECEIVED, scriptId, new Timestamp(new Date().getTime()), script.getScriptType(), internalSchedulingStatus).setRetryCounter(0);
            jobEntry = jobRepository.save(jobEntry);
            LOGGER.info("Job with id " + jobEntry.getId() + " has been inserted in table T_XQJOBS");
            saveJobHistory(jobEntry.getId().toString(), script, Constants.XQ_RECEIVED, SchedulingConstants.INTERNAL_STATUS_RECEIVED);
            script.setJobId(jobEntry.getId().toString());

            jobMessageSender.sendJobInfoOnDemandToRabbitMQ(script);

            Integer retryCounter = jobRepository.getRetryCounter(jobEntry.getId());
            jobRepository.updateJobInfo(Constants.XQ_PROCESSING, Properties.getHostname(), new Timestamp(new Date().getTime()), retryCounter + 1, jobEntry.getId());
            internalSchedulingStatus.setId(SchedulingConstants.INTERNAL_STATUS_QUEUED);
            jobRepository.updateIntStatusAndJobExecutorName(internalSchedulingStatus, null, new Timestamp(new Date().getTime()), jobEntry.getId());
            saveJobHistory(jobEntry.getId().toString(), script, Constants.XQ_PROCESSING, SchedulingConstants.INTERNAL_STATUS_QUEUED);
        } catch (Exception e) {
            LOGGER.info("Error during database transaction for job with id " + jobEntry.getId());
            throw new SQLException("Error during database transaction for job with id " + jobEntry.getId());
        }
        return jobEntry;
    }

    void saveJobHistory(String jobId, XQScript script, Integer nStatus, Integer internalStatus) {
        JobHistoryEntry jobHistoryEntry = new JobHistoryEntry(jobId, nStatus, new Timestamp(new Date().getTime()), script.getSrcFileUrl(), script.getScriptFileName(), script.getStrResultFile(), script.getScriptType());
        jobHistoryEntry.setIntSchedulingStatus(internalStatus);
        jobHistoryRepository.save(jobHistoryEntry);
        LOGGER.info("Job with id #" + jobId + " has been inserted in table JOB_HISTORY ");
    }
}























