package eionet.gdem.rabbitMQ.service;

import eionet.gdem.api.qa.model.QaResultsWrapper;
import eionet.gdem.jpa.Entities.JobEntry;

import java.util.List;

public interface CdrResponseMessageFactoryService {

    void createCdrResponseMessageAndSendToQueue(JobEntry jobEntry);
    void createCdrSummaryResponseMessageAndSendToQueue(String uuid, List<QaResultsWrapper> scheduledJobs);
}
