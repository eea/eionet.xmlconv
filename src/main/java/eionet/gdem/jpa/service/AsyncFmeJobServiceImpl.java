package eionet.gdem.jpa.service;

import eionet.gdem.Properties;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service("asyncFmeJobServiceImpl")
public class AsyncFmeJobServiceImpl implements AsyncFmeJobService {

    private RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncFmeJobServiceImpl.class);
    private static final String FME_TOKEN_HEADER="fmetoken token=";

    @Autowired
    public AsyncFmeJobServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean jobHasStatusSuccessOnFmeServer(Long fmeJobId) {
        HttpHeaders headers = getHttpHeaders();
        headers.add("Authorization", FME_TOKEN_HEADER + Properties.fmeToken);
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Object> response = restTemplate.exchange(Properties.fmeUrl + "/id/" + fmeJobId, HttpMethod.GET, entity, Object.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("Error while trying to find status in fme server of job with fme job id " + fmeJobId);
        }
        return false;
    }

    @Override
    public void cancelJobOnFMEServer(Long fmeJobId) {
        HttpHeaders headers = getHttpHeaders();
        String credentials = Properties.fmeUser + ":" + Properties.fmePassword;
        String encodedCredentials =
                new String(Base64.encodeBase64(credentials.getBytes()));
        headers.add("Authorization", "Basic " + encodedCredentials);

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(Properties.fmeUrl + "/active/" + fmeJobId, HttpMethod.DELETE, entity, String.class);
            int statusCode = response.getStatusCode().value();
            if (statusCode==HttpStatus.NO_CONTENT.value()) {
                LOGGER.info("Fme asynchronous job with fme job id " + fmeJobId + " successfully cancelled in fme server");
            } else if (statusCode==HttpStatus.NOT_FOUND.value()) {
                LOGGER.info("Fme asynchronous job with fme job id " + fmeJobId + " not found in fme server");
            }
        } catch (Exception e) {
            LOGGER.error("Error while trying to cancel fme asynchronous job with fme job id " + fmeJobId + " in fme server: " + e.getMessage());
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mediaTypes = new ArrayList();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypes);
        return headers;
    }
}




















