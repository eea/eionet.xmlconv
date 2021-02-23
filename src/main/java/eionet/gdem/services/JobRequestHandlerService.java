package eionet.gdem.services;

import eionet.gdem.XMLConvException;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public interface JobRequestHandlerService {

    HashMap analyzeMultipleXMLFiles(HashMap<String, List<String>> filesAndSchemas) throws XMLConvException;
    String analyzeSingleXMLFile(String sourceURL, String scriptId, String schema) throws XMLConvException;
    String analyze(String sourceURL, String xqScript, String scriptType) throws XMLConvException;
}
