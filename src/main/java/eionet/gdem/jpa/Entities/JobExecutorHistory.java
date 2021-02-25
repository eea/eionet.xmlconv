package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.sql.Time;
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

    public JobExecutorHistory() {
    }

    public JobExecutorHistory(String name, String containerId, Integer status, Timestamp dateAdded) {
        this.name = name;
        this.containerId = containerId;
        this.status = status;
        this.dateAdded = dateAdded;
    }

    public JobExecutorHistory(String name, String containerId, Integer status, Integer jobId, Timestamp dateAdded) {
        this.name = name;
        this.containerId = containerId;
        this.status = status;
        this.jobId = jobId;
        this.dateAdded = dateAdded;
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
}
