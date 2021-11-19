package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;


public interface HandleHeavyJobsService {

    void handle(WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage, JobEntry jobEntry, JobHistoryEntry jobHistoryEntry) throws DatabaseException;
}
