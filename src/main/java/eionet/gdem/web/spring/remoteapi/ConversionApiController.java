package eionet.gdem.web.spring.remoteapi;

import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 *
 *
 */
@Controller
@RequestMapping
public class ConversionApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionApiController.class);
    private MessageService messageService;

    protected static final String CONVERT_ID_PARAM_NAME = "convert_id";
    protected static final String URL_PARAM_NAME = "url";

    @Autowired
    public ConversionApiController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping("/convert")
    public ResponseEntity action(HttpServletRequest request, HttpServletResponse response) throws ServletException, XMLConvException {
        String convertId = null;
        String url = null;

        // create custom HttpServletResponseWrapper
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(response);
        // get request parameters
        Map params = request.getParameterMap();
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

    @ExceptionHandler({XMLConvException.class, ServletException.class})
    public void handleExceptions(Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(response);
        Map params = request.getParameterMap();

        methodResponse.flushXMLError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage(), "/convert", params);
        /*return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);*/
    }
}
