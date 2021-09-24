package eionet.gdem.rabbitMQ.service;

import eionet.gdem.rabbitMQ.model.WorkerInfo;

public interface RabbitMQMessageSender<T extends WorkerInfo> {


    void sendMessageToRabbitMQ(T t);

}
