package eionet.gdem.services.impl;

import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.web.spring.workqueue.JobEntryAndJobHistoryEntriesObject;
import eionet.gdem.web.spring.workqueue.JobMetadata;

import java.sql.SQLException;
import java.util.List;

public interface JobEntryAndJobHistoryEntriesService {

    JobEntryAndJobHistoryEntriesObject getJobEntryAndJobHistoryEntriesOfJob(String jobId) throws DatabaseException;
    List<JobMetadata> retrieveAllJobsWithMetadata() throws SQLException;
}
