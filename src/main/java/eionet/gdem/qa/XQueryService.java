/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.gdem.qa;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SpringApplicationContext;
import eionet.gdem.XMLConvException;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.rabbitMQ.errors.CreateMQMessageException;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageFactory;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.dcm.remote.RemoteService;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.qa.utils.ScriptUtils;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.web.spring.conversions.IConvTypeDao;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.xml.FeedbackAnalyzer;
import eionet.gdem.validation.InputAnalyser;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static eionet.gdem.Constants.JOB_VALIDATION;
import static eionet.gdem.qa.ListQueriesMethod.DEFAULT_CONTENT_TYPE_ID;
import static eionet.gdem.web.listeners.JobScheduler.getQuartzHeavyScheduler;
import static eionet.gdem.web.listeners.JobScheduler.getQuartzScheduler;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * QA Service Service Facade. The service is able to execute different QA related methods that are called through XML/RPC and HTTP
 * POST and GET.
 *
 * @author Enriko Käsper
 */
public class XQueryService extends RemoteService {

    private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
    private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();
    private IConvTypeDao convTypeDao = GDEMServices.getDaoService().getConvTypeDao();

    private SchemaManager schManager = new SchemaManager();

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(XQueryService.class);

    private static final long heavyJobThreshhold = Properties.heavyJobThreshhold;

    /**
     * Default constructor.
     */
    public XQueryService() {
        // for remote clients use trusted mode
        setTrustedMode(true);
    }

    /**
     * List all possible XQueries for this namespace.
     * @param schema Schema
     * @throws XMLConvException If an error occurs.
     */
    public Vector listQueries(String schema) throws XMLConvException {

        ListQueriesMethod method = new ListQueriesMethod();
        Vector v = method.listQueries(schema);
        return v;
    }

    /**
     * List all XQueries and their modification times for this namespace returns also XML Schema validation.
     * @param schema Schema
     * @throws XMLConvException If an error occurs.
     */
    public Vector listQAScripts(String schema) throws XMLConvException {
        ListQueriesMethod method = new ListQueriesMethod();
        Vector v = method.listQAScripts(schema);
        return v;
    }

    /**
     * List all XQueries and their modification times for this namespace returns also XML Schema validation.
     * @param schema Schema
     * @param active filter by active status
     * @throws XMLConvException If an error occurs.
     */
    public Vector listQAScripts(String schema, String active) throws XMLConvException {
        ListQueriesMethod method = new ListQueriesMethod();
        Vector v = method.listQAScripts(schema, active);
        return v;
    }

    /**
     * Request from XML/RPC client Stores the source files and starts a job in the workqueue.
     *
     * @param files - Structure with XMLschemas as a keys and values are list of XML Files
     * @return Hashtable result: Structure with JOB ids as a keys and source files as values
     * @throws XMLConvException If an error occurs.
     */
    public Vector analyzeXMLFiles(Hashtable files) throws XMLConvException {

        Vector result = new Vector();

        if (files == null) {
            return result;
        }

        Enumeration _schemas = files.keys();
        while (_schemas.hasMoreElements()) {
            String _schema = _schemas.nextElement().toString();
            Vector _files = (Vector) files.get(_schema);
            if (Utils.isNullVector(_files)) {
                continue;
            }

            for (int i = 0; i < _files.size(); i++) {
                String _file = (String) _files.get(i);
                analyzeXMLFiles(_schema, _file, result);
            }
        }
        return result;
    }

    /**
     * Stores one source file and starts a job in the workqueue.
     *
     * @param schema - XML Schema URL
     * @param file - Source file URL
     * @return Hashtable result: Structure with JOB ids as a keys and source files as values
     */
    // public Hashtable analyze(String schema, String file) throws XMLConvException{
    // return analyze(schema,file, null);
    // }

