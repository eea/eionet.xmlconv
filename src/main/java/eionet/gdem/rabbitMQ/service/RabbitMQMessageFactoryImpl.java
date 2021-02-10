package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.dto.Schema;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.logging.Markers;
import eionet.gdem.qa.IQueryDao;
import eionet.gdem.qa.QaScriptView;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.errors.CreateMQMessageException;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.JaxpValidationService;
import eionet.gdem.validation.ValidationService;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("createJob")
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

    private IXQJobDao xqJobDao;
    private IQueryDao queryDao;
    private JobHistoryRepository jobHistoryRepository;
    private WorkersJobMessageSender workersJobMessageSender;

    @Autowired
    public RabbitMQMessageFactoryImpl(IXQJobDao xqJobDao, IQueryDao queryDao, @Qualifier("jobHistoryRepository") JobHistoryRepository jobHistoryRepository,
                                      WorkersJobMessageSender workersJobMessageSender) {
        this.xqJobDao = xqJobDao;
        this.queryDao = queryDao;
        this.jobHistoryRepository = jobHistoryRepository;
        this.workersJobMessageSender = workersJobMessageSender;
    }

    public void createScriptAndSendMessageToRabbitMQ(String jobId) throws CreateMQMessageException {
        try {
            this.setJobId(jobId);
            schemaManager = new SchemaManager();
            init();
            String srcFile = url;

            // update the Job status on db
            processJob();

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
            }
        } catch (Exception e) {
            throw new CreateMQMessageException(e.getMessage());
        }
    }

    private void init() {
        try {
            String[] jobData = xqJobDao.getXQJobData(jobId);
            if (jobData == null) {
                handleError("No such job: " + jobId, true);
                return;
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

    private void processJob() throws SQLException {
        try {
            xqJobDao.processXQJob(jobId);
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
    private void changeStatus(int status) throws Exception {
        try {
            xqJobDao.changeJobStatus(jobId, status);

            jobHistoryRepository.save(new JobHistoryEntry(jobId, status, new Timestamp(new Date().getTime()), url, scriptFile, resultFile, scriptType));
            LOGGER.info("Job with id=" + jobId + " has been inserted in table JOB_HISTORY ");
            if (status == 3)
                LOGGER.info("### Job with id=" + jobId + " has changed status to " + Constants.JOB_READY + ".");
            else if (status == 7)
                LOGGER.info("### Job with id=" + jobId + " has changed status to " + Constants.XQ_INTERRUPTED + ".");
            else
                LOGGER.info("### Job with id=" + jobId + " has changed status to " + Constants.XQ_FATAL_ERR + ".");
        } catch (Exception e) {
            LOGGER.error("Database exception when changing job status. " + e.toString());
            throw e;
        }
    }

    private void setJobId(String id) {
        this.jobId = id;
    }
}