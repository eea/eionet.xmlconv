package eionet.gdem.web.spring.remoteapi;

import eionet.gdem.XMLConvException;
import eionet.gdem.qa.functions.Json;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.Utils;
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
import java.io.IOException;
import java.util.Map;

/**
 *
 *
 */
@Controller
@RequestMapping("/json2xml")
public class Json2XmlController {

    private MessageService messageService;
    private static final Logger LOGGER = LoggerFactory.getLogger(Json2XmlController.class);

    /** parameter name for passing json content or URL */
    protected static final String JSON_PARAM_NAME = "json";

    @Autowired
    public Json2XmlController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity action(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String jsonParam = null;

        Map params = request.getParameterMap();
        try {
            // parse request parameters
            if (params.containsKey(JSON_PARAM_NAME)) {
                jsonParam = ((String[]) params.get(JSON_PARAM_NAME))[0];
            }
            if (Utils.isNullStr(jsonParam)) {
                throw new XMLConvException("Missing request parameter: " + JSON_PARAM_NAME);
            }
            String xml = "";
            if (jsonParam.startsWith("http:")) {
                // append other parameters to service Url
                if (params.size() > 1) {
                    jsonParam = getJsonServiceUrl(jsonParam, params);
                }
                xml = Json.jsonRequest2xmlString(jsonParam);
            } else {
                xml = Json.jsonString2xml(jsonParam);
            }
            // set response properties
            response.setContentType("text/xml");
            response.setCharacterEncoding("UTF-8");
            response.setContentLength(xml.length());

            // write data into response
            response.getOutputStream().write(xml.getBytes());
        } catch (XMLConvException ge) {
            ge.printStackTrace();
            LOGGER.error("Unable to convert JSON to XML. " + ge.toString());
            throw new ServletException(ge);
        } catch (IOException ge) {
            ge.printStackTrace();
            LOGGER.error("Unable to convert JSON to XML. " + ge.toString());
            throw new ServletException(ge);
        } finally {
            try {
                response.getOutputStream().close();
                response.getOutputStream().flush();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                throw new ServletException(ioe);
            }
        }

        // Do nothing, then response is already sent.
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Append request parameters to the json web service URL.
     *
     * @param jsonParamUrl
     *            Json service URL received from request parameter
     * @param params
     *            map of request parameters
     * @return URL string
     */
    private String getJsonServiceUrl(String jsonParamUrl, Map<?, ?> params) {
        StringBuilder strBuilder = new StringBuilder(jsonParamUrl);
        boolean urlContainsParams = jsonParamUrl.contains("?");

        for (Map.Entry<?, ?> param : params.entrySet()) {
            String key = (String) param.getKey();
            if (!JSON_PARAM_NAME.equals(key)) {
                if (!urlContainsParams) {
                    strBuilder.append("?");
                    urlContainsParams = true;
                }
                if (params.get(key) != null) {
                    for (String value : (String[]) params.get(key)) {
                        strBuilder.append("&");
                        strBuilder.append(key + "=");
                        strBuilder.append(value);
                    }
                }
            }
        }
        return strBuilder.toString();
    }
}
