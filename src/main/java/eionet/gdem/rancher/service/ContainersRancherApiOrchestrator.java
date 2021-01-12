package eionet.gdem.rancher.service;

import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ContainerApiResponse;

public interface ContainersRancherApiOrchestrator {

    String getContainerId(String containerName) throws RancherApiException;

    ContainerApiResponse getContainerInfo(String containerName) throws RancherApiException;

}
