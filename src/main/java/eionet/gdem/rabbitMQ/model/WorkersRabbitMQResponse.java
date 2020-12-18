package eionet.gdem.rabbitMQ.model;

import eionet.gdem.qa.XQScript;

public class WorkersRabbitMQResponse {

    private XQScript xqScript;

    private int jobStatus;

    private boolean errorExists;

    private String errorMessage;

    private String containerName;

    private String executionTime;

    public XQScript getXqScript() {
        return xqScript;
    }

    public void setXqScript(XQScript xqScript) {
        this.xqScript = xqScript;
    }

    public int getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(int jobStatus) {
        this.jobStatus = jobStatus;
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
}
