package eionet.gdem.rancher.service;

import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ContainerApiResponse;

public interface ContainersRancherApiService {

    String getContainerId(String containerName) throws RancherApiException;

    ContainerApiResponse getContainerInfo(String containerName) throws RancherApiException;

}
