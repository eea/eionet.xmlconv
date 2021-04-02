package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.JobExecutorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("jobExecutorService")
public class JobExecutorServiceImpl implements JobExecutorService {

    private JobExecutorRepository jobExecutorRepository;

    /** */
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
            throw new DatabaseException(e.getMessage());
        }
        return jobExecutor;
    }

    @Transactional
    @Override
    public void saveOrUpdateJobExecutor(JobExecutor jobExecutor) throws DatabaseException {
        try {
            JobExecutor jobExec = jobExecutorRepository.findByName(jobExecutor.getName());
            if (jobExec!=null) {
                jobExecutorRepository.updateStatusAndJobId(jobExecutor.getStatus(), jobExecutor.getJobId(), jobExec.getName());
            } else {
                jobExecutorRepository.save(jobExecutor);
            }
        } catch (Exception e) {
            LOGGER.error("Database exception when updating worker with name " + jobExecutor.getName() + ", " + e.toString());
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public List<JobExecutor> listJobExecutor() throws DatabaseException {
        try {
            return jobExecutorRepository.findAll();
        } catch (Exception e) {
            LOGGER.error("Database exception when retrieving entries from JOB_EXECUTOR table");
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void deleteByContainerId(String containerId) throws DatabaseException {
        try {
            this.jobExecutorRepository.deleteByContainerId(containerId);
        } catch (Exception e) {
            LOGGER.error("Database exception when deleting jobExecutor with id " + containerId);
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public List<JobExecutor> findByStatus(Integer status) {
        return jobExecutorRepository.findByStatus(status);
    }

    @Override
    public void deleteByName(String name) throws DatabaseException {
        try {
           jobExecutorRepository.deleteByName(name);
        } catch (Exception e) {
            LOGGER.error("Database exception while trying to delete jobExecutor with name " + name);
            throw new DatabaseException(e.getMessage());
        }
    }
}























