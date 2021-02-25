package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutorHistory;

public interface JobExecutorHistoryService {

    void saveJobExecutorHistoryEntry(JobExecutorHistory entry);
}
