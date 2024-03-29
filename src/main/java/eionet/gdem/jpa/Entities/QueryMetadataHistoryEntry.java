package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "QUERY_METADATA_HISTORY")
public class QueryMetadataHistoryEntry implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "SCRIPT_FILENAME")
    private String scriptFilename;

    @Column(name = "T_QUERY_ID")
    private Integer queryId;

    @Column(name = "SCRIPT_TYPE")
    private String scriptType;

    @Column(name = "DURATION")
    private Long duration;

    @Column(name = "MARKED_HEAVY")
    private Boolean markedHeavy;

    @Column(name = "JOB_STATUS")
    private Integer jobStatus;

    @Column(name = "VERSION")
    private Integer version;

    @Column(name = "TIMESTAMP")
    private Timestamp timestamp;

    @Column(name = "JOB_ID")
    private Integer jobId;

    @Transient
    private String shortFileName;

    @Transient
    private String durationFormatted;

    @Transient
    private String statusName;

    @Column(name = "FME_JOB_ID")
    private Long fmeJobId;

    @Column(name = "XML_URL")
    private String xmlUrl;

    @Column(name = "XML_SIZE")
    private Long xmlSize;

    public QueryMetadataHistoryEntry() {
    }

    public QueryMetadataHistoryEntry(String scriptFilename, Integer queryId, String scriptType, Long duration, Boolean markedHeavy, Integer jobStatus, Integer version, Timestamp timestamp, Integer jobId) {
        this.scriptFilename = scriptFilename;
        this.queryId = queryId;
        this.scriptType = scriptType;
        this.duration = duration;
        this.markedHeavy = markedHeavy;
        this.jobStatus = jobStatus;
        this.version = version;
        this.timestamp = timestamp;
        this.jobId = jobId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getScriptFilename() {
        return scriptFilename;
    }

    public void setScriptFilename(String scriptFilename) {
        this.scriptFilename = scriptFilename;
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Boolean getMarkedHeavy() {
        return markedHeavy;
    }

    public void setMarkedHeavy(Boolean markedHeavy) {
        this.markedHeavy = markedHeavy;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getShortFileName() {
        return shortFileName;
    }

    public void setShortFileName(String shortFileName) {
        this.shortFileName = shortFileName;
    }

    public String getDurationFormatted() {
        return durationFormatted;
    }

    public void setDurationFormatted(String durationFormatted) {
        this.durationFormatted = durationFormatted;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Long getFmeJobId() {
        return fmeJobId;
    }

    public void setFmeJobId(Long fmeJobId) {
        this.fmeJobId = fmeJobId;
    }

    public String getXmlUrl() {
        return xmlUrl;
    }

    public void setXmlUrl(String xmlUrl) {
        this.xmlUrl = xmlUrl;
    }

    public Long getXmlSize() {
        return xmlSize;
    }

    public void setXmlSize(Long xmlSize) {
        this.xmlSize = xmlSize;
    }
}

