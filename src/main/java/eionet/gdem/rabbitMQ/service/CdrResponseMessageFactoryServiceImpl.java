package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Constants;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.rabbitMQ.model.CdrJobResponseMessage;
import eionet.gdem.rabbitMQ.model.CdrJobResultMessage;
import eionet.gdem.utils.StatusUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Hashtable;

@Service("cdrResponseMessageFactoryServiceImpl")
public class CdrResponseMessageFactoryServiceImpl implements CdrResponseMessageFactoryService{

    @Autowired
    QaService qaService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CdrResponseMessageFactoryServiceImpl.class);

    @Override
    public void createCdrResponseMessageAndSendToQueue(JobEntry jobEntry){
        CdrJobResponseMessage cdrJobResponseMessage = new CdrJobResponseMessage();
        cdrJobResponseMessage.setUUID(jobEntry.getUuid());
        cdrJobResponseMessage.setJobId(String.valueOf(jobEntry.getId()));
        cdrJobResponseMessage.setJobStatus(StatusUtils.getStatusNameByNumber(jobEntry.getnStatus()));
        if(jobEntry.getnStatus() != Constants.XQ_READY && jobEntry.getnStatus() != Constants.XQ_FATAL_ERR && jobEntry.getnStatus() != Constants.XQ_LIGHT_ERR){
            cdrJobResponseMessage.setJobResult(null);
        }
        else{
            //create job result message and set it up

            Hashtable<String, Object> results = null;
            /*try {
                results = qaService.getJobResults(String.valueOf(jobEntry.getId()));

                String executionStatusId = (String) results.get(Constants.RESULT_CODE_PRM);
                String executionStatusName = (String) results.get("executionStatusName");
                LinkedHashMap<String, Object> jsonResults = new LinkedHashMap<String, Object>();
                LinkedHashMap<String,String> executionStatusView = new LinkedHashMap<String,String>();
                executionStatusView.put("statusId", executionStatusId);
                executionStatusView.put("statusName", executionStatusName);
                jsonResults.put("scriptTitle",results.get(Constants.RESULT_SCRIPTTITLE_PRM));
                jsonResults.put("executionStatus",executionStatusView);
                jsonResults.put("feedbackStatus", results.get(Constants.RESULT_FEEDBACKSTATUS_PRM));
                jsonResults.put("feedbackMessage", results.get(Constants.RESULT_FEEDBACKMESSAGE_PRM));
                jsonResults.put("feedbackContentType", results.get(Constants.RESULT_METATYPE_PRM));

                LOGGER.info("For job id " + jobId + " statusId=" + (String) results.get(Constants.RESULT_CODE_PRM) + " and feedbackStatus=" + results.get(Constants.RESULT_FEEDBACKSTATUS_PRM));

                //if result file is zip
                if(results.get("REMOTE_FILES")!=null){
                    String[] fileUrls = (String[]) results.get("REMOTE_FILES");
                    if(fileUrls[0]!=null) {
                        jsonResults = qaService.checkIfZipFileExistsOrIsEmpty(fileUrls, jobId, jsonResults);
                    }
                }else{
                    //result file is html
                    jsonResults = qaService.checkIfHtmlResultIsEmpty(jobId, jsonResults, results);
                }
                if(executionStatusName.equals("Not Found")){
                    return new ResponseEntity<LinkedHashMap<String, Object>>(jsonResults, HttpStatus.NOT_FOUND);
                }
                else{
                    return new ResponseEntity<LinkedHashMap<String, Object>>(jsonResults, HttpStatus.OK);
                }
            } catch (XMLConvException e) {
                throw new RuntimeException(e);
            }*/


            CdrJobResultMessage jobResult = new CdrJobResultMessage();
            cdrJobResponseMessage.setJobResult(jobResult);
        }
        LOGGER.info("Created response for cdr request and sent it to the queue");
        //send cdrJobResponseMessage to queue
    }
}
