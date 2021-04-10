package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Constants;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.*;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
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

    @Autowired
    public WorkerAndJobAndJobStatusHandlerServiceImpl(JobService jobService, JobHistoryService jobHistoryService, JobExecutorService jobExecutorService,
                                                      JobExecutorHistoryService jobExecutorHistoryService) {
        this.jobService = jobService;
        this.jobHistoryService = jobHistoryService;
        this.jobExecutorService = jobExecutorService;
        this.jobExecutorHistoryService = jobExecutorHistoryService;
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
    public void updateWorkerRetriesAndWorkerStatus(Integer workerRetries, JobHistoryEntry jobHistoryEntry, JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException {
        jobService.updateWorkerRetries(workerRetries, new Timestamp(new Date().getTime()), Integer.parseInt(jobHistoryEntry.getJobName()));
        jobHistoryService.save(jobHistoryEntry);
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
    public void handleCancelledJob(JobEntry jobEntry) throws DatabaseException {
        if (jobEntry.getJobExecutorName()!=null) {
            JobExecutor jobExecutor = jobExecutorService.findByName(jobEntry.getJobExecutorName());
            jobExecutor.setStatus(SchedulingConstants.WORKER_FAILED);
            JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(jobEntry.getJobExecutorName(), jobExecutor.getContainerId(), SchedulingConstants.WORKER_FAILED, jobEntry.getId(), new Timestamp(new Date().getTime()), jobExecutor.getHeartBeatQueue());
            this.saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
        }
        InternalSchedulingStatus internalStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
        this.updateJobAndJobHistoryEntries(Constants.CANCELLED_BY_USER, internalStatus, jobEntry);
    }
}











