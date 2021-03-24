package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Properties;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessageInfo;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQMessageSenderImpl implements RabbitMQMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQMessageSenderImpl.class);

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQMessageSenderImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendJobInfoToRabbitMQ(WorkerJobRabbitMQRequest workerJobRequest) {
        rabbitTemplate.convertAndSend(Properties.WORKERS_JOBS_QUEUE, workerJobRequest);
        LOGGER.info("Job with id " + workerJobRequest.getScript().getJobId() + " added in rabbitmq queue " + Properties.WORKERS_JOBS_QUEUE);
    }

    @Override
    public void sendHeartBeatMessage(WorkerHeartBeatMessageInfo workerHeartBeatMessageInfo) {
        rabbitTemplate.convertAndSend(Properties.XMLCONV_HEART_BEAT_REQUEST_EXCHANGE, "", workerHeartBeatMessageInfo);
        LOGGER.info("Heart beat message sent for job " + workerHeartBeatMessageInfo.getJobId() + " and request timestamp " + workerHeartBeatMessageInfo.getRequestTimestamp());
    }
}
