package eionet.gdem.rabbitMQ.model;

import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.qa.XQScript;

public class WorkerJobInfoRabbitMQResponseMessage extends WorkerMessage {

    private XQScript script;

    private boolean errorExists;

    private String errorMessage;

    private String executionTime;

    private Integer jobExecutorStatus;

    private String heartBeatQueue;

    private JobExecutorType jobExecutorType;

    public XQScript getScript() {
        return script;
    }

    public WorkerJobInfoRabbitMQResponseMessage setScript(XQScript script) {
        this.script = script;
        return this;
    }

    public boolean isErrorExists() {
        return errorExists;
    }

    public WorkerJobInfoRabbitMQResponseMessage setErrorExists(boolean errorExists) {
        this.errorExists = errorExists;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public WorkerJobInfoRabbitMQResponseMessage setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getExecutionTime() {
        return executionTime;
    }

    public WorkerJobInfoRabbitMQResponseMessage setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
        return this;
    }

    public Integer getJobExecutorStatus() {
        return jobExecutorStatus;
    }

    public WorkerJobInfoRabbitMQResponseMessage setJobExecutorStatus(Integer jobExecutorStatus) {
        this.jobExecutorStatus = jobExecutorStatus;
        return this;
    }

    public String getHeartBeatQueue() {
        return heartBeatQueue;
    }

    public WorkerJobInfoRabbitMQResponseMessage setHeartBeatQueue(String heartBeatQueue) {
        this.heartBeatQueue = heartBeatQueue;
        return this;
    }

    public JobExecutorType getJobExecutorType() {
        return jobExecutorType;
    }

    public WorkerJobInfoRabbitMQResponseMessage setJobExecutorType(JobExecutorType jobExecutorType) {
        this.jobExecutorType = jobExecutorType;
        return this;
    }
}
