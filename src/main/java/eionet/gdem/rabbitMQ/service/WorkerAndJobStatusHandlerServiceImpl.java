package eionet.gdem.rabbitMQ.service;

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
    public void updateJobAndJobHistoryEntries(JobEntry jobEntry) throws DatabaseException {
        updateJobAndJobHistory(jobEntry);
    }

    protected void updateJobAndJobHistory(JobEntry jobEntry) throws DatabaseException {
        jobService.saveOrUpdate(jobEntry);
        jobHistoryService.updateJobHistory(jobEntry);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateJobExecutor(JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException {
        updateJobExecutorAndJobExecutorHistory(jobExecutor, jobExecutorHistory);
    }

    protected void updateJobExecutorAndJobExecutorHistory(JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException {
        JobExecutor jobExecDb = jobExecutorService.findByName(jobExecutor.getName());
        //check if worker sent its jobExecutorType. If worker wasn't able to find its status, find the status from database. If the worker can't be found
        //in the database, then set its type to unknown
        if (jobExecDb!=null || jobExecutor.getJobExecutorType()!=null) {
            JobExecutorType jobExecutorType;
            if (jobExecutor.getJobExecutorType()!=null) {
                jobExecutorType = jobExecutor.getJobExecutorType();
            } else jobExecutorType = jobExecDb.getJobExecutorType();
            jobExecutor.setJobExecutorType(jobExecutorType);
            jobExecutorHistory.setJobExecutorType(jobExecutorType);
        }
        if (jobExecutor.getJobExecutorType()==null) jobExecutor.setJobExecutorType(JobExecutorType.Unknown);
        jobExecutorService.saveOrUpdateJobExecutor(jobExecDb!=null, jobExecutor);
        jobExecutorHistoryService.saveJobExecutorHistoryEntry(jobExecutorHistory);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handleCancelledJob(JobEntry jobEntry, Integer workerStatus) throws DatabaseException {
        if (jobEntry.getJobExecutorName()!=null) {
            JobExecutor jobExecutor = jobExecutorService.findByName(jobEntry.getJobExecutorName());
            if (jobExecutor!=null) {
                jobExecutor.setStatus(workerStatus);
                JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(jobEntry.getJobExecutorName(), jobExecutor.getContainerId(), workerStatus, jobEntry.getId(), new Timestamp(new Date().getTime()), jobExecutor.getHeartBeatQueue());
                this.updateJobExecutorAndJobExecutorHistory(jobExecutor, jobExecutorHistory);
            }
        }
        this.updateJobAndJobHistory(jobEntry);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void resendMessageToWorker(JobEntry jobEntry, WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage,
                                      JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException {
        this.updateJobAndJobHistory(jobEntry);
        this.updateJobExecutorAndJobExecutorHistory(jobExecutor, jobExecutorHistory);
        if (jobEntry.isHeavy()) heavyRabbitMQMessageSender.sendMessageToRabbitMQ(workerJobRabbitMQRequestMessage);
        else rabbitMQMessageSender.sendMessageToRabbitMQ(workerJobRabbitMQRequestMessage);
    }
}











