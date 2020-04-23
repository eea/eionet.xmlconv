package eionet.gdem.jpa.Entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "JOB_HISTORY")
public class JobHistoryEntry implements Serializable {

    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "JOB_NAME")
    private String jobName;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "DATE_ADDED")
    private Timestamp dateAdded;

    @Column(name = "JOB_GROUP")
    private String jobGroup;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "JOB_CLASS_NAME")
    private String jobClassName;

    @Column(name = "URL")
    private String url;

    @Column(name = "XQ_FILE")
    private String xqFile;

    @Column(name = "RESULT_FILE")
    private String resultFile;

    @Column(name = "XQ_TYPE")
    private String xqType;

    public JobHistoryEntry() {
    }

    public JobHistoryEntry(Integer id, String jobName, Integer status, Timestamp dateAdded, String jobGroup,
                          String description, String jobClassName, String url, String xqFile, String resultFile, String xqType) {
        this.id = id;
        this.jobName = jobName;
        this.status = status;
        this.dateAdded = dateAdded;
        this.jobGroup = jobGroup;
        this.description = description;
        this.jobClassName = jobClassName;
        this.url = url;
        this.xqFile = xqFile;
        this.resultFile = resultFile;
        this.xqType = xqType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobClassName() {
        return jobClassName;
    }

    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getXqFile() {
        return xqFile;
    }

    public void setXqFile(String xqFile) {
        this.xqFile = xqFile;
    }

    public String getResultFile() {
        return resultFile;
    }

    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }

    public String getXqType() {
        return xqType;
    }

    public void setXqType(String xqType) {
        this.xqType = xqType;
    }
}
