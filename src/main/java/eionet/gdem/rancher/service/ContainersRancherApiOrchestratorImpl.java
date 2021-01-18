package eionet.gdem.rancher.service;

import eionet.gdem.Properties;
import eionet.gdem.rancher.config.TemplateConfig;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.exception.RancherApiTimoutException;
import eionet.gdem.rancher.model.ContainerApiResponse;
import eionet.gdem.rancher.model.ContainerData;
import eionet.gdem.rancher.model.ServiceApiRequestBody;
import org.apache.commons.lang.time.StopWatch;
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
    public static volatile boolean lock;

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
        lock = true;
        String containerId = getContainerId(containerName);
        HttpEntity<ContainerData> entity = new HttpEntity<>(TemplateConfig.getHeaders());
        ResponseEntity<ContainerData> result;
        List<String> instancesBeforeDelete;
        StopWatch timer = new StopWatch();
        timer.start();
        try {
            ContainerApiResponse containerApiResponse = getContainerInfo(containerName);
            String serviceId = containerApiResponse.getData().get(0).getServiceIds().get(0);
            instancesBeforeDelete = servicesRancherApiOrchestrator.getContainerInstances(serviceId);
            result = restTemplate.exchange(rancherApiUrl + "/" + containerId, HttpMethod.DELETE, entity, ContainerData.class);
            String state = result.getBody().getState();
            LOGGER.info("Deleting container with id " + containerId + " for container with name " + containerName);
            while (!state.equals("running")) {
                try {
                    containerApiResponse = getContainerInfo(containerName);
                    if (containerApiResponse.getData().size()>0) {
                        state = containerApiResponse.getData().get(0).getState();
                    }
                } catch(RancherApiException e) {
                    LOGGER.info(e.getMessage());
                    continue;
                }
                if (timer.getTime()>60000) {
                    throw new RancherApiTimoutException("Time exceeded for creating new container " + containerName);
                }
            }
            scaleDownInstances(serviceId, instancesBeforeDelete);
        } catch (Exception e) {
            LOGGER.info("Error deleting container with name " + containerName + ": " + e.getMessage());
            throw new RancherApiException(e.getMessage());
        } finally {
            lock = false;
            timer.stop();
        }
        return result.getBody();
    }

    void scaleDownInstances(String serviceId, List<String> instancesBeforeDelete) throws RancherApiException {
        ServiceApiRequestBody serviceApiRequestBody = new ServiceApiRequestBody().setScale(instancesBeforeDelete.size()-1);
        try {
            servicesRancherApiOrchestrator.scaleUpOrDownContainerInstances(serviceId, serviceApiRequestBody);
        } catch (RancherApiException e) {
            LOGGER.info("Error during scaling down: " + e.getMessage());
        }
        List<String> instancesAfterDelete = servicesRancherApiOrchestrator.getContainerInstances(serviceId);
        if (instancesAfterDelete.size()!=instancesBeforeDelete.size()-1) {
            LOGGER.info("Scaling up before scaling down!");
            serviceApiRequestBody.setScale(instancesBeforeDelete.size());
            servicesRancherApiOrchestrator.scaleUpOrDownContainerInstances(serviceId, serviceApiRequestBody);
            serviceApiRequestBody.setScale(instancesBeforeDelete.size()-1);
            servicesRancherApiOrchestrator.scaleUpOrDownContainerInstances(serviceId, serviceApiRequestBody);
        }
    }

}



















