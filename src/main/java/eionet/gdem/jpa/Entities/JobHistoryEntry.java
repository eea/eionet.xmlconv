package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "JOB_HISTORY")
public class JobHistoryEntry implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "JOB_NAME")
    private String jobName;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "DATE_ADDED")
    private Timestamp dateAdded;

    @Column(name = "URL")
    private String url;

    @Column(name = "XQ_FILE")
    private String xqFile;

    @Column(name = "RESULT_FILE")
    private String resultFile;

    @Column(name = "XQ_TYPE")
    private String xqType;

    @Column(name = "DURATION")
    private Long duration;

    @Transient
    private String fullStatusName;

    @Column(name = "INTERNAL_STATUS_ID")
    private Integer intSchedulingStatus;

    @Column(name = "JOB_EXECUTOR_NAME")
    private String jobExecutorName;

    @Column(name = "JOB_TYPE")
    private String jobType;

    @Column(name = "WORKER_RETRIES")
    private Integer workerRetries;

    @Column(name = "IS_HEAVY")
    private boolean isHeavy;

    @Column(name = "HEAVY_RETRIES_ON_FAILURE")
    private Integer heavyRetriesOnFailure;

    @Column(name = "FME_JOB_ID")
    private Long fmeJobId;

    public JobHistoryEntry() {
    }

    public JobHistoryEntry(Integer id, String jobName, Integer status, Timestamp dateAdded, String url, String xqFile, String resultFile, String xqType) {
        this.id = id;
        this.jobName = jobName;
        this.status = status;
        this.dateAdded = dateAdded;
        this.url = url;
        this.xqFile = xqFile;
        this.resultFile = resultFile;
        this.xqType = xqType;
    }

    public JobHistoryEntry(String jobName, Integer status, Timestamp dateAdded, String url, String xqFile, String resultFile, String xqType) {
        this.jobName = jobName;
        this.status = status;
        this.dateAdded = dateAdded;
        this.url = url;
        this.xqFile = xqFile;
        this.resultFile = resultFile;
        this.xqType = xqType;
    }

    public Integer getId() {
        return id;
    }

    public JobHistoryEntry setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getJobName() {
        return jobName;
    }

    public Integer getStatus() {
        return status;
    }

    public JobHistoryEntry setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public JobHistoryEntry setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
        return this;
    }

    public JobHistoryEntry setXqFile(String xqFile) {
        this.xqFile = xqFile;
        return this;
    }

    public JobHistoryEntry setResultFile(String resultFile) {
        this.resultFile = resultFile;
        return this;
    }

    public JobHistoryEntry setXqType(String xqType) {
        this.xqType = xqType;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public JobHistoryEntry setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getXqFile() {
        return xqFile;
    }

    public String getResultFile() {
        return resultFile;
    }

    public String getXqType() {
        return xqType;
    }

    public JobHistoryEntry setFullStatusName(String fullStatusName) {
        this.fullStatusName = fullStatusName;
        return this;
    }

    public String getFullStatusName() {
        return fullStatusName;
    }

    public Long getDuration() {
        return duration;
    }

    public JobHistoryEntry setDuration(Long duration) {
        this.duration = duration;
        return this;
    }

    public Integer getIntSchedulingStatus() {
        return intSchedulingStatus;
    }

    public JobHistoryEntry setIntSchedulingStatus(Integer intSchedulingStatus) {
        this.intSchedulingStatus = intSchedulingStatus;
        return this;
    }

    public String getJobExecutorName() {
        return jobExecutorName;
    }

    public JobHistoryEntry setJobExecutorName(String jobExecutorName) {
        this.jobExecutorName = jobExecutorName;
        return this;
    }

    public String getJobType() {
        return jobType;
    }

    public JobHistoryEntry setJobType(String jobType) {
        this.jobType = jobType;
        return this;
    }

    public Integer getWorkerRetries() {
        return workerRetries;
    }

    public JobHistoryEntry setWorkerRetries(Integer workerRetries) {
        this.workerRetries = workerRetries;
        return this;
    }

    public boolean isHeavy() {
        return isHeavy;
    }

    public JobHistoryEntry setHeavy(boolean heavy) {
        isHeavy = heavy;
        return this;
    }

    public Integer getHeavyRetriesOnFailure() {
        return heavyRetriesOnFailure;
    }

    public JobHistoryEntry setHeavyRetriesOnFailure(Integer heavyRetries) {
        this.heavyRetriesOnFailure = heavyRetries;
        return this;
    }

    public Long getFmeJobId() {
        return fmeJobId;
    }

    public void setFmeJobId(Long fmeJobId) {
        this.fmeJobId = fmeJobId;
    }
}
