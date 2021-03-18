package eionet.gdem.services;

import eionet.gdem.XMLConvException;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.qa.XQScript;

public interface JobOnDemandHandlerService {

    JobEntry createJobAndSendToRabbitMQ(XQScript xq, Integer scriptId) throws XMLConvException;
}
