package eionet.gdem.rabbitMQ.model;

import eionet.gdem.qa.XQScript;

public class WorkerJobRabbitMQRequestMessage extends WorkerMessage {

    private XQScript script;
    private Integer jobExecutionRetries;
    private String errorMessage;
    private Integer errorStatus;
    private Integer jobExecutorStatus;
    private String heartBeatQueue;
    private String jobType;
    /**
     * true if job is created through api, false if it's created through UI
     */
    private boolean isApi;

    public WorkerJobRabbitMQRequestMessage(){
    }

    public WorkerJobRabbitMQRequestMessage(XQScript script) {
        this.script = script;
    }

    public XQScript getScript() {
        return script;
    }

    public WorkerJobRabbitMQRequestMessage setScript(XQScript script) {
        this.script = script;
        return this;
    }

    public Integer getJobExecutionRetries() {
        return jobExecutionRetries;
    }

    public void setJobExecutionRetries(Integer jobExecutionRetries) {
        this.jobExecutionRetries = jobExecutionRetries;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(Integer errorStatus) {
        this.errorStatus = errorStatus;
    }

    public Integer getJobExecutorStatus() {
        return jobExecutorStatus;
    }

    public void setJobExecutorStatus(Integer jobExecutorStatus) {
        this.jobExecutorStatus = jobExecutorStatus;
    }

    public String getHeartBeatQueue() {
        return heartBeatQueue;
    }

    public void setHeartBeatQueue(String heartBeatQueue) {
        this.heartBeatQueue = heartBeatQueue;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public boolean isApi() {
        return isApi;
    }

    public void setApi(boolean api) {
        isApi = api;
    }
}
