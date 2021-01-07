package eionet.gdem.rancher.service;

import eionet.gdem.Properties;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ServiceBody;
import eionet.gdem.rancher.model.ServiceResponse;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RancherServicesApiServiceImpl implements RancherServicesApiService {

    private RestTemplate restTemplate;
    private String rancherApiUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(RancherServicesApiServiceImpl.class);

    @Autowired
    public RancherServicesApiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        rancherApiUrl = Properties.rancherApiUrl + "/" + Properties.rancherApiProjectId + "/services/";
    }


    @Override
    public ServiceResponse getServiceInfo(String serviceId) throws RancherApiException {
        HttpEntity<ServiceBody> entity = new HttpEntity<>(getHeaders());
        ResponseEntity<ServiceResponse> result;
        try {
            result = restTemplate.exchange(rancherApiUrl + serviceId, HttpMethod.GET, entity, ServiceResponse.class);
        } catch (Exception e) {
            LOGGER.info("Error scaling up container instances for service with id " + serviceId + ": " + e.getMessage());
            throw new RancherApiException(e.getMessage());
        }
        return result.getBody();
    }

    @Override
    public String scaleUpContainerInstances(String serviceId, ServiceBody serviceBody) {
        HttpEntity<ServiceBody> entity = new HttpEntity<>(serviceBody, getHeaders());
        try {
            restTemplate.exchange(rancherApiUrl + serviceId, HttpMethod.PUT, entity, ServiceResponse.class);
        } catch (Exception e) {
            LOGGER.info("Error scaling up container instances for service with id " + serviceId + ": " + e.getMessage());
            return "failure";
        }
        return "success";
    }

    @Override
    public String removeContainerInstances(String serviceId, ServiceBody serviceBody) {
        HttpEntity<ServiceBody> entity = new HttpEntity<>(serviceBody, getHeaders());
        try {
            restTemplate.exchange(rancherApiUrl + serviceId, HttpMethod.PUT, entity, ServiceResponse.class);
        } catch (Exception e) {
            LOGGER.info("Error removing container instances for service with id " + serviceId + ": " + e.getMessage());
            return "failure";
        }
        return "success";
    }

    private HttpHeaders getHeaders() {
        String credentials = Properties.rancherApiAccessKey + ":" + Properties.rancherApiSecretKey;
        String encodedCredentials =
                new String(Base64.encodeBase64(credentials.getBytes()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + encodedCredentials);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
