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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.Properties;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dcm.remote.RemoteService;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.qa.utils.ScriptUtils;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IConvTypeDao;
import eionet.gdem.services.db.dao.IQueryDao;
import eionet.gdem.services.db.dao.IXQJobDao;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.xml.FeedbackAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        LOGGER.info("XML/RPC call for analyze xml: " + origFile);

        if (result == null) {
            result = new Vector();
        }
        Vector outputTypes = null;
        // get all possible xqueries from db
        String newId = "-1"; // should not be returned with value -1;
        String file = origFile;

        Vector queries = listQueries(schema);

        try {
            outputTypes = convTypeDao.getConvTypes();
        } catch (SQLException sqe) {
            throw new XMLConvException("DB operation failed: " + sqe.toString());
        }

        try {
            // get the trusted URL from source file adapter
            file = HttpFileManager.getSourceUrlWithTicket(getTicket(), file, isTrustedMode());
        } catch (Exception e) {
            String err_mess = "File URL is incorrect";
            LOGGER.error(err_mess + "; " + e.toString());
            throw new XMLConvException(err_mess, e);
        }

        if (!Utils.isNullVector(queries)) {

            for (int j = 0; j < queries.size(); j++) {
                Hashtable query = (Hashtable) queries.get(j);
                String query_id = String.valueOf(query.get("query_id"));
                String queryFile = (String) query.get("query");
                String contentType = (String) query.get("content_type_id");
                String scriptType = (String) query.get("script_type");
                String fileExtension = getExtension(outputTypes, contentType);
                String resultFile =
                    Properties.tmpFolder + File.separatorChar + "gdem_q" + query_id + "_" + System.currentTimeMillis() + "."
                    + fileExtension;
                try {
                    int queryId = 0;
                    try {
                        queryId = Integer.parseInt(query_id);
                    } catch (NumberFormatException n) {
                        queryId = 0;
                    }
                    // if it is a XQuery script, then append the system folder
                    if (queryId != Constants.JOB_VALIDATION
                            && queryFile.startsWith(Properties.gdemURL + "/" + Constants.QUERIES_FOLDER)) {
                        queryFile =
                            Utils.Replace(queryFile, Properties.gdemURL + "/" + Constants.QUERIES_FOLDER,
                                    Properties.queriesFolder + File.separator);
                    }
                    newId = xqJobDao.startXQJob(file, queryFile, resultFile, queryId, scriptType);
                } catch (SQLException sqe) {
                    throw new XMLConvException("DB operation failed: " + sqe.toString());
                }
                Vector queryResult = new Vector();
                queryResult.add(newId);
                queryResult.add(origFile);
                result.add(queryResult);
            }
        }

        LOGGER.info("Analyze xml result: " + result.toString());
        return result;
    }

    /**
     * Gets file extension
     * @param outputTypes Output Types
     * @param content_type Content type
     * @return Extension
     */
    private String getExtension(Vector outputTypes, String content_type) {
        String ret = "html";
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

        LOGGER.info("XML/RPC call for analyze xml: " + sourceURL);
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
            newId = xqJobDao.startXQJob(sourceURL, xqFile, resultFile, scriptType);

        } catch (SQLException sqe) {
            LOGGER.error("DB operation failed: " + sqe.toString());
            throw new XMLConvException("DB operation failed: " + sqe.toString());
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
    private Hashtable result(int status, String[] jobData, HashMap scriptData, String jobId) throws XMLConvException {
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
                resultCode = Constants.JOB_LIGHT_ERROR;
            } else if (status == Constants.XQ_FATAL_ERR) {
                resultCode = Constants.JOB_FATAL_ERROR;
            } else {
                resultCode = -1; // not expected to reach here
            }

            try {
                int xq_id = 0;
                try {
                    xq_id = Integer.parseInt(jobData[5]);
                } catch (NumberFormatException n) {
                }

                if (xq_id == Constants.JOB_VALIDATION) {
                    metatype = "text/html";
                    script_title = "XML Schema validation";
                } else if (xq_id > 0) {
                    metatype = (String) scriptData.get("meta_type");
                    script_title = (String) scriptData.get("short_name");
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
}
