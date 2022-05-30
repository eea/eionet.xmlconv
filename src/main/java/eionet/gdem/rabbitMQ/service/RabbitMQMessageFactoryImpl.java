package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.JobNotFoundException;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.QueryJpaService;
import eionet.gdem.jpa.service.QueryMetadataService;
import eionet.gdem.logging.Markers;
import eionet.gdem.qa.IQueryDao;
import eionet.gdem.qa.QaScriptView;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.errors.CreateRabbitMQMessageException;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import eionet.gdem.services.JobHistoryService;
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
    /** Service for getting schema data. */
    private SchemaManager schemaManager;
    private IQueryDao queryDao;
    private JobHistoryService jobHistoryService;
    private JobService jobService;
    private QueryMetadataService queryMetadataService;
    private QueryJpaService queryJpaService;
    private DefineJobQueueAndSendToRabbitMQTemplate defineJobQueueByScriptAndScriptRules;

    private CdrResponseMessageFactoryService cdrResponseMessageFactoryService;

    @Autowired
    public RabbitMQMessageFactoryImpl(IQueryDao queryDao, JobHistoryService jobHistoryService, JobService jobService, QueryMetadataService queryMetadataService, QueryJpaService queryJpaService,
                                      DefineJobQueueAndSendToRabbitMQTemplate defineJobQueueByScriptAndScriptRules, CdrResponseMessageFactoryService cdrResponseMessageFactoryService) {
        this.queryDao = queryDao;
        this.jobHistoryService = jobHistoryService;
        this.queryJpaService = queryJpaService;
        this.defineJobQueueByScriptAndScriptRules = defineJobQueueByScriptAndScriptRules;
        this.jobService = jobService;
        this.queryMetadataService = queryMetadataService;
        this.cdrResponseMessageFactoryService = cdrResponseMessageFactoryService;
    }

    @Transactional(rollbackFor=Exception.class)
    public void createScriptAndSendMessageToRabbitMQ(String jobId) throws CreateRabbitMQMessageException {
        JobEntry jobEntry;
        try {
            schemaManager = new SchemaManager();
            jobEntry = getJobEntry(jobId);
            if (jobEntry == null) {
                throw new JobNotFoundException("Error during job entry retrieval from database for job " + jobId);
            }
            String srcFile = jobEntry.getUrl();
            String scriptFile = jobEntry.getFile();
            String resultFile = jobEntry.getResultFile();
            String queryID = jobEntry.getQueryId().toString();
            String scriptType = jobEntry.getScriptType();

            // Do validation
            if (queryID.equals(String.valueOf(Constants.JOB_VALIDATION))) {
                long startTime = System.nanoTime();
                Integer jobStatus = null;
                try {
                    // validate only the first XML Schema
                    if (scriptFile.contains(" ")) {
                        scriptFile = StringUtils.substringBefore(scriptFile, " ");
                    }
                    //change status to processing and add entry to job_history
                    processValidationJob(jobEntry);
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
                    long stopTime = System.nanoTime();

                    jobStatus = Constants.XQ_READY;
                    markValidationJobAsFinished(jobEntry, jobStatus);
                    LOGGER.info("### job with id: " + jobId + " has been Validated. Validation time in nanoseconds = " + (stopTime - startTime));
                } catch (Exception e) {
                    jobStatus = Constants.XQ_FATAL_ERR;
                    markValidationJobAsFinished(jobEntry, jobStatus);
                    handleError("Error during validation: " + ExceptionUtils.getRootCauseMessage(e), true,jobEntry, jobId);
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
                xq.setJobId(jobId);
                xq.setResulFile(resultFile);

                if (XQScript.SCRIPT_LANG_FME.equals(scriptType)) {
                    if (query != null && query.containsKey(QaScriptView.URL)) {
                        xq.setScriptSource((String) query.get(QaScriptView.URL));
                    }
                    if(query != null) {
                        String asynchronousExecution = (String) query.get(QaScriptView.ASYNCHRONOUS_EXECUTION);
                        if (asynchronousExecution != null && asynchronousExecution.equals("1")) {
                            xq.setAsynchronousExecution(true);
                        } else {
                            xq.setAsynchronousExecution(false);
                        }
                    }

                    LOGGER.info("** FME Job will be added in queue, ID=" + jobId + " params: " + xqParam[0] + " result will be stored to " + resultFile);
                } else {
                    LOGGER.info("** XQuery Job will be added in queue, ID=" + jobId + " params: " + xqParam[0] + " result will be stored to " + resultFile);
                }

                WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage = new WorkerJobRabbitMQRequestMessage(xq);
                QueryEntry queryEntry = null;
                if (jobEntry.getQueryId()!=0) {
                    queryEntry = queryJpaService.findByQueryId(Integer.parseInt(queryID));
                }
                //set jobExecutor=null in case job is restarted
                jobEntry.setJobExecutorName(null);
                defineJobQueueByScriptAndScriptRules.execute(queryEntry, jobEntry, workerJobRabbitMQRequestMessage);
            }
        } catch (Exception e) {
            throw new CreateRabbitMQMessageException(e.getMessage());
        }
    }

    private JobEntry getJobEntry(String jobId) {
        try {
            JobEntry jobEntry = jobService.findById(Integer.parseInt(jobId));
            if (jobEntry == null) {
                handleError("No such job: " + jobId, true,null, jobId);
                return null;
            }
            return jobEntry;
        } catch (Exception e) {
            handleError("Error getting WQ data from the DB: " + e.toString(), true,null, jobId);
            return null;
        }
    }

    void processValidationJob(JobEntry jobEntry) throws DatabaseException {
        try {
            Integer jobId = jobEntry.getId();
            LOGGER.info("Processing job with id " + jobId);
            InternalSchedulingStatus internalSchedulingStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_PROCESSING);
            jobEntry.setnStatus(Constants.XQ_PROCESSING).setIntSchedulingStatus(internalSchedulingStatus).setTimestamp(new Timestamp(new Date().getTime()));
            jobService.saveOrUpdate(jobEntry);
            JobHistoryEntry jobHistoryEntry = new JobHistoryEntry(jobId.toString(), Constants.XQ_PROCESSING, new Timestamp(new Date().getTime()), jobEntry.getUrl(), jobEntry.getFile(), jobEntry.getResultFile(), jobEntry.getScriptType());
            jobHistoryEntry.setIntSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_PROCESSING).setHeavy(false);
            jobHistoryEntry.setDuplicateIdentifier(jobEntry.getDuplicateIdentifier());
            jobHistoryEntry.setXmlSize(jobEntry.getXmlSize());
            jobHistoryEntry.setUuid(jobEntry.getUuid());
            jobHistoryEntry.setAddedFromQueue(jobEntry.getAddedFromQueue());
            jobHistoryService.save(jobHistoryEntry);
            LOGGER.info("Job with id=" + jobId + " has been inserted in table JOB_HISTORY ");
            if(jobEntry.getAddedFromQueue()) {
                cdrResponseMessageFactoryService.createCdrResponseMessageAndSendToQueue(jobEntry);
            }
        } catch (Exception e) {
            LOGGER.error("Database exception when processing validation job with id " + jobEntry.getId() + " Exception message is: " + e.toString());
            throw e;
        }
    }

    void markValidationJobAsFinished(JobEntry jobEntry, Integer status) throws DatabaseException {
        try {
            Integer jobId = jobEntry.getId();
            LOGGER.info("Job with id " + jobId + " has finished with status " + status);
            changeStatus(status, jobId.toString());
            JobHistoryEntry jobHistoryEntry = new JobHistoryEntry(jobId.toString(), status, new Timestamp(new Date().getTime()), jobEntry.getUrl(), jobEntry.getFile(), jobEntry.getResultFile(), jobEntry.getScriptType());
            jobHistoryEntry.setIntSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_PROCESSING).setHeavy(false);
            jobHistoryEntry.setDuplicateIdentifier(jobEntry.getDuplicateIdentifier());
            jobHistoryEntry.setXmlSize(jobEntry.getXmlSize());
            jobHistoryEntry.setUuid(jobEntry.getUuid());
            jobHistoryEntry.setAddedFromQueue(jobEntry.getAddedFromQueue());
            jobHistoryService.save(jobHistoryEntry);
            LOGGER.info("Job with id=" + jobId + " has been inserted in table JOB_HISTORY ");
            if(jobEntry.getAddedFromQueue()) {
                jobEntry.setnStatus(status);
                cdrResponseMessageFactoryService.createCdrResponseMessageAndSendToQueue(jobEntry);
            }
        } catch (Exception e) {
            LOGGER.error("Database exception when marking validation job as finished with id " + jobEntry.getId() + " Exception message is: " + e.toString());
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
    private void handleError(String error, boolean fatal,JobEntry jobEntry, String jobId) {
        LOGGER.error("Error handling started: <<< " + error + " >>> ");
        try {
            int errStatus;
            if (fatal) {
                errStatus = Constants.XQ_FATAL_ERR;
            } else {
                errStatus = Constants.XQ_LIGHT_ERR;
            }
            String resultFile = null;
            if (jobEntry!=null) {
                resultFile = jobEntry.getResultFile();
            }
            // if result file already ok, store the error message in the file:
            if (resultFile == null) {
                resultFile = Properties.tmpFolder + File.separatorChar + "gdem_error" + jobId + ".txt";
            }
            LOGGER.info("******* The error message is stored to: " + resultFile);
            if (error == null) {
                error = "No error message for job=" + jobEntry.getId();
            }
            Utils.saveStrToFile(resultFile, "<error>" + error + "</error>", null);
            changeStatus(errStatus,jobId);
            if(jobEntry.getAddedFromQueue()) {
                cdrResponseMessageFactoryService.createCdrResponseMessageAndSendToQueue(jobEntry);
            }
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
    void changeStatus(int status,String jobId) throws DatabaseException {
        jobService.changeNStatus(Integer.parseInt(jobId), status);
    }

}
