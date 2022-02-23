package eionet.gdem.qa;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SpringApplicationContext;
import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.dcm.remote.RemoteServiceMethod;
import eionet.gdem.dto.Schema;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.repositories.JobRepository;
import eionet.gdem.qa.utils.ScriptUtils;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.JobOnDemandHandlerService;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.xml.FeedbackAnalyzer;
import eionet.gdem.validation.JaxpValidationService;
import eionet.gdem.validation.ValidationService;
import eionet.gdem.web.spring.schemas.SchemaManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Vector;

/**
 * Implementation of run ad-hoc QA script methods.
 *
 * @author Enriko Käsper, TripleDev
 */
public class RunQAScriptMethod extends RemoteServiceMethod {

    /**
     * Query ID property key in ListQueries method result.
     */
    public static final String KEY_QUERY_ID = "query_id";
    /**
     * Query file property key in ListQueries method result.
     */
    public static final String KEY_QUERY = "query";
    /**
     * Query short name property key in ListQueries method result.
     */
    public static final String KEY_SHORT_NAME = "short_name";
    /**
     * Query description property key in ListQueries method result.
     */
    public static final String KEY_DESCRIPTION = "description";
    /**
     * Schema ID property key in ListQueries method result.
     */
    public static final String KEY_SCHEMA_ID = "schema_id";
    /**
     * Schema URL property key in ListQueries method result.
     */
    public static final String KEY_XML_SCHEMA = "xml_schema";
    /**
     * Type property key in ListQueries method result.
     */
    public static final String KEY_TYPE = "type";
    /**
     * Output content type property key in ListQueries method result.
     */
    public static final String KEY_CONTENT_TYPE_OUT = "content_type_out";
    /**
     * Output content type ID property key in ListQueries method result.
     */
    public static final String KEY_CONTENT_TYPE_ID = "content_type_id";
    /**
     * XML file upper limit property key in ListQueries method result.
     */
    public static final String KEY_UPPER_LIMIT = "upper_limit";
    /**
     * Upper limit for xml file size to be sent to manual QA.
     */
    public static final int VALIDATION_UPPER_LIMIT = Properties.qaValidationXmlUpperLimit;

    /**
     * QA script default output content type.
     */
    public static final String DEFAULT_CONTENT_TYPE = "text/html";

    /**
     * Business logic class for XML Schemas.
     */
    private SchemaManager schManager = new SchemaManager();
    /**
     * DAO for getting query info.
     */
    private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(RunQAScriptMethod.class);

    /**
     * time in milliseconds (2 minutes)
     */
    private static final int TIME_INTERVAL_FOR_JOB_STATUS = 120000;

