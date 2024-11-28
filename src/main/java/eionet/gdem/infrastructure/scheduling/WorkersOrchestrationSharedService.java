package eionet.gdem.infrastructure.scheduling;

import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.utils.JobExecutorType;
import io.fabric8.kubernetes.api.model.Pod;

import java.util.List;

public interface WorkersOrchestrationSharedService {

    /**
     * increases workers so that maxJobExecutorContainersAllowed is not exceeded for JobExecutor pods
     *
     * @param deploymentName
     * @param newWorkers
     * @param maxJobExecutorsAllowed
     */
    void createWorkers(String deploymentName, Integer newWorkers, Integer maxJobExecutorsAllowed);

    /**
     * deletes workers that have failed to run correctly
     *
     * @param deploymentName
     * @param jobExecutorType
     */
    void deleteFailedWorkers(String deploymentName, JobExecutorType jobExecutorType);

    /**
     * deletes worker from rancher and JOB_EXECUTOR table
     *
     * @param worker
     * @throws DatabaseException
     */
    void deleteFromRancherAndDatabase(JobExecutor worker) throws DatabaseException;

    /**
     * checks how many jobs have internalSchedulingStatus=2 (meaning the job has been added to rabbitmq queue and is waiting for a worker to grab it)
     * and field IS_HEAVY=true or false and how many light or heavy workers respectively have status=1 (meaning they are ready to receive a job) and
     * creates or deletes workers accordingly.
     * @param deploymentName
     * @param isHeavy
     * @param jobExecutorType
     * @param maxJobExecutorsAllowed
     */
    void scheduleWorkersOrchestration(String deploymentName, boolean isHeavy, JobExecutorType jobExecutorType, Integer maxJobExecutorsAllowed);

    /**
     * finds jobExecutor pods in rancher that have failed to run correctly (unhealthy state) and updates their status in database
     * with status=2 (FAILED)
     * @param pods
     * @param isHeavy
     */
    void updateDbStatusForFailedPods(List<Pod> pods, boolean isHeavy);

    /**
     * deletes from database jobExecutor instances that don't exist in rancher
     *
     * @param jobExecutors
     * @param podNames
     */
    void synchronizeRancherPodsWithDbEntries(List<JobExecutor> jobExecutors, List<String> podNames);

    /**
     * deletes worker's heart beat queue from rabbitmq
     * @param queueName
     */
    void deleteWorkerHeartBeatQueue(String queueName);
}
