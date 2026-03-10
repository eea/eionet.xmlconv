package eionet.gdem.rabbitMQ.service;

import eionet.gdem.XMLConvException;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public interface DefineJobQueueAndSendToRabbitMQTemplate {

    void checkHeavyOrLight(QueryEntry queryEntry, JobEntry jobEntry);
    void checkRules(QueryEntry queryEntry, JobEntry jobEntry) throws XMLConvException;
    void updateDatabase(JobEntry jobEntry) throws DatabaseException;
    void sendMsgToRabbitMQ(JobEntry jobEntry, WorkerJobRabbitMQRequestMessage message);

    default void execute(QueryEntry queryEntry, JobEntry jobEntry, WorkerJobRabbitMQRequestMessage message) throws XMLConvException, DatabaseException {
        checkHeavyOrLight(queryEntry, jobEntry);
        checkRules(queryEntry, jobEntry);
        updateDatabase(jobEntry);
        // send message to rabbitmq after the transaction is committed as there could be race conditions where the job executor
        // queries the db for the status of the job before the transaction is committed, resulting to job not found errors
        // (as the job does not exist in the db yet)
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        sendMsgToRabbitMQ(jobEntry, message);
                    }
                }
        );
    }
}
