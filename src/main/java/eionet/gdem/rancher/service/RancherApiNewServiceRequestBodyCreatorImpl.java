package eionet.gdem.rancher.service;

import eionet.gdem.Properties;
import eionet.gdem.rancher.model.RancherServiceRequestBody;
import eionet.gdem.rancher.model.ServiceLaunchConfig;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class RancherApiNewServiceRequestBodyCreatorImpl implements RancherApiNewServiceRequestBodyCreator {

    private static final Integer SCALE = 1;

    @Override
    public RancherServiceRequestBody buildBody(String serviceName, String stackId) {
        HashMap<String, String> environment = new HashMap<>();
        environment.put("spring.rabbitmq.host", Properties.rabbitMQHost);
        environment.put("spring.rabbitmq.port", Properties.rabbitMQPort.toString());
        environment.put("spring.rabbitmq.username", Properties.rabbitMQUsername);
        environment.put("spring.rabbitmq.password", Properties.rabbitMQPassword);
        HashMap<String, String> labels = new HashMap<>();
        labels.put("io.rancher.container.pull_image", "always");

        ServiceLaunchConfig launchConfig = new ServiceLaunchConfig().setImageUuid(Properties.rancherJobExecutorImageUuid)
                .setEnvironment(environment).setLabels(labels)
                .setStdinOpen(true).setTty(true).setMemory(Properties.rancherServiceMemory)
                .setMemoryReservation(Properties.rancherServiceMemoryReservation);

        RancherServiceRequestBody serviceRequestBody = new RancherServiceRequestBody().setLaunchConfig(launchConfig)
                .setName(serviceName).setScale(SCALE).setStackId(stackId).setStartOnCreate(true);

        return serviceRequestBody;
    }

}


















