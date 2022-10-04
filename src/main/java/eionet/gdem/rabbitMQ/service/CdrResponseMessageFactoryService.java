package eionet.gdem.rabbitMQ.service;

import eionet.gdem.XMLConvException;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import eionet.gdem.jpa.Entities.JobEntry;

import java.util.List;

public interface CdrResponseMessageFactoryService {

    void createCdrResponseMessageAndSendToQueueOrPendingJobsTable(JobEntry jobEntry);
    Boolean handleReadyOrFailedJobsAndSendToCdr (JobEntry jobEntry) throws XMLConvException;
    void createCdrSummaryResponseMessageAndSendToQueue(String uuid, String envelopeUrl, List<QaResultsWrapper> scheduledJobs);
}
