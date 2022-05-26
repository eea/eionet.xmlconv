package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.Entities.JobEntry;

public interface CdrResponseMessageFactoryService {

    void createCdrResponseMessageAndSendToQueue(JobEntry jobEntry);
}
