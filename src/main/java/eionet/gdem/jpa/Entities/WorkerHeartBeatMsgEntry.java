package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "WORKER_HEART_BEAT_MSG")
public class WorkerHeartBeatMsgEntry implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "JOB_ID")
    private Integer jobId;

    @Column(name = "JOB_EXECUTOR_NAME")
    private String jobExecutorName;

    @Column(name = "REQUEST_TIMESTAMP")
    private Timestamp requestTimestamp;

    @Column(name = "RESPONSE_TIMESTAMP")
    private Timestamp responseTimestamp;

    @Column(name = "JOB_STATUS")
    private Integer jobStatus;

    public WorkerHeartBeatMsgEntry() {
    }

    public WorkerHeartBeatMsgEntry(Integer jobId, String jobExecutorName, Timestamp requestTimestamp) {
        this.jobId = jobId;
        this.jobExecutorName = jobExecutorName;
        this.requestTimestamp = requestTimestamp;
    }

    public WorkerHeartBeatMsgEntry(Integer jobId, String jobExecutorName, Timestamp requestTimestamp, Timestamp responseTimestamp, Integer jobStatus) {
        this.jobId = jobId;
        this.jobExecutorName = jobExecutorName;
        this.requestTimestamp = requestTimestamp;
        this.responseTimestamp = responseTimestamp;
        this.jobStatus = jobStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getJobExecutorName() {
        return jobExecutorName;
    }

    public void setJobExecutorName(String jobExecutorName) {
        this.jobExecutorName = jobExecutorName;
    }

    public Timestamp getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(Timestamp requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    public Timestamp getResponseTimestamp() {
        return responseTimestamp;
    }

    public void setResponseTimestamp(Timestamp responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }

    public Integer getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }
}


































