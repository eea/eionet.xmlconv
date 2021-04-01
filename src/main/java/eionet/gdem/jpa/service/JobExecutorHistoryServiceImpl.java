package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.repositories.JobExecutorHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("jobExecutorHistoryService")
public class JobExecutorHistoryServiceImpl implements JobExecutorHistoryService{

    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutorServiceImpl.class);

    private JobExecutorHistoryRepository repository;

    @Autowired
    public JobExecutorHistoryServiceImpl(JobExecutorHistoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public void saveJobExecutorHistoryEntry(JobExecutorHistory entry) {
        try {
            repository.save(entry);
        } catch (Exception e) {
            LOGGER.error("Database exception when saving into JOB_EXECUTOR_HISTORY table worker with name " + entry.getName());
            throw e;
        }
    }

    @Transactional
    @Override
    public List<JobExecutorHistory> getJobExecutorHistoryEntriesById(String containerId){
        try {
            return repository.findByContainerId(containerId);
        } catch (Exception e) {
            LOGGER.error("Database exception when retrieving history for container with id " + containerId);
            throw e;
        }
    }
}
