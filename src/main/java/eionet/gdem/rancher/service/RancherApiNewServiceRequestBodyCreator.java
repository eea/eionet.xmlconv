package eionet.gdem.rancher.service;

import eionet.gdem.rancher.model.RancherApiNewServiceRequestBody;

public interface RancherApiNewServiceRequestBodyCreator {

    RancherApiNewServiceRequestBody buildBody(String serviceName, String stackId);
}
