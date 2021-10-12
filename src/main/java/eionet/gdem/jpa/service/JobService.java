package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.qa.XQScript;
import org.basex.core.jobs.Job;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface JobService {

    void changeNStatus(Integer jobId, Integer status) throws DatabaseException;

    void updateJob(Integer nStatus, InternalSchedulingStatus intStatus, String jobExecutorName, Timestamp timestamp, JobEntry jobEntry) throws DatabaseException;

    JobEntry findById(Integer id);

    List<JobEntry> findByIntSchedulingStatus(InternalSchedulingStatus intSchedulingStatus);

    List<JobEntry> findProcessingJobs();

    void updateWorkerRetries(Integer workerRetries, Timestamp timestamp, Integer jobId);

    JobEntry save(JobEntry jobEntry);

    Integer getRetryCounter(Integer jobId);

    void updateJobInfo(Integer nStatus, String instance, Timestamp timestamp, Integer retryCounter, Integer jobId);
}
