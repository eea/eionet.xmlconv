package eionet.gdem.web.spring.workqueue;

/**
 *
 *
 */
public class JobMetadata {

    /*Some variables were changed from camel case to snake case to be used properly in vue.js */

    private String jobId;
    private String url;
    private String url_name;
    private String fileName;
    private String script_file;
    private String script_url;
    private String result_file;
    private int status;
    private String statusName;
    private String timestamp;
    private String scriptId;
    private String instance;
    private String scriptType;
    private String durationInProgress;
    private String jobType;
    private String jobExecutorName;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getResult_file() {
        return result_file;
    }

    public void setResult_file(String result_file) {
        this.result_file = result_file;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }


    public String getScript_file() {
        return script_file;
    }

    public void setScript_file(String script_file) {
        this.script_file = script_file;
    }

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public String getDurationInProgress() {
        return durationInProgress;
    }

    public void setDurationInProgress(String durationInProgress) {
        this.durationInProgress = durationInProgress;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getUrl_name() {
        return url_name;
    }

    public void setUrl_name(String url_name) {
        this.url_name = url_name;
    }

    public String getJobExecutorName() {
        return jobExecutorName;
    }

    public void setJobExecutorName(String jobExecutorName) {
        this.jobExecutorName = jobExecutorName;
    }

    public String getScript_url() {
        return script_url;
    }

    public void setScript_url(String script_url) {
        this.script_url = script_url;
    }
}
