package eionet.gdem.jpa.service;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.repositories.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class JobServiceImpl implements JobService {

    JobRepository jobRepository;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    public JobServiceImpl(@Qualifier("jobRepository") JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    @Override
    public void changeNStatus(Integer jobId, Integer status) {
        try {
            jobRepository.updateJobNStatus(status, Properties.getHostname(), new Timestamp(new Date().getTime()), jobId);
            if (status == 3)
                LOGGER.info("### Job with id=" + jobId + " has changed status to " + Constants.JOB_READY + ".");
            else if (status == 7)
                LOGGER.info("### Job with id=" + jobId + " has changed status to " + Constants.XQ_INTERRUPTED + ".");
            else if (status == 9)
                LOGGER.info("### Job with id=" + jobId + " has changed status to " + Constants.CANCELLED_BY_USER + ".");
            else
                LOGGER.info("### Job with id=" + jobId + " has changed status to " + Constants.XQ_FATAL_ERR + ".");
        } catch (Exception e) {
            LOGGER.error("Database exception when changing status of job with id " + jobId + ", " + e.toString());
            throw e;
        }
    }

    @Transactional
    @Override
    public void changeIntStatusAndJobExecutorName(InternalSchedulingStatus intStatus, String jobExecutorName, Timestamp timestamp, Integer jobId) {
        try {
            jobRepository.updateIntStatusAndJobExecutorName(intStatus, jobExecutorName, timestamp, jobId);
        } catch (Exception e) {
            LOGGER.error("Database exception when changing internal status of job with id " + jobId + ", " + e.toString());
            throw e;
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
}













