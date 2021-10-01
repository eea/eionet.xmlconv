package eionet.gdem.jpa.Entities;

import javax.persistence.*;

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

    public QueryEntry() {
    }

    public QueryEntry(QueryEntryBuilder builder) {
        this.queryId = builder.queryId;
        this.description =builder.description;
        this.shortName = builder.shortName;
        this.queryFileName = builder.queryFileName;
        this.schemaId = builder.schemaId;
        this.resultType = builder.resultType;
        this.scriptType = builder.scriptType;
        this.upperLimit = builder.upperLimit;
        this.url = builder.url;
        this.active = builder.active;
        this.asynchronousExecution = builder.asynchronousExecution;
        this.version = builder.version;
    }

    public static class QueryEntryBuilder {

        private Integer queryId;
        private String description;
        private String shortName;
        private String queryFileName;
        private Integer schemaId;
        private String resultType;
        private String scriptType;
        private Integer upperLimit;
        private String url;
        private boolean active;
        private boolean asynchronousExecution;
        private Integer version;

        public QueryEntryBuilder() {
        }

        public QueryEntryBuilder(Integer queryId) {
            this.queryId = queryId;
        }

        public QueryEntryBuilder setQueryId(Integer queryId) {
            this.queryId = queryId;
            return this;
        }

        public QueryEntryBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public QueryEntryBuilder setShortName(String shortName) {
            this.shortName = shortName;
            return this;
        }

        public QueryEntryBuilder setQueryFileName(String queryFileName) {
            this.queryFileName = queryFileName;
            return this;
        }

        public QueryEntryBuilder setSchemaId(Integer schemaId) {
            this.schemaId = schemaId;
            return this;
        }

        public QueryEntryBuilder setResultType(String resultType) {
            this.resultType = resultType;
            return this;
        }

        public QueryEntryBuilder setScriptType(String scriptType) {
            this.scriptType = scriptType;
            return this;
        }

        public QueryEntryBuilder setUpperLimit(Integer upperLimit) {
            this.upperLimit = upperLimit;
            return this;
        }

        public QueryEntryBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public QueryEntryBuilder setActive(boolean active) {
            this.active = active;
            return this;
        }

        public QueryEntryBuilder setAsynchronousExecution(boolean asynchronousExecution) {
            this.asynchronousExecution = asynchronousExecution;
            return this;
        }

        public QueryEntryBuilder setVersion(Integer version) {
            this.version = version;
            return this;
        }

        public QueryEntry build() {
            return new QueryEntry(this);
        }
    }

    public Integer getQueryId() {
        return queryId;
    }

    public String getDescription() {
        return description;
    }

    public String getShortName() {
        return shortName;
    }

    public String getQueryFileName() {
        return queryFileName;
    }

    public Integer getSchemaId() {
        return schemaId;
    }

    public String getResultType() {
        return resultType;
    }

    public String getScriptType() {
        return scriptType;
    }

    public Integer getUpperLimit() {
        return upperLimit;
    }

    public String getUrl() {
        return url;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAsynchronousExecution() {
        return asynchronousExecution;
    }

    public Integer getVersion() {
        return version;
    }
}













