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

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "JOB_ID")
    private Integer jobId;

    public JobExecutor() {
    }

    public JobExecutor(String name, Integer status, Integer jobId) {
        this.name = name;
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
}
