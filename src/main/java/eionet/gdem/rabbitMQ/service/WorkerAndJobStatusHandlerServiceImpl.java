package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import eionet.gdem.services.JobHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

@Service("workerAndJobStatusHandlerService")
public class WorkerAndJobStatusHandlerServiceImpl implements WorkerAndJobStatusHandlerService {

    JobService jobService;
    JobHistoryService jobHistoryService;
    JobExecutorService jobExecutorService;
    JobExecutorHistoryService jobExecutorHistoryService;
    RabbitMQMessageSender rabbitMQMessageSender;
    RabbitMQMessageSender heavyRabbitMQMessageSender;

    @Autowired
    public WorkerAndJobStatusHandlerServiceImpl(JobService jobService, JobHistoryService jobHistoryService, JobExecutorService jobExecutorService, JobExecutorHistoryService jobExecutorHistoryService,
                                                @Qualifier("lightJobRabbitMessageSenderImpl") RabbitMQMessageSender rabbitMQMessageSender, @Qualifier("heavyJobRabbitMessageSenderImpl") RabbitMQMessageSender heavyRabbitMQMessageSender) {
        this.jobService = jobService;
        this.jobHistoryService = jobHistoryService;
        this.jobExecutorService = jobExecutorService;
        this.jobExecutorHistoryService = jobExecutorHistoryService;
        this.rabbitMQMessageSender = rabbitMQMessageSender;
        this.heavyRabbitMQMessageSender = heavyRabbitMQMessageSender;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateJobAndJobHistoryEntries(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry) throws DatabaseException {
        updateJobAndJobHistory(nStatus, internalStatus, jobEntry);
    }

    protected void updateJobAndJobHistory(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry) throws DatabaseException {
        jobService.updateJob(nStatus, internalStatus, jobEntry.getJobExecutorName(), new Timestamp(new Date().getTime()), jobEntry);
        jobHistoryService.updateJobHistory(nStatus, internalStatus.getId(), jobEntry);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateJobExecutor(JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException {
        updateJobExecutorAndJobExecutorHistory(jobExecutor, jobExecutorHistory);
    }

    protected void updateJobExecutorAndJobExecutorHistory(JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException {
        JobExecutor jobExecDb = jobExecutorService.findByName(jobExecutor.getName());
        if (jobExecDb!=null) {
            JobExecutorType jobExecutorType;
            if (jobExecutor.getJobExecutorType() != null) {
                jobExecutorType = jobExecutor.getJobExecutorType();
            } else jobExecutorType = jobExecDb.getJobExecutorType();
            jobExecutor.setJobExecutorType(jobExecutorType);
            jobExecutorHistory.setJobExecutorType(jobExecutorType);
        }
        jobExecutorService.saveOrUpdateJobExecutor(jobExecDb!=null, jobExecutor);
        jobExecutorHistoryService.saveJobExecutorHistoryEntry(jobExecutorHistory);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateJobAndJobExecTables(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry, JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException {
        this.updateJobAndJobHistory(nStatus, internalStatus, jobEntry);
        this.updateJobExecutorAndJobExecutorHistory(jobExecutor, jobExecutorHistory);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handleCancelledJob(JobEntry jobEntry, Integer workerStatus, Integer nStatus, InternalSchedulingStatus internalStatus) throws DatabaseException {
        if (jobEntry.getJobExecutorName()!=null) {
            JobExecutor jobExecutor = jobExecutorService.findByName(jobEntry.getJobExecutorName());
            if (jobExecutor!=null) {
                jobExecutor.setStatus(workerStatus);
                JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(jobEntry.getJobExecutorName(), jobExecutor.getContainerId(), workerStatus, jobEntry.getId(), new Timestamp(new Date().getTime()), jobExecutor.getHeartBeatQueue());
                this.updateJobExecutorAndJobExecutorHistory(jobExecutor, jobExecutorHistory);
            }
        }
        this.updateJobAndJobHistory(nStatus, internalStatus, jobEntry);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void resendMessageToWorker(Integer workerRetries, Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry, WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage,
                                      JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory, boolean isHeavy) throws DatabaseException {
        jobService.updateWorkerRetries(workerRetries, new Timestamp(new Date().getTime()), jobEntry.getId());
        this.updateJobAndJobHistory(nStatus, internalStatus, jobEntry);
        this.updateJobExecutorAndJobExecutorHistory(jobExecutor, jobExecutorHistory);
        if (isHeavy) heavyRabbitMQMessageSender.sendMessageToRabbitMQ(workerJobRabbitMQRequestMessage);
        else rabbitMQMessageSender.sendMessageToRabbitMQ(workerJobRabbitMQRequestMessage);
    }
}











