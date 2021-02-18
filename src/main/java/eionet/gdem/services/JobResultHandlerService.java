package eionet.gdem.services;

import eionet.gdem.XMLConvException;

import java.util.HashMap;
import java.util.Hashtable;

public interface JobResultHandlerService {
    Hashtable<String,String> getResult(String jobId) throws XMLConvException;
}
