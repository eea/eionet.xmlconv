package eionet.gdem.services;

import eionet.gdem.XMLConvException;

import java.util.HashMap;
import java.util.Hashtable;

public interface JobResultHandlerService {
    Hashtable getResult(String jobId) throws XMLConvException;
    Hashtable result(int status, String[] jobData, HashMap scriptData, String jobId) throws XMLConvException;

}
