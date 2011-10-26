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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.Constants;
import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dcm.business.SourceFileManager;
import eionet.gdem.dcm.remote.RemoteService;
import eionet.gdem.dto.Schema;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IConvTypeDao;
import eionet.gdem.services.db.dao.IQueryDao;
import eionet.gdem.services.db.dao.IXQJobDao;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.ValidationService;

/**
 * QA Service Service Facade. The service is able to execute different QA related methods that are called through XML/RPC and HTTP
 * POST and GET.
 *
 * @author Enriko Käsper
 */
public class XQueryService extends RemoteService{

    private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
    private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();
    private IConvTypeDao convTypeDao = GDEMServices.getDaoService().getConvTypeDao();

    private SchemaManager schManager = new SchemaManager();

    /** */
    private static final Log LOGGER = LogFactory.getLog(XQueryService.class);

    public XQueryService() {
        // for remote clients use trusted mode
        setTrustedMode(true);
    }

    /**
     * List all possible XQueries for this namespace
     */
    public Vector listQueries(String schema) throws GDEMException {

        ListQueriesMethod method = new ListQueriesMethod();
        Vector v = method.listQueries(schema);
        return v;
    }

    /**
     * List all XQueries and their modification times for this namespace returns also XML Schema validation
     */
    public Vector listQAScripts(String schema) throws GDEMException {
        ListQueriesMethod method = new ListQueriesMethod();
        Vector v = method.listQAScripts(schema);
        return v;
    }

