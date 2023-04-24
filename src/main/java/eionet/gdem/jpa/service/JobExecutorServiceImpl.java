package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.JobExecutorRepository;
import eionet.gdem.jpa.utils.JobExecutorType;
import org.hibernate.PessimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("jobExecutorService")
public class JobExecutorServiceImpl implements JobExecutorService {

    private JobExecutorRepository jobExecutorRepository;

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutorServiceImpl.class);

    @Autowired
    public JobExecutorServiceImpl(JobExecutorRepository jobExecutorRepository) {
        this.jobExecutorRepository = jobExecutorRepository;
    }

    @Override
    public JobExecutor findByName(String jobExecutorName) throws DatabaseException {
        JobExecutor jobExecutor = null;
        try {
            jobExecutor = jobExecutorRepository.findByName(jobExecutorName);
        } catch (Exception e) {
            LOGGER.info("Database exception during retrieval of jobExecutor with name " + jobExecutorName);
            throw new DatabaseException(e);
        }
        return jobExecutor;
    }

    @Override
    public void saveOrUpdateJobExecutor(boolean update,JobExecutor jobExecutor) throws DatabaseException {
        try {
            if (update) {
                jobExecutorRepository.updateJobExecutor(jobExecutor.getStatus(), jobExecutor.getJobId(), jobExecutor.getJobExecutorType().getId(), jobExecutor.getFmeJobId(), jobExecutor.getName());
            } else {
                jobExecutorRepository.save(jobExecutor);
            }
        } catch (PessimisticLockException | PessimisticLockingFailureException pem) {
            LOGGER.error("PessimisticLock exception when updating worker with name " + jobExecutor.getName() + " and jobId " + jobExecutor.getJobId() + ", " + pem.toString());
            throw pem;
        } catch (Exception e) {
            LOGGER.error("Database exception when updating worker with name " + jobExecutor.getName() + " and jobId " + jobExecutor.getJobId() + ", " + e.toString());
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<JobExecutor> listJobExecutor() throws DatabaseException {
        try {
            return jobExecutorRepository.findAll();
        } catch (Exception e) {
            LOGGER.error("Database exception when retrieving entries from JOB_EXECUTOR table");
            throw new DatabaseException(e);
        }
    }

    @Transactional
    @Override
    public void deleteByContainerId(String containerId) throws DatabaseException {
        try {
            this.jobExecutorRepository.deleteByContainerId(containerId);
        } catch (Exception e) {
            LOGGER.error("Database exception when deleting jobExecutor with id " + containerId);
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<JobExecutor> findByStatus(Integer status) {
        return jobExecutorRepository.findByStatus(status);
    }

    @Override
    public List<JobExecutor> findByStatusAndJobExecutorType(Integer status, JobExecutorType jobExecutorType) {
        return jobExecutorRepository.findByStatusAndJobExecutorType(status, jobExecutorType);
    }

    @Override
    public void deleteByName(String name) throws DatabaseException {
        try {
            jobExecutorRepository.deleteByName(name);
        } catch (Exception e) {
            LOGGER.error("Database exception while trying to delete jobExecutor with name " + name);
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<JobExecutor> findExecutorsByJobId(Integer jobId) throws DatabaseException {
        try {
            return jobExecutorRepository.findJobExecutorsByJobId(jobId);
        } catch (Exception e) {
            LOGGER.error("Database exception while trying to retrieve JOB_EXECUTOR entry for job with id " + jobId);
            throw new DatabaseException(e);
        }
    }
}























