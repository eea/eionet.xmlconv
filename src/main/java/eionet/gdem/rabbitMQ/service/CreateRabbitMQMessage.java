package eionet.gdem.rabbitMQ.service;

public interface CreateRabbitMQMessage {

    void createScriptAndSendMessageToRabbitMQ();

    void setJobId(String id);
}
