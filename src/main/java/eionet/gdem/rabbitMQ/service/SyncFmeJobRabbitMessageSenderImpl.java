package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Properties;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyncFmeJobRabbitMessageSenderImpl implements RabbitMQMessageSender<WorkerJobRabbitMQRequestMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncFmeJobRabbitMessageSenderImpl.class);

    private RabbitTemplate rabbitTemplate;
    private RabbitmqMsgPriorityService rabbitmqMsgPriorityService;

    @Autowired
    public SyncFmeJobRabbitMessageSenderImpl(RabbitTemplate rabbitTemplate, RabbitmqMsgPriorityService rabbitmqMsgPriorityService) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitmqMsgPriorityService = rabbitmqMsgPriorityService;
    }

    @Override
    public void sendMessageToRabbitMQ(WorkerJobRabbitMQRequestMessage workerJobRequest) {
        if (workerJobRequest.getJobExecutionRetries() == null) {
            workerJobRequest.setJobExecutionRetries(0);
        }
        rabbitTemplate.convertAndSend(Properties.SYNC_FME_JOBS_QUEUE, workerJobRequest, message -> {
            message.getMessageProperties().setPriority(rabbitmqMsgPriorityService.getMsgPriorityBasedOnJobType(workerJobRequest));
            return message;
        });
        LOGGER.info("Job with id " + workerJobRequest.getScript().getJobId() + " added in rabbitmq queue " + Properties.SYNC_FME_JOBS_QUEUE);
    }
}
