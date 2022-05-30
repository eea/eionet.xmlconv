package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Properties;
import eionet.gdem.rabbitMQ.model.CdrJobResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CdrJobResultMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdrJobResultMessageSender.class);

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public CdrJobResultMessageSender(@Qualifier("cdrRabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessageToRabbitMQ(CdrJobResponseMessage responseMessage) {
        rabbitTemplate.convertAndSend(Properties.CDR_RESULTS_QUEUE, responseMessage);
        LOGGER.info("Results for job with id " + responseMessage.getJobId() + " have been sent to the rabbitmq queue " + Properties.CDR_RESULTS_QUEUE);
    }
}
