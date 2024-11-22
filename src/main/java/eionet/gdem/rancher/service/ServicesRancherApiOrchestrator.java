package eionet.gdem.rancher.service;

import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ServiceApiRequestBody;
import eionet.gdem.rancher.model.ServiceApiResponse;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.api.model.apps.Deployment;

import java.util.List;

public interface ServicesRancherApiOrchestrator {

    /**
     * Get list of pod names for a deployment
     * @param deploymentName the deployment unique name
     * @return the list of pod names
     */
    List<String> getPodNames(String deploymentName);

    /**
     * Returns information of service with id serviceId
     * @param serviceId
     * @return
     * @throws RancherApiException
     */
    ServiceApiResponse getServiceInfo(String serviceId) throws RancherApiException;

    /**
     * Scales up or down container instances of service with id serviceId
     * @param serviceId
     * @param serviceApiRequestBody
     * @return
     * @throws RancherApiException
     */
    ServiceApiResponse scaleUpOrDownContainerInstances(String serviceId, ServiceApiRequestBody serviceApiRequestBody) throws RancherApiException;

    /**
     * Creates a new service
     * @param serviceName
     * @return
     * @throws RancherApiException
     */
    ServiceApiResponse createService(String serviceName) throws RancherApiException;

    /**
     * Deletes service with id serviceId
     * @param serviceId
     * @return
     * @throws RancherApiException
     */
    ServiceApiResponse deleteService(String serviceId) throws RancherApiException;

    /**
     * Get pod by unique pod name
     * @param podName the pod name
     * @return the pod
     */
    Pod getPod(String podName);

    /**
     * Delete pod by unique pod name
     * @param podName the pod name
     * @return List<StatusDetails>
     */
    List<StatusDetails> deletePod(String podName);

    /**
     * Get list of pods for a deployment
     * @param deploymentName the deployment unique name
     * @return the list of pods
     */
    List<Pod> getPods(String deploymentName);

    /**
     * Get deployment by name
     * @param name the deployment unique name
     * @return the deployment object
     */
    Deployment getDeploymentByName(String name);

    /**
     * Scale a deployment to the specified replicas
     * @param deploymentName the deployment unique name
     * @param replicas the replicas size
     * @return the deployment object
     */
    Deployment scaleDeployment(String deploymentName, int replicas);


    /**
     * Get number of running pods for a deployment
     * @param deploymentName the deployment unique name
     * @return number of running pods
     */
    Integer getRunningPods(String deploymentName);

    /**
     * Get list of failed pods for a deployment
     * @param deploymentName the deployment unique name
     * @return the list of failed pods
     */
    List<Pod> getFailedPods(String deploymentName);
}
