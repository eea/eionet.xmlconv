package eionet.gdem.rabbitMQ.service;

import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessageInfo;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;

public interface RabbitMQMessageSender {

    /**
     * sends job to light worker
     * @param workerJobRabbitMQRequest
     */
    void sendJobInfoToRabbitMQ(WorkerJobRabbitMQRequest workerJobRabbitMQRequest);

    /**
     * sends heart beat messages to workers
     * @param workerHeartBeatMessageInfo
     */
    void sendHeartBeatMessage(WorkerHeartBeatMessageInfo workerHeartBeatMessageInfo);

    /**
     * sends job to heavy worker
     * @param workerJobRabbitMQRequest
     */
    void sendJobInfoToHeavyRabbitmqQueue(WorkerJobRabbitMQRequest workerJobRabbitMQRequest);

}
