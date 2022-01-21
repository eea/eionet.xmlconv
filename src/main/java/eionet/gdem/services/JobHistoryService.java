package eionet.gdem.services;

import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.qa.XQScript;

import java.util.List;

public interface JobHistoryService {

    List<JobHistoryEntry> getJobHistoryEntriesOfJob(String jobId);

    void updateJobHistory(Integer nStatus, Integer internalStatus, JobEntry jobEntry) throws DatabaseException;

    JobHistoryEntry save(JobHistoryEntry jobHistoryEntry) throws DatabaseException;
}
