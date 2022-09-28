package eionet.gdem.rabbitMQ.model;

public class CdrJobResponseMessage {

    private String UUID;
    private String documentURL;
    private String jobId;
    private String jobStatus;
    private String scriptTitle;
    private String scriptId;
    private CdrJobResultMessage jobResult;

    private CdrJobExecutionStatus executionStatus;

    public CdrJobResponseMessage() {
    }

    public CdrJobResponseMessage(String UUID, String documentURL, String jobId, String jobStatus,
                                 String scriptTitle, String scriptId, CdrJobResultMessage jobResult,
                                 CdrJobExecutionStatus executionStatus) {
        this.UUID = UUID;
        this.documentURL = documentURL;
        this.jobId = jobId;
        this.jobStatus = jobStatus;
        this.scriptTitle = scriptTitle;
        this.scriptId = scriptId;
        this.jobResult = jobResult;
        this.executionStatus = executionStatus;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getDocumentURL() {
        return documentURL;
    }

    public void setDocumentURL(String documentURL) {
        this.documentURL = documentURL;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getScriptTitle() {
        return scriptTitle;
    }

    public void setScriptTitle(String scriptTitle) {
        this.scriptTitle = scriptTitle;
    }

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    public CdrJobResultMessage getJobResult() {
        return jobResult;
    }

    public void setJobResult(CdrJobResultMessage jobResult) {
        this.jobResult = jobResult;
    }

    public CdrJobExecutionStatus getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(CdrJobExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }
}
