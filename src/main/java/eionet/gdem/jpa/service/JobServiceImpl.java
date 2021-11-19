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
            if (status == 3)
                LOGGER.info("### Job with id=" + jobId + " has changed status to " + Constants.JOB_READY + ".");
            else if (status == Constants.XQ_INTERRUPTED)
                LOGGER.info("### Job with id=" + jobId + " has changed status to " + Constants.XQ_INTERRUPTED + ".");
            else if (status == Constants.CANCELLED_BY_USER)
                LOGGER.info("### Job with id=" + jobId + " has changed status to " + Constants.CANCELLED_BY_USER + ".");
            else if (status == Constants.DELETED)
                LOGGER.info("### Job with id=" + jobId + " has changed status to " + Constants.DELETED + ".");
            else
                LOGGER.info("### Job with id=" + jobId + " has changed status to " + Constants.XQ_FATAL_ERR + ".");
        } catch (Exception e) {
            LOGGER.error("Database exception when changing status of job with id " + jobId + ", " + e.toString());
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void updateJob(Integer nStatus, InternalSchedulingStatus intStatus, String jobExecutorName, Timestamp timestamp, JobEntry jobEntry) throws DatabaseException {
        try {
            jobRepository.updateJob(nStatus, intStatus, jobExecutorName, timestamp, jobEntry.isHeavy(), jobEntry.getId());
        } catch (Exception e) {
            LOGGER.error("Database exception when changing internal status of job with id " + jobEntry.getId() + ", " + e.toString());
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public JobEntry findById(Integer id) {
        JobEntry jobEntry = null;
        try {
            jobEntry = jobRepository.findById(id);
        } catch (Exception e) {
            LOGGER.info("Exception during retrieval of job with id " + id);
            throw e;
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
    public void updateWorkerRetries(Integer workerRetries, Timestamp timestamp, Integer jobId) {
        jobRepository.updateWorkerRetries(workerRetries, timestamp, jobId);
    }

    @Override
    public void updateIsHeavyAndHeavyRetries(boolean isHeavy, Integer heavyRetries, Timestamp timestamp, Integer jobId) {
        jobRepository.updateHeavyRetries(isHeavy, heavyRetries, timestamp, jobId);
    }

    @Override
    public JobEntry save(JobEntry jobEntry) {
        return jobRepository.save(jobEntry);
    }

    @Override
    public Integer getRetryCounter(Integer jobId) {
        return jobRepository.getRetryCounter(jobId);
    }

    @Override
    public void updateJobInfo(Integer nStatus, String instance, Timestamp timestamp, Integer retryCounter, Integer jobId) {
        jobRepository.updateJobInfo(nStatus, instance, timestamp, retryCounter, jobId);
    }
}













