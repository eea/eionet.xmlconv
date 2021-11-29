package eionet.gdem.web.spring.workqueue;

import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;

import java.util.List;

public class JobEntryAndJobHistoryEntriesObject {

    private JobEntry jobEntry;
    private List<JobHistoryEntry> jobHistoryEntries;

    public JobEntryAndJobHistoryEntriesObject(JobEntry jobEntry, List<JobHistoryEntry> jobHistoryEntries) {
        this.jobEntry = jobEntry;
        this.jobHistoryEntries = jobHistoryEntries;
    }

    public JobEntry getJobEntry() {
        return jobEntry;
    }

    public void setJobEntry(JobEntry jobEntry) {
        this.jobEntry = jobEntry;
    }

    public List<JobHistoryEntry> getJobHistoryEntries() {
        return jobHistoryEntries;
    }

    public void setJobHistoryEntries(List<JobHistoryEntry> jobHistoryEntries) {
        this.jobHistoryEntries = jobHistoryEntries;
    }
}
