package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.web.spring.workqueue.EntriesForPageObject;
import eionet.gdem.web.spring.workqueue.JobMetadata;
import java.util.List;
import java.util.Map;

public interface JobService {

    void changeNStatus(Integer jobId, Integer status) throws DatabaseException;

    JobEntry findById(Integer id) throws DatabaseException;

    List<JobEntry> findByIntSchedulingStatus(InternalSchedulingStatus intSchedulingStatus);

    List<JobEntry> findByIntSchedulingStatusAndIsHeavy(InternalSchedulingStatus intSchedulingStatus, boolean isHeavy);

    List<JobEntry> findProcessingJobs();

    JobEntry saveOrUpdate(JobEntry jobEntry) throws DatabaseException;

    Integer getRetryCounter(Integer jobId) throws DatabaseException;

    List<JobMetadata> getJobsMetadata(List<JobEntry> jobEntries);

    Integer getNumberOfTotalJobs();

    EntriesForPageObject getPagedAndSortedEntries(Integer page, Integer itemsPerPage, String sortBy, Boolean sortDesc, String searchParam, String keyword, String[] searchedStatuses);

    void deleteJobById(Integer jobId);

    void changeJobStatusAndTimestampByStatus(Integer currentStatus, Integer newStatus);

    void updateJobDurationsByIds(Map<String, Long> jobHashmap);

}
