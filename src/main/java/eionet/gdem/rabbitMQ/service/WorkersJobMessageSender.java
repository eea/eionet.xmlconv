package eionet.gdem.rabbitMQ.service;

import eionet.gdem.qa.XQScript;

public interface WorkersJobMessageSender {

    void sendJobInfoToRabbitMQ(XQScript xq);

    void sendJobInfoOnDemandToRabbitMQ(XQScript xq);
}
