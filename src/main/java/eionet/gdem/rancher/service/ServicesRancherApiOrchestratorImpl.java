package eionet.gdem.rancher.service;

import eionet.gdem.Properties;
import eionet.gdem.rancher.config.TemplateConfig;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.exception.RancherApiTimoutException;
import eionet.gdem.rancher.model.RancherApiNewServiceRequestBody;
import eionet.gdem.rancher.model.ServiceApiRequestBody;
import eionet.gdem.rancher.model.ServiceApiResponse;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ServicesRancherApiOrchestratorImpl implements ServicesRancherApiOrchestrator {

    private RestTemplate restTemplate;
    private RancherApiNewServiceRequestBodyCreator rancherApiNewServiceRequestBodyCreator;
    private String rancherApiUrl;
    private static final Integer TIME_LIMIT = 60000;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicesRancherApiOrchestratorImpl.class);

    @Autowired
    public ServicesRancherApiOrchestratorImpl(RestTemplate restTemplate, RancherApiNewServiceRequestBodyCreator rancherApiNewServiceRequestBodyCreator) {
        this.restTemplate = restTemplate;
        this.rancherApiNewServiceRequestBodyCreator = rancherApiNewServiceRequestBodyCreator;
        rancherApiUrl = Properties.rancherApiUrl + "/services/";
    }

    @Override
    public List<String> getContainerInstances(String serviceId) throws RancherApiException {
        ServiceApiResponse response = getServiceInfo(serviceId);
        return response.getInstanceIds();
    }

    @Override
    public ServiceApiResponse getServiceInfo(String serviceId) throws RancherApiException {
        HttpEntity<ServiceApiResponse> entity = new HttpEntity<>(TemplateConfig.getHeaders());
        ResponseEntity<ServiceApiResponse> result;
        try {
            result = restTemplate.exchange(rancherApiUrl + serviceId, HttpMethod.GET, entity, ServiceApiResponse.class);
        } catch (Exception e) {
            LOGGER.info("Error getting service information for service with id " + serviceId + ": " + e.getMessage());
            throw new RancherApiException(e.getMessage());
        }
        return result.getBody();
    }

    @Override
    public synchronized ServiceApiResponse scaleUpOrDownContainerInstances(String serviceId, ServiceApiRequestBody serviceApiRequestBody) throws RancherApiException {
        HttpEntity<ServiceApiRequestBody> entity = new HttpEntity<>(serviceApiRequestBody, TemplateConfig.getHeaders());
        ResponseEntity<ServiceApiResponse> result;
        StopWatch timer = new StopWatch();
        try {
            timer.start();
            result = restTemplate.exchange(rancherApiUrl + serviceId, HttpMethod.PUT, entity, ServiceApiResponse.class);
            String state = getServiceInfo(serviceId).getState();
            while (state.equals("updating-active")) {
                state = getServiceInfo(serviceId).getState();
                if (timer.getTime()>TIME_LIMIT) {
                    throw new RancherApiTimoutException("Time exceeded for getting service info of service " + serviceId);
                }
            }
        } catch (Exception e) {
            LOGGER.info("Error scaling container instances for service with id " + serviceId + ": " + e.getMessage());
            throw new RancherApiException(e.getMessage());
        } finally {

        }
        return result.getBody();
    }

    @Override
    public ServiceApiResponse createService(String serviceName) throws RancherApiException {
        RancherApiNewServiceRequestBody rancherApiNewServiceRequestBody = rancherApiNewServiceRequestBodyCreator.buildBody(serviceName, Properties.rancherJobExecutorStackId);
        HttpEntity<RancherApiNewServiceRequestBody> entity = new HttpEntity<>(rancherApiNewServiceRequestBody, TemplateConfig.getHeaders());
        ResponseEntity<ServiceApiResponse> result;
        try {
            result = restTemplate.exchange(rancherApiUrl, HttpMethod.POST, entity, ServiceApiResponse.class);
        } catch (Exception e) {
            LOGGER.info("Error creating new service for stack with id " + Properties.rancherJobExecutorStackId + ": " + e.getMessage());
            throw new RancherApiException(e.getMessage());
        }
        return result.getBody();
    }

    @Override
    public ServiceApiResponse deleteService(String serviceId) throws RancherApiException {
        HttpEntity<ServiceApiResponse> entity = new HttpEntity<>(TemplateConfig.getHeaders());
        ResponseEntity<ServiceApiResponse> result;
        try {
            result = restTemplate.exchange(rancherApiUrl + serviceId, HttpMethod.DELETE, entity, ServiceApiResponse.class);
        } catch (Exception e) {
            LOGGER.info("Error deleting service with id " + serviceId + ": " + e.getMessage());
            throw new RancherApiException(e.getMessage());
        }
        return result.getBody();
    }

}




























