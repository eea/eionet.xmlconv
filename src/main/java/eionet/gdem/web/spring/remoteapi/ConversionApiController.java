package eionet.gdem.web.spring.remoteapi;

import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.dcm.remote.ListConversionsResult;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.MultipartFileUpload;
import eionet.gdem.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.Map;
import java.util.Vector;

/**
 *
 *
 */
@Controller
@RequestMapping("/api/convert")
public class ConversionApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionApiController.class);
    private MessageService messageService;

    protected static final String CONVERT_ID_PARAM_NAME = "convert_id";
    protected static final String URL_PARAM_NAME = "url";
    protected static final String SCHEMA_PARAM_NAME = "schema";
    /** Binary data of the file. */
    public static final String CONVERT_FILE_PARAM_NAME = "convert_file";
    /** File name or URL of the file original location. */
    public static final String FILE_NAME_PARAM_NAME = "file_name";

    @Autowired
    public ConversionApiController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/convert")
    public ResponseEntity action(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String convertId = null;
        String url = null;

        // create custom HttpServletResponseWrapper
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(response);
        // get request parameters
        Map params = request.getParameterMap();
        try {
            // parse request parameters
            if (params.containsKey(CONVERT_ID_PARAM_NAME)) {
                convertId = (String) ((Object[]) params.get(CONVERT_ID_PARAM_NAME))[0];
            }
            if (Utils.isNullStr(convertId)) {
                throw new XMLConvException(CONVERT_ID_PARAM_NAME + " parameter is missing from request.");
            }
            if (params.containsKey(URL_PARAM_NAME)) {
                url = (String) ((Object[]) params.get(URL_PARAM_NAME))[0];
            }
            if (Utils.isNullStr(url)) {
                throw new XMLConvException(URL_PARAM_NAME + " parameter is missing from request.");
            }

            // call ConversionService
            ConversionServiceIF cs = new ConversionService();
            // set up the servlet outputstream form converter
            cs.setHttpResponse(methodResponse);
            cs.setTicket(getTicket(request));
            // execute conversion
            cs.convert(url, convertId);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            try {
                // error happened
                methodResponse.flushXMLError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), null, params);
            } catch (Exception ge) {
                LOGGER.error("Unable to flush XML error: " + ge.toString());
                throw new ServletException(ge);
            }
        } finally {
            if (methodResponse != null) {
                try {
                    // flush the content
                    methodResponse.flush();
                } catch (Exception e) {
                    LOGGER.error("Unable to close Servlet Output Stream.", e);
                    e.printStackTrace();
                }
            }
        }
        // Do nothing, then response is already sent.
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/convertPush")
    public ResponseEntity push(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        InputStream fileInput = null;
        Map params = null;

        // create custom HttpServletResponseWrapper
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(response);
        try {
            String convertId = null;
            String fileName = null;

            // parse multipart form data
            MultipartFileUpload fu = new MultipartFileUpload(false);
            fu.processMultiPartRequest(request);
            params = fu.getRequestParams();

            // get convert_id parameter
            if (params.containsKey(CONVERT_ID_PARAM_NAME)) {
                convertId = (String) params.get(CONVERT_ID_PARAM_NAME);
            }
            if (Utils.isNullStr(convertId)) {
                throw new XMLConvException(CONVERT_ID_PARAM_NAME + " parameter is missing from request.");
            }

            // get the file as inputstream from request
            fileInput = fu.getFileAsInputStream(CONVERT_FILE_PARAM_NAME);
            // get file name from parameter, if this is not provided then use real file name from multipart content.
            if (params.containsKey(FILE_NAME_PARAM_NAME)) {
                fileName = (String) params.get(FILE_NAME_PARAM_NAME);
            } else {
                fileName = fu.getFileName(CONVERT_FILE_PARAM_NAME);
            }
            // XXX: Convert to Spring ResponseEntity
            // call ConversionService
            ConversionServiceIF cs = new ConversionService();
            // set up the servlet outputstream form converter
            cs.setHttpResponse(methodResponse);
            // execute conversion
            cs.convertPush(fileInput, convertId, fileName);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            try {
                // error happened
                methodResponse.flushXMLError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), null, params);
            } catch (Exception ge) {
                LOGGER.error("Unable to flush XML error: " + ge.toString());
                throw new ServletException(ge);
            }
        } finally {
            if (methodResponse != null) {
                try {
                    // flush the content
                    methodResponse.flush();
                } catch (Exception e) {
                    LOGGER.error("Unable to close Servlet Output Stream.", e);
                    e.printStackTrace();
                }
            }
            IOUtils.closeQuietly(fileInput);
        }
        // Do nothing, the response is already sent.
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity listConversions(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        // create custom HttpServletResponseWrapper
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(response);
        // get request parameters
        Map params = request.getParameterMap();

        try {
            String schema = null;
            if (params.containsKey(SCHEMA_PARAM_NAME)) {
                schema = (String) ((Object[]) params.get(SCHEMA_PARAM_NAME))[0];
            }
            if (Utils.isNullStr(schema)) {
                schema = null;
            }

            // Call ConversionService
            ConversionServiceIF cs = new ConversionService();
            Vector v = cs.listConversions(schema);

            // parse the result of Conversion Service method and format it as XML
            ListConversionsResult xmlResult = new ListConversionsResult();
            xmlResult.setResult(v);
            xmlResult.writeXML();
            // flush the result into servlet outputstream
            methodResponse.flushXML(xmlResult);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            try {
                // if error happened, then flush the error in XML format into servlet outputstream
                methodResponse.flushXMLError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), null, params);
            } catch (Exception ge) {
                LOGGER.error("Unable to flush XML error: " + ge.toString());
                throw new ServletException(ge);
            }
        }
        // Do nothing, then response is already sent.
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Returns ticket
     * @param req Request
     * @return Ticket
     */
    protected String getTicket(HttpServletRequest req) {
        String ticket = null;
        HttpSession httpSession = req.getSession(false);
        if (httpSession != null) {
            ticket = (String) httpSession.getAttribute(Constants.TICKET_ATT);
        }

        return ticket;
    }
}
