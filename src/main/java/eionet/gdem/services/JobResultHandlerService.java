package eionet.gdem.services;

import eionet.gdem.XMLConvException;

import java.util.HashMap;
import java.util.Hashtable;

public interface JobResultHandlerService {
    Hashtable<String,Object> getResult(String jobId, Boolean addedThroughRabbitMq) throws XMLConvException;
}
