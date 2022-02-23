package eionet.gdem.web.spring.workqueue;

import java.util.List;

public class WorkqueuePageInfo {

    private List<JobMetadata> jobMetadataList;
    private Integer totalJobEntries;
    private WorkqueuePermissions workqueuePermissions;
    private String username;

    public WorkqueuePageInfo(List<JobMetadata> jobMetadataList, Integer totalJobEntries, WorkqueuePermissions workqueuePermissions, String username) {
        this.jobMetadataList = jobMetadataList;
        this.totalJobEntries = totalJobEntries;
        this.workqueuePermissions = workqueuePermissions;
        this.username = username;
    }

    public List<JobMetadata> getJobMetadataList() {
        return jobMetadataList;
    }

    public void setJobMetadataList(List<JobMetadata> jobMetadataList) {
        this.jobMetadataList = jobMetadataList;
    }

    public WorkqueuePermissions getWorkqueuePermissions() {
        return workqueuePermissions;
    }

    public void setWorkqueuePermissions(WorkqueuePermissions workqueuePermissions) {
        this.workqueuePermissions = workqueuePermissions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getTotalJobEntries() {
        return totalJobEntries;
    }

    public void setTotalJobEntries(Integer totalJobEntries) {
        this.totalJobEntries = totalJobEntries;
    }
}
