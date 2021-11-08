package eionet.gdem.infrastructure.scheduling;

import eionet.gdem.Properties;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.service.ServicesRancherApiOrchestratorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HeavyWorkersScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeavyWorkersScheduledTasks.class);

    private WorkersOrchestrationSharedServiceImpl workersOrchestrationSharedService;
    private ServicesRancherApiOrchestratorImpl servicesRancherApiOrchestrator;
    private JobExecutorService jobExecutorService;

    @Autowired
    public HeavyWorkersScheduledTasks(WorkersOrchestrationSharedServiceImpl workersOrchestrationSharedService, ServicesRancherApiOrchestratorImpl servicesRancherApiOrchestrator,
                                      JobExecutorService jobExecutorService) {
        this.workersOrchestrationSharedService = workersOrchestrationSharedService;
        this.servicesRancherApiOrchestrator = servicesRancherApiOrchestrator;
        this.jobExecutorService = jobExecutorService;
    }

    /**
     * The task runs every minute and checks how many jobs have internalSchedulingStatus=2 (meaning the job has been added to rabbitmq queue and is waiting
     * for a worker to grab it) and field IS_HEAVY=true and how many heavy workers have status=1 (meaning they are ready to receive a job) and creates or
     * deletes workers accordingly.
     *
     * @throws RancherApiException
     */
    @Transactional
    @Scheduled(cron = "0 */1 * * * *")  //every minute
    public void scheduleHeavyWorkersOrchestration() {
        if (!Properties.enableJobExecRancherScheduledTask) {
            return;
        }
        workersOrchestrationSharedService.scheduleWorkersOrchestration(Properties.rancherHeavyJobExecServiceId, true, JobExecutorType.Heavy, Properties.maxHeavyJobExecutorContainersAllowed);
    }

    /**
     * Finds heavy jobExecutor instances in rancher that have failed to run correctly (unhealthy state) and updates their status in database
     * with status=2 (FAILED). The task also finds heavy jobExecutors in database that don't exist in rancher and deletes them from database.
     *
     * @throws RancherApiException
     * @throws DatabaseException
     */
    @Scheduled(cron = "0 */2 * * * *") //Every 2 minutes
    public void synchronizeRancherHeavyContainersAndDbEntriesByExistenceAndStatus() throws RancherApiException, DatabaseException {
        if (!Properties.enableJobExecRancherScheduledTask) {
            return;
        }
        try {
            //Retrieve jobExecutor instances names from Rancher
            List<String> instances = servicesRancherApiOrchestrator.getContainerInstances(Properties.rancherHeavyJobExecServiceId);
            workersOrchestrationSharedService.updateDbContainersHealthStatusFromRancher(instances);

            List<JobExecutor> jobExecutors = jobExecutorService.listJobExecutor();
            List<JobExecutor> heavyJobExecutors = jobExecutors.stream().filter(jobExecutor -> jobExecutor.getJobExecutorType().equals(JobExecutorType.Heavy)).collect(Collectors.toList());
            workersOrchestrationSharedService.synchronizeRancherContainersWithDbEntries(heavyJobExecutors, instances);
        } catch (RancherApiException rae) {
            LOGGER.error("RancherApiException: Could not retrieve job Executor instances info from Rancher");
            throw rae;
        }
    }
}