    /**
     * Request from XML/RPC client Stores the source files and starts a job in the workqueue
     *
     * @param Hashtable
     *            files: Structure with XMLschemas as a keys and values are list of XML Files
     * @return Hashtable result: Structure with JOB ids as a keys and source files as values
     */
    public Vector analyzeXMLFiles(Hashtable files) throws GDEMException {

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
     * Stores one source file and starts a job in the workqueue
     *
     * @param String
     *            schema: XML Schema URL
     * @param String
     *            file: Source file URL
     * @return Hashtable result: Structure with JOB ids as a keys and source files as values
     */
    // public Hashtable analyze(String schema, String file) throws GDEMException{
    // return analyze(schema,file, null);
    // }
    public Vector analyzeXMLFiles(String schema, String orig_file, Vector result) throws GDEMException {

        LOGGER.info("XML/RPC call for analyze xml: " + orig_file);

        if (result == null) {
            result = new Vector();
        }
        Vector outputTypes = null;
        // get all possible xqueries from db
        String newId = "-1"; // should not be returned with value -1;
        String file = orig_file;

        Vector _queries = listQueries(schema);

        try {
            outputTypes = convTypeDao.getConvTypes();
        } catch (SQLException sqe) {
            throw new GDEMException("DB operation failed: " + sqe.toString());
        }

        try {
            // get the trusted URL from source file adapter
            file = SourceFileManager.getSourceFileAdapterURL(getTicket(), file, isTrustedMode());
        } catch (Exception e) {
            String err_mess = "File URL is incorrect";
            LOGGER.error(err_mess + "; " + e.toString());
            throw new GDEMException(err_mess, e);
        }

        if (!Utils.isNullVector(_queries)) {

            for (int j = 0; j < _queries.size(); j++) {
                Hashtable _querie = (Hashtable) _queries.get(j);
                String query_id = String.valueOf(_querie.get("query_id"));
                String query_file = (String) _querie.get("query");
                String content_type = (String) _querie.get("content_type_id");
                String fileExtension = getExtension(outputTypes, content_type);
                String resultFile =
                    Properties.tmpFolder + "gdem_q" + query_id + "_" + System.currentTimeMillis() + "." + fileExtension;
                try {
                    int int_qID = 0;
                    try {
                        int_qID = Integer.parseInt(query_id);
                    } catch (NumberFormatException n) {
                        int_qID = 0;
                    }
                    // if it is a XQuery script, then append the system folder
                    if (int_qID != Constants.JOB_VALIDATION && query_file.startsWith(Properties.gdemURL + "/" + Constants.QUERIES_FOLDER)) {
                        query_file =
                            Utils.Replace(query_file, Properties.gdemURL + "/" + Constants.QUERIES_FOLDER,
                                    Properties.queriesFolder);
                    }
                    newId = xqJobDao.startXQJob(file, query_file, resultFile, int_qID);
                } catch (SQLException sqe) {
                    throw new GDEMException("DB operation failed: " + sqe.toString());
                }
                Vector _res = new Vector();
                _res.add(newId);
                _res.add(orig_file);
                result.add(_res);
            }
        }

        LOGGER.info("Analyze xml result: " + result.toString());
        return result;
    }

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
     * Request from XML/RPC client Stores the xqScript and starts a job in the workqueue
     *
     * @param String
     *            url: URL of the srouce XML
     * @param String
     *            xqScript: XQueryScript to be processed
     * @param String
     *            scriptType: xquery, xsl or xgawk
     */
    public String analyze(String sourceURL, String xqScript, String scriptType) throws GDEMException {
        String xqFile = "";

        LOGGER.info("XML/RPC call for analyze xml: " + sourceURL);
        // save XQScript in a text file for the WQ
        try {
            xqFile = Utils.saveStrToFile(xqScript, scriptType);
        } catch (FileNotFoundException fne) {
            throw new GDEMException("Folder does not exist: :" + fne.toString());
        } catch (IOException ioe) {
            throw new GDEMException("Error storing XQScript into file:" + ioe.toString());
        }

        // name for temporary output file where the esult is stored:
        String resultFile = Properties.tmpFolder + "gdem_" + System.currentTimeMillis() + ".html";
        String newId = "-1"; // should not be returned with value -1;

        // start a job in the Workqueue
        try {
            // get the trusted URL from source file adapter
            sourceURL = SourceFileManager.getSourceFileAdapterURL(getTicket(), sourceURL, isTrustedMode());
            newId = xqJobDao.startXQJob(sourceURL, xqFile, resultFile);

        } catch (SQLException sqe) {
            sqe.printStackTrace();
            LOGGER.error("DB operation failed: " + sqe.toString());
            throw new GDEMException("DB operation failed: " + sqe.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            LOGGER.error("Source file URL is wrong: " + e.toString());
            throw new GDEMException("Source file URL is wrong: " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Error opening source file: " + e.toString());
            throw new GDEMException("Error opening source file: " + e.toString());
        }
        return newId;
    }

    /**
     * Checks if the job is ready (or error) and returns the result (or error message)
     *
     * @param String
     *            jobId
     * @return String fileName where the client can download the result Returns a Hash including code and result
     */
    public Hashtable getResult(String jobId) throws GDEMException {

        LOGGER.info("XML/RPC call for getting result with JOB ID: " + jobId);

        String[] jobData = null;
        HashMap scriptData = null;
        int status = 0;
        try {
            jobData = xqJobDao.getXQJobData(jobId);

            if (jobData == null) { // no such job
                // throw new GDEMException("** No such job with ID=" + jobId + " in the queue.");
                status = Constants.XQ_JOBNOTFOUND_ERR;
            } else {
                scriptData = queryDao.getQueryInfo(jobData[5]);

                status = Integer.valueOf(jobData[3]).intValue();
            }
        } catch (SQLException sqle) {
            throw new GDEMException("Error gettign XQJob data from DB: " + sqle.toString());
        }

        LOGGER.info("XQuerySrevice found status for job (" + jobId + "):" + String.valueOf(status));

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

    // Hashtable to be composed for the getResult() method return value
    private Hashtable result(int status, String[] jobData, HashMap scriptData, String jobId) throws GDEMException {
        Hashtable<String, String> h = new Hashtable<String, String>();
        int resultCode;
        String resultValue = "";
        String metatype = "";
        String script_title = "";

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
            }
            else {
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
        } catch (Exception e) {
            String err_mess =
                "JobID: " + jobId + "; Creating result Hashtable for getResult method failed result: " + e.toString();
            LOGGER.error(err_mess);
            throw new GDEMException(err_mess, e);
        }

        return h;

    }

    /**
     * Request from XML/RPC client running the QA script on the fly
     *
     * @param String
     *            url: URL of the srouce XML
     * @param String
     *            xqScript: XQueryScript ID or -1 (XML Schema validation) to be processed
     */
    public Vector runQAScript(String orig_file_url, String script_id) throws GDEMException {

        Vector result = new Vector();
        ByteArrayOutputStream outstream = null;
        String file_url = null;
        String content_type = "text/html";
        byte[] result_bytes;
        LOGGER.debug("==xmlconv== runQAScript: id=" + script_id + " file_url=" + orig_file_url + "; ");

        try {
            // get the trusted URL from source file adapter
            file_url = SourceFileManager.getSourceFileAdapterURL(getTicket(), orig_file_url, isTrustedMode());
        } catch (Exception e) {
            String err_mess = "File URL is incorrect";
            LOGGER.error(err_mess + "; " + e.toString());
            throw new GDEMException(err_mess, e);
        }
        if (script_id.equals(String.valueOf(Constants.JOB_VALIDATION))) {
            try {
                ValidationService vs = new ValidationService();
                String val_result = vs.validate(file_url);
                result_bytes = val_result.getBytes();
            } catch (Exception e) {
                String err_mess = "Could not execute runQAMethod";
                LOGGER.error(err_mess + "; " + e.toString());
                throw new GDEMException(err_mess, e);

            }
        } else {
            String[] pars = new String[1];
            pars[0] = Constants.XQ_SOURCE_PARAM_NAME + "=" + file_url;

            try {
                String xqScript = queryDao.getQueryText(script_id);
                HashMap hash = queryDao.getQueryInfo(script_id);
                String schemaId = (String) hash.get("schema_id");
                Schema schema = null;
                // check because ISchemaDao.getSchema(null) returns first schema
                if (schemaId != null) {
                    schema = schManager.getSchema(schemaId);
                }

                if (Utils.isNullStr(xqScript) || hash == null) {
                    String err_mess = "Could not find QA script with id: " + script_id;
                    LOGGER.error(err_mess);
                    throw new GDEMException(err_mess, new Exception());
                } else {
                    if (!Utils.isNullStr((String) hash.get("meta_type"))) {
                        content_type = (String) hash.get("meta_type");
                    }
                    outstream = new ByteArrayOutputStream();
                    XQScript xq = new XQScript(xqScript, pars, (String) hash.get("content_type"));
                    xq.setScriptType((String) hash.get("script_type"));
                    xq.setSrcFileUrl(file_url);
                    xq.setSchema(schema);

                    String xqResult = xq.getResult();

                    // xq.getResult(outstream);
                    // result_bytes = outstream.toByteArray();
                    result_bytes = xqResult.getBytes();

                }
            } catch (SQLException sqle) {
                throw new GDEMException("Error getting data from DB: " + sqle.toString());
            } catch (Exception e) {
                String err_mess = "Could not execute runQAMethod";
                LOGGER.error(err_mess + "; " + e.toString());
                throw new GDEMException(err_mess, e);
            } finally {
                if (outstream != null) {
                    try {
                        outstream.flush();
                        outstream.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
        result.add(content_type);
        result.add(result_bytes);
        return result;
    }

    public static void main(String args[]) {
        try {
            XQueryService xqs = new XQueryService();
            // Vector v =
            // xqs.listQAScripts("http://dd.eionet.eu.int/namespace.jsp?ns_id=10 http://dd.eionet.eu.int/GetSchema?id=TBL3227");
            Vector v = xqs.runQAScript("http://cdr.eionet.eu.int/at/eea/ewn1/envq2hsrw/ProxyPressures.xml", "15");
            // Hashtable h = xqs.getResult("383");
            String s = new String((byte[]) v.get(1), "UTF-8");
            System.out.println(s);
            // System.out.println("h.toString()");
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

}
