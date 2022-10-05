package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.PendingCdrJobEntry;
import eionet.gdem.jpa.service.PendingCdrJobsService;
import eionet.gdem.rabbitMQ.model.CdrJobResponseMessage;
import eionet.gdem.rabbitMQ.model.CdrJobResultMessage;
import eionet.gdem.rabbitMQ.model.CdrSummaryResponseMessage;
import eionet.gdem.utils.StatusUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service("cdrResponseMessageFactoryServiceImpl")
public class CdrResponseMessageFactoryServiceImpl implements CdrResponseMessageFactoryService{

    @Autowired
    QaService qaService;

    @Autowired
    CdrJobResultMessageSender cdrJobResultMessageSender;

    @Autowired
    private PendingCdrJobsService pendingCdrJobsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CdrResponseMessageFactoryServiceImpl.class);

    @Override
    public void createCdrResponseMessageAndSendToQueueOrPendingJobsTable(JobEntry jobEntry){
        CdrJobResponseMessage cdrJobResponseMessage = setupBasicCdrJobResponseMessage(jobEntry);

        if(jobEntry.getnStatus() != Constants.XQ_READY && jobEntry.getnStatus() != Constants.XQ_FATAL_ERR && jobEntry.getnStatus() != Constants.XQ_LIGHT_ERR){
            if(jobEntry.getnStatus() == Constants.DELETED){
                //create job result message and set it up
                CdrJobResultMessage jobResult = new CdrJobResultMessage();
                jobResult.setFeedbackMessage(Constants.JOB_FEEDBACK_MESSAGE_DELETED);
                cdrJobResponseMessage.setJobResult(jobResult);
            }
            else {
                cdrJobResponseMessage.setJobResult(null);
            }
            LOGGER.info("Created response for cdr request for job id " + cdrJobResponseMessage.getJobId() + " and status " + cdrJobResponseMessage.getJobStatus() + " Job status is " + StatusUtils.getStatusNameByNumber(jobEntry.getnStatus()));
            //send cdrJobResponseMessage to queue
            cdrJobResultMessageSender.sendMessageToRabbitMQ(cdrJobResponseMessage);
        }
        else{
            //store job as pending until results are ready.
            LOGGER.info("Storing job with id " + jobEntry.getId() + " as pending until results are ready");
            PendingCdrJobEntry pendingCdrJobEntry = new PendingCdrJobEntry(jobEntry.getId(), jobEntry.getUuid(), jobEntry.getnStatus(), new Timestamp(new Date().getTime()));
            pendingCdrJobsService.savePendingEntry(pendingCdrJobEntry);
        }
    }

    @Override
    public void createCdrSummaryResponseMessageAndSendToQueue(String uuid,  String envelopeUrl, List<QaResultsWrapper> scheduledJobs){
        List<String> jobIds = scheduledJobs.stream().map(QaResultsWrapper::getJobId).collect(Collectors.toList());

        CdrSummaryResponseMessage cdrSummaryResponseMessage = new CdrSummaryResponseMessage(uuid, scheduledJobs.size(), envelopeUrl, jobIds);

        LOGGER.info("Created summary response for cdr request for uuid " + cdrSummaryResponseMessage.getUuid());
        //send cdrSummaryResponseMessage to queue
        cdrJobResultMessageSender.sendSummaryMessageToRabbitMQ(cdrSummaryResponseMessage);
    }

    @Override
    public Boolean handleReadyOrFailedJobsAndSendToCdr(JobEntry jobEntry) throws XMLConvException {
        CdrJobResponseMessage cdrJobResponseMessage = setupBasicCdrJobResponseMessage(jobEntry);
        //create job result message and set it up
        CdrJobResultMessage jobResult = new CdrJobResultMessage();
        Hashtable<String, Object> results = null;
        Boolean sendResultsToQueue = true;
        try {
            results = qaService.getJobResults(String.valueOf(jobEntry.getId()), true);

            LOGGER.info("For job id " + jobEntry.getId() + " statusId=" + (String) results.get(Constants.RESULT_CODE_PRM) + " and feedbackStatus=" + results.get(Constants.RESULT_FEEDBACKSTATUS_PRM) + " Job status is " + StatusUtils.getStatusNameByNumber(jobEntry.getnStatus()));

            LinkedHashMap<String, Object> jsonResults = new LinkedHashMap<String, Object>();
            LinkedHashMap<String, String> executionStatusView = new LinkedHashMap<String, String>();
            String executionStatusId = (String) results.get(Constants.RESULT_CODE_PRM);
            String executionStatusName = (String) results.get("executionStatusName");
            executionStatusView.put("statusId", executionStatusId);
            executionStatusView.put("statusName", executionStatusName);
            jsonResults.put("executionStatus", executionStatusView);

            if (results.get("REMOTE_FILES") != null) {
                LOGGER.info("For job id " + jobEntry.getId() + " we have remote files parameters");
                String[] fileUrls = (String[]) results.get("REMOTE_FILES");
                if (fileUrls[0] != null) {
                    if (fileUrls[0].endsWith(".zip")) {
                        jsonResults = qaService.checkIfZipFileExistsOrIsEmpty(fileUrls, String.valueOf(jobEntry.getId()), jsonResults);
                    } else {
                        if (jobEntry.getnStatus() == Constants.XQ_READY) {
                            jsonResults = qaService.checkIfHtmlResultIsEmpty(String.valueOf(jobEntry.getId()), jsonResults, results, true, true, fileUrls[0]);
                        } else {
                            jsonResults = qaService.checkIfHtmlResultIsEmpty(String.valueOf(jobEntry.getId()), jsonResults, results, true, false, null);
                        }
                    }

                    sendResultsToQueue = canResultsBeSentToCdr(jobEntry.getId(), jsonResults);
                    if(sendResultsToQueue == false){
                        //Results are not ready and they should not be sent to the queue.
                        return false;
                    }

                    String[] remoteFiles = (String[]) jsonResults.get("REMOTE_FILES");
                    if (remoteFiles != null && remoteFiles.length > 0) {
                        jobResult.setRemoteFiles(remoteFiles[0]);
                    }
                    if (jsonResults.get("feedbackContent") != null) {
                        jobResult.setFeedbackContent((String) jsonResults.get("feedbackContent"));
                    }
                } else {
                    jobResult.setFeedbackContent((String) jsonResults.get(""));
                }
            } else {
                //we have not empty feedback content
                jsonResults = qaService.checkIfHtmlResultIsEmpty(String.valueOf(jobEntry.getId()), jsonResults, results, true, false, null);
                sendResultsToQueue = canResultsBeSentToCdr(jobEntry.getId(), jsonResults);
                if(sendResultsToQueue == false){
                    //Results are not ready and they should not be sent to the queue.
                    return false;
                }
                if (jsonResults.get("feedbackContent") != null) {
                    jobResult.setFeedbackContent((String) jsonResults.get("feedbackContent"));
                }
            }
            if (results.get(Constants.RESULT_FEEDBACKSTATUS_PRM) != null) {
                jobResult.setFeedbackStatus((String) results.get(Constants.RESULT_FEEDBACKSTATUS_PRM));
            }
            if (results.get(Constants.RESULT_FEEDBACKMESSAGE_PRM) != null) {
                jobResult.setFeedbackMessage((String) results.get(Constants.RESULT_FEEDBACKMESSAGE_PRM));
            }
            if (results.get(Constants.RESULT_METATYPE_PRM) != null) {
                jobResult.setFeedbackContentType((String) results.get(Constants.RESULT_METATYPE_PRM));
            }
            cdrJobResponseMessage.setJobResult(jobResult);
            LOGGER.info("Created response for cdr request for job id " + cdrJobResponseMessage.getJobId() + " and status " + cdrJobResponseMessage.getJobStatus());
            //send cdrJobResponseMessage to queue
            cdrJobResultMessageSender.sendMessageToRabbitMQ(cdrJobResponseMessage);

        } catch (XMLConvException e) {
            LOGGER.error("Error checking if result can be sent to cdr for jobId " + jobEntry.getId() + " and uuid " + jobEntry.getUuid() + " Error message: " + e.getMessage() );
            throw e;
        }
        return true;
    }

    private CdrJobResponseMessage setupBasicCdrJobResponseMessage(JobEntry jobEntry){
        CdrJobResponseMessage cdrJobResponseMessage = new CdrJobResponseMessage();
        cdrJobResponseMessage.setUUID(jobEntry.getUuid());
        cdrJobResponseMessage.setJobId(String.valueOf(jobEntry.getId()));
        cdrJobResponseMessage.setJobStatus(StatusUtils.getStatusNameByNumber(jobEntry.getnStatus()));
        String documentUrl = jobEntry.getUrl();
        if(documentUrl.contains("source_url=")){
            //get xml url without ticket
            String[] parts = documentUrl.split("source_url=");
            if(parts.length > 1){
                documentUrl = parts[1];
            }
        }
        cdrJobResponseMessage.setDocumentURL(documentUrl);
        cdrJobResponseMessage.setScriptId(jobEntry.getQueryId().toString());
        String scriptFullTitle = jobEntry.getFile();
        String[] splittedTitleArray = scriptFullTitle.split("/");
        if(splittedTitleArray.length == 0){
            cdrJobResponseMessage.setScriptTitle(scriptFullTitle);
        }
        else{
            String scriptTitle = splittedTitleArray[splittedTitleArray.length - 1];
            cdrJobResponseMessage.setScriptTitle(scriptTitle);
        }

        //set up Execution Status
        cdrJobResponseMessage.setExecutionStatus(StatusUtils.createJobExecutionStatus(jobEntry.getnStatus()));

        return cdrJobResponseMessage;
    }

    private Boolean canResultsBeSentToCdr(Integer jobId, LinkedHashMap<String, Object> jsonResults ){
        LinkedHashMap<String,String> executionStatusView = (LinkedHashMap<String, String>) jsonResults.get("executionStatus");
        if(executionStatusView != null){
            String statusId = executionStatusView.get("statusId");
            if(statusId != null && statusId.equals(String.valueOf(Constants.JOB_READY))){
                LOGGER.info("Results for job " + jobId + " can be sent to the results queue");
                return true;
            }
            else{
                if( statusId == null) {
                    LOGGER.info("Results for job " + jobId + " can not be sent to the results queue. statusId is null");
                }
                else{
                    LOGGER.info("Results for job " + jobId + " can not be sent to the results queue. statusId is " + statusId);
                }
            }
        }
        else{
            LOGGER.info("Results for job " + jobId + " can not be sent to the results queue. executionStatusView object is null");
        }

        return false;
    }

}
