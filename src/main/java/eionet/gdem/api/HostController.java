package eionet.gdem.api;

import eionet.gdem.exceptions.RestApiException;
import eionet.gdem.http.HttpFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HostController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostController.class);

    @Autowired
    public HostController() {
    }

    /**
     *Schema information by xmlUrl
     *
     **/
    @RequestMapping(value = "/host/authentication/{hostName}", method = RequestMethod.GET)
    public String getAuthenticatedHostByName(@PathVariable String hostName) throws RestApiException {
        try {
            LOGGER.info("Retrieving authentication for host " + hostName);
            String authenticatedHost = HttpFileManager.getHostCredentials(hostName);
            return authenticatedHost;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new RestApiException("Could not retrieve authentication for host " + hostName);
        }
    }

}
