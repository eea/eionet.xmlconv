package eionet.gdem.infrastructure.scheduling;

import eionet.gdem.Properties;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.service.ServicesRancherApiOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LightWorkersScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(LightWorkersScheduledTasks.class);

    private ServicesRancherApiOrchestrator servicesOrchestrator;
    private JobExecutorService jobExecutorService;
    private WorkersOrchestrationSharedService workersOrchestrationSharedService;

    @Autowired
    public LightWorkersScheduledTasks(ServicesRancherApiOrchestrator servicesOrchestrator, JobExecutorService jobExecutorService,
                                      WorkersOrchestrationSharedService workersOrchestrationSharedService) {
        this.servicesOrchestrator = servicesOrchestrator;
        this.jobExecutorService = jobExecutorService;
        this.workersOrchestrationSharedService = workersOrchestrationSharedService;
    }

    /**
     * The task runs every minute and checks how many jobs have internalSchedulingStatus=2 (meaning the job has been added to rabbitmq queue and is waiting
     * for a worker to grab it) and field IS_HEAVY=false and how many light workers have status=1 (meaning they are ready to receive a job) and creates or
     * deletes workers accordingly.
     *
     * @throws RancherApiException
     */
    @Transactional
    @Scheduled(cron = "0 */1 * * * *")  //every minute
    public void scheduleLightWorkersOrchestration() {
        if (!Properties.enableJobExecRancherScheduledTask) {
            return;
        }
        workersOrchestrationSharedService.scheduleWorkersOrchestration(Properties.rancherLightJobExecServiceId, false, JobExecutorType.Light, Properties.maxLightJobExecutorContainersAllowed);
    }

    /**
     * Finds light jobExecutor instances in rancher that have failed to run correctly (unhealthy state) and updates their status in database
     * with status=2 (FAILED). The task also finds light jobExecutors in database that don't exist in rancher and deletes them from database.
     *
     * @throws RancherApiException
     * @throws DatabaseException
     */
    @Scheduled(cron = "0 */2 * * * *") //Every 2 minutes
    public void synchronizeRancherLightContainersAndDbEntriesByExistenceAndStatus() throws RancherApiException, DatabaseException {
        if (!Properties.enableJobExecRancherScheduledTask) {
            return;
        }
        try {
            //Retrieve jobExecutor instances names from Rancher
            List<String> instances = servicesOrchestrator.getContainerInstances(Properties.rancherLightJobExecServiceId);
            workersOrchestrationSharedService.updateDbContainersHealthStatusFromRancher(instances, false);

            List<JobExecutor> jobExecutors = jobExecutorService.listJobExecutor();
            List<JobExecutor> lightJobExecutors = jobExecutors.stream().filter(jobExecutor -> jobExecutor.getJobExecutorType().equals(JobExecutorType.Light)).collect(Collectors.toList());
            workersOrchestrationSharedService.synchronizeRancherContainersWithDbEntries(lightJobExecutors, instances);
        } catch (RancherApiException rae) {
            LOGGER.error("RancherApiException: Could not retrieve job Executor instances info from Rancher");
            throw rae;
        }
    }
}
