package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "QUERY_HISTORY")
public class QueryHistoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SHORT_NAME")
    private String shortName;

    @Column(name = "QUERY_FILENAME")
    private String queryFileName;

    @Column(name = "SCHEMA_ID")
    private Integer schemaId;

    @Column(name = "RESULT_TYPE")
    private String resultType;

    @Column(name = "SCRIPT_TYPE")
    private String scriptType;

    @Column(name = "UPPER_LIMIT")
    private Integer upperLimit;

    @Column(name = "URL")
    private String url;

    @Column(name = "ACTIVE")
    private boolean active;

    @Column(name = "ASYNCHRONOUS_EXECUTION")
    private boolean asynchronousExecution;

    @Column(name = "VERSION")
    private Integer version;

    @Column(name = "USER")
    private String user;

    @Column(name = "DATE_MODIFIED")
    private Date dateModified;

    @ManyToOne
    @JoinColumn(name = "QUERY_ID")
    private QueryEntry queryEntry;

    @OneToOne
    @JoinColumn(name = "QUERY_BACK_UP_ID")
    private QueryBackupEntry queryBackupEntry;

    @Transient
    private String dateMod;

    @Column(name = "MARKED_HEAVY")
    private Boolean markedHeavy;

    @Column(name = "MARKED_HEAVY_REASON")
    private Integer markedHeavyReason;

    @Column(name = "MARKED_HEAVY_REASON_OTHER")
    private String markedHeavyReasonOther;

    @Column(name = "RULE_MATCH")
    private String ruleMatch;

    public QueryHistoryEntry() {
    }

    public Integer getId() {
        return id;
    }

    public QueryHistoryEntry setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public QueryHistoryEntry setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getShortName() {
        return shortName;
    }

    public QueryHistoryEntry setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public String getQueryFileName() {
        return queryFileName;
    }

    public QueryHistoryEntry setQueryFileName(String queryFileName) {
        this.queryFileName = queryFileName;
        return this;
    }

    public Integer getSchemaId() {
        return schemaId;
    }

    public QueryHistoryEntry setSchemaId(Integer schemaId) {
        this.schemaId = schemaId;
        return this;
    }

    public String getResultType() {
        return resultType;
    }

    public QueryHistoryEntry setResultType(String resultType) {
        this.resultType = resultType;
        return this;
    }

    public String getScriptType() {
        return scriptType;
    }

    public QueryHistoryEntry setScriptType(String scriptType) {
        this.scriptType = scriptType;
        return this;
    }

    public Integer getUpperLimit() {
        return upperLimit;
    }

    public QueryHistoryEntry setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public QueryHistoryEntry setUrl(String url) {
        this.url = url;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public QueryHistoryEntry setActive(boolean active) {
        this.active = active;
        return this;
    }

    public boolean isAsynchronousExecution() {
        return asynchronousExecution;
    }

    public QueryHistoryEntry setAsynchronousExecution(boolean asynchronousExecution) {
        this.asynchronousExecution = asynchronousExecution;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public QueryHistoryEntry setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public String getUser() {
        return user;
    }

    public QueryHistoryEntry setUser(String user) {
        this.user = user;
        return this;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public QueryHistoryEntry setDateModified(Date dateModified) {
        this.dateModified = dateModified;
        return this;
    }

    public QueryEntry getQueryEntry() {
        return queryEntry;
    }

    public QueryHistoryEntry setQueryEntry(QueryEntry queryEntry) {
        this.queryEntry = queryEntry;
        return this;
    }

    public QueryBackupEntry getQueryBackupEntry() {
        return queryBackupEntry;
    }

    public QueryHistoryEntry setQueryBackupEntry(QueryBackupEntry queryBackupEntry) {
        this.queryBackupEntry = queryBackupEntry;
        return this;
    }

    public String getDateMod() {
        return dateMod;
    }

    public void setDateMod(String dateMod) {
        this.dateMod = dateMod;
    }

    public Boolean getMarkedHeavy() {
        return markedHeavy;
    }

    public QueryHistoryEntry setMarkedHeavy(Boolean markedHeavy) {
        this.markedHeavy = markedHeavy;
        return this;
    }

    public Integer getMarkedHeavyReason() {
        return markedHeavyReason;
    }

    public QueryHistoryEntry setMarkedHeavyReason(Integer markedHeavyReason) {
        this.markedHeavyReason = markedHeavyReason;
        return this;
    }

    public String getMarkedHeavyReasonOther() {
        return markedHeavyReasonOther;
    }

    public QueryHistoryEntry setMarkedHeavyReasonOther(String markedHeavyReasonOther) {
        this.markedHeavyReasonOther = markedHeavyReasonOther;
        return this;
    }

    public String getRuleMatch() {
        return ruleMatch;
    }

    public QueryHistoryEntry setRuleMatch(String ruleMatch) {
        this.ruleMatch = ruleMatch;
        return this;
    }
}
