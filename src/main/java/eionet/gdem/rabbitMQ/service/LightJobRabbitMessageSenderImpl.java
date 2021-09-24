package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Properties;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LightJobRabbitMessageSenderImpl implements RabbitMQMessageSender<WorkerJobRabbitMQRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LightJobRabbitMessageSenderImpl.class);

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public LightJobRabbitMessageSenderImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendMessageToRabbitMQ(WorkerJobRabbitMQRequest workerJobRequest) {
        if (workerJobRequest.getJobExecutionRetries() == null) {
            workerJobRequest.setJobExecutionRetries(0);
        }
        rabbitTemplate.convertAndSend(Properties.WORKERS_JOBS_QUEUE, workerJobRequest);
        LOGGER.info("Job with id " + workerJobRequest.getScript().getJobId() + " added in rabbitmq queue " + Properties.WORKERS_JOBS_QUEUE);
    }
}
