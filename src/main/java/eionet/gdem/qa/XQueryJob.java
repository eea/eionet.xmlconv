package eionet.gdem.qa;

/*
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import eionet.gdem.XMLConvException;
import eionet.gdem.logging.Markers;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;



import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.conversion.datadict.DataDictUtil;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IQueryDao;
import eionet.gdem.services.db.dao.IXQJobDao;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.ValidationService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XQuery job in the workqueue. A task executing the XQuery task and storing the results of processing.
 */
public class XQueryJob implements Job {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(XQueryJob.class);
    /** Script file name. */
    private String scriptFile;
    /** Result file name. */
    private String resultFile;
    /** Job ID to be executed. */
    private String jobId;

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    /** query ID to be executed. */
    private String queryID;
    /** Script type */
    private String scriptType;
    /** Source url for XML. */
    private String url;
    /** Dao for getting job data. */
    private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();
    /** Dao for getting query data. */
    private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
    /** Service for getting schema data. */
    private SchemaManager schemaManager;

    public XQueryJob() {

    }

    /**
     * Run XQuery script: steps: - download the source from URL - run XQuery - store the result in a text file.
     */
    @Override
    public void execute(JobExecutionContext paramJobExecutionContext) throws JobExecutionException {
        try {
            
            LOGGER.info("Job ID=  " + jobId + " started getting source file.");
            schemaManager = new SchemaManager();
            initVariables();
            
            String srcFile = null;
            srcFile = url;

            // status to -processing
            changeStatus(Constants.XQ_PROCESSING);

            // Do validation
            if (queryID.equals(String.valueOf(Constants.JOB_VALIDATION))) {
                LOGGER.info("Job ID=" + jobId + " Validation started");

                try {
					// validate only the first XML Schema
                    if (scriptFile.contains(" ")) {
                        scriptFile = StringUtils.substringBefore(scriptFile, " ");
                    }
                    LOGGER.info("** XQuery starts, ID=" + jobId + " schema: " + scriptFile + " result will be stored to "
                            + resultFile);
                    ValidationService vs = new ValidationService();

                    // XML Schema should be in schemaLocation attribute
                    String result = vs.validateSchema(srcFile, scriptFile);

                    LOGGER.debug("Validation proceeded, now store to the result file");

                    Utils.saveStrToFile(resultFile, result, null);
                } catch (Exception e) {
                    handleError("Error during validation:" + e.toString(), true);
                    return;
                }
            } else {
                // Do xq job
                LOGGER.info("Job ID=" + jobId + " XQ processing started");

                // read query info from DB.
                Map query = getQueryInfo(queryID);
                String contentType = null;
                Schema schema = null;
                boolean schemaExpired = false;
                boolean isNotLatestReleasedDDSchema = false;

                if (query != null && query.containsKey("content_type")) {
                    contentType = (String) query.get("content_type");
                }
                // get script type if it comes from T_QUERY table
                if (query != null && query.containsKey("script_type")) {
                    scriptType = (String) query.get("script_type");
                }

                // stylesheet - to check if it is expired
                if (query != null && query.containsKey("xml_schema")) {
                    // set schema if exists:
                    schema = getSchema((String) query.get("xml_schema"));
                    schemaExpired = (schema != null && schema.isExpired());
                    isNotLatestReleasedDDSchema = DataDictUtil.isDDSchemaAndNotLatestReleased(schema.getSchema());

                }

                // get script type if it stored in filesystem and we have to
                // guess it by file extension
                if (Utils.isNullStr(scriptType)) {
                    scriptType =
                            scriptFile.endsWith(XQScript.SCRIPT_LANG_XSL) ? XQScript.SCRIPT_LANG_XSL
                                    : scriptFile.endsWith(XQScript.SCRIPT_LANG_XGAWK) ? XQScript.SCRIPT_LANG_XGAWK
                                            : XQScript.SCRIPT_LANG_XQUERY1;
                }
                String[] xqParam = {Constants.XQ_SOURCE_PARAM_NAME + "=" + srcFile};

                try {
                    if (scriptFile.contains(" ")) {
                        scriptFile = StringUtils.substringBefore(scriptFile, " ");
                    }
                    LOGGER.info("** XQuery starts, ID=" + jobId + " params: " + (xqParam == null ? "<< no params >>" : xqParam[0])
                            + " result will be stored to " + resultFile);
                    LOGGER.debug("Script: \n" + scriptFile);
                    XQScript xq = new XQScript(null, xqParam, contentType);
                    xq.setScriptFileName(scriptFile);
                    xq.setScriptType(scriptType);
                    xq.setSrcFileUrl(srcFile);
                    xq.setSchema(schema);

                    if (XQScript.SCRIPT_LANG_FME.equals(scriptType)) {
                        if (query != null && query.containsKey("url")) {
                            xq.setScriptSource((String) query.get("url"));
                        }
                    }

                    FileOutputStream out = null;
                    try {
                        // if result type is HTML and schema is expired parse
                        // result (add warning) before writing to file
                        if ((schemaExpired || isNotLatestReleasedDDSchema) && contentType.equals(XQScript.SCRIPT_RESULTTYPE_HTML)) {
                            String res = xq.getResult();
                            Utils.saveStrToFile(resultFile, res, null);
                        } else {
                            out = new FileOutputStream(new File(resultFile));
                            xq.getResult(out);
                        }
                    } catch (IOException ioe) {
                        throw new XMLConvException(ioe.toString());
                    } catch (XMLConvException e) {
                        // store error in feedback, it could be XML processing error
                        StringBuilder errBuilder = new StringBuilder();
                        errBuilder.append("<div class=\"feedbacktext\"><h2>Unexpected error occured!</h2>");
                        errBuilder.append(Utils.escapeXML(e.toString()));
                        errBuilder.append("</div>");
                        IOUtils.write(errBuilder.toString(), out);
                    } finally {
                        IOUtils.closeQuietly(out);
                    }

                    LOGGER.debug("Script proceeded, now store to the result file");

                } catch (Exception e) {
                    handleError("Error processing QA script:" + e.toString(), true);
                    return;

                }
            }

            changeStatus(Constants.XQ_READY);

            // TODO: Change to failed if script has failed
            LOGGER.info("Job ID=" + jobId + " succeeded");

            // all done, thread stops here, job is waiting for pulling from the
            // client side

        } catch (Exception ee) {
            handleError("Error in thread run():" + ee.toString(), true);
        }
    }

