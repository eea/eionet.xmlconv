package eionet.gdem.services;

import eionet.gdem.rabbitMQ.RabbitMQException;

public interface RabbitMQProducerService {
    void sendMessageToQueue(String message) throws RabbitMQException;
}
