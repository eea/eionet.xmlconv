package eionet.gdem.rabbitMQ.service;

import eionet.gdem.rabbitMQ.model.WorkerJobExecutionInfo;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;

public interface WorkersJobMessageSender {

    void sendJobInfoToRabbitMQ(WorkerJobRabbitMQRequest workerJobRabbitMQRequest);

    void sendMessageForJobExecution(WorkerJobExecutionInfo workerJobExecutionInfo);

}
