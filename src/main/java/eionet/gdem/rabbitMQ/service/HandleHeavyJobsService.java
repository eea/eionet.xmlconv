package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;


public interface HandleHeavyJobsService {

    void handle(WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage) throws DatabaseException;
}