    /**
     * Remote method for running the QA script on the fly.
     *
     * @param sourceUrl URL of the source XML
     * @param scriptId  XQueryScript ID or -1 (XML Schema validation) to be processed
     * @return Vector of 2 fields: content type and byte array
     * @throws XMLConvException in case of business logic error
     */
    public Vector runQAScript(String sourceUrl, String scriptId) throws XMLConvException {
        StopWatch timer = new StopWatch();
        Vector result = new Vector();
        String fileUrl;
        String contentType = DEFAULT_QA_CONTENT_TYPE;
        String strResult = "";
        LOGGER.debug("==xmlconv== runQAScript: id=" + scriptId + " file_url=" + sourceUrl + "; ");
        String jobId = null;
        try {
            if (scriptId.equals(String.valueOf(Constants.JOB_VALIDATION))) {
                ValidationService vs = new JaxpValidationService();
                //vs.setTicket(getTicket());
                strResult = vs.validate(sourceUrl);
            } else {
                fileUrl = HttpFileManager.getSourceUrlWithTicket(getTicket(), sourceUrl, isTrustedMode());
                String[] pars = new String[1];
                pars[0] = Constants.XQ_SOURCE_PARAM_NAME + "=" + fileUrl;
                try {
                    HashMap hash = queryDao.getQueryInfo(scriptId);
                    String xqScript = "";
                    // If the script type is not FME, the script content is retrieved.
                    if (!XQScript.SCRIPT_LANG_FME.equals((String) hash.get(QaScriptView.SCRIPT_TYPE))) {
                        xqScript = queryDao.getQueryText(scriptId);
                    } else {
                        xqScript = XQScript.SCRIPT_LANG_FME; // Dummy value
                    }
                    String schemaId = (String) hash.get(QaScriptView.SCHEMA_ID);
                    Schema schema = null;
                    // check because ISchemaDao.getSchema(null) returns first schema
                    if (schemaId != null) {
                        schema = schManager.getSchema(schemaId);
                    }

                    if (Utils.isNullStr(xqScript) || hash == null) {
                        String errMess = "Could not find QA script with id: " + scriptId;
                        LOGGER.error(errMess);
                        throw new XMLConvException(errMess, new Exception());
                    } else {
                        if (!Utils.isNullStr((String) hash.get(QaScriptView.META_TYPE))) {
                            contentType = (String) hash.get(QaScriptView.META_TYPE);
                        }
                        LOGGER.debug("Script: " + xqScript);
                        XQScript xq = new XQScript(xqScript, pars, (String) hash.get(QaScriptView.CONTENT_TYPE));
                        xq.setScriptType((String) hash.get(QaScriptView.SCRIPT_TYPE));
                        xq.setSrcFileUrl(fileUrl);
                        xq.setSchema(schema);

                        if (XQScript.SCRIPT_LANG_FME.equals(xq.getScriptType())) {
                            xq.setScriptSource((String) hash.get(QaScriptView.URL));
                            Boolean asynchronousExecution = false;
                            try {
                                asynchronousExecution = queryDao.getAsynchronousExecution(scriptId);
                                if (asynchronousExecution != null){
                                    xq.setAsynchronousExecution(asynchronousExecution);
                                }
                                else{
                                    xq.setAsynchronousExecution(false);
                                }
                            }
                            catch(Exception e){
                                xq.setAsynchronousExecution(false);
                            }
                        }

                        String scriptFile = (String) hash.get(QaScriptView.QUERY);

                        String resultFile = Properties.tmpFolder + File.separatorChar + "gdem_" + System.currentTimeMillis() + "." + xq.getOutputType().toLowerCase();
                        xq.setStrResultFile(resultFile);
                        xq.setScriptFileName(Properties.queriesFolder + File.separator + scriptFile);

                        JobEntry jobEntry = getJobOnDemandHandlerService().createJobAndSendToRabbitMQ(xq, Integer.parseInt(scriptId), true);
                        LOGGER.info("Job with id " + jobEntry.getId() + " was created to handle xmlrpc/rest call.");
                        jobId = jobEntry.getId().toString();

                        timer.start();
                        while (jobEntry.getnStatus() != Constants.XQ_READY && jobEntry.getnStatus() != Constants.XQ_FATAL_ERR) {
                            if (timer.getTime()>Properties.jobsOnDemandLimitBeforeTimeout) {
                                throw new XMLConvException("Time exceeded for getting status of job with id " + jobEntry.getId());
                            }
                            Thread.sleep(TIME_INTERVAL_FOR_JOB_STATUS);
                            Optional<JobEntry> jobEntryOptional = getJobRepository().findById(jobEntry.getId());
                            if(jobEntryOptional.isPresent()){
                                jobEntry = jobEntryOptional.get();
                            }
                            else{
                                jobEntry = null;
                                throw new XMLConvException("Error getting data from DB");
                            }
                        }
                        String jobResult = jobEntry.getResultFile();
                        File file = new File(jobResult);
                        strResult = FileUtils.readFileToString(file, "UTF-8");
                    }
                } catch (SQLException sqle) {
                    throw new XMLConvException("Error getting data from DB: " + sqle.toString());
                } catch (Exception e) {
                    String errMess = "Could not execute runQAMethod";
                    LOGGER.error(errMess + "; " + e.toString(), e);
                    throw new XMLConvException(errMess, e);
                } finally {
                    timer.stop();
                }
            }
            if (isHttpRequest()) {
                try {
                    HttpMethodResponseWrapper httpResponse = getHttpResponse();
                    httpResponse.setContentType(contentType);
                    httpResponse.setCharacterEncoding("UTF-8");
                    httpResponse.setContentDisposition("qaresult.xml");
                    OutputStream outstream = httpResponse.getOutputStream();
                    IOUtils.write(strResult, outstream, "UTF-8");
                } catch (IOException e) {
                    LOGGER.error("Error getting response outputstream ", e);
                    throw new XMLConvException("Error getting response outputstream " + e.toString(), e);
                }
            } else {
                result.add(contentType);
                result.add(strResult.getBytes());

                HashMap<String, String> fbResult = FeedbackAnalyzer.getFeedbackResultFromStr(strResult);
                result.add(fbResult.get(Constants.RESULT_FEEDBACKSTATUS_PRM).getBytes());
                result.add((fbResult.get(Constants.RESULT_FEEDBACKMESSAGE_PRM).getBytes()));
                if(jobId != null) {
                    //added JobId to the vector in order to find if result is empty. Refs #142711
                    result.add(jobId);
                }

            }
        } catch (XMLConvException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

    private JobOnDemandHandlerService getJobOnDemandHandlerService() {
        return (JobOnDemandHandlerService) SpringApplicationContext.getBean("jobOnDemandHandlerService");
    }

    private JobRepository getJobRepository() {
        return (JobRepository) SpringApplicationContext.getBean("jobRepository");
    }
}
