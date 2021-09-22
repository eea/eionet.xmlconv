package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;


public interface HandleHeavyJobsService {

    void handle(WorkerJobRabbitMQRequest workerJobRabbitMQRequest) throws DatabaseException;
}
