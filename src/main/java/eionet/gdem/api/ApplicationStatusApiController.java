package eionet.gdem.api;

import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.api.model.ApplicationStatus;
import eionet.gdem.api.model.ContainerInfo;
import eionet.gdem.api.model.JobExecutorReportStatus;
import eionet.gdem.jpa.repositories.JobExecutorRepository;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ContainerData;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestrator;
import eionet.gdem.rancher.service.ServicesRancherApiOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/application")
public class ApplicationStatusApiController {

    private JobExecutorRepository jobExecutorRepository;
    private RabbitTemplate rabbitTemplate;
    private RestTemplate restTemplate;
    private ServicesRancherApiOrchestrator servicesRancherApiOrchestrator;
    private ContainersRancherApiOrchestrator containersRancherApiOrchestrator;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStatusApiController.class);
    List<String> jobExecutorRancherServicesIds = Arrays.asList(Properties.rancherLightJobExecServiceId, Properties.rancherHeavyJobExecServiceId, Properties.rancherSyncFmeJobExecServiceId, Properties.rancherAsyncFmeJobExecServiceId);

    @Autowired
    public ApplicationStatusApiController(JobExecutorRepository jobExecutorRepository, RabbitTemplate rabbitTemplate, RestTemplate restTemplate,
                                          ServicesRancherApiOrchestrator servicesRancherApiOrchestrator, ContainersRancherApiOrchestrator containersRancherApiOrchestrator) {
        this.jobExecutorRepository = jobExecutorRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = restTemplate;
        this.servicesRancherApiOrchestrator = servicesRancherApiOrchestrator;
        this.containersRancherApiOrchestrator = containersRancherApiOrchestrator;
    }

    @GetMapping("/status")
    public ApplicationStatus getStatus(HttpServletRequest httpRequest) {
        String databaseConnection = getDatabaseConnection();
        String rabbitmqConnection = getRabbitmqConnection();
        String rancherConnection = getRancherConnection(httpRequest);
        JobExecutorReportStatus reportStatus = getJobExecutorsReportStatus();
        ApplicationStatus applicationStatus = new ApplicationStatus().setDatabaseConnection(databaseConnection).setRabbitmqConnection(rabbitmqConnection)
                .setRancherConnection(rancherConnection).setJobExecutorReportStatus(reportStatus);
        return applicationStatus;
    }

    private String getDatabaseConnection() {
        try {
            jobExecutorRepository.checkConnection();
        } catch (Exception e) {
            LOGGER.error("Something went wrong in database connection" + e.getMessage());
            return ApplicationStatus.Status.DOWN.getValue();
        }
        return ApplicationStatus.Status.UP.getValue();
    }

    private String getRabbitmqConnection() {
        String response = "";
        try {
            rabbitTemplate.convertAndSend(Properties.XMLCONV_HEALTH_EXCHANGE, Properties.XMLCONV_HEALTH_ROUTING_KEY, "healthCheck");
            response = (String) rabbitTemplate.receiveAndConvert(Properties.XMLCONV_HEALTH_QUEUE);
            if (!response.equals("healthCheck")) {
                return ApplicationStatus.Status.DOWN.getValue();
            }
        } catch (Exception e) {
            LOGGER.error("Something went wrong in rabbitmq connection: " + e.getMessage());
            return ApplicationStatus.Status.DOWN.getValue();
        }
        return ApplicationStatus.Status.UP.getValue();
    }

    private String getRancherConnection(HttpServletRequest httpRequest) {
        if (httpRequest.getRequestURL().toString().contains("localhost")) {
            return ApplicationStatus.Status.UP.getValue();
        }

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<ContainerInfo> result;
        try {
            result = restTemplate.exchange(Properties.rancherContainerMetadataUrl, HttpMethod.GET, entity, ContainerInfo.class);
            String state = result.getBody().getState();
            if (!state.equals("running")) {
                ApplicationStatus.Status.DOWN.getValue();
            }
        } catch (Exception e) {
            LOGGER.info("Error retrieving rancher metadata for converters container: " + e.getMessage());
            return ApplicationStatus.Status.DOWN.getValue();
        }
        LOGGER.info("Retrieved status running for converters container");
        return ApplicationStatus.Status.UP.getValue();
    }

    private JobExecutorReportStatus getJobExecutorsReportStatus() {
        JobExecutorReportStatus jobExecutorReportStatus = new JobExecutorReportStatus();
        jobExecutorReportStatus.setLightJobExecutorInstancesRunning(0).setHeavyJobExecutorInstancesRunning(0)
                .setFmeSyncJobExecutorInstancesRunning(0).setFmeAsyncJobExecutorInstancesRunning(0);
        for (String serviceId : jobExecutorRancherServicesIds) {
            try {
                List<String> instances = servicesRancherApiOrchestrator.getContainerInstances(serviceId);
                Integer count = 0;
                for (String instance : instances) {
                    ContainerData containerData = containersRancherApiOrchestrator.getContainerInfoById(instance);
                    if (containerData.getState().equals(SchedulingConstants.CONTAINER_STATE_ENUM.RUNNING.getValue())) {
                        ++count;
                    }
                }
                if (serviceId.equals(Properties.rancherLightJobExecServiceId)) {
                    jobExecutorReportStatus.setLightJobExecutorInstancesRunning(count);
                } else if (serviceId.equals(Properties.rancherHeavyJobExecServiceId)) {
                    jobExecutorReportStatus.setHeavyJobExecutorInstancesRunning(count);
                } else if (serviceId.equals(Properties.rancherSyncFmeJobExecServiceId)) {
                    jobExecutorReportStatus.setFmeSyncJobExecutorInstancesRunning(count);
                } else if (serviceId.equals(Properties.rancherAsyncFmeJobExecServiceId)) {
                    jobExecutorReportStatus.setFmeAsyncJobExecutorInstancesRunning(count);
                }
            } catch (RancherApiException e) {
                LOGGER.error("Error while retrieving status of jobExecutor instances " + e.getMessage());
            }
        }
        return jobExecutorReportStatus;
    }
}















