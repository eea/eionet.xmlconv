package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

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
    private BigInteger duration;

    @Column(name = "MARKED_HEAVY")
    private Boolean markedHeavy;

    @Column(name = "VERSION")
    private Integer version;

    public QueryMetadataHistoryEntry() {
    }

    public QueryMetadataHistoryEntry(Integer id, String scriptFilename, Integer queryId, String scriptType, BigInteger duration, Boolean markedHeavy, Integer version) {
        this.id = id;
        this.scriptFilename = scriptFilename;
        this.queryId = queryId;
        this.scriptType = scriptType;
        this.duration = duration;
        this.markedHeavy = markedHeavy;
        this.version = version;
    }

    public QueryMetadataHistoryEntry(String scriptFilename, Integer queryId, String scriptType, BigInteger duration, Boolean markedHeavy, Integer version) {
        this.scriptFilename = scriptFilename;
        this.queryId = queryId;
        this.scriptType = scriptType;
        this.duration = duration;
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

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public BigInteger getDuration() {
        return duration;
    }

    public void setDuration(BigInteger duration) {
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
}
