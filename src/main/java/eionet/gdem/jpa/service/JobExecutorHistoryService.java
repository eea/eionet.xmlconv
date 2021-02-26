package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;

import java.util.List;

public interface JobExecutorHistoryService {

    void saveJobExecutorHistoryEntry(JobExecutorHistory entry);
    List<JobExecutorHistory> getJobExecutorHistoryEntriesById(String containerId);
}
