package eionet.gdem.api.qa.service;

import eionet.gdem.XMLConvException;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import eionet.gdem.dto.Schema;
import eionet.gdem.qa.QueryService;
import org.w3c.dom.Document;

import java.io.UnsupportedEncodingException;
import java.util.*;

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
     *  Calls  the method  {@link eionet.gdem.services.JobRequestHandlerService#analyzeMultipleXMLFiles(java.util.HashMap, Boolean, Boolean, String)  }
     *  which returns hashmap. Each entry contains a JobID and a FileURL.
     * @return a map containing each Job Id and corresponding File URL as Key value pair.
     */
    List<QaResultsWrapper> scheduleJobs(String envelopeUrl, Boolean checkForDuplicateJob, Boolean addedThroughRabbitMq, String uuid) throws XMLConvException;

    Hashtable<String,Object> getJobResults(String jobId) throws XMLConvException;
    Vector runQaScript(String sourceUrl, String scriptId) throws XMLConvException;
    Vector runQaScript(String sourceUrl, String scriptId,boolean setBodyInResponse, boolean isTrustedMode) throws XMLConvException;

    List<LinkedHashMap<String,String>> listQAScripts(String schema, String active) throws XMLConvException;

    QueryService getQueryService();

    public Document getXMLFromEnvelopeURL(String envelopeURL) throws XMLConvException ;

    Schema getSchemaBySchemaUrl(String schemaUrl) throws Exception;

    Integer getJobExternalStatus(String jobId) throws XMLConvException;

    LinkedHashMap<String, Object> checkIfZipFileExistsOrIsEmpty(String[] fileUrls, String jobId, LinkedHashMap<String, Object> jsonResults) throws XMLConvException;

    LinkedHashMap<String, Object> checkIfHtmlResultIsEmpty(String jobId, LinkedHashMap<String, Object> jsonResults, Hashtable<String, Object> results) throws XMLConvException;

    LinkedHashMap<String, String> handleOnDemandJobsResults(Vector results, String sourceXml, String scriptId) throws XMLConvException;
}
