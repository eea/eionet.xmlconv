package eionet.gdem.jpa.Entities;

import javax.persistence.*;

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

    @ManyToOne()
    @JoinColumn(name = "QUERY_ID")
    private QueryEntry queryEntry;

    @ManyToOne
    @JoinColumn(name = "QUERY_BACK_UP_ID")
    private QueryBackupEntry queryBackupEntry;

    public QueryHistoryEntry() {
    }

    public QueryHistoryEntry(QueryHistoryEntryBuilder builder) {
        this.id = builder.id;
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
        this.queryEntry = builder.queryEntry;
        this.queryBackupEntry = builder.queryBackupEntry;
    }

    public static class QueryHistoryEntryBuilder {

        private Integer id;
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
        private String user;
        private QueryEntry queryEntry;
        private QueryBackupEntry queryBackupEntry;

        public QueryHistoryEntryBuilder setId(Integer id) {
            this.id = id;
            return this;
        }

        public QueryHistoryEntryBuilder setQueryEntry(QueryEntry queryEntry) {
            this.queryEntry = queryEntry;
            return this;
        }

        public QueryHistoryEntryBuilder setQueryBackupEntry(QueryBackupEntry queryBackupEntry) {
            this.queryBackupEntry = queryBackupEntry;
            return this;
        }

        public QueryHistoryEntryBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public QueryHistoryEntryBuilder setShortName(String shortName) {
            this.shortName = shortName;
            return this;
        }

        public QueryHistoryEntryBuilder setQueryFileName(String queryFileName) {
            this.queryFileName = queryFileName;
            return this;
        }

        public QueryHistoryEntryBuilder setSchemaId(Integer schemaId) {
            this.schemaId = schemaId;
            return this;
        }

        public QueryHistoryEntryBuilder setResultType(String resultType) {
            this.resultType = resultType;
            return this;
        }

        public QueryHistoryEntryBuilder setScriptType(String scriptType) {
            this.scriptType = scriptType;
            return this;
        }

        public QueryHistoryEntryBuilder setUpperLimit(Integer upperLimit) {
            this.upperLimit = upperLimit;
            return this;
        }

        public QueryHistoryEntryBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public QueryHistoryEntryBuilder setActive(boolean active) {
            this.active = active;
            return this;
        }

        public QueryHistoryEntryBuilder setAsynchronousExecution(boolean asynchronousExecution) {
            this.asynchronousExecution = asynchronousExecution;
            return this;
        }

        public QueryHistoryEntryBuilder setVersion(Integer version) {
            this.version = version;
            return this;
        }

        public QueryHistoryEntryBuilder setUser(String user) {
            this.user = user;
            return this;
        }

        public QueryHistoryEntry build() {
            return new QueryHistoryEntry(this);
        }
    }

    public Integer getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public QueryEntry getQueryEntry() {
        return queryEntry;
    }

    public QueryBackupEntry getQueryBackupEntry() {
        return queryBackupEntry;
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

    public String getUser() {
        return user;
    }
}
