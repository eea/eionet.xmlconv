package eionet.gdem.api.qa.service;

import eionet.gdem.XMLConvException;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.RestApiException;
import eionet.gdem.qa.XQueryService;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public interface QaService {

    /**
     *
     * Given an envelopeUrl , it makes a call to the envelopeUrl/xml and parses the output XML stream 
     * in order to extract the Schemas and Files of the given envelopeUrl.
     ***/
    HashMap<String, String> extractFileLinksAndSchemasFromEnvelopeUrl(String envelopeUrl) throws XMLConvException;

    List<String> extractObligationUrlsFromEnvelopeUrl(String envelopeUrl) throws XMLConvException;
    /**
     *  Calls  the method  {@link eionet.gdem.services.JobRequestHandlerService#analyzeMultipleXMLFiles(java.util.HashMap)  }
     *  which returns hashmap. Each entry contains a JobID and a FileURL.
     * @return a map containing each Job Id and corresponding File URL as Key value pair.
     */
    List<QaResultsWrapper> scheduleJobs(String envelopeUrl) throws XMLConvException;
    
    Hashtable<String,String> getJobResults(String jobId) throws XMLConvException;
    Vector runQaScript(String sourceUrl, String scriptId) throws XMLConvException;
    List<LinkedHashMap<String,String>> listQAScripts(String schema, String active) throws XMLConvException;

    XQueryService getXqueryService();
    
    public Document getXMLFromEnvelopeURL(String envelopeURL) throws XMLConvException ;

    Schema getSchemaBySchemaUrl(String schemaUrl) throws Exception;
}
