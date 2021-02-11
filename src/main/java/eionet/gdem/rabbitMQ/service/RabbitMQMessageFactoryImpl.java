package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.dto.Schema;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.jpa.repositories.JobRepository;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.logging.Markers;
import eionet.gdem.qa.IQueryDao;
import eionet.gdem.qa.QaScriptView;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.errors.CreateRabbitMQMessageException;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.JaxpValidationService;
import eionet.gdem.validation.ValidationService;
import eionet.gdem.web.spring.schemas.SchemaManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("rabbitMQMessageFactory")
public class RabbitMQMessageFactoryImpl implements RabbitMQMessageFactory {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQMessageFactoryImpl.class);
    /** Script file name. */
    private String scriptFile;
    /** Result file name. */
    private String resultFile;
    /** Job ID to be executed. */
    private String jobId;
    /** query ID to be executed. */
    private String queryID;
    /** Script type. */
    private String scriptType;
    /** Source url for XML. */
    private String url;
    /** Service for getting schema data. */
    private SchemaManager schemaManager;

    private IQueryDao queryDao;
    private JobHistoryRepository jobHistoryRepository;
    private WorkersJobMessageSender workersJobMessageSender;
    private JobRepository jobRepository;
    private JobService jobService;

    @Autowired
    public RabbitMQMessageFactoryImpl(IQueryDao queryDao, @Qualifier("jobHistoryRepository") JobHistoryRepository jobHistoryRepository,
                                      WorkersJobMessageSender workersJobMessageSender, @Qualifier("jobRepository") JobRepository jobRepository, JobService jobService) {
        this.queryDao = queryDao;
        this.jobHistoryRepository = jobHistoryRepository;
        this.workersJobMessageSender = workersJobMessageSender;
        this.jobRepository = jobRepository;
        this.jobService = jobService;
    }

    @Transactional
    public void createScriptAndSendMessageToRabbitMQ() throws CreateRabbitMQMessageException {
        try {
            schemaManager = new SchemaManager();
            init();
            String srcFile = url;

            // Do validation
            if (queryID.equals(String.valueOf(Constants.JOB_VALIDATION))) {
                try {
                    long startTime = System.nanoTime();

                    // validate only the first XML Schema
                    if (scriptFile.contains(" ")) {
                        scriptFile = StringUtils.substringBefore(scriptFile, " ");
                    }
                    LOGGER.info("** XML Validation Job starting, ID=" + jobId + " schema: " + scriptFile + " result will be stored to " + resultFile);
                    ValidationService vs = new JaxpValidationService();
                    String query = StringUtils.defaultIfEmpty(new URI(srcFile).getQuery(), "");
                    List<NameValuePair> params = URLEncodedUtils.parse(query, StandardCharsets.UTF_8);
                    for (NameValuePair param : params) {
                        if (Constants.TICKET_PARAM.equals(param.getName())) {
                            //vs.setTicket(param.getValue());
                        }
                        if (Constants.SOURCE_URL_PARAM.equals(param.getName())) {
                            srcFile = param.getValue();
                        }
                    }
                    // XML Schema should be in schemaLocation attribute
                    String result = vs.validateSchema(srcFile, scriptFile);
                    LOGGER.debug("Validation proceeded, now store to the result file");
                    Utils.saveStrToFile(resultFile, result, null);
                    changeStatus(Constants.XQ_READY);
                    long stopTime = System.nanoTime();
                    LOGGER.info("### job with id: " + jobId + " has been Validated. Validation time in nanoseconds = " + (stopTime - startTime));
                } catch (Exception e) {
                    handleError("Error during validation: " + ExceptionUtils.getRootCauseMessage(e), true);
                }
            } else {

                // read query info from DB.
                Map query = getQueryInfo(queryID);
                String contentType = null;
                Schema schema = null;
                //boolean schemaExpired = false;
                //boolean isNotLatestReleasedDDSchema = false;

                if (query != null && query.containsKey(QaScriptView.CONTENT_TYPE)) {
                    contentType = (String) query.get(QaScriptView.CONTENT_TYPE);
                }
                // get script type if it comes from T_QUERY table
                if (query != null && query.containsKey(QaScriptView.SCRIPT_TYPE)) {
                    scriptType = (String) query.get(QaScriptView.SCRIPT_TYPE);
                }

                // stylesheet - to check if it is expired
                if (query != null && query.containsKey(QaScriptView.XML_SCHEMA)) {
                    // set schema if exists:
                    schema = getSchema((String) query.get(QaScriptView.XML_SCHEMA));
                    //schemaExpired = (schema != null && schema.isExpired());
                    //isNotLatestReleasedDDSchema = DataDictUtil.isDDSchemaAndNotLatestReleased(schema.getSchema());
                }

                // get script type if it stored in filesystem and we have to guess it by file extension.
                if (Utils.isNullStr(scriptType)) {
                    scriptType =
                            scriptFile.endsWith(XQScript.SCRIPT_LANG_XSL) ? XQScript.SCRIPT_LANG_XSL
                                    : scriptFile.endsWith(XQScript.SCRIPT_LANG_XGAWK) ? XQScript.SCRIPT_LANG_XGAWK
                                    : XQScript.SCRIPT_LANG_XQUERY1;
                }
                String[] xqParam = {Constants.XQ_SOURCE_PARAM_NAME + "=" + srcFile};

                if (scriptFile.contains(" ")) {
                    scriptFile = StringUtils.substringBefore(scriptFile, " ");
                }

                XQScript xq = new XQScript(null, xqParam, contentType);
                xq.setScriptFileName(scriptFile);
                xq.setScriptType(scriptType);
                xq.setSrcFileUrl(srcFile);
                xq.setSchema(schema);
                xq.setJobId(this.jobId);
                xq.setResulFile(resultFile);

                if (XQScript.SCRIPT_LANG_FME.equals(scriptType)) {
                    if (query != null && query.containsKey(QaScriptView.URL)) {
                        xq.setScriptSource((String) query.get(QaScriptView.URL));
                    }
                    LOGGER.info("** FME Job will be added in queue, ID=" + jobId + " params: " + xqParam[0] + " result will be stored to " + resultFile);
                } else {
                    LOGGER.info("** XQuery Job will be added in queue, ID=" + jobId + " params: " + xqParam[0] + " result will be stored to " + resultFile);
                }
                workersJobMessageSender.sendJobInfoToRabbitMQ(xq);
                processJob();
            }
        } catch (Exception e) {
            throw new CreateRabbitMQMessageException(e.getMessage());
        }
    }

    private void init() {
        try {
            JobEntry jobEntry = jobRepository.findById(Integer.parseInt(jobId));
            if (jobEntry == null) {
                handleError("No such job: " + jobId, true);
                return;
            }
            url = jobEntry.getUrl();
            scriptFile = jobEntry.getFile();
            resultFile = jobEntry.getResultFile(); // just a file name, file is not created
            queryID = jobEntry.getQueryId().toString();
            scriptType = jobEntry.getType();
        } catch (Exception e) {
            handleError("Error getting WQ data from the DB: " + e.toString(), true);
        }
    }

    void processJob() {
        try {
            Integer retryCounter = jobRepository.getRetryCounter(Integer.parseInt(jobId));
            jobRepository.updateJobInfo(Constants.XQ_PROCESSING, Properties.getHostname(), new Timestamp(new Date().getTime()), retryCounter + 1, Integer.parseInt(jobId));
            InternalSchedulingStatus intStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_QUEUED);
            jobRepository.updateInternalStatus(intStatus, Integer.parseInt(jobId));
            LOGGER.info("Updating job information of job with id " + jobId + " in table T_XQJOBS");
            jobHistoryRepository.save(new JobHistoryEntry(jobId, Constants.XQ_PROCESSING, new Timestamp(new Date().getTime()), url, scriptFile, resultFile, scriptType));
            LOGGER.info("Job with id=" + jobId + " has been inserted in table JOB_HISTORY ");
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
                    return schemaManager.getSchema(schemaId);
                }
            }
        } catch (Exception e) {
            LOGGER.error("getSchema() error : " + e.toString());
        }
        return null;
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
            LOGGER.error(Markers.FATAL, "** Error occurred when handling XQ error: " + e.toString());
        }
    }

    /**
     * Change job status in DB.
     * @param status Job status to be stored in DB.
     * @throws Exception Unable to store data into DB.
     */
     void changeStatus(int status) throws Exception {
         XQScript script = new XQScript();
         script.setJobId(jobId);
         script.setSrcFileUrl(url);
         script.setScriptFileName(scriptFile);
         script.setStrResultFile(resultFile);
         script.setScriptType(scriptType);
         jobService.changeNStatus(script, status);
    }

    @Override
    public void setJobId(String id) {
        this.jobId = id;
    }
}
