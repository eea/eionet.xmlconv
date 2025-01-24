package eionet.gdem.rancher.service;

import eionet.gdem.Properties;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.PodStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RancherApiServiceImpl implements RancherApiService {

    private final KubernetesClient kubernetesClient;

    @Autowired
    public RancherApiServiceImpl(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @Override
    public Deployment getDeploymentByName(String name) {
        return kubernetesClient
                .apps()
                .deployments()
                .withName(name)
                .get();
    }

    @Override
    public Deployment scaleDeployment(String deploymentName, int replicas) {
        return kubernetesClient
                .apps()
                .deployments()
                .withName(deploymentName)
                .scale(replicas);
    }

    @Override
    public Pod getPod(String podName) {
        return kubernetesClient
                .pods()
                .withName(podName)
                .get();
    }

    @Override
    public List<StatusDetails> deletePod(String podName) {
        return kubernetesClient
                .pods()
                .withName(podName)
                .delete();
    }

    @Override
    public List<Pod> getPods(String deploymentName) {
        return kubernetesClient
                .pods()
                .withLabels(getDeploymentByName(deploymentName).getSpec().getSelector().getMatchLabels())
                .list()
                .getItems();
    }

    @Override
    public List<Pod> getFailedPods(String deploymentName) {
        return getPods(deploymentName)
                .stream()
                .filter(pod -> Properties.POD_FAILED_PHASE.equals(pod.getStatus().getPhase()))
                .collect(Collectors.toList());
    }

    @Override
    public Integer getRunningPods(String deploymentName) {
        return (int) getPods(deploymentName)
                .stream()
                .filter(PodStatusUtil::isRunning)
                .count();
    }

    @Override
    public List<String> getPodNames(String deploymentName) {
        return getPods(deploymentName)
                .stream()
                .map(pod -> pod.getMetadata().getName())
                .collect(Collectors.toList());
    }

}
