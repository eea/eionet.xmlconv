package eionet.gdem.rabbitMQ.model;

import java.sql.Timestamp;

public class WorkerHeartBeatMessageInfo {

    private Integer id;
    private String jobExecutorName;
    private Integer jobId;
    private Integer jobStatus;
    private Timestamp requestTimestamp;

    public WorkerHeartBeatMessageInfo() {
    }

    public WorkerHeartBeatMessageInfo(String jobExecutorName, Integer jobId) {
        this.jobExecutorName = jobExecutorName;
        this.jobId = jobId;
    }

    public WorkerHeartBeatMessageInfo(String jobExecutorName, Integer jobId, Timestamp requestTimestamp) {
        this.jobExecutorName = jobExecutorName;
        this.jobId = jobId;
        this.requestTimestamp = requestTimestamp;
    }

    public Integer getId() {
        return id;
    }

    public WorkerHeartBeatMessageInfo setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getJobExecutorName() {
        return jobExecutorName;
    }

    public WorkerHeartBeatMessageInfo setJobExecutorName(String jobExecutorName) {
        this.jobExecutorName = jobExecutorName;
        return this;
    }

    public Integer getJobId() {
        return jobId;
    }

    public WorkerHeartBeatMessageInfo setJobId(Integer jobId) {
        this.jobId = jobId;
        return this;
    }

    public Integer getJobStatus() {
        return jobStatus;
    }

    public WorkerHeartBeatMessageInfo setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
        return this;
    }

    public Timestamp getRequestTimestamp() {
        return requestTimestamp;
    }

    public WorkerHeartBeatMessageInfo setRequestTimestamp(Timestamp requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
        return this;
    }
}
