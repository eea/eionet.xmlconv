package eionet.gdem.web.spring.workqueue;

import eionet.gdem.jpa.Entities.JobEntry;
import org.springframework.data.domain.Page;

import java.util.List;

public class EntriesForPageObject {

    List<JobEntry> jobEntriesForPage;
    List<JobMetadata> jobMetadataEntriesForPage;
    Integer totalNumberOfJobEntries;

    public EntriesForPageObject() {
    }

    public List<JobEntry> getJobEntriesForPage() {
        return jobEntriesForPage;
    }

    public void setJobEntriesForPage(List<JobEntry> jobEntriesForPage) {
        this.jobEntriesForPage = jobEntriesForPage;
    }

    public List<JobMetadata> getJobMetadataEntriesForPage() {
        return jobMetadataEntriesForPage;
    }

    public void setJobMetadataEntriesForPage(List<JobMetadata> jobMetadataEntriesForPage) {
        this.jobMetadataEntriesForPage = jobMetadataEntriesForPage;
    }

    public Integer getTotalNumberOfJobEntries() {
        return totalNumberOfJobEntries;
    }

    public void setTotalNumberOfJobEntries(Integer totalNumberOfJobEntries) {
        this.totalNumberOfJobEntries = totalNumberOfJobEntries;
    }

}
