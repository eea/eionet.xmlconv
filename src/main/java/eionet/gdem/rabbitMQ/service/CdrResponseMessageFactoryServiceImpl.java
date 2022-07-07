package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.service.CdrRequestsService;
import eionet.gdem.rabbitMQ.model.CdrJobResponseMessage;
import eionet.gdem.rabbitMQ.model.CdrJobResultMessage;
import eionet.gdem.rabbitMQ.model.CdrSummaryResponseMessage;
import eionet.gdem.utils.StatusUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("cdrResponseMessageFactoryServiceImpl")
public class CdrResponseMessageFactoryServiceImpl implements CdrResponseMessageFactoryService{

    @Autowired
    QaService qaService;

    @Autowired
    CdrJobResultMessageSender cdrJobResultMessageSender;

    @Autowired
    private CdrRequestsService cdrRequestsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CdrResponseMessageFactoryServiceImpl.class);

    @Override
    public void createCdrResponseMessageAndSendToQueue(JobEntry jobEntry){
        CdrJobResponseMessage cdrJobResponseMessage = new CdrJobResponseMessage();
        cdrJobResponseMessage.setUUID(jobEntry.getUuid());
        cdrJobResponseMessage.setJobId(String.valueOf(jobEntry.getId()));
        cdrJobResponseMessage.setJobStatus(StatusUtils.getStatusNameByNumber(jobEntry.getnStatus()));
        cdrJobResponseMessage.setDocumentURL(jobEntry.getUrl());
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

        if(jobEntry.getnStatus() != Constants.XQ_READY && jobEntry.getnStatus() != Constants.XQ_FATAL_ERR && jobEntry.getnStatus() != Constants.XQ_LIGHT_ERR
                && jobEntry.getnStatus() != Constants.DELETED){
            cdrJobResponseMessage.setJobResult(null);
        }
        else{
            //create job result message and set it up
            CdrJobResultMessage jobResult = new CdrJobResultMessage();

            if(jobEntry.getnStatus() == Constants.DELETED){
                jobResult.setFeedbackMessage(Constants.JOB_FEEDBACK_MESSAGE_DELETED);
                cdrJobResponseMessage.setJobResult(jobResult);
            }
            else{

                Hashtable<String, Object> results = null;
                try {
                    results = qaService.getJobResults(String.valueOf(jobEntry.getId()), true);

                    LOGGER.info("For job id " + jobEntry.getId() + " statusId=" + (String) results.get(Constants.RESULT_CODE_PRM) + " and feedbackStatus=" + results.get(Constants.RESULT_FEEDBACKSTATUS_PRM));

                    LinkedHashMap<String, Object> jsonResults = new LinkedHashMap<String, Object>();
                    if(results.get("REMOTE_FILES")!=null){
                        String[] fileUrls = (String[]) results.get("REMOTE_FILES");
                        if(fileUrls[0]!=null) {
                            LinkedHashMap<String,String> executionStatusView = new LinkedHashMap<String,String>();
                            String executionStatusId = (String) results.get(Constants.RESULT_CODE_PRM);
                            String executionStatusName = (String) results.get("executionStatusName");
                            executionStatusView.put("statusId", executionStatusId);
                            executionStatusView.put("statusName", executionStatusName);
                            jsonResults.put("executionStatus",executionStatusView);
                            if(fileUrls[0].endsWith(".zip")){
                                jsonResults = qaService.checkIfZipFileExistsOrIsEmpty(fileUrls, String.valueOf(jobEntry.getId()), jsonResults);
                            }
                            else{
                                if(jobEntry.getnStatus() == Constants.XQ_READY){
                                    jsonResults = qaService.checkIfHtmlResultIsEmpty(String.valueOf(jobEntry.getId()), jsonResults, results, true, true, fileUrls[0]);
                                }
                                else{
                                    jsonResults = qaService.checkIfHtmlResultIsEmpty(String.valueOf(jobEntry.getId()), jsonResults, results, true, false, null);
                                }

                            }

                            String[] remoteFiles = (String[]) jsonResults.get("REMOTE_FILES");
                            if(remoteFiles != null && remoteFiles.length > 0)
                            {
                                jobResult.setRemoteFiles(remoteFiles[0]);
                            }
                            if(jsonResults.get("feedbackContent") != null){
                                jobResult.setFeedbackContent((String) jsonResults.get("feedbackContent"));
                            }
                        }
                        else{
                            jobResult.setFeedbackContent((String) jsonResults.get(""));
                        }
                    }else{
                        //result file is html
                        jsonResults = qaService.checkIfHtmlResultIsEmpty(String.valueOf(jobEntry.getId()), jsonResults, results, true, false, null);
                        if(jsonResults.get("feedbackContent") != null){
                            jobResult.setFeedbackContent((String) jsonResults.get("feedbackContent"));
                        }
                    }
                    if(results.get(Constants.RESULT_FEEDBACKSTATUS_PRM) != null){
                        jobResult.setFeedbackStatus((String) results.get(Constants.RESULT_FEEDBACKSTATUS_PRM));
                    }
                    if(results.get(Constants.RESULT_FEEDBACKMESSAGE_PRM) != null){
                        jobResult.setFeedbackMessage((String) results.get(Constants.RESULT_FEEDBACKMESSAGE_PRM));
                    }
                    if(results.get(Constants.RESULT_METATYPE_PRM) != null){
                        jobResult.setFeedbackContentType((String) results.get(Constants.RESULT_METATYPE_PRM));
                    }
                    cdrJobResponseMessage.setJobResult(jobResult);
                } catch (XMLConvException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        LOGGER.info("Created response for cdr request for job id " + cdrJobResponseMessage.getJobId() + " and status " + cdrJobResponseMessage.getJobStatus());
        //send cdrJobResponseMessage to queue
        cdrJobResultMessageSender.sendMessageToRabbitMQ(cdrJobResponseMessage);
    }

    @Override
    public void createCdrSummaryResponseMessageAndSendToQueue(String uuid, List<QaResultsWrapper> scheduledJobs){
        List<String> jobIds = scheduledJobs.stream().map(QaResultsWrapper::getJobId).collect(Collectors.toList());

        CdrSummaryResponseMessage cdrSummaryResponseMessage = new CdrSummaryResponseMessage(uuid, scheduledJobs.size(), jobIds);

        LOGGER.info("Created summary response for cdr request for uuid " + cdrSummaryResponseMessage.getUuid());
        //send cdrSummaryResponseMessage to queue
        cdrJobResultMessageSender.sendSummaryMessageToRabbitMQ(cdrSummaryResponseMessage);
    }

}
