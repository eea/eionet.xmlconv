package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.JobExecutorHistoryRepository;
import org.hibernate.PessimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("jobExecutorHistoryService")
public class JobExecutorHistoryServiceImpl implements JobExecutorHistoryService{

    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutorServiceImpl.class);

    private JobExecutorHistoryRepository repository;

    @Autowired
    public JobExecutorHistoryServiceImpl(JobExecutorHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveJobExecutorHistoryEntry(JobExecutorHistory entry) throws DatabaseException {
        try {
            repository.save(entry);
        } catch (PessimisticLockException | PessimisticLockingFailureException pem) {
            LOGGER.error("PessimisticLock exception when saving into JOB_EXECUTOR_HISTORY table worker with name " + entry.getName() + " and job id " + entry.getJobId());
        } catch (Exception e) {
            LOGGER.error("Database exception when saving into JOB_EXECUTOR_HISTORY table worker with name " + entry.getName() + " and job id " + entry.getJobId());
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<JobExecutorHistory> getJobExecutorHistoryEntriesById(String containerId) throws DatabaseException {
        try {
            return repository.findByContainerId(containerId);
        } catch (Exception e) {
            LOGGER.error("Database exception when retrieving history for container with id " + containerId);
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<JobExecutorHistory> getJobExecutorHistoryEntriesByJobId(String jobId) throws DatabaseException {
        try {
            return repository.findByJobId(Integer.valueOf(jobId));
        } catch (Exception e) {
            LOGGER.error("Database exception when retrieving history for job with id " + jobId);
            throw new DatabaseException(e);
        }
    }
}
