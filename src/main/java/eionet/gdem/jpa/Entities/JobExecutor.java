package eionet.gdem.jpa.Entities;

import javax.persistence.*;

@Entity
@Table(name = "JOB_EXECUTOR")
public class JobExecutor {

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

    @Column(name = "HEART_BEAT_QUEUE")
    private String heartBeatQueue;

    public JobExecutor() {
    }

    public JobExecutor(String name, String containerId, Integer status, String heartBeatQueue) {
        this.name = name;
        this.containerId = containerId;
        this.status = status;
        this.heartBeatQueue = heartBeatQueue;
    }

    public JobExecutor(String name, String containerId, Integer status, Integer jobId) {
        this.name = name;
        this.containerId = containerId;
        this.status = status;
        this.jobId = jobId;
    }

    public Integer getId() {
        return id;
    }

    public JobExecutor setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public JobExecutor setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public JobExecutor setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Integer getJobId() {
        return jobId;
    }

    public JobExecutor setJobId(Integer jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getContainerId() {
        return containerId;
    }

    public JobExecutor setContainerId(String containerId) {
        this.containerId = containerId;
        return this;
    }

    public String getHeartBeatQueue() {
        return heartBeatQueue;
    }

    public JobExecutor setHeartBeatQueue(String heartBeatQueue) {
        this.heartBeatQueue = heartBeatQueue;
        return this;
    }
}
