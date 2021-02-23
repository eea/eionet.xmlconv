package eionet.gdem.rabbitMQ.service;

import eionet.gdem.rabbitMQ.errors.CreateRabbitMQMessageException;

public interface RabbitMQMessageFactory {

    void createScriptAndSendMessageToRabbitMQ(String jobId) throws CreateMQMessageException;
}
