package eionet.gdem.services.impl;

import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.services.JobHistoryService;
import eionet.gdem.web.spring.workqueue.EntriesForPageObject;
import eionet.gdem.web.spring.workqueue.JobEntryAndJobHistoryEntriesObject;
import eionet.gdem.web.spring.workqueue.JobHistoryMetadata;
import eionet.gdem.web.spring.workqueue.JobMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobEntryAndJobHistoryEntriesServiceImpl implements JobEntryAndJobHistoryEntriesService {

    private JobService jobService;
    private JobHistoryService jobHistoryService;

    @Autowired
    public JobEntryAndJobHistoryEntriesServiceImpl(JobService jobService, JobHistoryService jobHistoryService) {
        this.jobService = jobService;
        this.jobHistoryService = jobHistoryService;
    }

    @Override
    public List<JobHistoryMetadata> getJobHistoryMetadata(String jobId) throws DatabaseException {
        List<JobHistoryEntry> jobHistoryEntries = jobHistoryService.getJobHistoryEntriesOfJob(jobId);
        List<JobHistoryMetadata> list = new ArrayList<>();
        for(JobHistoryEntry entry: jobHistoryEntries){
            list.add(new JobHistoryMetadata(entry.getFullStatusName(), entry.getDateAdded().toString(), entry.getJobExecutorName()));
        }
        return list;
    }

    @Override
    public EntriesForPageObject getSortedJobsForPage(Integer page, Integer itemsPerPage, String sortBy, Boolean sortDesc, String searchParam, String keyword) {
        //use page and itemsPerPage to get specific jobs
        EntriesForPageObject entriesForPageObject = jobService.getPagedAndSortedEntries(page, itemsPerPage, sortBy, sortDesc, searchParam, keyword);
        List<JobMetadata> jobMetadataList = jobService.getJobsMetadata(entriesForPageObject.getJobEntriesForPage());
        entriesForPageObject.setJobMetadataEntriesForPage(jobMetadataList);
        return entriesForPageObject;
    }

    @Override
    public Integer getNumberOfTotalJobs() {
        return jobService.getNumberOfTotalJobs();
    }
}














