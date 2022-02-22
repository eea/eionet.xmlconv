package eionet.gdem.web.spring.workqueue;

import java.util.List;

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
    private String script_type;
    private String durationInProgress;
    private String jobType;
    private String jobExecutorName;
    private String fme_job_id;
    private String fme_job_url;
    private String converters_graylog_url;
    private String job_executor_graylog_url;
    private String from_date;
    private String to_date;
    private List<JobHistoryMetadata> job_history_metadata_list;
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

    public String getScript_type() {
        return script_type;
    }

    public void setScript_type(String script_type) {
        this.script_type = script_type;
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

    public String getFme_job_url() {
        return fme_job_url;
    }

    public void setFme_job_url(String fme_job_url) {
        this.fme_job_url = fme_job_url;
    }

    public String getConverters_graylog_url() {
        return converters_graylog_url;
    }

    public void setConverters_graylog_url(String converters_graylog_url) {
        this.converters_graylog_url = converters_graylog_url;
    }

    public String getJob_executor_graylog_url() {
        return job_executor_graylog_url;
    }

    public void setJob_executor_graylog_url(String job_executor_graylog_url) {
        this.job_executor_graylog_url = job_executor_graylog_url;
    }

    public String getFme_job_id() {
        return fme_job_id;
    }

    public void setFme_job_id(String fme_job_id) {
        this.fme_job_id = fme_job_id;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public List<JobHistoryMetadata> getJob_history_metadata_list() {
        return job_history_metadata_list;
    }

    public void setJob_history_metadata_list(List<JobHistoryMetadata> job_history_metadata_list) {
        this.job_history_metadata_list = job_history_metadata_list;
    }
}
