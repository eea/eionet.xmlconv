package eionet.gdem.rabbitMQ.model;

import eionet.gdem.qa.XQScript;

public class WorkersRabbitMQResponse {

    private XQScript script;

    private boolean errorExists;

    private String errorMessage;

    private String containerName;

    private String executionTime;

    private Integer jobExecutorStatus;

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

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
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
}
