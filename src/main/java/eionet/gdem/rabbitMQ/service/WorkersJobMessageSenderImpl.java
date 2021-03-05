package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Properties;
import eionet.gdem.qa.XQScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkersJobMessageSenderImpl implements WorkersJobMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkersJobMessageSenderImpl.class);

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public WorkersJobMessageSenderImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendJobInfoToRabbitMQ(XQScript xq) {
        rabbitTemplate.convertAndSend(Properties.WORKERS_JOBS_QUEUE, xq);
        LOGGER.info("Job with id " + xq.getJobId() + " added in rabbitmq queue " + Properties.WORKERS_JOBS_QUEUE);
    }
}
