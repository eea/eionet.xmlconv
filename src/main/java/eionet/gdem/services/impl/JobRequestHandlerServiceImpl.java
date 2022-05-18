package eionet.gdem.services.impl;

import eionet.gdem.Properties;
import eionet.gdem.*;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.dcm.remote.RemoteService;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.*;
import eionet.gdem.qa.utils.ScriptUtils;
import eionet.gdem.rabbitMQ.errors.CreateRabbitMQMessageException;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageFactory;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.JobHistoryService;
import eionet.gdem.services.JobRequestHandlerService;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.InputAnalyser;
import eionet.gdem.web.spring.conversions.IConvTypeDao;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static eionet.gdem.Constants.JOB_VALIDATION;
import static eionet.gdem.qa.ListQueriesMethod.DEFAULT_CONTENT_TYPE_ID;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service("jobRequestHandlerService")
public class JobRequestHandlerServiceImpl extends RemoteService implements JobRequestHandlerService  {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRequestHandlerService.class);

    private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
    private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();
    private IConvTypeDao convTypeDao = GDEMServices.getDaoService().getConvTypeDao();

    private SchemaManager schManager = new SchemaManager();

    QueryService queryService;
    private static final String NOT_HEAVY = "0";

    @Autowired
    public JobRequestHandlerServiceImpl() {
        queryService = new QueryService();
        setTrustedMode(true);
    }

    /**
     * This method is copied from QueryService public Vector analyzeXMLFiles(Hashtable files) throws XMLConvException {
     *
     * @param filesAndSchemas - Structure with XMLschemas as a keys and values are list of XML Files
     * @return Hashtable result: Structure with JOB ids as a keys and source files as values
     * @throws XMLConvException If an error occurs.
     */
    @Override
    public HashMap analyzeMultipleXMLFiles(HashMap<String, List<String>> filesAndSchemas, Boolean checkForDuplicateJob) throws XMLConvException {

        HashMap result = new HashMap();

        if (filesAndSchemas == null) {
            return result;
        }

        for (Map.Entry<String, List<String>> entry : filesAndSchemas.entrySet()) {
            String schema = entry.getKey();
            List<String> fileList = entry.getValue();
            if (Utils.isNullList(fileList)) {
                continue;
            }

            for (String file: fileList){
                // get all possible xqueries from db
                String newId = "-1"; // should not be returned with value -1;

                List<Hashtable> queries = queryService.listQueries(schema);

                if (!Utils.isNullList(queries)) {
                    for(Hashtable ht: queries){
                        String query_id = String.valueOf(ht.get( ListQueriesMethod.KEY_QUERY_ID ));
                        newId = analyzeSingleXMLFile( file, query_id , schema, checkForDuplicateJob );
                        result.put(newId, file);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String analyzeSingleXMLFile(String sourceURL, String scriptId, String schema, Boolean checkForDuplicateJob) throws XMLConvException{
        String jobId = null;
        if(checkForDuplicateJob){
            jobId = getJobService().findDuplicateNotCompletedJob(sourceURL, scriptId);
        }
        if(!Utils.isNullStr(jobId)){
            return jobId;
        }
        else{
            jobId = "-1";
        }
        HashMap query;
        String originalSourceURL = sourceURL;

        try {

            if ( String.valueOf(Constants.JOB_VALIDATION).equals(scriptId )  ){ // Validation
                query = createMapForJobValidation(sourceURL, schema);
            }
            else{
                query = queryDao.getQueryInfo( scriptId);
            }

            if (isNull(  query ) ){
                throw new XMLConvException("Script ID does not exist");
            }
            if ( "0".equals(query.get(QaScriptView.IS_ACTIVE) )){
                throw new XMLConvException("Script is not active");
            }

            Vector outputTypes = convTypeDao.getConvTypes();

            String query_id = String.valueOf(query.get(QaScriptView.QUERY_ID));
            String queryFile = (String) query.get(QaScriptView.QUERY);
            String contentType = (String) query.get(QaScriptView.CONTENT_TYPE_ID);
            String scriptType = (String) query.get(QaScriptView.SCRIPT_TYPE);
            String asynchronousExecutionStr = (String) query.get(QaScriptView.ASYNCHRONOUS_EXECUTION);
            Boolean asynchronousExecution;
            if(asynchronousExecutionStr != null && asynchronousExecutionStr.equals("1")){
                asynchronousExecution = true;
            }
            else{
                asynchronousExecution = false;
            }
            String fileExtension = getExtension(outputTypes, contentType,scriptType, asynchronousExecution);
            String resultFile =
                    eionet.gdem.Properties.tmpFolder + File.separatorChar + "gdem_q" + query_id + "_" + System.currentTimeMillis() + "."
                            + fileExtension;

            int queryId;
            try {
                queryId = Integer.parseInt(query_id);
            } catch (NumberFormatException n) {
                queryId = 0;
            }
            // if it is a XQuery script, then append the system folder
            if (queryId != JOB_VALIDATION
                    && queryFile.startsWith(eionet.gdem.Properties.gdemURL + "/" + Constants.QUERIES_FOLDER)) {
                queryFile =
                        Utils.Replace(queryFile, eionet.gdem.Properties.gdemURL + "/" + Constants.QUERIES_FOLDER,
                                eionet.gdem.Properties.queriesFolder + File.separator);
            }
            else if (queryId != JOB_VALIDATION
                    && ! queryFile.startsWith(eionet.gdem.Properties.gdemURL + "/" + Constants.QUERIES_FOLDER)) {
                queryFile = eionet.gdem.Properties.queriesFolder + File.separator + queryFile;
            }

            jobId = startJobInDbAndSchedule(sourceURL, originalSourceURL, queryFile, resultFile, scriptType, queryId);

        } catch (SQLException e) {
            LOGGER.error("AnalyzeXMLFile:" , e);
            throw new XMLConvException(e.getMessage());
        } catch (CreateRabbitMQMessageException e) {
            LOGGER.error("AnalyzeXMLFile:" , e);
            throw new XMLConvException(e.getMessage());
        } catch (URISyntaxException e) {
            LOGGER.error("AnalyzeXMLFile:" , e);
            throw new XMLConvException(e.getMessage());
        } catch (DatabaseException e) {
            LOGGER.error("Database exception: ", e);
            throw new XMLConvException(e.getMessage());
        }
        return jobId;
    }

    /**
     * Request from XML/RPC client Stores the xqScript and starts a job in the workqueue.
     *
     * @param sourceURL - URL of the source XML
     * @param xqScript - XQueryScript to be processed
     * @param scriptType - xquery, xsl or xgawk
     * @throws XMLConvException If an error occurs.
     */
    @Override
    public String analyze(String sourceURL, String xqScript, String scriptType) throws XMLConvException {
        String xqFile = "";
        String originalSourceURL = sourceURL;

        LOGGER.info("XML/RPC call for analyze xml with custom script: " + sourceURL);
        // save XQScript in a text file for the WQ
        try {
            String extension = ScriptUtils.getExtensionFromScriptType(scriptType);
            xqFile = Utils.saveStrToFile(xqScript, extension);
        } catch (FileNotFoundException fne) {
            throw new XMLConvException("Folder does not exist: :" + fne.toString());
        } catch (IOException ioe) {
            throw new XMLConvException("Error storing XQScript into file:" + ioe.toString());
        }

        // name for temporary output file where the esult is stored:
        String resultFile = Properties.tmpFolder + File.separatorChar + "gdem_" + System.currentTimeMillis() + ".html";
        String newJobId = "-1"; // should not be returned with value -1;

        try {
            newJobId = startJobInDbAndSchedule(sourceURL, originalSourceURL, xqFile, resultFile, scriptType, null);
        } catch (SQLException sqe) {
            LOGGER.error("DB operation failed: " + sqe.toString());
            throw new XMLConvException("DB operation failed: " + sqe.toString());
        } catch (URISyntaxException e) {
            throw new XMLConvException("URI syntax error: " + e);
        } catch (CreateRabbitMQMessageException e) {
            LOGGER.error("Scheduling job exception: " + e.toString());
            throw new XMLConvException("Scheduling job exception: " + e.toString());
        } catch (Exception sqe) {
            LOGGER.error("DB operation failed: " + sqe.toString());
            throw new XMLConvException("DB operation failed: " + sqe.toString());
        }
        return newJobId;
    }

    private String startJobInDbAndSchedule(String sourceURL, String originalSourceURL, String xqFile, String resultFile, String scriptType, Integer queryId) throws URISyntaxException, XMLConvException, SQLException, CreateRabbitMQMessageException, DatabaseException {
        // get the trusted URL from source file adapter
        sourceURL = HttpFileManager.getSourceUrlWithTicket(getTicket(), sourceURL, isTrustedMode());
        long sourceSize = HttpFileManager.getSourceURLSize(getTicket(), originalSourceURL, isTrustedMode());
        LOGGER.info("### File with size=" + sourceSize + " Bytes has been downloaded.");

        String jobId = "-1";
        String duplicateIdentifier = null;
        if(queryId == null) {
            InternalSchedulingStatus internalSchedulingStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_RECEIVED);
            JobEntry jobEntry = new JobEntry(sourceURL, xqFile, resultFile, Constants.XQ_RECEIVED, Constants.JOB_FROMSTRING, new Timestamp(new Date().getTime()), scriptType, internalSchedulingStatus).setRetryCounter(0);
            jobEntry.setXmlSize(sourceSize);
            jobEntry = getJobService().saveOrUpdate(jobEntry);
            jobId = jobEntry.getId().toString();
            LOGGER.info("Job with id " + jobId + " has been inserted in table T_XQJOBS");

        }
        else{
            InternalSchedulingStatus internalSchedulingStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_RECEIVED);
            JobEntry jobEntry = new JobEntry(sourceURL, xqFile, resultFile, Constants.XQ_RECEIVED, queryId, new Timestamp(new Date().getTime()), scriptType, internalSchedulingStatus).setRetryCounter(0);
            jobEntry.setXmlSize(sourceSize);
            duplicateIdentifier = getJobService().getDuplicateIdentifier(originalSourceURL, queryId.toString());
            jobEntry.setDuplicateIdentifier(duplicateIdentifier);
            jobEntry = getJobService().saveOrUpdate(jobEntry);
            jobId = jobEntry.getId().toString();
            LOGGER.info("Job with id " + jobId + " has been inserted in table T_XQJOBS");
        }
        LOGGER.debug( jobId + " : " + sourceURL + " size: " + sourceSize );
        LOGGER.info("### Job with id=" + jobId + " has been created.");

        JobHistoryEntry jobHistoryEntry = new JobHistoryEntry(jobId, Constants.XQ_RECEIVED, new Timestamp(new Date().getTime()), sourceURL, xqFile, resultFile, scriptType);
        jobHistoryEntry.setIntSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_RECEIVED);
        jobHistoryEntry.setDuplicateIdentifier(duplicateIdentifier);
        jobHistoryEntry.setXmlSize(sourceSize);
        getJobHistoryService().save(jobHistoryEntry);
        LOGGER.info("Job with id #" + jobId + " has been inserted in table JOB_HISTORY ");
        getRabbitMQMessageFactory().createScriptAndSendMessageToRabbitMQ(jobId);
        LOGGER.info("### Job with id=" + jobId + " has been send to the queue.");

        return jobId;
    }

    private HashMap<String,String> createMapForJobValidation(String sourceFileURL, String schema) throws XMLConvException {
        HashMap<String,String> query = new HashMap();
        if ( isEmpty(schema)){
            InputAnalyser analyser = new InputAnalyser();
            try {
                analyser.parseXML(sourceFileURL);
                schema = analyser.getSchemaOrDTD();
            } catch (Exception e) {
                throw new XMLConvException("Could not extract schema");
            }
            query.put( QaScriptView.QUERY, schema);
        }
        query.put(QaScriptView.QUERY , schema);
        query.put( QaScriptView.QUERY_ID , "-1");
        query.put( QaScriptView.CONTENT_TYPE, DEFAULT_CONTENT_TYPE_ID);
        query.put( QaScriptView.SCRIPT_TYPE, "xsd");
        return query;
    }

    /**
     * Gets file extension
     * @param outputTypes Output Types
     * @param content_type Content type
     * @return Extension
     */
    private String getExtension(Vector outputTypes, String content_type,String scriptType, Boolean asynchronousExecution)  {
        String ret = null;
        if(scriptType.equals( XQScript.SCRIPT_LANG_FME) && asynchronousExecution == true){
            ret ="zip";
        }else{
            ret = "html";
        }

        if (outputTypes == null) {
            return ret;
        }
        if (content_type == null) {
            return ret;
        }

        for (int i = 0; i < outputTypes.size(); i++) {
            Hashtable outType = (Hashtable) outputTypes.get(i);
            if (outType == null) {
                continue;
            }
            if (!outType.containsKey("conv_type") || !outType.containsKey("file_ext") || outType.get("conv_type") == null
                    || outType.get("file_ext") == null) {
                continue;
            }
            String typeId = (String) outType.get("conv_type");
            if (!content_type.equalsIgnoreCase(typeId)) {
                continue;
            }
            ret = (String) outType.get("file_ext");
        }
        return ret;
    }

    private RabbitMQMessageFactory getRabbitMQMessageFactory() {
        return (RabbitMQMessageFactory) SpringApplicationContext.getBean("rabbitMQMessageFactory");
    }
    private JobService getJobService() {
        return (JobService) SpringApplicationContext.getBean("jobService");
    }

    private JobHistoryService getJobHistoryService() {
        return (JobHistoryService) SpringApplicationContext.getBean("jobHistoryService");
    }


}
