package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.Entities.*;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.rabbitMQ.model.WorkerJobInfoRabbitMQResponse;

public interface WorkerAndJobStatusHandlerService {

    void updateJobAndJobHistoryEntries(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry) throws DatabaseException;

    void saveOrUpdateJobExecutor(JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException;

    void updateJobAndJobExecTables(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry, JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException;

    void changeStatusesForWorkerRetries(Integer workerRetries, JobHistoryEntry jobHistoryEntry, WorkerJobInfoRabbitMQResponse response);

    void changeStatusForInterruptedJobs(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry) throws DatabaseException;

    void handleCancelledJob(JobEntry jobEntry, Integer workerStatus, Integer nStatus, InternalSchedulingStatus internalStatus) throws DatabaseException;
}
