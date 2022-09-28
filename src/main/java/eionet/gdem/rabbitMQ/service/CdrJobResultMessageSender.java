package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Properties;
import eionet.gdem.rabbitMQ.model.CdrJobDeadLetterQueueMessage;
import eionet.gdem.rabbitMQ.model.CdrJobResponseMessage;
import eionet.gdem.rabbitMQ.model.CdrSummaryResponseMessage;
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

    public void sendSummaryMessageToRabbitMQ(CdrSummaryResponseMessage responseMessage) {
        rabbitTemplate.convertAndSend(Properties.CDR_RESULTS_QUEUE, responseMessage);
        LOGGER.info("Summary of jobs with UUID " + responseMessage.getUuid() + " has been sent to the rabbitmq queue " + Properties.CDR_RESULTS_QUEUE);
    }

    public void sendMessageToDeadLetterQueue(CdrJobDeadLetterQueueMessage dlqMessage) {
        rabbitTemplate.convertAndSend(Properties.CDR_DEAD_LETTER_QUEUE, dlqMessage);
        LOGGER.info("Error message for cdr request with UUID " + dlqMessage.getUUID() + " and envelope url " + dlqMessage.getEnvelopeUrl() + " has been sent to the rabbitmq queue " + Properties.CDR_DEAD_LETTER_QUEUE);
    }
}
