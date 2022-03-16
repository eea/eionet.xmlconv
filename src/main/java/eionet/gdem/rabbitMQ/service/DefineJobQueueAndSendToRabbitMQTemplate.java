package eionet.gdem.rabbitMQ.service;

import eionet.gdem.XMLConvException;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;

public interface DefineJobQueueAndSendToRabbitMQTemplate {

    void checkHeavyOrLight(QueryEntry queryEntry, JobEntry jobEntry);
    void checkRules(QueryEntry queryEntry, JobEntry jobEntry) throws XMLConvException;
    void updateDatabase(JobEntry jobEntry) throws DatabaseException;
    void sendMsgToRabbitMQ(QueryEntry queryEntry, JobEntry jobEntry, WorkerJobRabbitMQRequestMessage message);

    default void execute(QueryEntry queryEntry, JobEntry jobEntry, WorkerJobRabbitMQRequestMessage message) throws XMLConvException, DatabaseException {
        checkHeavyOrLight(queryEntry, jobEntry);
        checkRules(queryEntry, jobEntry);
        updateDatabase(jobEntry);
        sendMsgToRabbitMQ(queryEntry, jobEntry, message);
    }
}
