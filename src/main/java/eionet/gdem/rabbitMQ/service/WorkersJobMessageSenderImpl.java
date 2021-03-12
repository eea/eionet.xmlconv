package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Properties;
import eionet.gdem.rabbitMQ.model.WorkerJobExecutionInfo;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;
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
    public void sendJobInfoToRabbitMQ(WorkerJobRabbitMQRequest workerJobRequest) {
        rabbitTemplate.convertAndSend(Properties.WORKERS_JOBS_QUEUE, workerJobRequest);
        LOGGER.info("Job with id " + workerJobRequest.getScript().getJobId() + " added in rabbitmq queue " + Properties.WORKERS_JOBS_QUEUE);
    }

    @Override
    public void sendMessageForJobExecution(WorkerJobExecutionInfo workerJobExecutionInfo) {
        rabbitTemplate.convertAndSend(Properties.WORKER_JOB_EXECUTION_REQUEST_QUEUE, workerJobExecutionInfo);
    }
}
