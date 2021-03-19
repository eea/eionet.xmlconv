package eionet.gdem.rabbitMQ.model;

public class WorkerStateRabbitMQResponse {

    private String jobExecutorName;
    private Integer jobExecutorStatus;
    private String healthState;
    private String queueName;

    public WorkerStateRabbitMQResponse() {
    }

    public WorkerStateRabbitMQResponse(String jobExecutorName, Integer jobExecutorStatus) {
        this.jobExecutorName = jobExecutorName;
        this.jobExecutorStatus = jobExecutorStatus;
    }

    public String getJobExecutorName() {
        return jobExecutorName;
    }

    public WorkerStateRabbitMQResponse setJobExecutorName(String jobExecutorName) {
        this.jobExecutorName = jobExecutorName;
        return this;
    }

    public Integer getJobExecutorStatus() {
        return jobExecutorStatus;
    }

    public WorkerStateRabbitMQResponse setJobExecutorStatus(Integer jobExecutorStatus) {
        this.jobExecutorStatus = jobExecutorStatus;
        return this;
    }

    public String getHealthState() {
        return healthState;
    }

    public WorkerStateRabbitMQResponse setHealthState(String healthState) {
        this.healthState = healthState;
        return this;
    }

    public String getQueueName() {
        return queueName;
    }

    public WorkerStateRabbitMQResponse setQueueName(String queueName) {
        this.queueName = queueName;
        return this;
    }
}
