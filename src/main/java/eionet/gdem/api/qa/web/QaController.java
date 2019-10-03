package eionet.gdem.api.qa.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.api.errors.BadRequestException;
import eionet.gdem.api.errors.EmptyParameterException;
import eionet.gdem.api.qa.model.EnvelopeWrapper;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConvertDDXMLMethod;
import eionet.gdem.dto.ConversionResultDto;
import eionet.gdem.qa.XQueryService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static eionet.gdem.qa.ScriptStatus.getActiveStatusList;
import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    public QaController(QaService qaService) {
        this.qaService = qaService;
    }


    /**
     * Method specific for Habitats Directive - allows uploading two files for QA checks
     * @param report
     * @param checklist
     * @param request
     * @return
     * @throws XMLConvException
     * @throws IOException
     */
    @RequestMapping(value = "/dataflows/nature", method = RequestMethod.POST)
    public String uploadFiles(@RequestParam("report") MultipartFile report, @RequestParam(value= "checklist", required = false) MultipartFile checklist, HttpServletRequest request) throws XMLConvException, IOException {

        if (report.isEmpty()) {
            throw new XMLConvException("Report file is mandatory");
        }

        String scriptId = request.getHeader("scriptId");
        if (scriptId==null) {
            scriptId = "-1";
        }

        String parentdir = eionet.gdem.Properties.appRootFolder + "/tmpfile/";
        String country = StringUtils.substringBefore(report.getOriginalFilename(), "_");
//        String uuid = "df-" + System.currentTimeMillis() + "-" + UUID.randomUUID();
        String uuid = "habitats-df-" + country;
        String tmpdir = parentdir + uuid;
        if (Files.exists(Paths.get(tmpdir))) {
            FileUtils.cleanDirectory(new File(tmpdir));
        }
        Files.createDirectories(Paths.get(tmpdir));

        File dest1 = new File(tmpdir + "/" + report.getOriginalFilename());
        FileUtils.copyInputStreamToFile(report.getInputStream(), dest1);

        if (checklist != null && !checklist.isEmpty()) {
            File dest2 = new File(tmpdir + "/" + checklist.getOriginalFilename());
            FileUtils.copyInputStreamToFile(checklist.getInputStream(), dest2);
        }
        String fileURL = "https://" + eionet.gdem.Properties.appHost + "/tmpfile/" + uuid + "/" + dest1.getName();

        //we set scriptId=-1 to perform only xml validation for now
        Vector results = qaService.runQaScript(fileURL, scriptId);

        LinkedHashMap<String, String> jsonResults = new LinkedHashMap<String, String>();
        jsonResults.put("feedbackStatus", ConvertByteArrayToString((byte[]) results.get(2)));
        jsonResults.put("feedbackMessage", ConvertByteArrayToString((byte[]) results.get(3)));
        jsonResults.put("feedbackContentType", results.get(0).toString());
        jsonResults.put("feedbackContent", ConvertByteArrayToString((byte[]) results.get(1)));
        return ConvertByteArrayToString((byte[]) results.get(1));
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

    /**
     * Edpoint to test xmlrpc
     *
     **/
    @RequestMapping(value = "/qarpc", method = RequestMethod.GET)
    @ResponseBody
    public List<QaResultsWrapper> analyzeXMlFilesXMlRpc(@RequestParam(value = "schema", required = false) String schema, @RequestParam(value = "file", required = false, defaultValue = "true") String file) throws XMLConvException, BadRequestException {

        HashMap<String, String> fileLinksAndSchemas =new LinkedHashMap<>();
        fileLinksAndSchemas.put(file,schema);
        XQueryService xqService = new XQueryService();
        Hashtable table = new Hashtable();
        try {
            for (Map.Entry<String, String> entry : fileLinksAndSchemas.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != "" && value != "") {
                    Vector files = new Vector();
                    files.add(key);
                    table.put(value, files);
                }
            }
            Vector jobIdsAndFileUrls = xqService.analyzeXMLFiles(table);
            List<QaResultsWrapper> results = new ArrayList<QaResultsWrapper>();
            for (int i = 0; i < jobIdsAndFileUrls.size(); i++) {
                Vector<String> KeyValuePair = (Vector<String>) jobIdsAndFileUrls.get(i);
                QaResultsWrapper qaResult = new QaResultsWrapper();
                qaResult.setJobId(KeyValuePair.get(0));
                qaResult.setFileUrl(KeyValuePair.get(1));
                results.add(qaResult);
            }

            return results;
        } catch (XMLConvException ex) {
            throw new XMLConvException("error scheduling Jobs with XQueryService ", ex);
        }
    }

    /**
     * Edpoint to test xmlrpc
     *
     **/
    @RequestMapping(value = "/excelXML", method = RequestMethod.GET)
    @ResponseBody
    public Hashtable<String, Object> convertExcelToXML(@RequestParam(value = "sourceUrl", required = false, defaultValue = "true") String sourceUrl) throws XMLConvException, BadRequestException {

        ConversionService conversionService = new ConversionService();
        Hashtable<String, Object> result =    conversionService.convertDD_XMLremoveEmptyElems(sourceUrl);

             return result;
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
        errorResult.put("errorDescription", String.valueOf(ExceptionUtils.getRootCause(exception)));
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
