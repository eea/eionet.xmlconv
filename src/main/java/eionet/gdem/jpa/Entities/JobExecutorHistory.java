package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "JOB_EXECUTOR_HISTORY")
public class JobExecutorHistory {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CONTAINER_ID")
    private String containerId;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "JOB_ID")
    private Integer jobId;

    @Column(name = "DATE_ADDED")
    private Timestamp dateAdded;

    @Column(name = "HEART_BEAT_QUEUE")
    private String heartBeatQueue;

//    @Column(name = "TYPE")
//    private JobExecutorType type;

    public JobExecutorHistory() {
    }

    public JobExecutorHistory(String name, String containerId, Integer status, Timestamp dateAdded, String heartBeatQueue) {
        this.name = name;
        this.containerId = containerId;
        this.status = status;
        this.dateAdded = dateAdded;
        this.heartBeatQueue = heartBeatQueue;
    }

    public JobExecutorHistory(String name, String containerId, Integer status, Integer jobId, Timestamp dateAdded, String heartBeatQueue) {
        this.name = name;
        this.containerId = containerId;
        this.status = status;
        this.jobId = jobId;
        this.dateAdded = dateAdded;
        this.heartBeatQueue = heartBeatQueue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getHeartBeatQueue() {
        return heartBeatQueue;
    }

    public void setHeartBeatQueue(String heartBeatQueue) {
        this.heartBeatQueue = heartBeatQueue;
    }

//    public JobExecutorType getType() {
//        return type;
//    }
//
//    public void setType(JobExecutorType jobExecutorType) {
//        this.type = jobExecutorType;
//    }
}
