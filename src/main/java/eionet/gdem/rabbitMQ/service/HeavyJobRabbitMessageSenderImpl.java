package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Properties;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeavyJobRabbitMessageSenderImpl implements RabbitMQMessageSender<WorkerJobRabbitMQRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeavyJobRabbitMessageSenderImpl.class);

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public HeavyJobRabbitMessageSenderImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendMessageToRabbitMQ(WorkerJobRabbitMQRequest workerJobRabbitMQRequest) {
        if (workerJobRabbitMQRequest.getJobExecutionRetries() == null) {
            workerJobRabbitMQRequest.setJobExecutionRetries(0);
        }
        rabbitTemplate.convertAndSend(Properties.HEAVY_WORKERS_JOBS_QUEUE, workerJobRabbitMQRequest);
        LOGGER.info("Heavy job with id " + workerJobRabbitMQRequest.getScript().getJobId() + " added in heavy rabbitmq queue " + Properties.HEAVY_WORKERS_JOBS_QUEUE);
    }
}
