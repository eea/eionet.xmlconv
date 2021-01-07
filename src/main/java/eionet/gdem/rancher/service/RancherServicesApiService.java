package eionet.gdem.rancher.service;

import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ServiceBody;
import eionet.gdem.rancher.model.ServiceResponse;

public interface RancherServicesApiService {

    ServiceResponse getServiceInfo(String serviceId) throws RancherApiException;

    String scaleUpContainerInstances(String serviceId, ServiceBody serviceBody);

    String removeContainerInstances(String serviceId, ServiceBody serviceBody);
}
