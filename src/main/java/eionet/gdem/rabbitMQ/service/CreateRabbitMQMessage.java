package eionet.gdem.rabbitMQ.service;

import eionet.gdem.rabbitMQ.errors.CreateMQMessageException;

public interface CreateRabbitMQMessage {

    void createScriptAndSendMessageToRabbitMQ() throws CreateMQMessageException;

    void setJobId(String id);
}
