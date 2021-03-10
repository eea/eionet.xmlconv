package eionet.gdem.api;

import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ContainerApiResponse;
import eionet.gdem.rancher.model.ContainerData;
import eionet.gdem.rancher.model.ServiceApiRequestBody;
import eionet.gdem.rancher.model.ServiceApiResponse;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestrator;
import eionet.gdem.rancher.service.ServicesRancherApiOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rancher")
public class RancherApiController {

    private ServicesRancherApiOrchestrator servicesRancherApiOrchestrator;
    private ContainersRancherApiOrchestrator containersRancherApiOrchestrator;

    @Autowired
    public RancherApiController(ServicesRancherApiOrchestrator servicesRancherApiOrchestrator, ContainersRancherApiOrchestrator containersRancherApiOrchestrator) {
        this.servicesRancherApiOrchestrator = servicesRancherApiOrchestrator;
        this.containersRancherApiOrchestrator = containersRancherApiOrchestrator;
    }

    @GetMapping("/instances/{serviceId}")
    public List<String> getContainerInstances(@PathVariable String serviceId) throws RancherApiException {
        return servicesRancherApiOrchestrator.getContainerInstances(serviceId);
    }

    @GetMapping("/info/{serviceId}")
    public ServiceApiResponse getServiceInfo(@PathVariable String serviceId) throws RancherApiException {
        return servicesRancherApiOrchestrator.getServiceInfo(serviceId);
    }

    @PutMapping("/scaleUpOrDown/{serviceId}")
    public String scaleUpOrDownContainerInstances(@PathVariable String serviceId, @RequestBody ServiceApiRequestBody serviceApiRequestBody) {
        try {
            servicesRancherApiOrchestrator.scaleUpOrDownContainerInstances(serviceId, serviceApiRequestBody);
        } catch (RancherApiException e) {
            return "failure";
        }
        return "success";
    }

    @PostMapping("/createService/{containerName}")
    public ServiceApiResponse createService(@PathVariable String containerName) throws RancherApiException {
        return servicesRancherApiOrchestrator.createService(containerName);
    }

    @DeleteMapping("/deleteService/{serviceId}")
    public ServiceApiResponse deleteService(@PathVariable String serviceId) throws RancherApiException {
        return servicesRancherApiOrchestrator.deleteService(serviceId);
    }

    @GetMapping("/container/info/{containerName}")
    public ContainerApiResponse getContainerInfo(@PathVariable String containerName) throws RancherApiException {
        return containersRancherApiOrchestrator.getContainerInfo(containerName);
    }

    @GetMapping("/container/info/id/{containerId}")
    public ContainerData getContainerInfoById(@PathVariable String containerId) throws RancherApiException {
        return containersRancherApiOrchestrator.getContainerInfoById(containerId);
    }

    @GetMapping("/container/id/{containerName}")
    public String getContainerId(@PathVariable String containerName) throws RancherApiException {
        return containersRancherApiOrchestrator.getContainerId(containerName);
    }

    @PostMapping("/container/start/{containerName}")
    public ContainerData startContainer(@PathVariable String containerName) throws RancherApiException {
        return containersRancherApiOrchestrator.startContainer(containerName);
    }

    @PostMapping("/container/restart/{containerName}")
    public ContainerData reStartContainer(@PathVariable String containerName) throws RancherApiException {
        return containersRancherApiOrchestrator.restartContainer(containerName);
    }

    @PostMapping("/container/stop/{containerName}")
    public ContainerData stopContainer(@PathVariable String containerName) throws RancherApiException {
        return containersRancherApiOrchestrator.stopContainer(containerName);
    }

    @DeleteMapping("/container/delete/{containerName}")
    public String deleteContainer(@PathVariable String containerName) {
        try {
            containersRancherApiOrchestrator.deleteContainer(containerName);
        } catch (RancherApiException e) {
            return "failure";
        }
        return "success";
    }
}



















