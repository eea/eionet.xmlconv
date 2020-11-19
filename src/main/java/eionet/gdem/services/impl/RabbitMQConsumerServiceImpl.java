package eionet.gdem.services.impl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import eionet.gdem.Properties;
import eionet.gdem.rabbitMQ.RabbitMQException;
import eionet.gdem.services.RabbitMQConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RabbitMQConsumerServiceImpl implements RabbitMQConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumerService.class);
    private String QUEUE_NAME = null;

    @Autowired
    @Resource(name = "consumerChannel")
    private Channel consumerChannel;

    @Autowired
    public RabbitMQConsumerServiceImpl() {
    }

    @Override
    public void receiveMessageFromQueue() throws RabbitMQException {
        this.setQUEUE_NAME(Properties.rabbitMQConsumerQueueName);
        try
        {
            LOGGER.info("Waiting for messages from queue "  + this.getQUEUE_NAME());

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                //The actual message that was received from the queue
                String message = new String(delivery.getBody(), "UTF-8");
                LOGGER.info("Received message from queue "  + this.getQUEUE_NAME());
            };
            String message = consumerChannel.basicConsume(this.getQUEUE_NAME(), true, deliverCallback, consumerTag -> {
            });
        }
        catch(Exception e){
            LOGGER.error("Error when receiving message to queue" + this.getQUEUE_NAME());
            throw new RabbitMQException(e.getMessage());
        }
    }

    protected String getQUEUE_NAME() {
        return QUEUE_NAME;
    }

    private void setQUEUE_NAME(String QUEUE_NAME) {
        this.QUEUE_NAME = QUEUE_NAME;
    }
}
