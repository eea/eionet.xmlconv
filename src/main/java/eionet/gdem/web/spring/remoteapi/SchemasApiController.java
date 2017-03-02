package eionet.gdem.web.spring.remoteapi;

import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dcm.remote.GetXMLSchemasResult;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *
 *
 */
@Controller
@RequestMapping("/schemasapi")
public class SchemasApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemasApiController.class);
    private MessageService messageService;

    @Autowired
    public SchemasApiController(MessageService messageService) {
        this.messageService = messageService;
    }

    public ResponseEntity action(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        // create custom HttpServletResponseWrapper
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(response);
        // get request parameters
        Map params = request.getParameterMap();

        try {
            // Call ConversionService
            ConversionServiceIF cs = new ConversionService();
            List schemas = cs.getXMLSchemas();
            // parse the result of Conversion Service method and format it as XML
            GetXMLSchemasResult xmlResult = new GetXMLSchemasResult();
            xmlResult.setResult(schemas);
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
}
