package eionet.gdem.rabbitMQ.service;

import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessageInfo;
import eionet.gdem.rabbitMQ.model.WorkerJobInfoRabbitMQResponse;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;

public interface RabbitMQMessageSender {

    void sendJobInfoToRabbitMQ(WorkerJobRabbitMQRequest workerJobRabbitMQRequest);

    void sendHeartBeatMessage(WorkerHeartBeatMessageInfo workerHeartBeatMessageInfo);

    void sendJobResponse(WorkerJobInfoRabbitMQResponse workerJobInfoRabbitMQResponse);
}