    /**
     * Analyzes XML files
     * @param schema XML Schema
     * @param origFile Original file
     * @param result Result
     * @return Processed result
     * @throws XMLConvException If an error occurs.
     */
    public Vector analyzeXMLFiles(String schema, String origFile, Vector result) throws XMLConvException {

//        LOGGER.info("analyzeXMLFiles: " + origFile);

        if (result == null) {
            result = new Vector();
        }
        // get all possible xqueries from db
        String newId = "-1"; // should not be returned with value -1;
        String file = origFile;

        Vector queries = listQueries(schema);

        if (!Utils.isNullVector(queries)) {
            for (int j = 0; j < queries.size(); j++) {

                String query_id = String.valueOf( ( (Hashtable) queries.get(j)).get( ListQueriesMethod.KEY_QUERY_ID ));
                newId = analyzeXMLFile( file, query_id , schema );

                Vector queryResult = new Vector();
                queryResult.add(newId);
                queryResult.add(origFile);
                result.add(queryResult);
            }
        }

//        LOGGER.info("Analyze xml result: " + result.toString());
        return result;
    }

    /**
     * Gets file extension
     * @param outputTypes Output Types
     * @param content_type Content type
     * @return Extension
     */
    private String getExtension(Vector outputTypes, String content_type, String scriptType) {
        String ret = null;
        if(scriptType.equals( XQScript.SCRIPT_LANG_FME)){
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

    /**
     * Request from XML/RPC client Stores the xqScript and starts a job in the workqueue.
     *
     * @param sourceURL - URL of the source XML
     * @param xqScript - XQueryScript to be processed
     * @param scriptType - xquery, xsl or xgawk
     * @throws XMLConvException If an error occurs.
     */
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
        String newId = "-1"; // should not be returned with value -1;

        // start a job in the Workqueue
        try {
            // get the trusted URL from source file adapter
            sourceURL = HttpFileManager.getSourceUrlWithTicket(getTicket(), sourceURL, isTrustedMode());
            long sourceSize = HttpFileManager.getSourceURLSize(getTicket(), originalSourceURL, isTrustedMode());

            newId = xqJobDao.startXQJob(sourceURL, xqFile, resultFile, scriptType);

            if (Properties.enableQuartz) {
                scheduleJob(newId, sourceSize, scriptType);
                getJobHistoryRepository().save(new JobHistoryEntry(newId, Constants.XQ_RECEIVED, new Timestamp(new Date().getTime()), sourceURL, xqFile, resultFile, scriptType));
                LOGGER.info("Job with id #" + newId + " has been inserted in table JOB_HISTORY ");
            } else {
                getJobHistoryRepository().save(new JobHistoryEntry(newId, Constants.XQ_RECEIVED, new Timestamp(new Date().getTime()), sourceURL, xqFile, resultFile, scriptType));
                LOGGER.info("Job with id #" + newId + " has been inserted in table JOB_HISTORY ");
                getRabbitMQMessageFactory().setJobId(newId);
                getRabbitMQMessageFactory().createScriptAndSendMessageToRabbitMQ();
            }
        } catch (SQLException sqe) {
            LOGGER.error("DB operation failed: " + sqe.toString());
            throw new XMLConvException("DB operation failed: " + sqe.toString());
        } catch (URISyntaxException e) {
            throw new XMLConvException("URI syntax error: " + e);
        } catch (SchedulerException | CreateMQMessageException e) {
            LOGGER.error("Scheduling job exception: " + e.toString());
            throw new XMLConvException("Scheduling job exception: " + e.toString());
        }
        return newId;
    }

    /**
     * Checks if the job is ready (or error) and returns the result (or error message).
     *
     * @param jobId Job Id
     * @return Hash including code and result
     * @throws XMLConvException If an error occurs.
     */
    public Hashtable getResult(String jobId) throws XMLConvException {

        LOGGER.info("XML/RPC call for getting result with JOB ID: " + jobId);

        String[] jobData = null;
        HashMap scriptData = null;
        int status = 0;
        try {
            jobData = xqJobDao.getXQJobData(jobId);

            if (jobData == null) { // no such job
                // throw new XMLConvException("** No such job with ID=" + jobId + " in the queue.");
                status = Constants.XQ_JOBNOTFOUND_ERR;
            } else {
                scriptData = queryDao.getQueryInfo(jobData[5]);

                status = Integer.valueOf(jobData[3]).intValue();
            }
        } catch (SQLException sqle) {
            throw new XMLConvException("Error getting XQJob data from DB: " + sqle.toString());
        }

        LOGGER.info("XQueryService found status for job (" + jobId + "):" + String.valueOf(status));

        Hashtable ret = result(status, jobData, scriptData, jobId);
        if (LOGGER.isInfoEnabled()) {
            String result = ret.toString();
            if (result.length() > 100) {
                result = result.substring(0, 100).concat("....");
            }
            LOGGER.info("result: " + result);
        }
        return ret;
    }

    /**
     * Hashtable to be composed for the getResult() method return value.
     * @param status Status
     * @param jobData Job data
     * @param scriptData Script data
     * @param jobId Job Id
     * @return Result
     * @throws XMLConvException If an error occurs.
     */
    protected Hashtable result(int status, String[] jobData, HashMap scriptData, String jobId) throws XMLConvException {
        Hashtable<String, String> h = new Hashtable<String, String>();
        int resultCode;
        String resultValue = "";
        String metatype = "";
        String script_title = "";

        String feedbackStatus = Constants.XQ_FEEDBACKSTATUS_UNKNOWN;
        String feedbackMsg = "";

        if (status == Constants.XQ_RECEIVED || status == Constants.XQ_DOWNLOADING_SRC || status == Constants.XQ_PROCESSING) {
            resultCode = Constants.JOB_NOT_READY;
            resultValue = "*** Not ready ***";
        } else if (status == Constants.XQ_JOBNOTFOUND_ERR) {
            resultCode = Constants.JOB_LIGHT_ERROR;
            resultValue = "*** No such job or the job result has been already downloaded. ***";
        } else {
            if (status == Constants.XQ_READY) {
                resultCode = Constants.JOB_READY;
            } else if (status == Constants.XQ_LIGHT_ERR) {
                resultCode = Constants.JOB_READY;
            } else if (status == Constants.XQ_FATAL_ERR) {
                resultCode = Constants.JOB_READY;
            } else {
                resultCode = -1; // not expected to reach here
            }

            try {
                int xq_id = 0;
                try {
                    xq_id = Integer.parseInt(jobData[5]);
                } catch (NumberFormatException n) {
                }

                if (xq_id == JOB_VALIDATION) {
                    metatype = "text/html";
                    script_title = "XML Schema validation";
                } else if (xq_id > 0) {
                    metatype = (String) scriptData.get(QaScriptView.META_TYPE);
                    script_title = (String) scriptData.get(QaScriptView.SHORT_NAME);
                }

                resultValue = Utils.readStrFromFile(jobData[2]);
                HashMap<String, String> feedbackResult = FeedbackAnalyzer.getFeedbackResultFromFile(jobData[2]);

                feedbackStatus = feedbackResult.get(Constants.RESULT_FEEDBACKSTATUS_PRM);
                feedbackMsg = feedbackResult.get(Constants.RESULT_FEEDBACKMESSAGE_PRM);


            } catch (Exception ioe) {
                resultCode = Constants.JOB_FATAL_ERROR;
                resultValue = "<error>Error reading the XQ value from the file:" + jobData[2] + "</error>";
            }

        }
        try {
            h.put(Constants.RESULT_CODE_PRM, Integer.toString(resultCode));
            h.put(Constants.RESULT_VALUE_PRM, resultValue);
            h.put(Constants.RESULT_METATYPE_PRM, metatype);
            h.put(Constants.RESULT_SCRIPTTITLE_PRM, script_title);
            h.put(Constants.RESULT_FEEDBACKSTATUS_PRM, feedbackStatus);
            h.put(Constants.RESULT_FEEDBACKMESSAGE_PRM, feedbackMsg);

        } catch (Exception e) {
            String err_mess =
                "JobID: " + jobId + "; Creating result Hashtable for getResult method failed result: " + e.toString();
            LOGGER.error(err_mess);
            throw new XMLConvException(err_mess, e);
        }

        return h;

    }


    /**
     * Schedule to workqueue one job with direct script id
     * @param sourceURL
     * @param scriptId
     * @return the jobId on succesful scheduling
     * @throws XMLConvException
     */
    public String analyzeXMLFile(String sourceURL, String scriptId) throws XMLConvException {
        return analyzeXMLFile(sourceURL, scriptId, null);
    }

    /**
     * Schedule to workqueue one job with direct script id and schema if needed for validation
     * @param sourceURL
     * @param scriptId
     * @param schema
     * @return the jobId on succesful scheduling
     * @throws XMLConvException
     */
    public String analyzeXMLFile(String sourceURL, String scriptId, String schema) throws XMLConvException {

        String jobId = "-1";
        HashMap query;
        String originalSourceURL = sourceURL;

        try {

            if ( String.valueOf(Constants.JOB_VALIDATION).equals(scriptId )  ){ // Validation
                query = new HashMap();
                if ( isEmpty(schema)){
                    InputAnalyser analyser = new InputAnalyser();
                    try {
                        analyser.parseXML(sourceURL);
                        schema = analyser.getSchemaOrDTD();
                        //return schemaOrDTD;
                    } catch (Exception e) {
                        throw new XMLConvException("Could not extract schema");
                    }
                    //String schemaUrl = findSchemaFromXml(sourceUrl);
                    query.put( QaScriptView.QUERY, schema);
                }
                //else {
                query.put(QaScriptView.QUERY , schema);
                    //Vector schemas = schemaDao.getSchemas(schema, false);
                //}
                query.put( QaScriptView.QUERY_ID , "-1");
                query.put( QaScriptView.CONTENT_TYPE, DEFAULT_CONTENT_TYPE_ID);
                query.put( QaScriptView.SCRIPT_TYPE, "xsd");

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
            String fileExtension = getExtension(outputTypes, contentType, scriptType);
            String resultFile =
                    Properties.tmpFolder + File.separatorChar + "gdem_q" + query_id + "_" + System.currentTimeMillis() + "."
                            + fileExtension;

            int queryId;
            try {
                queryId = Integer.parseInt(query_id);
            } catch (NumberFormatException n) {
                queryId = 0;
            }
            // if it is a XQuery script, then append the system folder
            if (queryId != JOB_VALIDATION
                    && queryFile.startsWith(Properties.gdemURL + "/" + Constants.QUERIES_FOLDER)) {
                queryFile =
                        Utils.Replace(queryFile, Properties.gdemURL + "/" + Constants.QUERIES_FOLDER,
                                Properties.queriesFolder + File.separator);
            }
            else if (queryId != JOB_VALIDATION
                    && ! queryFile.startsWith(Properties.gdemURL + "/" + Constants.QUERIES_FOLDER)) {
                queryFile = Properties.queriesFolder + File.separator + queryFile;
            }

            long startTime4 = System.nanoTime();
            sourceURL = HttpFileManager.getSourceUrlWithTicket(getTicket(), sourceURL, isTrustedMode());
            long stopTime4 = System.nanoTime();

            long sourceSize = HttpFileManager.getSourceURLSize(getTicket(), originalSourceURL, isTrustedMode());
            LOGGER.info("### File with size=" + sourceSize + " Bytes has been downloaded. Download time in nanoseconds = " + (stopTime4 - startTime4) + ".");
            //save the job definition in the DB
            long startTime = System.nanoTime();
            jobId = xqJobDao.startXQJob(sourceURL, queryFile, resultFile, queryId ,scriptType);
            long stopTime = System.nanoTime();
            LOGGER.debug( jobId + " : " + sourceURL + " size: " + sourceSize );
            LOGGER.info("### Job with id=" + jobId + " has been created.  Job creation time in nanoseconds = " + (stopTime - startTime) + ".");
            long startTime1 = System.nanoTime();

            if (Properties.enableQuartz) {
                scheduleJob(jobId, sourceSize, scriptType);
                long stopTime1 = System.nanoTime();
                LOGGER.info("### Job with id=" + jobId + " has been scheduled. Scheduling time in nanoseconds = " + (stopTime1 - startTime1) + ".");
                getJobHistoryRepository().save(new JobHistoryEntry(jobId, Constants.XQ_RECEIVED, new Timestamp(new Date().getTime()), sourceURL, queryFile, resultFile, scriptType));
                LOGGER.info("Job with id #" + jobId + " has been inserted in table JOB_HISTORY ");
            } else {
                getJobHistoryRepository().save(new JobHistoryEntry(jobId, Constants.XQ_RECEIVED, new Timestamp(new Date().getTime()), sourceURL, queryFile, resultFile, scriptType));
                LOGGER.info("Job with id #" + jobId + " has been inserted in table JOB_HISTORY ");
                getRabbitMQMessageFactory().setJobId(jobId);
                getRabbitMQMessageFactory().createScriptAndSendMessageToRabbitMQ();
                long stopTime1 = System.nanoTime();
                LOGGER.info("### Job with id=" + jobId + " has been scheduled. Scheduling time in nanoseconds = " + (stopTime1 - startTime1) + ".");
            }
        } catch (SQLException e) {
            LOGGER.error("AnalyzeXMLFile:" , e);
            throw new XMLConvException(e.getMessage());
        } catch (SchedulerException | CreateMQMessageException e) {
            LOGGER.error("AnalyzeXMLFile:" , e);
            throw new XMLConvException(e.getMessage());
        } catch (URISyntaxException e) {
            LOGGER.error("AnalyzeXMLFile:" , e);
            throw new XMLConvException(e.getMessage());
        }
        return jobId;
    }

    /**
     *  Reschedule a job with quartz
     * @param JobID the id of the job
     */
    public void rescheduleJob(String JobID) throws SchedulerException, SQLException, XMLConvException {

        String[] jobData = xqJobDao.getXQJobData(JobID);
        String url = jobData[0];
        if(url.indexOf(Constants.GETSOURCE_URL)>0 && url.indexOf(Constants.SOURCE_URL_PARAM)>0) {
            int idx = url.indexOf(Constants.SOURCE_URL_PARAM);
            url = url.substring(idx + Constants.SOURCE_URL_PARAM.length() + 1);
        }

        String scriptType = jobData[8];

        long sourceSize = HttpFileManager.getSourceURLSize(getTicket(), url, isTrustedMode());

        JobDetail job1 = newJob(eionet.gdem.qa.XQueryJob.class)
                .withIdentity(JobID, "XQueryJob")
                .usingJobData("jobId", JobID )
                .requestRecovery()
                .build();

        // Define a Trigger that will fire "now", and not repeat
        Trigger trigger = newTrigger()
                .startNow()
                .build();

        // Reschedule the job
        // Heavy jobs go into a separate scheduler
        if (sourceSize > heavyJobThreshhold && ! scriptType.equals( XQScript.SCRIPT_LANG_FME ) ) {
            Scheduler quartzScheduler = getQuartzHeavyScheduler();
            quartzScheduler.scheduleJob(job1, trigger);
        }
        else {
            Scheduler quartzScheduler = getQuartzScheduler();
            quartzScheduler.scheduleJob(job1, trigger);
        }

    }

    /**
     *  Schedule a job with quartz
     * @param JobID the id of the job
     */
    public void scheduleJob (String JobID, long sizeInBytes, String scriptType ) throws SchedulerException {
        // ** Schedule the job with quartz to execute as soon as possibly.
        // only the job_id is needed for the job to be executed
        // Define an anonymous job
        JobDetail job1 = newJob(eionet.gdem.qa.XQueryJob.class)
                .withIdentity(JobID, "XQueryJob")
                .usingJobData("jobId", JobID )
                .requestRecovery()
                .build();

        // Define a Trigger that will fire "now", and not repeat
        Trigger trigger = newTrigger()
                .startNow()
                .build();

        // Schedule the job
        // Heavy jobs go into a separate scheduler
        if (sizeInBytes > heavyJobThreshhold && ! scriptType.equals( XQScript.SCRIPT_LANG_FME) ) {
            Scheduler quartzScheduler = getQuartzHeavyScheduler();
            quartzScheduler.scheduleJob(job1, trigger);
        }
        else {
            Scheduler quartzScheduler = getQuartzScheduler();
            quartzScheduler.scheduleJob(job1, trigger);
        }

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

        if (!isHTTPRequest() && LOGGER.isDebugEnabled()) {
            LOGGER.debug("ConversionService.convert method called through XML-rpc.");
        }
        RunQAScriptMethod runQaMethod = new RunQAScriptMethod();
        setGlobalParameters(runQaMethod);
        return runQaMethod.runQAScript(sourceUrl, scriptId);

    }

    private JobHistoryRepository getJobHistoryRepository() {
        return (JobHistoryRepository) SpringApplicationContext.getBean("jobHistoryRepository");
    }

    private RabbitMQMessageFactory getRabbitMQMessageFactory() {
        return (RabbitMQMessageFactory) SpringApplicationContext.getBean("createJob");
    }
}
