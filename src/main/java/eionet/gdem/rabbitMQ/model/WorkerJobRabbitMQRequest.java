package eionet.gdem.rabbitMQ.model;

import eionet.gdem.qa.XQScript;

public class WorkerJobRabbitMQRequest {

    private XQScript script;
    private String jobExecutorName;
    private String errorMessage;
    private Integer errorStatus;
    private Integer jobExecutorStatus;
    private String heartBeatQueue;

    public WorkerJobRabbitMQRequest(){
    }

    public WorkerJobRabbitMQRequest(XQScript script) {
        this.script = script;
    }

    public XQScript getScript() {
        return script;
    }

    public WorkerJobRabbitMQRequest setScript(XQScript script) {
        this.script = script;
        return this;
    }

    public String getJobExecutorName() {
        return jobExecutorName;
    }

    public WorkerJobRabbitMQRequest setJobExecutorName(String jobExecutorName) {
        this.jobExecutorName = jobExecutorName;
        return this;
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
}
