package eionet.gdem.rabbitMQ.model;

import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.qa.XQScript;

public class WorkerJobInfoRabbitMQResponse {

    private XQScript script;

    private boolean errorExists;

    private String errorMessage;

    private String jobExecutorName;

    private String executionTime;

    private Integer jobExecutorStatus;

    private String heartBeatQueue;

    private JobExecutorType jobExecutorType;

    public XQScript getScript() {
        return script;
    }

    public WorkerJobInfoRabbitMQResponse setScript(XQScript script) {
        this.script = script;
        return this;
    }

    public boolean isErrorExists() {
        return errorExists;
    }

    public WorkerJobInfoRabbitMQResponse setErrorExists(boolean errorExists) {
        this.errorExists = errorExists;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public WorkerJobInfoRabbitMQResponse setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getJobExecutorName() {
        return jobExecutorName;
    }

    public WorkerJobInfoRabbitMQResponse setJobExecutorName(String jobExecutorName) {
        this.jobExecutorName = jobExecutorName;
        return this;
    }

    public String getExecutionTime() {
        return executionTime;
    }

    public WorkerJobInfoRabbitMQResponse setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
        return this;
    }

    public Integer getJobExecutorStatus() {
        return jobExecutorStatus;
    }

    public WorkerJobInfoRabbitMQResponse setJobExecutorStatus(Integer jobExecutorStatus) {
        this.jobExecutorStatus = jobExecutorStatus;
        return this;
    }

    public String getHeartBeatQueue() {
        return heartBeatQueue;
    }

    public WorkerJobInfoRabbitMQResponse setHeartBeatQueue(String heartBeatQueue) {
        this.heartBeatQueue = heartBeatQueue;
        return this;
    }

    public JobExecutorType getJobExecutorType() {
        return jobExecutorType;
    }

    public WorkerJobInfoRabbitMQResponse setJobExecutorType(JobExecutorType jobExecutorType) {
        this.jobExecutorType = jobExecutorType;
        return this;
    }
}
