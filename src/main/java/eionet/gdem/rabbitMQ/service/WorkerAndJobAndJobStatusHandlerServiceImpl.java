package eionet.gdem.rabbitMQ.service;

import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;
import eionet.gdem.services.JobHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

@Service("workerAndJobStatusHandlerService")
public class WorkerAndJobAndJobStatusHandlerServiceImpl implements WorkerAndJobStatusHandlerService {

    JobService jobService;
    JobHistoryService jobHistoryService;
    JobExecutorService jobExecutorService;
    JobExecutorHistoryService jobExecutorHistoryService;
    RabbitMQMessageSender rabbitMQMessageSender;

    @Autowired
    public WorkerAndJobAndJobStatusHandlerServiceImpl(JobService jobService, JobHistoryService jobHistoryService, JobExecutorService jobExecutorService,
                                                      JobExecutorHistoryService jobExecutorHistoryService, RabbitMQMessageSender rabbitMQMessageSender) {
        this.jobService = jobService;
        this.jobHistoryService = jobHistoryService;
        this.jobExecutorService = jobExecutorService;
        this.jobExecutorHistoryService = jobExecutorHistoryService;
        this.rabbitMQMessageSender = rabbitMQMessageSender;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateJobAndJobHistoryEntries(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry) throws DatabaseException {
        updateJobAndJobHistory(nStatus, internalStatus, jobEntry);
    }

    protected void updateJobAndJobHistory(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry) throws DatabaseException {
        jobService.changeStatusesAndJobExecutorName(nStatus, internalStatus, jobEntry.getJobExecutorName(), new Timestamp(new Date().getTime()), jobEntry.getId());
        jobHistoryService.updateStatusesAndJobExecutorName(nStatus, internalStatus.getId(), jobEntry);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateJobExecutor(JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException {
        updateJobExecutorAndJobExecutorHistory(jobExecutor, jobExecutorHistory);
    }

    protected void updateJobExecutorAndJobExecutorHistory(JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException {
        jobExecutorService.saveOrUpdateJobExecutor(jobExecutor);
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
    public void changeStatusForInterruptedJobs(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry) throws DatabaseException {
        this.updateJobAndJobHistory(nStatus, internalStatus, jobEntry);
        JobExecutor jobExecutor = jobExecutorService.findByName(jobEntry.getJobExecutorName());
        jobExecutor.setStatus(SchedulingConstants.WORKER_FAILED);
        JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(jobEntry.getJobExecutorName(), jobExecutor.getContainerId(), SchedulingConstants.WORKER_FAILED, jobEntry.getId(), new Timestamp(new Date().getTime()), jobExecutor.getHeartBeatQueue());
        this.updateJobExecutorAndJobExecutorHistory(jobExecutor, jobExecutorHistory);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handleCancelledJob(JobEntry jobEntry, Integer workerStatus, Integer nStatus, InternalSchedulingStatus internalStatus) throws DatabaseException {
        if (jobEntry.getJobExecutorName()!=null) {
            JobExecutor jobExecutor = jobExecutorService.findByName(jobEntry.getJobExecutorName());
            jobExecutor.setStatus(workerStatus);
            JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(jobEntry.getJobExecutorName(), jobExecutor.getContainerId(), workerStatus, jobEntry.getId(), new Timestamp(new Date().getTime()), jobExecutor.getHeartBeatQueue());
            this.saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
        }
        this.updateJobAndJobHistoryEntries(nStatus, internalStatus, jobEntry);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void resendMessageToWorker(Integer workerRetries, Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry, WorkerJobRabbitMQRequest workerJobRabbitMQRequest) throws DatabaseException {
        jobService.updateWorkerRetries(workerRetries, new Timestamp(new Date().getTime()), jobEntry.getId());
        this.updateJobAndJobHistory(nStatus, internalStatus, jobEntry);
        rabbitMQMessageSender.sendJobInfoToRabbitMQ(workerJobRabbitMQRequest);
    }
}











