package eionet.gdem.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.api.qa.web.QaController;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.RestApiException;
import eionet.gdem.http.HttpFileManager;
import net.sf.json.JSONObject;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class HostController {

    HttpFileManager fileManager = new HttpFileManager();
    private static final Logger LOGGER = LoggerFactory.getLogger(HostController.class);

    /**
     *Schema information by xmlUrl
     *
     **/
    @RequestMapping(value = "/host/authentication/{hostName}", method = RequestMethod.GET)
    public String getAuthenticatedHostByName(@PathVariable String hostName) throws RestApiException {
        try {
            LOGGER.info("Retrieving authentication for host " + hostName);
            String authenticatedHost = fileManager.getHostCredentials(hostName);
            return authenticatedHost;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new RestApiException("Could not retrieve authentication for host " + hostName);
        }
    }

}
