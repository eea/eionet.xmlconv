package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.Entities.*;
import eionet.gdem.jpa.errors.DatabaseException;

public interface WorkerAndJobStatusHandlerService {

    void updateJobAndJobHistoryEntries(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry) throws DatabaseException;

    void saveOrUpdateJobExecutor(JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException;

    void updateJobAndJobExecTables(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry, JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException;

    void updateWorkerRetriesAndWorkerStatus(Integer workerRetries, JobHistoryEntry jobHistoryEntry, JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException;

    void changeStatusForInterruptedJobs(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry) throws DatabaseException;

    void handleCancelledJob(JobEntry jobEntry) throws DatabaseException;
}
