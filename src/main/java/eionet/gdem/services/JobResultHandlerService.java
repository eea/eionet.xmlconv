package eionet.gdem.services;

import eionet.gdem.XMLConvException;
import eionet.gdem.jpa.Entities.JobEntry;

import java.util.Hashtable;

public interface JobResultHandlerService {
    Hashtable<String,Object> getResult(String jobId, Boolean addedThroughRabbitMq) throws XMLConvException;

    void setResultFileContentToFailed(JobEntry job) throws XMLConvException;
}
