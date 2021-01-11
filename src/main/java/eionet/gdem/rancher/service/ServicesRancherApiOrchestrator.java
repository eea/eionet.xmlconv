package eionet.gdem.rancher.service;

import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ServiceApiRequestBody;
import eionet.gdem.rancher.model.ServiceApiResponse;

public interface ServicesRancherApiOrchestrator {

    String[] getContainerInstances(String serviceId) throws RancherApiException;

    ServiceApiResponse getServiceInfo(String serviceId) throws RancherApiException;

    String scaleUpContainerInstances(String serviceId, ServiceApiRequestBody serviceApiRequestBody);

    String removeContainerInstances(String serviceId, ServiceApiRequestBody serviceApiRequestBody);

    ServiceApiResponse createService(String serviceName, String stackId) throws RancherApiException;
}
