package eionet.gdem.services.impl;

import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.web.spring.workqueue.EntriesForPageObject;
import eionet.gdem.web.spring.workqueue.JobHistoryMetadata;

import java.util.List;

public interface JobEntryAndJobHistoryEntriesService {

    List<JobHistoryMetadata> getJobHistoryMetadata(String jobId) throws DatabaseException;
    EntriesForPageObject getSortedJobsForPage(Integer page, Integer itemsPerPage, String sortBy, Boolean sortDesc, String searchParam, String keyword, String[] searchedStatuses);
    Integer getNumberOfTotalJobs();
}
