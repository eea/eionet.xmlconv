package eionet.gdem.services;

import eionet.gdem.XMLConvException;
import eionet.gdem.jpa.Entities.JobEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

public interface JobResultHandlerService {
    Hashtable<String,Object> getResult(String jobId, Boolean addedThroughRabbitMq) throws XMLConvException;

    void setResultFileContentToFailed(JobEntry job) throws XMLConvException;
}
