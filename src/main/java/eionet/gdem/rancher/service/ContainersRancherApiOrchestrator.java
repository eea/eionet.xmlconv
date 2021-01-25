package eionet.gdem.rancher.service;

import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ContainerApiResponse;
import eionet.gdem.rancher.model.ContainerData;

public interface ContainersRancherApiOrchestrator {

    /**
     * Returns container id of container with name containerName
     * @param containerName
     * @return
     * @throws RancherApiException
     */
    String getContainerId(String containerName) throws RancherApiException;

    /**
     * Returns useful information of container with name containerName
     * @param containerName
     * @return
     * @throws RancherApiException
     */
    ContainerApiResponse getContainerInfo(String containerName) throws RancherApiException;

    /**
     * Starts container with name containerName
     * @param containerName
     * @return
     * @throws RancherApiException
     */
    ContainerData startContainer(String containerName) throws RancherApiException;

    /**
     * Stops container with name containerName
     * @param containerName
     * @return
     * @throws RancherApiException
     */
    ContainerData stopContainer(String containerName) throws RancherApiException;

    /**
     * Removes container with name containerName from service instances. Initially deletes the container with name containerName (a new
     * container is then created automatically by rancher to preserve scale) and immediately scales down to remove the new container created
     * @param containerName
     * @return
     */
    ContainerData deleteContainer(String containerName) throws RancherApiException;
}
