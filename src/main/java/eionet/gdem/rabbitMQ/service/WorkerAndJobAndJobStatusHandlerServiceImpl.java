package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Constants;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.*;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
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

    @Transactional
    @Override
    public void saveOrUpdateJob(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry) throws DatabaseException {
        jobService.changeStatusesAndJobExecutorName(nStatus, internalStatus, jobEntry.getJobExecutorName(), new Timestamp(new Date().getTime()), jobEntry.getId());
        jobHistoryService.updateStatusesAndJobExecutorName(nStatus, internalStatus.getId(), jobEntry);
    }

    @Transactional
    @Override
    public void saveOrUpdateJobExecutor(JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException {
        jobExecutorService.saveOrUpdateJobExecutor(jobExecutor);
        jobExecutorHistoryService.saveJobExecutorHistoryEntry(jobExecutorHistory);
    }

    @Transactional
    @Override
    public void updateJobAndJobExecTables(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry, JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException {
        this.saveOrUpdateJob(nStatus, internalStatus, jobEntry);
        this.saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
    }

    @Transactional
    @Override
    public void updateWorkerRetriesAndWorkerStatus(Integer workerRetries, JobHistoryEntry jobHistoryEntry, JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException {
        jobService.updateWorkerRetries(workerRetries, new Timestamp(new Date().getTime()), Integer.parseInt(jobHistoryEntry.getJobName()));
        jobHistoryService.save(jobHistoryEntry);
        this.saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
    }

    @Transactional
    @Override
    public void changeStatusForInterruptedJobs(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry) throws DatabaseException {
        this.saveOrUpdateJob(nStatus, internalStatus, jobEntry);
        JobExecutor jobExecutor = jobExecutorService.findByName(jobEntry.getJobExecutorName());
        jobExecutor.setStatus(SchedulingConstants.WORKER_FAILED);
        JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(jobEntry.getJobExecutorName(), jobExecutor.getContainerId(), SchedulingConstants.WORKER_FAILED, jobEntry.getId(), new Timestamp(new Date().getTime()), jobExecutor.getHeartBeatQueue());
        this.saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
    }
}











