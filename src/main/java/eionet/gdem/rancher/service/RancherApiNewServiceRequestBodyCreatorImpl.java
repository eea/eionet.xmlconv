package eionet.gdem.rancher.service;

import eionet.gdem.Properties;
import eionet.gdem.rancher.model.RancherApiNewServiceRequestBody;
import eionet.gdem.rancher.model.ServiceLaunchConfig;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class RancherApiNewServiceRequestBodyCreatorImpl implements RancherApiNewServiceRequestBodyCreator {

    @Override
    public RancherApiNewServiceRequestBody buildBody(String serviceName, String stackId) {
        HashMap<String, String> environment = new HashMap<>();
        environment.put("spring.rabbitmq.host", Properties.rabbitMQHost);
        environment.put("spring.rabbitmq.port", Properties.rabbitMQPort.toString());
        environment.put("spring.rabbitmq.username", Properties.rabbitMQUsername);
        environment.put("spring.rabbitmq.password", Properties.rabbitMQPassword);
        HashMap<String, String> labels = new HashMap<>();
        labels.put("io.rancher.container.pull_image", "always");

        ServiceLaunchConfig launchConfig = new ServiceLaunchConfig().setImageUuid(Properties.rancherJobExecutorImageUuid)
                .setEnvironment(environment).setLabels(labels)
                .setStdinOpen(true).setTty(true).setMemory(Properties.rancherJobExecServiceMemory)
                .setMemoryReservation(Properties.rancherJobExecServiceMemoryReservation);

        RancherApiNewServiceRequestBody serviceRequestBody = new RancherApiNewServiceRequestBody().setLaunchConfig(launchConfig)
                .setName(serviceName).setScale(Properties.rancherJobExecutorServiceScale).setStackId(stackId).setStartOnCreate(true);

        return serviceRequestBody;
    }

}


















