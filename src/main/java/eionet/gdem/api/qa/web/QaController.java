package eionet.gdem.api.qa.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.api.errors.BadRequestException;
import eionet.gdem.api.errors.EmptyParameterException;
import eionet.gdem.api.qa.model.EnvelopeWrapper;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.qa.XQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static eionet.gdem.qa.ScriptStatus.getActiveStatusList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@RestController
@Validated
public class QaController {

    private final QaService qaService;

    private static final Logger LOGGER = LoggerFactory.getLogger(QaController.class);
    private static final List<String> ACTIVE_STATUS
            = getActiveStatusList();

        private static String UPLOADED_FOLDER = "/home/dev-vs/workspace/github.com/eea/xmlconv-old-version-branch06112017/eionet.xmlconv/";

    
    @Autowired
    public QaController(QaService qaService) {
        this.qaService = qaService;
    }

    @RequestMapping(value = "/fileupload/qajobs", method = RequestMethod.POST )
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile uploadfile) throws IOException, XMLConvException {

        if (uploadfile.isEmpty()) {
            return new ResponseEntity("please select a file!", HttpStatus.OK);
        }

        //Upload the file in the way UplXmlFileManager.storeXmlFile() does it.
        OutputStream output = null;
        InputStream in = uploadfile.getInputStream();
        String filepath = eionet.gdem.Properties.xmlfileFolder + File.separator + uploadfile.getOriginalFilename();
      
       
       String fileURL = "http://"+eionet.gdem.Properties.appHost+"/xmlfile/"+uploadfile.getOriginalFilename();

        try {
            output = new FileOutputStream(filepath);
            IOUtils.copy(in, output);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(output);
        }
        
         Vector results = qaService.runQaScript(fileURL, "-1");
         
        LinkedHashMap<String, String> jsonResults = new LinkedHashMap<String, String>();
        jsonResults.put("feedbackStatus", ConvertByteArrayToString((byte[]) results.get(2)));
        jsonResults.put("feedbackMessage", ConvertByteArrayToString((byte[]) results.get(3)));
        jsonResults.put("feedbackContentType", results.get(0).toString());
        jsonResults.put("feedbackContent", ConvertByteArrayToString((byte[]) results.get(1)));
        return new ResponseEntity<HashMap<String, String>>(jsonResults, HttpStatus.OK);
    }

    
    /**
     * Synchronous QA for a single file
     *
     */
    @RequestMapping(value = "/qajobs", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<HashMap<String, String>> performInstantQARequestOnFile(@RequestBody EnvelopeWrapper envelopeWrapper) throws XMLConvException, EmptyParameterException, UnsupportedEncodingException {

        if (envelopeWrapper.getSourceUrl() == null) {
            throw new EmptyParameterException("sourceUrl");
        }
        if (envelopeWrapper.getScriptId() == null) {
            throw new EmptyParameterException("scriptId");
        }

        Vector results = qaService.runQaScript(envelopeWrapper.getSourceUrl(), envelopeWrapper.getScriptId());
        LinkedHashMap<String, String> jsonResults = new LinkedHashMap<String, String>();
        jsonResults.put("feedbackStatus", ConvertByteArrayToString((byte[]) results.get(2)));
        jsonResults.put("feedbackMessage", ConvertByteArrayToString((byte[]) results.get(3)));
        jsonResults.put("feedbackContentType", results.get(0).toString());
        jsonResults.put("feedbackContent", ConvertByteArrayToString((byte[]) results.get(1)));
        return new ResponseEntity<HashMap<String, String>>(jsonResults, HttpStatus.OK);
    }

    /**
     *
     * Asynchronous QA for a Single File
     *
     */
    @RequestMapping(value = "/asynctasks/qajobs")
    public ResponseEntity<HashMap<String,String>> scheduleQARequestOnFile(@RequestBody EnvelopeWrapper envelopeWrapper) throws XMLConvException, EmptyParameterException, UnsupportedEncodingException {

         if (envelopeWrapper.getSourceUrl() == null) {
            throw new EmptyParameterException("sourceUrl");
        }
        if (envelopeWrapper.getScriptId() == null) {
            throw new EmptyParameterException("scriptId");
        }      
        
        XQueryService xqueryService = new XQueryService();
          String jobId = xqueryService.analyzeXMLFile(envelopeWrapper.getSourceUrl(), envelopeWrapper.getScriptId());
          xqueryService.analyzeXMLFile(envelopeWrapper.getSourceUrl(),envelopeWrapper.getScriptId(),null);
          LinkedHashMap<String,String> results = new LinkedHashMap<String,String>(); 
          results.put("jobId",jobId);
          return  new ResponseEntity<HashMap<String,String>>(results,HttpStatus.OK);
    }

    /**
     * Schedule a Qa Job for an Envelope
     *
     */
    @RequestMapping(value = "/asynctasks/qajobs/batch", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<LinkedHashMap<String, List<QaResultsWrapper>>> scheduleQaRequestOnEnvelope(@RequestBody EnvelopeWrapper envelopeWrapper) throws XMLConvException, EmptyParameterException, JsonProcessingException {

        if (envelopeWrapper.getEnvelopeUrl() == null) {
            throw new EmptyParameterException("envelopeUrl");
        }
        List<QaResultsWrapper> qaResults = qaService.scheduleJobs(envelopeWrapper.getEnvelopeUrl());
        LinkedHashMap<String, List<QaResultsWrapper>> jobsQaResults = new LinkedHashMap<String, List<QaResultsWrapper>>();
        jobsQaResults.put("jobs", qaResults);
        return new ResponseEntity<LinkedHashMap<String, List<QaResultsWrapper>>>(jobsQaResults, HttpStatus.OK);
    }

 

    /**
     * Get QA Job Status
     *
     */
    @RequestMapping(value = "/asynctasks/qajobs/{jobId}", method = RequestMethod.GET)
    public ResponseEntity<LinkedHashMap<String, Object>> getQAResultsForJob(@PathVariable String jobId) throws XMLConvException, JsonProcessingException {

        Hashtable<String, String> results = qaService.getJobResults(jobId);
        LinkedHashMap<String, Object> jsonResults = new LinkedHashMap<String, Object>();
        LinkedHashMap<String,String> executionStatusView = new LinkedHashMap<String,String>();
        executionStatusView.put("statusId", results.get(Constants.RESULT_CODE_PRM));
        executionStatusView.put("statusName",results.get("executionStatusName"));
        jsonResults.put("scriptTitle",results.get(Constants.RESULT_SCRIPTTITLE_PRM));
        jsonResults.put("executionStatus",executionStatusView);
        jsonResults.put("feedbackStatus", results.get(Constants.RESULT_FEEDBACKSTATUS_PRM));
        jsonResults.put("feedbackMessage", results.get(Constants.RESULT_FEEDBACKMESSAGE_PRM));
        jsonResults.put("feedbackContentType", results.get(Constants.RESULT_METATYPE_PRM));
        jsonResults.put("feedbackContent", results.get(Constants.RESULT_VALUE_PRM));
        return new ResponseEntity<LinkedHashMap<String, Object>>(jsonResults, HttpStatus.OK);
    }

   /**
    *Get Qa Scripts for a given schema and status , or if empty , return all schemas.
    * 
    **/
    @RequestMapping(value = "/qascripts", method = RequestMethod.GET)
    public ResponseEntity<List<LinkedHashMap<String,String>>> listQaScripts(@RequestParam(value = "schema", required = false) String schema, @RequestParam(value = "active", required = false, defaultValue = "true") String active) throws XMLConvException, BadRequestException {

        if (!ACTIVE_STATUS.contains(active)) {
            throw new BadRequestException("parameter active value must be one of :" + ACTIVE_STATUS.toString());
        }

        List<LinkedHashMap<String,String>> results = qaService.listQAScripts(schema, active);
       
        return new ResponseEntity<List<LinkedHashMap<String,String>>>(results, HttpStatus.OK);
    }


    @ExceptionHandler(EmptyParameterException.class)
    public ResponseEntity<HashMap<String, String>> HandleEmptyParameterException(Exception exception) {
        
        LOGGER.info("QAController Empty Parameter Exception:",exception);
        HashMap<String, String> errorResult = new HashMap<String, String>();
        errorResult.put("httpStatusCode", HttpStatus.BAD_REQUEST.toString());
        errorResult.put("errorMessage", exception.getMessage());
        return new ResponseEntity<HashMap<String, String>>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(XMLConvException.class)
    public ResponseEntity<HashMap<String, String>> HandleXMLConvException(Exception exception, HttpServletResponse response) {
        LOGGER.error("XMLConv Exception:",exception);
           HashMap<String, String> errorResult = new HashMap<String, String>();
        errorResult.put("httpStatusCode", HttpStatus.INTERNAL_SERVER_ERROR.toString());
        errorResult.put("errorMessage", exception.getMessage());
        return new ResponseEntity<HashMap<String, String>>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<HashMap<String, String>> HandleUnsupportedOperationException(Exception exception, HttpServletResponse response) {
        LOGGER.error("QAController Unsupported Operation Exception",exception);
        LinkedHashMap<String, String> errorResult = new LinkedHashMap<String, String>();
        errorResult.put("httpStatusCode", HttpStatus.NOT_IMPLEMENTED.toString());
        errorResult.put("errorMessage", exception.getMessage());
        return new ResponseEntity<HashMap<String, String>>(errorResult, HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<HashMap<String, String>> HandleBadRequestException(Exception exception, HttpServletResponse response) {
        LOGGER.info("QAController Bad Request Exception:",exception);
        LinkedHashMap<String, String> errorResult = new LinkedHashMap<String, String>();
        errorResult.put("httpStatusCode", HttpStatus.BAD_REQUEST.toString());
        errorResult.put("errorMessage", exception.getMessage());
        return new ResponseEntity<HashMap<String, String>>(errorResult, HttpStatus.BAD_REQUEST);
    }

    public String ConvertByteArrayToString(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, "UTF-8");
    }

}
