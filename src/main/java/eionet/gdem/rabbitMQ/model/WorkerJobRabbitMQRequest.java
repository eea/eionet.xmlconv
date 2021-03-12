package eionet.gdem.rabbitMQ.model;

import eionet.gdem.qa.XQScript;

public class WorkerJobRabbitMQRequest {

    private XQScript script;
    private String jobExecutorName;

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
}
