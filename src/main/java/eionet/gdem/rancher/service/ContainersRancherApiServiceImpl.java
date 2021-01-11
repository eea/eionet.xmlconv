package eionet.gdem.rancher.service;

import eionet.gdem.Properties;
import eionet.gdem.rancher.config.TemplateConfig;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ContainerApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ContainersRancherApiServiceImpl implements ContainersRancherApiService {

    private RestTemplate restTemplate;
    private String rancherApiUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainersRancherApiServiceImpl.class);

    public ContainersRancherApiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        rancherApiUrl = Properties.rancherApiUrl + "/containers/";
    }

    @Override
    public String getContainerId(String containerName) throws RancherApiException {
        return getContainerInfo(containerName).getData()[0].getId();
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
}
