package eionet.gdem.rancher.service;

import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ContainerApiResponse;
import eionet.gdem.rancher.model.ContainerData;

public interface ContainersRancherApiOrchestrator {

    /**
     * Returns container id of a container by container name
     * @param containerName
     * @return
     * @throws RancherApiException
     */
    String getContainerId(String containerName) throws RancherApiException;

    /**
     * Returns useful information of container
     * @param containerName
     * @return
     * @throws RancherApiException
     */
    ContainerApiResponse getContainerInfo(String containerName) throws RancherApiException;

    /**
     * Starts a specific container
     * @param containerName
     * @return
     * @throws RancherApiException
     */
    ContainerData startContainer(String containerName) throws RancherApiException;

    /**
     * Stops a specific container
     * @param containerName
     * @return
     * @throws RancherApiException
     */
    ContainerData stopContainer(String containerName) throws RancherApiException;

}
