package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Properties;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeavyJobRabbitMessageSenderImpl implements RabbitMQMessageSender<WorkerJobRabbitMQRequestMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeavyJobRabbitMessageSenderImpl.class);

    private RabbitTemplate rabbitTemplate;
    private RabbitmqMsgPriorityService rabbitmqMsgPriorityService;

    @Autowired
    public HeavyJobRabbitMessageSenderImpl(RabbitTemplate rabbitTemplate, RabbitmqMsgPriorityService rabbitmqMsgPriorityService) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitmqMsgPriorityService = rabbitmqMsgPriorityService;
    }

    @Override
    public void sendMessageToRabbitMQ(WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage) {
        if (workerJobRabbitMQRequestMessage.getJobExecutionRetries() == null) {
            workerJobRabbitMQRequestMessage.setJobExecutionRetries(0);
        }
        rabbitTemplate.convertAndSend(Properties.HEAVY_WORKERS_JOBS_QUEUE, workerJobRabbitMQRequestMessage, message -> {
            message.getMessageProperties().setPriority(rabbitmqMsgPriorityService.getMsgPriorityBasedOnJobType(workerJobRabbitMQRequestMessage));
            return message;
        });
        LOGGER.info("Heavy job with id " + workerJobRabbitMQRequestMessage.getScript().getJobId() + " added in heavy rabbitmq queue " + Properties.HEAVY_WORKERS_JOBS_QUEUE);
    }
}
