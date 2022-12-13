package eionet.gdem.api.qa.service.impl;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.dto.Schema;
import eionet.gdem.jpa.Entities.*;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.*;
import eionet.gdem.qa.QaScriptView;
import eionet.gdem.qa.QueryService;
import eionet.gdem.rabbitMQ.service.CdrResponseMessageFactoryService;
import eionet.gdem.services.*;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.hosts.IHostDao;
import eionet.gdem.web.spring.schemas.ISchemaDao;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@Service
public class QaServiceImpl implements QaService {

    private Long maxMsToWaitForEmptyFileForOnDemandJobs = Properties.maxMsToWaitForEmptyFileForOnDemandJobs;
    private Long timeoutToWaitForEmptyFileForOnDemandJobs = Properties.timeoutToWaitForEmptyFileForOnDemandJobs;

    private QueryService queryService;
    /** DAO for getting schema info. */
    private ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();
    private static final Logger LOGGER = LoggerFactory.getLogger(QaService.class);

    private JobRequestHandlerService jobRequestHandlerService;

    private JobResultHandlerService jobResultHandlerService;

    private RunScriptAutomaticService runScriptAutomaticService;

    private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();

    private JobService jobService;
    private JobHistoryService jobHistoryService;
    private JobExecutorHistoryService jobExecutorHistoryService;
    private QueryMetadataService queryMetadataService;

    private CdrResponseMessageFactoryService cdrResponseMessageFactoryService;

    public QaServiceImpl() {
    }

    @Autowired
    public QaServiceImpl(QueryService queryService, JobRequestHandlerService jobRequestHandlerService, JobResultHandlerService jobResultHandlerService,
                         RunScriptAutomaticService runScriptAutomaticService, JobService jobService, JobHistoryService jobHistoryService, JobExecutorHistoryService jobExecutorHistoryService,
                         QueryMetadataService queryMetadataService, CdrResponseMessageFactoryService cdrResponseMessageFactoryService) {
        this.queryService = queryService;
        this.jobRequestHandlerService = jobRequestHandlerService;
        this.jobResultHandlerService = jobResultHandlerService;
        this.runScriptAutomaticService = runScriptAutomaticService;
        this.jobService = jobService;
        this.jobHistoryService = jobHistoryService;
        this.jobExecutorHistoryService = jobExecutorHistoryService;
        this.queryMetadataService = queryMetadataService;
        this.cdrResponseMessageFactoryService = cdrResponseMessageFactoryService;
    }

    @Override
    public HashMap<String, String> extractFileLinksAndSchemasFromEnvelopeUrl(String envelopeUrl) throws XMLConvException {
        HashMap<String, String> fileSchemaAndLinks = new HashMap<String, String>();

        try {
            Document doc = this.getXMLFromEnvelopeURL(envelopeUrl);
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expressionForEnvelopeFiles = xPath.compile("//envelope/file");
            NodeList envelopeFilesNodeList = (NodeList) expressionForEnvelopeFiles.evaluate(doc, XPathConstants.NODESET);
            int length = envelopeFilesNodeList.getLength();
            for (int i = 0; i < length; i++) {
                NamedNodeMap fileNode = envelopeFilesNodeList.item(i).getAttributes();
                fileSchemaAndLinks.put(fileNode.getNamedItem("link").getTextContent(), fileNode.getNamedItem("schema").getTextContent());
            }
        } catch (XPathExpressionException ex) {
            throw new XMLConvException("exception while parsing the envelope XML:" + envelopeUrl + " to extract files and schemas", ex);
        }

        return fileSchemaAndLinks;
    }

