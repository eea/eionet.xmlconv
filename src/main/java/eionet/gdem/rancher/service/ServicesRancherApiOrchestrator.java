package eionet.gdem.rancher.service;

import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ServiceApiRequestBody;
import eionet.gdem.rancher.model.ServiceApiResponse;

import java.util.List;

public interface ServicesRancherApiOrchestrator {

    /**
     * Lists all container instances of a service
     * @param serviceId
     * @return
     * @throws RancherApiException
     */
    List<String> getContainerInstances(String serviceId) throws RancherApiException;

    /**
     * Returns information of a specific service
     * @param serviceId
     * @return
     * @throws RancherApiException
     */
    ServiceApiResponse getServiceInfo(String serviceId) throws RancherApiException;

    /**
     * Scales up container instances of service
     * @param serviceId
     * @param serviceApiRequestBody
     * @return
     * @throws RancherApiException
     */
    ServiceApiResponse scaleUpContainerInstances(String serviceId, ServiceApiRequestBody serviceApiRequestBody) throws RancherApiException;

    /**
     * Scales down container instances of service
     * @param serviceId
     * @param serviceApiRequestBody
     * @return
     * @throws RancherApiException
     */
    ServiceApiResponse removeContainerInstances(String serviceId, ServiceApiRequestBody serviceApiRequestBody) throws RancherApiException;

    /**
     * Creates a new service
     * @param serviceName
     * @return
     * @throws RancherApiException
     */
    ServiceApiResponse createService(String serviceName) throws RancherApiException;
}
