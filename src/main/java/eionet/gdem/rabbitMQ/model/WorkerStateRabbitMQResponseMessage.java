package eionet.gdem.rabbitMQ.model;

import eionet.gdem.jpa.utils.JobExecutorType;

public class WorkerStateRabbitMQResponseMessage extends WorkerMessage {

    private Integer jobExecutorStatus;
    private String healthState;
    private String heartBeatQueue;
    private JobExecutorType jobExecutorType;

    public WorkerStateRabbitMQResponseMessage() {
    }

    public WorkerStateRabbitMQResponseMessage(String jobExecutorName, Integer jobExecutorStatus) {
        super(jobExecutorName);
        this.jobExecutorStatus = jobExecutorStatus;
    }

    public Integer getJobExecutorStatus() {
        return jobExecutorStatus;
    }

    public WorkerStateRabbitMQResponseMessage setJobExecutorStatus(Integer jobExecutorStatus) {
        this.jobExecutorStatus = jobExecutorStatus;
        return this;
    }

    public String getHealthState() {
        return healthState;
    }

    public WorkerStateRabbitMQResponseMessage setHealthState(String healthState) {
        this.healthState = healthState;
        return this;
    }

    public String getHeartBeatQueue() {
        return heartBeatQueue;
    }

    public WorkerStateRabbitMQResponseMessage setHeartBeatQueue(String heartBeatQueue) {
        this.heartBeatQueue = heartBeatQueue;
        return this;
    }

    public JobExecutorType getJobExecutorType() {
        return jobExecutorType;
    }

    public WorkerStateRabbitMQResponseMessage setJobExecutorType(JobExecutorType jobExecutorType) {
        this.jobExecutorType = jobExecutorType;
        return this;
    }
}
