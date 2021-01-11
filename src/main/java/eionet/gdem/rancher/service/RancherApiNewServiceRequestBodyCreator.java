package eionet.gdem.rancher.service;

import eionet.gdem.rancher.model.RancherServiceRequestBody;

public interface RancherApiNewServiceRequestBodyCreator {

    RancherServiceRequestBody buildBody(String serviceName, String stackId);
}
