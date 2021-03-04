package eionet.gdem.services;

import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.qa.XQScript;

import java.util.List;

public interface JobHistoryService {

    List<JobHistoryEntry> getAdditionalInfoOfJob(String jobId);

    void updateStatusesAndJobExecutorName(XQScript script, Integer nStatus, Integer internalStatus, String jobExecutorName, String jobType);
}
