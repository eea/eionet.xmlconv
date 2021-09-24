package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Properties;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeartBeatRabbitMessageSenderImpl implements RabbitMQMessageSender<WorkerHeartBeatMessageInfo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatRabbitMessageSenderImpl.class);

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public HeartBeatRabbitMessageSenderImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendMessageToRabbitMQ(WorkerHeartBeatMessageInfo workerHeartBeatMessageInfo) {
        rabbitTemplate.convertAndSend(Properties.XMLCONV_HEART_BEAT_REQUEST_EXCHANGE, "", workerHeartBeatMessageInfo);
        LOGGER.info("Heart beat message sent for job " + workerHeartBeatMessageInfo.getJobId() + " and request timestamp " + workerHeartBeatMessageInfo.getRequestTimestamp());
    }
}
