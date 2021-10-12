package eionet.gdem.rabbitMQ.service;

import eionet.gdem.rabbitMQ.model.WorkerMessage;

public interface RabbitMQMessageSender<T extends WorkerMessage> {


    void sendMessageToRabbitMQ(T t);

}
