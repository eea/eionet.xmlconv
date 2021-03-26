package eionet.gdem.rabbitMQ.model;

import eionet.gdem.qa.XQScript;

public class WorkerJobInfoRabbitMQResponse {

    private XQScript script;

    private boolean errorExists;

    private String errorMessage;

    private String jobExecutorName;

    private String executionTime;

    private Integer jobExecutorStatus;

    private String heartBeatQueue;

    private Integer errorStatus;

    private Integer jobExecutionRetries;

    public XQScript getScript() {
        return script;
    }

    public void setScript(XQScript script) {
        this.script = script;
    }

    public boolean isErrorExists() {
        return errorExists;
    }

    public void setErrorExists(boolean errorExists) {
        this.errorExists = errorExists;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getJobExecutorName() {
        return jobExecutorName;
    }

    public void setJobExecutorName(String jobExecutorName) {
        this.jobExecutorName = jobExecutorName;
    }

    public String getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
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

    public Integer getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(Integer errorStatus) {
        this.errorStatus = errorStatus;
    }

    public Integer getJobExecutionRetries() {
        return jobExecutionRetries;
    }

    public void setJobExecutionRetries(Integer jobExecutionRetries) {
        this.jobExecutionRetries = jobExecutionRetries;
    }
}
