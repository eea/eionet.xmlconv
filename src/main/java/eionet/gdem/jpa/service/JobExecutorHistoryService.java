package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.errors.DatabaseException;

import java.util.List;

public interface JobExecutorHistoryService {

    void saveJobExecutorHistoryEntry(JobExecutorHistory entry) throws DatabaseException;
    List<JobExecutorHistory> getJobExecutorHistoryEntriesById(String containerId) throws DatabaseException;
}
