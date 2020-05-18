package eionet.gdem.services;

import eionet.gdem.jpa.Entities.JobHistoryEntry;

import java.util.List;

public interface JobHistoryService {

    List<JobHistoryEntry> getAdditionalInfoOfJob(String jobId);
}
