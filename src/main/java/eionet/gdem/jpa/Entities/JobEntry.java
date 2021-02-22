package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "T_XQJOBS")
public class JobEntry implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "JOB_ID")
    private Integer id;

    @Column(name = "URL")
    private String url;

    @Column(name = "XQ_FILE")
    private String file;

    @Column(name = "RESULT_FILE")
    private String resultFile;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "TIME_STAMP")
    private Timestamp timestamp;

    @Column(name = "N_STATUS")
    private Integer nStatus;

    @Column(name = "QUERY_ID")
    private Integer queryId;

    @Column(name = "SRC_FILE")
    private String srcFile;

    @Column(name = "XQ_TYPE")
    private String type;

    @Column(name = "INSTANCE")
    private String instance;

    @Column(name = "RETRY_COUNTER")
    private Integer retryCounter;

    @Column(name = "DURATION")
    private BigInteger duration;

    @ManyToOne()
    @JoinColumn(name = "INTERNAL_STATUS_ID")
    private InternalSchedulingStatus intSchedulingStatus;

    @Column(name = "JOB_EXECUTOR_NAME")
    private String jobExecutorName;

    public JobEntry() {
    }

    public JobEntry(String url, String file, String resultFile, Integer nStatus, Integer queryId, Timestamp timestamp, String type, InternalSchedulingStatus intSchStatus) {
        this.url = url;
        this.file = file;
        this.resultFile = resultFile;
        this.timestamp = timestamp;
        this.nStatus = nStatus;
        this.queryId = queryId;
        this.type = type;
        this.intSchedulingStatus = intSchStatus;
    }

    public Integer getId() {
        return id;
    }

    public JobEntry setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public JobEntry setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getFile() {
        return file;
    }

    public JobEntry setFile(String file) {
        this.file = file;
        return this;
    }

    public String getResultFile() {
        return resultFile;
    }

    public JobEntry setResultFile(String resultFile) {
        this.resultFile = resultFile;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public JobEntry setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public JobEntry setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Integer getnStatus() {
        return nStatus;
    }

    public JobEntry setnStatus(Integer nStatus) {
        this.nStatus = nStatus;
        return this;
    }

    public Integer getQueryId() {
        return queryId;
    }

    public JobEntry setQueryId(Integer queryId) {
        this.queryId = queryId;
        return this;
    }

    public String getSrcFile() {
        return srcFile;
    }

    public JobEntry setSrcFile(String srcFile) {
        this.srcFile = srcFile;
        return this;
    }

    public String getType() {
        return type;
    }

    public JobEntry setType(String type) {
        this.type = type;
        return this;
    }

    public String getInstance() {
        return instance;
    }

    public JobEntry setInstance(String instance) {
        this.instance = instance;
        return this;
    }

    public Integer getRetryCounter() {
        return retryCounter;
    }

    public JobEntry setRetryCounter(Integer retryCounter) {
        this.retryCounter = retryCounter;
        return this;
    }

    public BigInteger getDuration() {
        return duration;
    }

    public JobEntry setDuration(BigInteger duration) {
        this.duration = duration;
        return this;
    }

    public InternalSchedulingStatus getIntSchedulingStatus() {
        return intSchedulingStatus;
    }

    public JobEntry setIntSchedulingStatus(InternalSchedulingStatus intSchedulingStatus) {
        this.intSchedulingStatus = intSchedulingStatus;
        return this;
    }

    public String getJobExecutorName() {
        return jobExecutorName;
    }

    public JobEntry setJobExecutorName(String jobExecutorName) {
        this.jobExecutorName = jobExecutorName;
        return this;
    }
}
