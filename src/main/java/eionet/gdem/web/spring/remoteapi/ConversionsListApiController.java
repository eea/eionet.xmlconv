package eionet.gdem.web.spring.remoteapi;

import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.dcm.remote.ListConversionsResult;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Vector;

/**
 *
 *
 */
@Controller
@RequestMapping("/listConversions")
public class ConversionsListApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionsListApiController.class);
    private MessageService messageService;

    protected static final String SCHEMA_PARAM_NAME = "schema";


    @GetMapping
    public ResponseEntity listConversions(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // create custom HttpServletResponseWrapper
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(response);
        // get request parameters
        Map params = request.getParameterMap();

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

        return new ResponseEntity(HttpStatus.OK);
    }

    @ExceptionHandler
    public void handleExceptions(Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(response);
        Map params = request.getParameterMap();
        methodResponse.flushXMLError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage(), "/listConversions", params);
    }
}
