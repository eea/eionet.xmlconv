package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;

public interface WorkerAndJobStatusHandlerService {

    void updateJobAndJobHistoryEntries(Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry) throws DatabaseException;

    void saveOrUpdateJobExecutor(JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException;

    void handleCancelledJob(JobEntry jobEntry, Integer workerStatus, Integer nStatus, InternalSchedulingStatus internalStatus) throws DatabaseException;

    void resendMessageToWorker(Integer workerRetries, Integer nStatus, InternalSchedulingStatus internalStatus, JobEntry jobEntry, WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage, JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory, boolean isHeavy) throws DatabaseException;
}
