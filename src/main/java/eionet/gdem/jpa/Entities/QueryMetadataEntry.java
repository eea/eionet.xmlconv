package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Table(name = "QUERY_METADATA")
public class QueryMetadataEntry implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "SCRIPT_FILENAME")
    private String scriptFilename;

    @Column(name = "T_QUERY_ID")
    private Integer queryId;

    @Column(name = "AVERAGE_DURATION")
    private Long averageDuration;

    @Column(name = "SCRIPT_TYPE")
    private String scriptType;

    @Column(name = "NUMBER_OF_EXECUTIONS")
    private Integer numberOfExecutions;

    @Column(name = "MARKED_HEAVY")
    private Boolean markedHeavy;

    @Column(name = "VERSION")
    private Integer version;

    public QueryMetadataEntry() {
    }

    public QueryMetadataEntry(Integer id, String scriptFilename, Integer queryId, String scriptType, Long averageDuration, Integer numberOfExecutions, Boolean markedHeavy, Integer version) {
        this.id = id;
        this.scriptFilename = scriptFilename;
        this.queryId = queryId;
        this.averageDuration = averageDuration;
        this.scriptType = scriptType;
        this.numberOfExecutions = numberOfExecutions;
        this.markedHeavy = markedHeavy;
        this.version = version;
    }

    public QueryMetadataEntry(String scriptFilename, Integer queryId, String scriptType, Long averageDuration, Integer numberOfExecutions, Boolean markedHeavy, Integer version) {
        this.scriptFilename = scriptFilename;
        this.queryId = queryId;
        this.averageDuration = averageDuration;
        this.scriptType = scriptType;
        this.numberOfExecutions = numberOfExecutions;
        this.markedHeavy = markedHeavy;
        this.version = version;
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

    public Long getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(Long averageDuration) {
        this.averageDuration = averageDuration;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public Integer getNumberOfExecutions() {
        return numberOfExecutions;
    }

    public void setNumberOfExecutions(Integer numberOfExecutions) {
        this.numberOfExecutions = numberOfExecutions;
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
}