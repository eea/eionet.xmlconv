package eionet.gdem.rabbitMQ.service;

import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessageInfo;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;

public interface WorkersJobMessageSender {

    void sendJobInfoToRabbitMQ(WorkerJobRabbitMQRequest workerJobRabbitMQRequest);

    void sendMessageForJobExecution(WorkerHeartBeatMessageInfo workerHeartBeatMessageInfo);

}
