package eionet.gdem.infrastructure.scheduling;

import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.rancher.exception.RancherApiException;

import java.util.List;

public interface WorkersOrchestrationSharedService {

    /**
     * increases workers so that maxJobExecutorContainersAllowed is not exceeded for JobExecutor containers
     *
     * @param serviceId
     * @param newWorkers
     * @param maxJobExecutorsAllowed
     * @throws RancherApiException
     */
    void createWorkers(String serviceId, Integer newWorkers, Integer maxJobExecutorsAllowed) throws RancherApiException;

    /**
     * deletes workers that have failed to run correctly
     *
     * @param serviceId
     * @throws RancherApiException
     * @throws DatabaseException
     */
    void deleteFailedWorkers(String serviceId, JobExecutorType jobExecutorType) throws RancherApiException, DatabaseException;

    /**
     * deletes worker from rancher and JOB_EXECUTOR table
     *
     * @param worker
     * @throws RancherApiException
     * @throws DatabaseException
     */
    void deleteFromRancherAndDatabase(JobExecutor worker) throws RancherApiException, DatabaseException;

    /**
     * checks how many jobs have internalSchedulingStatus=2 (meaning the job has been added to rabbitmq queue and is waiting for a worker to grab it)
     * and field IS_HEAVY=true or false and how many light or heavy workers respectively have status=1 (meaning they are ready to receive a job) and
     * creates or deletes workers accordingly.
     * @param serviceId
     * @param isHeavy
     * @param jobExecutorType
     * @param maxJobExecutorsAllowed
     */
    void scheduleWorkersOrchestration(String serviceId, boolean isHeavy, JobExecutorType jobExecutorType, Integer maxJobExecutorsAllowed);

    /**
     * finds jobExecutor instances in rancher that have failed to run correctly (unhealthy state) and updates their status in database
     * with status=2 (FAILED)
     * @param instances
     * @param isHeavy
     * @throws RancherApiException
     */
    void updateDbContainersHealthStatusFromRancher(List<String> instances, boolean isHeavy) throws RancherApiException;

    /**
     * deletes from database jobExecutor instances that don't exist in rancher
     *
     * @param jobExecutors
     * @param instances
     */
    void synchronizeRancherContainersWithDbEntries(List<JobExecutor> jobExecutors, List<String> instances);

    /**
     * deletes worker's heart beat queue from rabbitmq
     * @param queueName
     */
    void deleteWorkerHeartBeatQueue(String queueName);
}
