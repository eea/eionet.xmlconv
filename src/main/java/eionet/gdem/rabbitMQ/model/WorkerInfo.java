package eionet.gdem.rabbitMQ.model;

public class WorkerInfo {

    private String jobExecutorName;

    public WorkerInfo() {
    }

    public WorkerInfo(String jobExecutorName) {
        this.jobExecutorName = jobExecutorName;
    }

    public String getJobExecutorName() {
        return jobExecutorName;
    }

    public void setJobExecutorName(String jobExecutorName) {
        this.jobExecutorName = jobExecutorName;
    }
}
