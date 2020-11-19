package eionet.gdem.services;

import eionet.gdem.rabbitMQ.RabbitMQException;

public interface RabbitMQConsumerService {
    void receiveMessageFromQueue() throws RabbitMQException;
}
