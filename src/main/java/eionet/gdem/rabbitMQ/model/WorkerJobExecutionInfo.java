package eionet.gdem.rabbitMQ.model;

public class WorkerJobExecutionInfo {

    private String jobExecutorName;
    private Integer jobId;
    private boolean executing;

    public WorkerJobExecutionInfo() {
    }

    public WorkerJobExecutionInfo(String jobExecutorName, Integer jobId) {
        this.jobExecutorName = jobExecutorName;
        this.jobId = jobId;
    }

    public String getJobExecutorName() {
        return jobExecutorName;
    }

    public WorkerJobExecutionInfo setJobExecutorName(String jobExecutorName) {
        this.jobExecutorName = jobExecutorName;
        return this;
    }

    public Integer getJobId() {
        return jobId;
    }

    public WorkerJobExecutionInfo setJobId(Integer jobId) {
        this.jobId = jobId;
        return this;
    }

    public boolean isExecuting() {
        return executing;
    }

    public WorkerJobExecutionInfo setExecuting(boolean executing) {
        this.executing = executing;
        return this;
    }
}
