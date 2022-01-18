package eionet.gdem.jpa.service;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service("jobService")
public class JobServiceImpl implements JobService {

    JobRepository jobRepository;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    public JobServiceImpl(@Qualifier("jobRepository") JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void changeNStatus(Integer jobId, Integer status) throws DatabaseException {
        try {
            jobRepository.updateJobNStatus(status, Properties.getHostname(), new Timestamp(new Date().getTime()), jobId);
            LOGGER.info("### Job with id=" + jobId + " has changed status to " + status + ".");
        } catch (Exception e) {
            LOGGER.error("Database exception when changing status of job with id " + jobId + ", " + e.toString());
            throw new DatabaseException(e);
        }
    }

    @Override
    public void changeNStatusAndInternalStatus(Integer jobId, Integer status, Integer internalStatus) throws DatabaseException {
        try {
            jobRepository.updateJobNStatusAndInternalStatus(status, internalStatus, Properties.getHostname(), new Timestamp(new Date().getTime()), jobId);
            LOGGER.info("### Job with id=" + jobId + " has changed status to " + status + " and internal status to " +  internalStatus + ".");
        } catch (Exception e) {
            LOGGER.error("Database exception when changing statuses of job with id " + jobId + ", " + e.toString());
            throw new DatabaseException(e);
        }
    }

    @Override
    public void updateJob(Integer nStatus, InternalSchedulingStatus intStatus, String jobExecutorName, Timestamp timestamp, JobEntry jobEntry) throws DatabaseException {
        try {
            jobRepository.updateJob(nStatus, intStatus, jobExecutorName, timestamp, jobEntry.isHeavy(), jobEntry.getFmeJobId(), jobEntry.getId());
        } catch (Exception e) {
            LOGGER.error("Database exception when changing internal status of job with id " + jobEntry.getId() + ", " + e.toString());
            throw new DatabaseException(e);
        }
    }

    @Override
    public JobEntry findById(Integer id) throws DatabaseException {
        JobEntry jobEntry = null;
        try {
            jobEntry = jobRepository.findById(id);
        } catch (Exception e) {
            LOGGER.info("Database exception during retrieval of job with id " + id);
            throw new DatabaseException(e);
        }
        return jobEntry;
    }

    @Override
    public List<JobEntry> findByIntSchedulingStatus(InternalSchedulingStatus intSchedulingStatus) {
        return jobRepository.findByIntSchedulingStatus(intSchedulingStatus);
    }

    @Override
    public List<JobEntry> findByIntSchedulingStatusAndIsHeavy(InternalSchedulingStatus intSchedulingStatus, boolean isHeavy) {
        return jobRepository.findByIntSchedulingStatusAndIsHeavy(intSchedulingStatus, isHeavy);
    }

    @Override
    public List<JobEntry> findProcessingJobs() {
        return jobRepository.findProcessingJobs();
    }

    @Override
    public void updateWorkerRetries(Integer workerRetries, Timestamp timestamp, Integer jobId) throws DatabaseException {
        try {
            jobRepository.updateWorkerRetries(workerRetries, timestamp, jobId);
        } catch (Exception e) {
            LOGGER.error("Database exception while trying to update worker retries for job with id " + jobId);
            throw new DatabaseException(e);
        }
    }

    @Override
    public void updateHeavyRetriesOnFailure(Integer heavyRetries, Timestamp timestamp, Integer jobId) throws DatabaseException {
        try {
            jobRepository.updateHeavyRetriesOnFailure(heavyRetries, timestamp, jobId);
        } catch (Exception e) {
            LOGGER.error("Database exception while trying to update heavyRetriesOnFailure for job with id " + jobId);
            throw new DatabaseException(e);
        }
    }

    @Override
    public JobEntry save(JobEntry jobEntry) {
        return jobRepository.save(jobEntry);
    }

    @Override
    public Integer getRetryCounter(Integer jobId) throws DatabaseException {
        Integer result;
        try {
            result = jobRepository.getRetryCounter(jobId);
        } catch (Exception e) {
            LOGGER.error("Database exception while retrieving retryCounter for job with id " + jobId);
            throw new DatabaseException(e);
        }
        return result;
    }

    @Override
    public void updateJobInfo(Integer nStatus, String instance, Timestamp timestamp, Integer retryCounter, Integer jobId) throws DatabaseException {
        try {
            jobRepository.updateJobInfo(nStatus, instance, timestamp, retryCounter, jobId);
        } catch (Exception e) {
            LOGGER.error("Database exception while trying to update job information for job with id " + jobId);
            throw new DatabaseException(e);
        }
    }
}













