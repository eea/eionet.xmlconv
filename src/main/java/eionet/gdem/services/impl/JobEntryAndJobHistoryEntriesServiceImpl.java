package eionet.gdem.services.impl;

import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.services.JobHistoryService;
import eionet.gdem.web.spring.workqueue.JobEntryAndJobHistoryEntriesObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public JobEntryAndJobHistoryEntriesObject getJobEntryAndJobHistoryEntriesOfJob(String jobId) throws DatabaseException {
        JobEntry jobEntry = jobService.findById(Integer.parseInt(jobId));
        jobEntry.setFromDate(jobEntry.getTimestamp().toLocalDateTime().minusDays(1).toString());
        jobEntry.setToDate(jobEntry.getTimestamp().toLocalDateTime().plusDays(1).toString());
        List<JobHistoryEntry> jobHistoryEntries = jobHistoryService.getJobHistoryEntriesOfJob(jobId);
        JobEntryAndJobHistoryEntriesObject jobEntryAndJobHistoryEntriesObject = new JobEntryAndJobHistoryEntriesObject(jobEntry, jobHistoryEntries);
        return jobEntryAndJobHistoryEntriesObject;
    }
}














