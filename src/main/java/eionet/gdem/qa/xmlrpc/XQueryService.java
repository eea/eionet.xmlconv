package eionet.gdem.qa.xmlrpc;

import eionet.gdem.XMLConvException;
import eionet.gdem.qa.QueryService;
import eionet.gdem.services.JobRequestHandlerService;
import eionet.gdem.services.JobResultHandlerService;
import eionet.gdem.services.RunScriptAutomaticService;
import eionet.gdem.dcm.remote.RemoteService;
import eionet.gdem.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * QA Service Service Facade. The service is able to execute different QA related methods that are called through XML/RPC and HTTP
 * POST and GET.
 *
 * @author Enriko Käsper
 */
@Service
public class XQueryService extends RemoteService {

    private QueryService queryService;

    private JobRequestHandlerService jobRequestHandlerService;

    private JobResultHandlerService jobResultHandlerService;

    private RunScriptAutomaticService runScriptAutomaticService;

    /**
     * Default constructor.
     */
    @Autowired
    public XQueryService(QueryService queryService, JobRequestHandlerService jobRequestHandlerService, JobResultHandlerService jobResultHandlerService, RunScriptAutomaticService runScriptAutomaticService) {
        // for remote clients use trusted mode
        setTrustedMode(true);
        this.queryService = queryService;
        this.jobRequestHandlerService = jobRequestHandlerService;
        this.jobResultHandlerService = jobResultHandlerService;
        this.runScriptAutomaticService = runScriptAutomaticService;
    }

    /**
     * List all possible XQueries for this namespace.
     * @param schema Schema
     * @throws XMLConvException If an error occurs.
     */
    public Vector listQueries(String schema) throws XMLConvException {
        List<Hashtable> queries = getQueryService().listQueries(schema);
        if(!Utils.isNullList(queries)){
            //convert list of hashtables into vector of hashtables
            return new Vector(queries);
        }
        return new Vector();
    }

    /**
     * List all XQueries and their modification times for this namespace returns also XML Schema validation.
     * @param schema Schema
     * @throws XMLConvException If an error occurs.
     */
    public Vector listQAScripts(String schema) throws XMLConvException {
        return getQueryService().listQAScripts(schema);
    }

    /**
     * Request from XML/RPC client Stores the source files and starts a job in the workqueue.
     *
     * @param files - Structure with XMLschemas as a keys and values are list of XML Files
     * @return Hashtable result: Structure with JOB ids as a keys and source files as values
     * @throws XMLConvException If an error occurs.
     */
    public Vector analyzeXMLFiles(Hashtable files) throws XMLConvException {
        HashMap<String, List<String>> filesAndSchemas = new HashMap<>();
        Hashtable table = new Hashtable();
            // getting keySet() into Set

            Set<String> schemaSet = files.keySet();
            for(String schema : schemaSet) {
                List fileList = new ArrayList();

                Vector<String> schemaFiles = (Vector) files.get(schema);
                for (String file: schemaFiles){
                    fileList.add(file);
                }
                filesAndSchemas.put(schema, fileList);
            }
            HashMap<String,String> hashMapResult = getJobRequestHandlerService().analyzeMultipleXMLFiles(filesAndSchemas);
            Vector result = new Vector();
            if(!Utils.isNullHashMap(hashMapResult)){
                //convert hashmap to vector where each element is a vector of strings
                for (Map.Entry<String, String> entry : hashMapResult.entrySet()) {
                    Vector idFileVector = new Vector();
                    idFileVector.add(entry.getKey());
                    idFileVector.add(entry.getValue());
                    result.add(idFileVector);
                }
            }
            return result;
    }


    /**
     * Request from XML/RPC client Stores the xqScript and starts a job in the workqueue.
     *
     * @param sourceURL - URL of the source XML
     * @param xqScript - XQueryScript to be processed
     * @param scriptType - xquery, xsl or xgawk
     * @throws XMLConvException If an error occurs.
     */
    public String analyze(String sourceURL, String xqScript, String scriptType) throws XMLConvException {
        return getJobRequestHandlerService().analyze(sourceURL, xqScript, scriptType);
    }

    /**
     * Checks if the job is ready (or error) and returns the result (or error message).
     *
     * @param jobId Job Id
     * @return Hash including code and result
     * @throws XMLConvException If an error occurs.
     */
    public Hashtable getResult(String jobId) throws XMLConvException {
        return getJobResultHandlerService().getResult(jobId);
    }

    /**
     * Remote method for running the QA script on the fly.
     *
     * @param sourceUrl URL of the source XML
     * @param scriptId XQueryScript ID or -1 (XML Schema validation) to be processed
     * @return Vector of 2 fields: content type and byte array
     * @throws XMLConvException in case of business logic error
     */
    public Vector runQAScript(String sourceUrl, String scriptId) throws XMLConvException {
        return getRunScriptAutomaticService().runQAScript(sourceUrl, scriptId);
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public JobRequestHandlerService getJobRequestHandlerService() {
        return jobRequestHandlerService;
    }

    public JobResultHandlerService getJobResultHandlerService() {
        return jobResultHandlerService;
    }

    public RunScriptAutomaticService getRunScriptAutomaticService() {
        return runScriptAutomaticService;
    }
}
