package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;

public interface WorkerAndJobStatusHandlerService {

    void updateJobAndJobHistoryEntries(JobEntry jobEntry) throws DatabaseException;

    void saveOrUpdateJobExecutor(JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException;

    void handleCancelledJob(JobEntry jobEntry, Integer workerStatus) throws DatabaseException;

    void resendMessageToWorker(JobEntry jobEntry, WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage, JobExecutor jobExecutor, JobExecutorHistory jobExecutorHistory) throws DatabaseException;
}
