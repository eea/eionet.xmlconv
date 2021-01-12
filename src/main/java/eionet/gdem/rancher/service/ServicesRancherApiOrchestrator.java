package eionet.gdem.rancher.service;

import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ServiceApiRequestBody;
import eionet.gdem.rancher.model.ServiceApiResponse;

import java.util.List;

public interface ServicesRancherApiOrchestrator {

    List<String> getContainerInstances(String serviceId) throws RancherApiException;

    ServiceApiResponse getServiceInfo(String serviceId) throws RancherApiException;

    ServiceApiResponse scaleUpContainerInstances(String serviceId, ServiceApiRequestBody serviceApiRequestBody) throws RancherApiException;

    ServiceApiResponse removeContainerInstances(String serviceId, ServiceApiRequestBody serviceApiRequestBody) throws RancherApiException;

    ServiceApiResponse createService(String serviceName) throws RancherApiException;
}