    /**
     * Read data from the DB where it is stored for further processing.
     */
    private void initVariables() {
        try {
            String[] jobData = xqJobDao.getXQJobData(jobId);
            if (jobData == null) {
                handleError("No such job: " + jobId, true);
            }
            url = jobData[0];
            scriptFile = jobData[1];
            resultFile = jobData[2]; // just a file name, file is not created
            queryID = jobData[5];
            scriptType = jobData[8];
        } catch (SQLException sqe) {
            handleError("Error getting WQ data from the DB: " + sqe.toString(), true);
        }
    }

    /**
     * Changes the status to ERROR and finishes the thread normally saves the error message as the result of the job.
     * @param error Error message.
     * @param fatal True if the error is fatal and there is no result.
     */
    private void handleError(String error, boolean fatal) {
        LOGGER.error("Error handling started: <<< " + error + " >>> ");
        try {
            int errStatus;
            if (fatal) {
                errStatus = Constants.XQ_FATAL_ERR;
            } else {
                errStatus = Constants.XQ_LIGHT_ERR;
            }

            changeStatus(errStatus);

            // if result file already ok, store the error message in the file:
            if (resultFile == null) {
                resultFile = Properties.tmpFolder + File.separatorChar + "gdem_error" + jobId + ".txt";
            }

            LOGGER.info("******* The error message is stored to: " + resultFile);

            if (error == null) {
                error = "No error message for job=" + jobId;
            }

            Utils.saveStrToFile(resultFile, "<error>" + error + "</error>", null);

        } catch (Exception e) {
            // what to do if exception occurs here...
            LOGGER.error(Markers.fatal, "** Error occurred when handling XQ error: " + e.toString());
        }
    }

    /**
     * Change job status in DB.
     * @param status Job status to be stored in DB.
     * @throws Exception Unable to store data into DB.
     */
    private void changeStatus(int status) throws Exception {
        try {
            xqJobDao.changeJobStatus(jobId, status);
        } catch (Exception e) {
            LOGGER.error("Database exception when changing job status. " + e.toString());
            throw e;
        }
    }

    /**
     * Loads Query info from database.
     *
     * @param id Query Id to be queried from DB.
     * @return Map with query data.
     */
    private Map getQueryInfo(String id) {
        Map query = null;
        if (id != null) {
            try {
                query = queryDao.getQueryInfo(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return query;
    }

    /**
     * Query Schema information from DB.
     * @param schemaUrl Schema Url or unique ID in DB to be queried.
     * @return Schema object.
     */
    private Schema getSchema(String schemaUrl) {
        try {
            if (schemaUrl != null) {
                String schemaId = schemaManager.getSchemaId(schemaUrl);
                if (schemaId != null) {
                    Schema schema = schemaManager.getSchema(schemaId);
                    return schema;
                }
            }
        } catch (Exception e) {
            LOGGER.error("getSchema() error : " + e.toString());
        }

        return null;

    }

}