    @Override
    public List<String> extractObligationUrlsFromEnvelopeUrl(String envelopeUrl) throws XMLConvException {
        try {
            Document doc = this.getXMLFromEnvelopeURL(envelopeUrl);
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expressionForObligation = xPath.compile("//envelope/obligation");
            NodeList obligationNodeList = (NodeList) expressionForObligation.evaluate(doc, XPathConstants.NODESET);
            int length = obligationNodeList.getLength();
            List<String> obligationUrls = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                Node obligationNode = obligationNodeList.item(i);
                obligationUrls.add(obligationNode.getTextContent());
            }
            return obligationUrls;
        } catch (XPathExpressionException ex) {
            throw new XMLConvException("exception while parsing the envelope XML:" + envelopeUrl + " to extract obligation", ex);
        }
    }

    @Override
    public List<QaResultsWrapper> scheduleJobs(String envelopeUrl, Boolean checkForDuplicateJob, Boolean addedThroughRabbitMq, String uuid) throws XMLConvException {

        HashMap<String, String> fileLinksAndSchemas = extractFileLinksAndSchemasFromEnvelopeUrl(envelopeUrl);

        HashMap map = new HashMap();
        try {
            for (Map.Entry<String, String> entry : fileLinksAndSchemas.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != "" && value != "") {
                    if(map.containsKey(value)){  //schema already exists in the table
                        List<String> files = (List<String>) map.get(value);
                        files.add(key);
                    }
                    else{
                        List<String> files = new ArrayList<>();
                        files.add(key);
                        map.put(value, files);
                    }
                }
            }

            this.addObligationsFiles(map,envelopeUrl);
            if (map.size() == 0) {
                LOGGER.info("Could not find files and their schemas. There was an issue with the envelope " + envelopeUrl);
            }
            HashMap<String, String> jobIdsAndFileUrls = getJobRequestHandlerService().analyzeMultipleXMLFiles(map, checkForDuplicateJob, addedThroughRabbitMq, uuid);

            List<QaResultsWrapper> results = new ArrayList<QaResultsWrapper>();
            for (Map.Entry<String, String> entry : jobIdsAndFileUrls.entrySet()) {
                QaResultsWrapper qaResult = new QaResultsWrapper();
                qaResult.setJobId(entry.getKey());
                qaResult.setFileUrl(entry.getValue());
                results.add(qaResult);
            }
            return results;
        } catch (XMLConvException ex) {
            throw new XMLConvException("error scheduling Jobs with QueryService ", ex);
        }

    }

    @Override
    public Vector runQaScript(String sourceUrl, String scriptId) throws XMLConvException {
        try {
            return getRunScriptAutomaticService().runQAScript(sourceUrl, scriptId);
        } catch (XMLConvException ex) {
            throw new XMLConvException("error running Qa Script for sourceUrl :" + sourceUrl + " and scriptId:" + scriptId, ex);
        }
    }

    @Override
    public Vector runQaScript(String sourceUrl, String scriptId,boolean setBodyInResponse, boolean isTrustedMode) throws XMLConvException {
        try {
            return getRunScriptAutomaticService().runQAScript(sourceUrl, scriptId,setBodyInResponse, isTrustedMode);
        } catch (XMLConvException ex) {
            throw new XMLConvException("error running Qa Script for sourceUrl :" + sourceUrl + " and scriptId:" + scriptId, ex);
        }
    }

    @Override
    public Hashtable<String, Object> getJobResults(String jobId, Boolean addedThroughRabbitMq) throws XMLConvException {

        QueryService queryService = getQueryService(); // new QueryService();
        Hashtable<String, Object> results = getJobResultHandlerService().getResult(jobId, addedThroughRabbitMq);
        int resultCode = Integer.parseInt((String) results.get(Constants.RESULT_CODE_PRM));
        String executionStatusName = "";
        switch (resultCode) {

            case Constants.JOB_READY:
                executionStatusName = "Ready";
                break;
            case Constants.JOB_LIGHT_ERROR:
                executionStatusName = "Not Found";
                break;

            case Constants.JOB_FATAL_ERROR:
                executionStatusName = "Failed";
                break;

            case Constants.JOB_NOT_READY:
                executionStatusName = "Pending";
                break;

            case Constants.CANCELLED_BY_USER:
                executionStatusName = "Cancelled by user";
                break;

            case Constants.XQ_INTERRUPTED:
                executionStatusName = "Interrupted";
                break;

            case Constants.DELETED:
                //change feedback
                executionStatusName = "Deleted";
                results.put(Constants.RESULT_FEEDBACKMESSAGE_PRM, "Job canceled by reporter");
                results.put(Constants.RESULT_CODE_PRM, Integer.toString(Constants.JOB_READY));
                break;

        }
        results.put("executionStatusName", executionStatusName);
        return results;
    }

    @Override
    public List<LinkedHashMap<String, String>> listQAScripts(String schema, String active) throws XMLConvException {
        QueryService queryService = new QueryService();
        Vector queryServiceResults = queryService.listQAScripts(schema, active);
        List<LinkedHashMap<String, String>> resultsList = new LinkedList<LinkedHashMap<String, String>>();
        for (Object queryServiceResult : queryServiceResults) {
            Hashtable hs = (Hashtable) queryServiceResult;
            String scriptType = (String) hs.get(QaScriptView.SCRIPT_TYPE);
            if (scriptType == null) {
                scriptType = "xsd";
            }
            LinkedHashMap<String, String> rearrangedResults = new LinkedHashMap<String, String>();
            rearrangedResults.put(QaScriptView.QUERY_ID, (String) hs.get(QaScriptView.QUERY_ID));
            rearrangedResults.put(QaScriptView.TYPE, scriptType);
            rearrangedResults.put(QaScriptView.CONTENT_TYPE_ID, (String) hs.get(QaScriptView.CONTENT_TYPE_ID));
            rearrangedResults.put(QaScriptView.QUERY_AS_URL, (String) hs.get(QaScriptView.QUERY));
            rearrangedResults.put(QaScriptView.SHORT_NAME, (String) hs.get(QaScriptView.SHORT_NAME));
            rearrangedResults.put(QaScriptView.DESCRIPTION, (String) hs.get(QaScriptView.DESCRIPTION));
            rearrangedResults.put(QaScriptView.IS_ACTIVE, (String) hs.get(QaScriptView.IS_ACTIVE));
            rearrangedResults.put(QaScriptView.UPPER_LIMIT, (String) hs.get(QaScriptView.UPPER_LIMIT));
            rearrangedResults.put(QaScriptView.XML_SCHEMA, (String) hs.get(QaScriptView.XML_SCHEMA));
            resultsList.add(rearrangedResults);
        }

        return resultsList;
    }

    @Override
    public QueryService getQueryService() {
        if (queryService == null) {
            queryService = new QueryService();
        }
        return queryService;
    }

    @Override
    public Document getXMLFromEnvelopeURL(String envelopeUrl) throws XMLConvException {

        Document doc;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            URL url = new URL(envelopeUrl + "/xml");
            URLConnection uc = url.openConnection();

            //get credentials for host
            IHostDao hostDao = GDEMServices.getDaoService().getHostDao();
            Vector v = hostDao.getHosts(url.getHost());

            if (v != null && v.size() > 0) {
                Hashtable h = (Hashtable) v.get(0);
                String user = (String) h.get("user_name");
                String pwd = (String) h.get("pwd");
                String userpass = user + ":" + pwd;

                //add basic authorization
                String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
                uc.setRequestProperty ("Authorization", basicAuth);
            }
            InputStream in = uc.getInputStream();
            doc = db.parse(in);
        } catch (SAXException | IOException | ParserConfigurationException | SQLException ex) {
            throw new XMLConvException("exception while parsing the envelope URL:" + envelopeUrl + " to extract files and schemas", ex);
        }
        return doc;
    }

    protected void addObligationsFiles(HashMap<String,List<String>> map,String envelopeUrl) throws XMLConvException{
        List<String> obligationUrls = extractObligationUrlsFromEnvelopeUrl(envelopeUrl);
        for (String obligationUrl: obligationUrls
        ) {
            if(obligationUrl!=null && !obligationUrl.isEmpty())    {
                List<String> obligation = new ArrayList<>();
                obligation.add(envelopeUrl+"/xml");
                map.put(obligationUrl,obligation);
            }
        }
    }

    @Override
    public Schema getSchemaBySchemaUrl(String schemaUrl) throws Exception {
        Schema schema = null;
        try {
            schema = schemaDao.getSchemaBySchemaUrl(schemaUrl);
        } catch (Exception e) {
            throw new Exception("Could not retrieve schema information for schema url " + schemaUrl);
        }
        return schema;
    }

    @Override
    public Integer getJobExternalStatus(String jobId) throws XMLConvException {
        String[] jobData = null;
        try {
            jobData = xqJobDao.getXQJobData(jobId);
        } catch (SQLException e) {
            throw new XMLConvException("Error getting XQJob data from DB: " + e.toString());
        }
        if (jobData!=null)
            return Integer.parseInt(jobData[3]);
        else
            return Constants.XQ_JOBNOTFOUND_ERR;
    }

    @Override
    public LinkedHashMap<String, Object> checkIfZipFileExistsOrIsEmpty(String[] fileUrls, String jobId, LinkedHashMap<String, Object> jsonResults) throws XMLConvException {
        if(jsonResults.get("executionStatus") != null){
            LinkedHashMap<String,String> executionStatus = (LinkedHashMap<String, String>) jsonResults.get("executionStatus");
            if(executionStatus.get("statusName") != null) {
                String executionStatusName = (String) executionStatus.get("statusName");
                if (executionStatusName.equals("Deleted")){
                    //if job has been deleted, feedback content should stay empty and we will return execution status code Constants.JOB_READY
                    return jsonResults;
                }
            }
        }

        try {
            JobEntry jobEntry = jobService.findById(Integer.valueOf(jobId));
            if (jobEntry.getnStatus() == Constants.XQ_FATAL_ERR) {
                jsonResults.put("feedbackContent", "");
                jsonResults.put("REMOTE_FILES",fileUrls);
                jsonResults.put("feedbackStatus", Constants.JOB_FAILED_BECAUSE_OF_WORKER_STATUS);
                jsonResults.put("feedbackMessage", Constants.JOB_FAILED_BECAUSE_OF_WORKER_MESSAGE);
                return jsonResults;
            }
        } catch (DatabaseException e) {
            LOGGER.error("Could not set up the failed feedback message and status for async fme job with id " + jobId);
        }

        String fileName = fileUrls[0].replace(eionet.gdem.Properties.gdemURL + "/restapi/download/zip/","");
        if (fileName == null || fileName.isEmpty() || "/".equals(fileName)) {
            throw new XMLConvException("FileName " + fileName + " is not correct for jobId " + jobId);
        }
        String urlPath = new StringBuilder("/tmp/").append(fileName).toString();
        String filePath = Properties.appRootFolder + urlPath;
        File zipFile = new File(filePath);
        Path file = Paths.get(filePath);

        if (Files.exists(file) && zipFile.length()>0) {
            jsonResults.put("feedbackContent", "");
            jsonResults.put("REMOTE_FILES",fileUrls);
        } else {
            LOGGER.info("Zip file " + fileName + " of job with id " + jobId + " is not ready");
            jsonResults.put("feedbackContent","");
            LinkedHashMap<String,String> executionStatusView = new LinkedHashMap<String,String>();
            executionStatusView.put("statusId", String.valueOf(Constants.JOB_NOT_READY));
            executionStatusView.put("statusName","Not Ready");
            jsonResults.put("executionStatus",executionStatusView);
        }
        return jsonResults;
    }

    @Override
    public LinkedHashMap<String, Object> checkIfHtmlResultIsEmpty(String jobId, LinkedHashMap<String, Object> jsonResults, Hashtable<String, Object> results, Boolean addedThroughRabbitMq, Boolean isReady, String fileUrl){
        if(results.get("executionStatusName") != null){
            String executionStatusName = (String) results.get("executionStatusName");
            if (executionStatusName.equals("Deleted")){
                //if job has been deleted, feedback content should stay empty and we will return execution status code Constants.JOB_READY
                return jsonResults;
            }
        }

        String htmlFileContent = (String) results.get(Constants.RESULT_VALUE_PRM);
        String feedbackStatus = (String) results.get(Constants.RESULT_FEEDBACKSTATUS_PRM);
        if(feedbackStatus.equals(Constants.XQ_FEEDBACKSTATUS_UNKNOWN) && htmlFileContent.length() == 0){
            LOGGER.info("Html file for job id " + jobId + " is not ready");
            jsonResults.put("feedbackContent", "");
            LinkedHashMap<String,String> executionStatusView = new LinkedHashMap<String,String>();
            executionStatusView.put("statusId", String.valueOf(Constants.JOB_NOT_READY));
            executionStatusView.put("statusName","Not Ready");
            jsonResults.put("executionStatus",executionStatusView);
        }
        else{
            if(addedThroughRabbitMq && isReady){
                String[] fileUrls = {fileUrl};
                jsonResults.put("REMOTE_FILES", fileUrls);
            }
            else{
                jsonResults.put("feedbackContent", htmlFileContent);
            }
        }
        return jsonResults;
    }

    @Override
    public LinkedHashMap<String, String> handleOnDemandJobsResults(Vector results, String sourceXml, String scriptId) throws XMLConvException {
        //Vector results contains feedbackContentType, feedbackContent, feedbackStatus, feedbackMessage, jobId. JobId is null for schema validation
        String jobResultFileName = null;
        String jobId = null;
        Long maxMs = this.getMaxMsToWaitForEmptyFileForOnDemandJobs();
        Long timeoutInMs = this.getTimeoutToWaitForEmptyFileForOnDemandJobs();
        Integer retries = (int) (maxMs / timeoutInMs);
        retries = (retries <= 0) ? 1 : retries;
        LinkedHashMap<String, String> jsonResults = new LinkedHashMap<String, String>();
        try {
            String feedbackContent = ConvertByteArrayToString((byte[]) results.get(1));

            if (results.size() > 4 && results.get(4) != null) {
                jobId = (String) results.get(4);
                LOGGER.info("Handling on demand jobs results for job with id " + jobId + " xml " + sourceXml + " and script id " + scriptId);
            }

            if (jobId != null) {
                //Retrieve filename
                JobEntry jobEntry = jobService.findById(Integer.valueOf(jobId));
                jobResultFileName = jobEntry.getResultFile();
            }

            for (int i = 0; i < retries; i++) {
                if (feedbackContent.isEmpty()) {
                    Thread.sleep(timeoutInMs);
                    if (jobResultFileName != null) {
                        //Read file again
                        File file = new File(jobResultFileName);
                        feedbackContent = FileUtils.readFileToString(file, "UTF-8");
                    }
                    else{
                        break;
                    }
                } else {
                    //feedback file is not empty
                    jsonResults.put("feedbackStatus", ConvertByteArrayToString((byte[]) results.get(2)));
                    jsonResults.put("feedbackMessage", ConvertByteArrayToString((byte[]) results.get(3)));
                    jsonResults.put("feedbackContentType", results.get(0).toString());
                    jsonResults.put("feedbackContent", feedbackContent);
                    String msg = "Found not empty content for on demand job ";
                    if (jobId != null) {
                        msg += "id " + jobId;
                    }
                    msg += " with xml " + sourceXml + " and script id " + scriptId + ". Retry was " + (i+1) + " of " + retries;
                    LOGGER.info(msg);
                    break;
                }
            }

            if (feedbackContent.isEmpty()) {
                //send blocker
                jsonResults.put("feedbackStatus", Constants.ON_DEMAND_JOBS_EMPTY_CONTENT_FEEDBACK_STATUS);
                jsonResults.put("feedbackMessage", Constants.ON_DEMAND_JOBS_EMPTY_CONTENT_FEEDBACK_MESSAGE);
                jsonResults.put("feedbackContentType", Constants.ON_DEMAND_JOBS_EMPTY_CONTENT_FEEDBACK_CONTENT_TYPE);
                jsonResults.put("feedbackContent", Constants.ON_DEMAND_JOBS_EMPTY_CONTENT_FEEDBACK_CONTENT);

                //change job status to fatal error and store it in T_XQJOBS and JOB_HISTORY
                if (jobId != null) {
                    LOGGER.info("Updating tables for fatal error on demand job with empty content. Job id is " + jobId);
                    try {
                        updateJobRelatedTablesStatus(jobId, scriptId);
                    } catch (DatabaseException e) {
                        LOGGER.error("Could not updata job related tables status for jobId " + jobId + " Exception message is: " + e.getMessage());
                        throw e;
                    }
                }
            }
        }
        catch(Exception e){
            throw new XMLConvException(e.getMessage());
        }
        return jsonResults;
    }

    private void updateJobRelatedTablesStatus(String jobId, String scriptId) throws DatabaseException {
        JobEntry jobEntry = jobService.findById(Integer.valueOf(jobId));
        jobEntry.setnStatus(Constants.XQ_FATAL_ERR);
        jobEntry.setTimestamp(new Timestamp(new Date().getTime()));
        jobService.saveOrUpdate(jobEntry);
        LOGGER.info("Changed status to FATAL ERROR in T_XQJOBS table for jobId " + jobId);
        List<JobHistoryEntry> jobHistoryEntries = jobHistoryService.getJobHistoryEntriesOfJob(jobId);
        if(!Utils.isNullList(jobHistoryEntries)){
            JobHistoryEntry jobHistoryLastEntry = jobHistoryEntries.get(jobHistoryEntries.size()-1);
            jobHistoryLastEntry.setStatus(Constants.XQ_FATAL_ERR);
            jobHistoryService.save(jobHistoryLastEntry);
            LOGGER.info("Changed status to FATAL ERROR in JOB_HISTORY table for jobId " + jobId + ". Job history entry id is " + jobHistoryLastEntry.getId());
        }
        List<JobExecutorHistory> jobExecutorHistoryEntries = jobExecutorHistoryService.getJobExecutorHistoryEntriesByJobId(jobId);
        if(!Utils.isNullList(jobExecutorHistoryEntries)){
            JobExecutorHistory jobExecutorHistoryLastEntry = jobExecutorHistoryEntries.get(jobExecutorHistoryEntries.size()-1);
            jobExecutorHistoryLastEntry.setStatus(Constants.XQ_FATAL_ERR);
            jobExecutorHistoryService.saveJobExecutorHistoryEntry(jobExecutorHistoryLastEntry);
            LOGGER.info("Changed status to FATAL ERROR in JOB_EXECUTOR_HISTORY table for jobId " + jobId + ". Job executor history entry id is " + jobExecutorHistoryLastEntry.getId());
        }
        List<QueryMetadataHistoryEntry> queryMetadataHistoryEntries = queryMetadataService.findByJobId(Integer.valueOf(jobId));
        if(!Utils.isNullList(queryMetadataHistoryEntries)){
            QueryMetadataHistoryEntry queryMetadataHistoryLastEntry = queryMetadataHistoryEntries.get(queryMetadataHistoryEntries.size()-1);
            queryMetadataHistoryLastEntry.setJobStatus(Constants.XQ_FATAL_ERR);
            queryMetadataService.saveQueryMetadataHistoryEntry(queryMetadataHistoryLastEntry);
            LOGGER.info("Changed status to FATAL ERROR in QUERY_MEATADATA_HISTORY table for jobId " + jobId + ". Query Metadata history entry id is " + queryMetadataHistoryLastEntry.getId()
                    + " and script Id is " + scriptId);
        }
        if(jobEntry.getAddedFromQueue() != null && jobEntry.getAddedFromQueue()) {
            cdrResponseMessageFactoryService.createCdrResponseMessageAndSendToQueueOrPendingJobsTable(jobEntry);
        }

    }

    public String ConvertByteArrayToString(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, "UTF-8");
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

    public Long getMaxMsToWaitForEmptyFileForOnDemandJobs() {
        return maxMsToWaitForEmptyFileForOnDemandJobs;
    }

    public Long getTimeoutToWaitForEmptyFileForOnDemandJobs() {
        return timeoutToWaitForEmptyFileForOnDemandJobs;
    }
}
