package eionet.gdem.rancher.service;

import eionet.gdem.Properties;
import eionet.gdem.rancher.config.TemplateConfig;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ContainerApiResponse;
import eionet.gdem.rancher.model.ContainerData;
import eionet.gdem.rancher.model.ServiceApiRequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ContainersRancherApiOrchestratorImpl implements ContainersRancherApiOrchestrator {

    private RestTemplate restTemplate;
    private String rancherApiUrl;
    private ServicesRancherApiOrchestrator servicesRancherApiOrchestrator;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainersRancherApiOrchestratorImpl.class);

    public ContainersRancherApiOrchestratorImpl(RestTemplate restTemplate, ServicesRancherApiOrchestrator servicesRancherApiOrchestrator) {
        this.restTemplate = restTemplate;
        rancherApiUrl = Properties.rancherApiUrl + "/containers/";
        this.servicesRancherApiOrchestrator = servicesRancherApiOrchestrator;
    }

    @Override
    public String getContainerId(String containerName) throws RancherApiException {
        return getContainerInfo(containerName).getData().get(0).getId();
    }

    @Override
    public ContainerApiResponse getContainerInfo(String containerName) throws RancherApiException {
        HttpEntity<ContainerApiResponse> entity = new HttpEntity<>(TemplateConfig.getHeaders());
        ResponseEntity<ContainerApiResponse> result;
        try {
            result = restTemplate.exchange(rancherApiUrl + "?name={containerName}", HttpMethod.GET, entity, ContainerApiResponse.class, containerName);
        } catch (Exception e) {
            LOGGER.info("Error getting container information of container with name: " + containerName + ": " + e.getMessage());
            throw new RancherApiException(e.getMessage());
        }
        return result.getBody();
    }

    @Override
    public ContainerData startContainer(String containerName) throws RancherApiException {
        String containerId = getContainerId(containerName);
        HttpEntity<ContainerData> entity = new HttpEntity<>(TemplateConfig.getHeaders());
        ResponseEntity<ContainerData> result;
        try {
            result = restTemplate.exchange(rancherApiUrl + "/" + containerId + "?action=start", HttpMethod.POST, entity, ContainerData.class);
        } catch (Exception e) {
            LOGGER.info("Error starting container with name: " + containerName + ": " + e.getMessage());
            throw new RancherApiException(e.getMessage());
        }
        return result.getBody();
    }

    @Override
    public ContainerData stopContainer(String containerName) throws RancherApiException {
        String containerId = getContainerId(containerName);
        HttpEntity<ContainerData> entity = new HttpEntity<>(TemplateConfig.getHeaders());
        ResponseEntity<ContainerData> result;
        try {
            result = restTemplate.exchange(rancherApiUrl + "/" + containerId + "?action=stop", HttpMethod.POST, entity, ContainerData.class);
        } catch (Exception e) {
            LOGGER.info("Error stopping container with name: " + containerName + ": " + e.getMessage());
            throw new RancherApiException(e.getMessage());
        }
        return result.getBody();
    }

    @Override
    public synchronized ContainerData deleteContainer(String containerName) throws RancherApiException {
        String containerId = getContainerId(containerName);
        HttpEntity<ContainerData> entity = new HttpEntity<>(TemplateConfig.getHeaders());
        ResponseEntity<ContainerData> result;
        try {
            ContainerApiResponse containerApiResponse = getContainerInfo(containerName);
            result = restTemplate.exchange(rancherApiUrl + "/" + containerId, HttpMethod.DELETE, entity, ContainerData.class);
            String state = result.getBody().getState();
            LOGGER.info("Deleting container with id " + containerId);
            while (!state.equals("running")) {
                containerApiResponse = getContainerInfo(containerName);
                if (containerApiResponse.getData().size()>0) {
                    state = containerApiResponse.getData().get(0).getState();
                }
            }
            String serviceId = containerApiResponse.getData().get(0).getServiceIds().get(0);
            List<String> instances = servicesRancherApiOrchestrator.getContainerInstances(serviceId);
            ServiceApiRequestBody serviceApiRequestBody = new ServiceApiRequestBody().setScale(instances.size()-1);
            servicesRancherApiOrchestrator.scaleUpOrDownContainerInstances(serviceId, serviceApiRequestBody);
        } catch (Exception e) {
            LOGGER.info("Error deleting container with name: " + containerName + ": " + e.getMessage());
            throw new RancherApiException(e.getMessage());
        }
        return result.getBody();
    }

}



















