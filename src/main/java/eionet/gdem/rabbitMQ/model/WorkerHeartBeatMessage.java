package eionet.gdem.rabbitMQ.model;

import eionet.gdem.jpa.utils.JobExecutorType;

import java.sql.Timestamp;

public class WorkerHeartBeatMessage extends WorkerMessage {

    private Integer id;
    private Integer jobId;
    private Integer jobStatus;
    private Timestamp requestTimestamp;
    private JobExecutorType jobExecutorType;

    public WorkerHeartBeatMessage() {

    }

    public WorkerHeartBeatMessage(String jobExecutorName, Integer jobId) {
        super(jobExecutorName);
        this.jobId = jobId;
    }

    public WorkerHeartBeatMessage(String jobExecutorName, Integer jobId, Timestamp requestTimestamp) {
        super(jobExecutorName);
        this.jobId = jobId;
        this.requestTimestamp = requestTimestamp;
    }

    public Integer getId() {
        return id;
    }

    public WorkerHeartBeatMessage setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getJobId() {
        return jobId;
    }

    public WorkerHeartBeatMessage setJobId(Integer jobId) {
        this.jobId = jobId;
        return this;
    }

    public Integer getJobStatus() {
        return jobStatus;
    }

    public WorkerHeartBeatMessage setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
        return this;
    }

    public Timestamp getRequestTimestamp() {
        return requestTimestamp;
    }

    public WorkerHeartBeatMessage setRequestTimestamp(Timestamp requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
        return this;
    }

    public JobExecutorType getJobExecutorType() {
        return jobExecutorType;
    }

    public WorkerHeartBeatMessage setJobExecutorType(JobExecutorType jobExecutorType) {
        this.jobExecutorType = jobExecutorType;
        return this;
    }
}
