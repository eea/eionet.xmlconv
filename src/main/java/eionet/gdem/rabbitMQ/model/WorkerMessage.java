package eionet.gdem.rabbitMQ.model;

public class WorkerMessage {

    private String jobExecutorName;

    public WorkerMessage() {
    }

    public WorkerMessage(String jobExecutorName) {
        this.jobExecutorName = jobExecutorName;
    }

    public String getJobExecutorName() {
        return jobExecutorName;
    }

    public void setJobExecutorName(String jobExecutorName) {
        this.jobExecutorName = jobExecutorName;
    }
}
