package eionet.gdem.rancher.service;

import eionet.gdem.Properties;
import eionet.gdem.rancher.config.TemplateConfig;
import eionet.gdem.rancher.exception.ContainerScalingFailedException;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.exception.RancherApiTimoutException;
import eionet.gdem.rancher.model.ContainerApiResponse;
import eionet.gdem.rancher.model.ContainerData;
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
public class ContainersRancherApiOrchestratorImpl implements ContainersRancherApiOrchestrator {

    private RestTemplate restTemplate;
    private String rancherApiUrl;
    private ServicesRancherApiOrchestrator servicesRancherApiOrchestrator;

    @Autowired
    private ServicesRancherApiOrchestrator servicesOrchestrator;

    /**
     * time in milliseconds
     */
    private static final Integer TIME_LIMIT = 60000;
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainersRancherApiOrchestratorImpl.class);

    public ContainersRancherApiOrchestratorImpl(RestTemplate restTemplate, ServicesRancherApiOrchestrator servicesRancherApiOrchestrator) {
        this.restTemplate = restTemplate;
        rancherApiUrl = Properties.rancherApiUrl + "/containers";
        this.servicesRancherApiOrchestrator = servicesRancherApiOrchestrator;
    }

    @Override
    public String getContainerId(String containerName) throws RancherApiException {
        try {
            LOGGER.info("Retrieving id for container with name: '" + containerName + "'");
            ContainerApiResponse containerApiResponse = getContainerInfo(containerName);
            return containerApiResponse.getData().get(0).getId();
        } catch (RancherApiException rae) {
            LOGGER.info("Error getting container id of container with name: " + containerName + ", " + rae.getMessage());
            throw rae;
        }
    }

    @Override
    public ContainerApiResponse getContainerInfo(String containerName) throws RancherApiException {
        HttpEntity<ContainerApiResponse> entity = new HttpEntity<>(TemplateConfig.getHeaders());
        ResponseEntity<ContainerApiResponse> result;
        try {
            result = restTemplate.exchange(rancherApiUrl + "?name={containerName}", HttpMethod.GET, entity, ContainerApiResponse.class, containerName);
        } catch (Exception e) {
            LOGGER.info("Error getting container information of container with name: " + containerName + ", " + e);
            throw new RancherApiException(e);
        }
        return result.getBody();
    }

    @Override
    public ContainerData getContainerInfoById(String containerId) throws RancherApiException {
        HttpEntity<ContainerData> entity = new HttpEntity<>(TemplateConfig.getHeaders());
        ResponseEntity<ContainerData> result;
        try {
            result = restTemplate.exchange(rancherApiUrl + "/" + containerId, HttpMethod.GET, entity, ContainerData.class);
        } catch (Exception e) {
            LOGGER.info("Error getting container information of container with id: " + containerId + ": " + e.getMessage());
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
    public ContainerData restartContainer(String containerName) throws RancherApiException {
        String containerId = getContainerId(containerName);
        HttpEntity<ContainerData> entity = new HttpEntity<>(TemplateConfig.getHeaders());
        ResponseEntity<ContainerData> result;
        StopWatch timer = new StopWatch();
        try {
            timer.start();
            result = restTemplate.exchange(rancherApiUrl + "/" + containerId + "?action=restart", HttpMethod.POST, entity, ContainerData.class);
            String state = result.getBody().getState();
            LOGGER.info("Restarting container with id " + containerId + " for container with name " + containerName);
            ContainerApiResponse containerApiResponse;
            while (!state.equals("running")) {
                try {
                    containerApiResponse = getContainerInfo(containerName);
                    if (containerApiResponse.getData().size() > 0) {
                        state = containerApiResponse.getData().get(0).getState();
                    }
                } catch (RancherApiException e) {
                    LOGGER.info(e.getMessage());
                    continue;
                }
                if (timer.getTime() > TIME_LIMIT) {
                    throw new RancherApiTimoutException("Time exceeded for restarting container " + containerName);
                }
            }
        } catch (Exception e) {
            LOGGER.info("Error restarting container with name: " + containerName + ": " + e.getMessage());
            throw new RancherApiException(e.getMessage());
        } finally {
            timer.stop();
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
    public synchronized void deleteContainer(String containerName) throws RancherApiException {
        StopWatch timer = new StopWatch();
        ResponseEntity<ContainerData> newContainerReplacingTheJustDeletedOne = null;
        try {
            timer.start();
            HttpEntity<ContainerData> entity = new HttpEntity<>(TemplateConfig.getHeaders());
            String containerId = getContainerId(containerName);
            ContainerApiResponse containerApiResponse = getContainerInfo(containerName);
            String serviceId = containerApiResponse.getData().get(0).getServiceIds().get(0);
            //instancesBeforeDelete is the number of container instances we have before we start deleting a container
            List<String> instancesBeforeDelete = servicesRancherApiOrchestrator.getContainerInstances(serviceId);

            //After we delete a container, rancher will immediately replace it with a new one, which we have in the result.
            //THis happens because rancher has already in mind a scale number which we haven't yet touched, so it tries to replace what we delete.
            newContainerReplacingTheJustDeletedOne = restTemplate.exchange(rancherApiUrl + "/" + containerId, HttpMethod.DELETE, entity, ContainerData.class);
            String state = newContainerReplacingTheJustDeletedOne.getBody().getState();
            LOGGER.info("Deleting container with id " + containerId + " for container with name " + containerName);
            while (!state.equals("running")) {
                try {
                    containerApiResponse = getContainerInfo(containerName);
                    if (containerApiResponse.getData().size() > 0) {
                        state = containerApiResponse.getData().get(0).getState();
                    }
                } catch (RancherApiException e) {
                    LOGGER.error("Error getting information for container " + containerName + ", " + e);
                    throw new RancherApiException(e);
                }
                if (timer.getTime() > TIME_LIMIT) {
                    throw new RancherApiTimoutException("Time exceeded for creating new container " + containerName);
                }
            }
            int scaleDownCount = 0;
            int maxRetries = 2;
            while (true) {
                try {
                    // Scale down will do the needed scale down of the containers, and rancher will delete the last container created which is fine
                    //since the last one is the one created above.
                    scaleDownInstancesByOne(serviceId, instancesBeforeDelete);
                    return;
                } catch (ContainerScalingFailedException e) {
                    LOGGER.error("Scaling down by 1 failed: " + e.getMessage());
                    if (++scaleDownCount == maxRetries) return;
                }
            }
        } catch (Exception e) {
            LOGGER.info("Error deleting container with name " + containerName + ": " + e.getMessage());
            throw new RancherApiException(e.getMessage());
        } finally {
            timer.stop();
        }
    }

    @Override
    public void synchronizeRancherScaleAndActualContainers(String serviceId) throws RancherApiException {
        LOGGER.info("Attempting to synchronize Rancher Scale number and actual containers ");
        ServiceApiResponse serviceInfo = servicesOrchestrator.getServiceInfo(serviceId);
        List<String> instances = servicesOrchestrator.getContainerInstances(serviceId);
        if (serviceInfo.getScale() < instances.size()) {
            Integer newScale = instances.size() - serviceInfo.getScale();
            ServiceApiRequestBody serviceApiRequestBody = new ServiceApiRequestBody().setScale(serviceInfo.getScale() + newScale);
            LOGGER.info("Scaling up again because of error");
            servicesOrchestrator.scaleUpOrDownContainerInstances(serviceId, serviceApiRequestBody);
        }
    }

    /**
     * This method will delete the last container in the list, which we expect to be the new container that immediately replaced
     * the one we tried to delete.
     **/
    synchronized void scaleDownInstancesByOne(String serviceId, List<String> instancesBeforeDelete) throws ContainerScalingFailedException {
        ServiceApiRequestBody serviceApiRequestBody = new ServiceApiRequestBody().setScale(instancesBeforeDelete.size() - 1);
        try {
            servicesRancherApiOrchestrator.scaleUpOrDownContainerInstances(serviceId, serviceApiRequestBody);
        } catch (RancherApiException e) {
            //We assume that the scale down failed here, and rancher has still the original number of containers
            LOGGER.error("Error during scaling down: " + e.getMessage());
            try {
                this.synchronizeRancherScaleAndActualContainers(serviceId);
            } catch (RancherApiException rancherApiException) {
                LOGGER.error(rancherApiException.getMessage());
            }
            throw new ContainerScalingFailedException();
        }
        //By now we should have the containers reduced by one
        //Below we check the case where the containers never actually scaled down by one.
        List<String> instancesAfterDelete = null;
        try {
            instancesAfterDelete = servicesRancherApiOrchestrator.getContainerInstances(serviceId);
        } catch (RancherApiException ex) {
            throw new ContainerScalingFailedException("Failed to Scale down containers by 1");
        }
        if (instancesAfterDelete.size() == instancesBeforeDelete.size()) {
            LOGGER.info("Scale Down Failed.");
            try {
                this.synchronizeRancherScaleAndActualContainers(serviceId);
            } catch (RancherApiException rancherApiException) {
                LOGGER.error(rancherApiException.getMessage());
            }
            throw new ContainerScalingFailedException("Failed to Scale down containers by 1");
        }
        if (instancesAfterDelete.size() == instancesBeforeDelete.size() - 1) {
            LOGGER.info("Scaled down Successfully.");
            return;
        }

    }


}
