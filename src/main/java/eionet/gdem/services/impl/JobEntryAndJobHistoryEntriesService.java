package eionet.gdem.services.impl;

import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.web.spring.workqueue.JobEntryAndJobHistoryEntriesObject;
import eionet.gdem.web.spring.workqueue.JobHistoryMetadata;
import eionet.gdem.web.spring.workqueue.JobMetadata;

import java.sql.SQLException;
import java.util.List;

public interface JobEntryAndJobHistoryEntriesService {

    List<JobHistoryMetadata> getJobHistoryMetadata(String jobId) throws DatabaseException;
    List<JobMetadata> getSortedJobsForPage(Integer page, Integer itemsPerPage, String sortBy, Boolean sortDesc, String searchParam, String keyword);
    Integer getNumberOfTotalJobs();
}
