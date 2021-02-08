package eionet.gdem.rabbitMQ.service;

import eionet.gdem.rabbitMQ.errors.CreateMQMessageException;

public interface RabbitMQMessageFactory {

    void createScriptAndSendMessageToRabbitMQ(String jobId) throws CreateMQMessageException;
}
