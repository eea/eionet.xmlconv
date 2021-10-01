package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_QUERY")
public class QueryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUERY_ID")
    private Integer queryId;

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

    @OneToMany(mappedBy = "queryEntry", cascade = CascadeType.PERSIST)
    private List<QueryHistoryEntry> queryHistoryEntries;

    public QueryEntry() {
    }

    public QueryEntry(Integer queryId) {
        this.queryId = queryId;
    }

    public Integer getQueryId() {
        return queryId;
    }

    public QueryEntry setQueryId(Integer queryId) {
        this.queryId = queryId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public QueryEntry setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getShortName() {
        return shortName;
    }

    public QueryEntry setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public String getQueryFileName() {
        return queryFileName;
    }

    public QueryEntry setQueryFileName(String queryFileName) {
        this.queryFileName = queryFileName;
        return this;
    }

    public Integer getSchemaId() {
        return schemaId;
    }

    public QueryEntry setSchemaId(Integer schemaId) {
        this.schemaId = schemaId;
        return this;
    }

    public String getResultType() {
        return resultType;
    }

    public QueryEntry setResultType(String resultType) {
        this.resultType = resultType;
        return this;
    }

    public String getScriptType() {
        return scriptType;
    }

    public QueryEntry setScriptType(String scriptType) {
        this.scriptType = scriptType;
        return this;
    }

    public Integer getUpperLimit() {
        return upperLimit;
    }

    public QueryEntry setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public QueryEntry setUrl(String url) {
        this.url = url;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public QueryEntry setActive(boolean active) {
        this.active = active;
        return this;
    }

    public boolean isAsynchronousExecution() {
        return asynchronousExecution;
    }

    public QueryEntry setAsynchronousExecution(boolean asynchronousExecution) {
        this.asynchronousExecution = asynchronousExecution;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public QueryEntry setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public List<QueryHistoryEntry> getQueryHistoryEntries() {
        return queryHistoryEntries;
    }

    public void setQueryHistoryEntries(List<QueryHistoryEntry> queryHistoryEntries) {
        this.queryHistoryEntries = queryHistoryEntries;
    }

    public void addQueryHistoryEntry(QueryHistoryEntry queryHistoryEntry) {
        if (queryHistoryEntries == null) {
            queryHistoryEntries = new ArrayList<>();
        }
        queryHistoryEntry.setQueryEntry(this);
        queryHistoryEntries.add(queryHistoryEntry);
    }
}













