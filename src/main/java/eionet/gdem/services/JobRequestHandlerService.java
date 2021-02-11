package eionet.gdem.services;

import eionet.gdem.XMLConvException;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public interface JobRequestHandlerService {

    HashMap analyzeMultipleXMLFiles(HashMap<String, List<String>> filesAndSchemas) throws XMLConvException;
}
