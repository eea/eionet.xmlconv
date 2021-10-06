package eionet.gdem.web.spring.remoteapi;

import eionet.gdem.Constants;
import eionet.gdem.SpringApplicationContext;
import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.qa.QueryService;
import eionet.gdem.services.MessageService;
import eionet.gdem.services.RunScriptAutomaticService;
import eionet.gdem.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 *
 *
 */
@Controller
@RequestMapping("/runQAScript")
public class QAScriptsApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QAScriptsApiController.class);
    private MessageService messageService;
    /** Script ID parameter name */
    protected static final String SCRIPT_ID_PARAM_NAME = "script_id";
    /** URL parameter name */
    protected static final String URL_PARAM_NAME = "url";

    @Autowired
    public QAScriptsApiController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping
    public ResponseEntity action(HttpServletRequest request, HttpServletResponse response) throws ServletException, XMLConvException, URISyntaxException {
        String scriptId = null;
        String url = null;

        // create custom HttpServletResponseWrapper
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(response);
        // get request parameters
        Map params = request.getParameterMap();
        // parse request parameters
        if (params.containsKey(SCRIPT_ID_PARAM_NAME)) {
            scriptId = (String) ((Object[]) params.get(SCRIPT_ID_PARAM_NAME))[0];
        }
        if (Utils.isNullStr(scriptId)) {
            throw new XMLConvException(SCRIPT_ID_PARAM_NAME + " parameter is missing from request.");
        }
        if (params.containsKey(URL_PARAM_NAME)) {
            url = (String) ((Object[]) params.get(URL_PARAM_NAME))[0];
            if (StringUtils.contains(url, Constants.SOURCE_URL_PARAM)) {
                String sourceUrl = new URI(url).getQuery();
                List<NameValuePair> parameters = URLEncodedUtils.parse(sourceUrl, StandardCharsets.UTF_8);
                for (NameValuePair param : parameters) {
                    if (Constants.SOURCE_URL_PARAM.equals(param.getName())) {
                        url = param.getValue();
                    }
                }
            }
        }
        if (Utils.isNullStr(url)) {
            throw new XMLConvException(URL_PARAM_NAME + " parameter is missing from request.");
        }

        // call QueryService
        //TODO we need Refactoring here. QueryService is not needed in the upgraded Scheduling Version.
        // Compare with old one(master branch) and enhance RunScriptAutomaticService
        QueryService xqs = new QueryService();
        // set up the servlet outputstream form converter
        xqs.setHttpResponse(methodResponse);
        xqs.setTicket(getTicket(request));
        // execute conversion
        getRunScriptAutomaticServiceBean().runQAScript(url, scriptId,methodResponse,true);

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

    @ExceptionHandler
    public void handleExceptions(Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(response);
        Map params = request.getParameterMap();
        methodResponse.flushXMLError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage(), "/runQAScript", params);
    }

    private static RunScriptAutomaticService getRunScriptAutomaticServiceBean() {
        return (RunScriptAutomaticService) SpringApplicationContext.getBean("runScriptAutomaticService");
    }
}
