package eionet.gdem.api;

import eionet.gdem.Properties;
import eionet.gdem.api.model.ApplicationStatus;
import eionet.gdem.api.model.JobExecutorReportStatus;
import eionet.gdem.jpa.repositories.JobExecutorRepository;
import eionet.gdem.rancher.service.ServicesRancherApiOrchestrator;
import io.fabric8.kubernetes.client.utils.PodStatusUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/application")
public class ApplicationStatusApiController {

    private final JobExecutorRepository jobExecutorRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ServicesRancherApiOrchestrator servicesRancherApiOrchestrator;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStatusApiController.class);

    @Autowired
    public ApplicationStatusApiController(JobExecutorRepository jobExecutorRepository, RabbitTemplate rabbitTemplate,
                                          ServicesRancherApiOrchestrator servicesRancherApiOrchestrator
    ) {
        this.jobExecutorRepository = jobExecutorRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.servicesRancherApiOrchestrator = servicesRancherApiOrchestrator;
    }

    @GetMapping("/status")
    public ApplicationStatus getStatus(HttpServletRequest httpRequest) {
        return new ApplicationStatus()
                .setDatabaseConnection(getDatabaseConnection())
                .setRabbitmqConnection(getRabbitmqConnection())
                .setRancherConnection(getRancherConnection())
                .setJobExecutorReportStatus(getJobExecutorsReportStatus());
    }

    private String getDatabaseConnection() {
        try {
            jobExecutorRepository.checkConnection();
        } catch (Exception e) {
            LOGGER.error("Something went wrong in database connection: {}", e.getMessage());
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
            LOGGER.error("Something went wrong in rabbitmq connection: {}", e.getMessage());
            return ApplicationStatus.Status.DOWN.getValue();
        }
        return ApplicationStatus.Status.UP.getValue();
    }

    private String getRancherConnection() {
        return PodStatusUtil.isRunning(servicesRancherApiOrchestrator.getPod(Properties.RANCHER_CONVERTERS_POD_NAME))
                ? ApplicationStatus.Status.UP.getValue()
                : ApplicationStatus.Status.DOWN.getValue();
    }

    private JobExecutorReportStatus getJobExecutorsReportStatus() {
        return new JobExecutorReportStatus()
                .setLightJobExecutorInstancesRunning(servicesRancherApiOrchestrator.getRunningPods(Properties.RANCHER_LIGTH_JOBEXEC_DEPLOYMENT_NAME))
                .setHeavyJobExecutorInstancesRunning(servicesRancherApiOrchestrator.getRunningPods(Properties.RANCHER_HEAVY_JOBEXEC_DEPLOYMENT_NAME))
                .setFmeSyncJobExecutorInstancesRunning(servicesRancherApiOrchestrator.getRunningPods(Properties.RANCHER_SYNC_FME_JOBEXEC_DEPLOYMENT_NAME))
                .setFmeAsyncJobExecutorInstancesRunning(servicesRancherApiOrchestrator.getRunningPods(Properties.RANCHER_ASYNC_FME_JOBEXEC_DEPLOYMENT_NAME));
    }
}















