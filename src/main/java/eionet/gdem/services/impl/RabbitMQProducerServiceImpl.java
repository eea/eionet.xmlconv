package eionet.gdem.services.impl;

import com.rabbitmq.client.Channel;
import eionet.gdem.Properties;
import eionet.gdem.rabbitMQ.RabbitMQException;
import eionet.gdem.services.RabbitMQProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RabbitMQProducerServiceImpl implements RabbitMQProducerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQProducerService.class);
    private String QUEUE_NAME = null;

    @Autowired
    @Resource(name = "producerChannel")
    private Channel producerChannel;

    @Autowired
    public RabbitMQProducerServiceImpl() {
    }

    @Override
    public void sendMessageToQueue(String message) throws RabbitMQException {
        this.setQUEUE_NAME(Properties.rabbitMQProducerQueueName);
        try
        {
            producerChannel.basicPublish("", this.getQUEUE_NAME(), null, message.getBytes());
            LOGGER.info("Message was sent to queue "  + this.getQUEUE_NAME());
        }
        catch(Exception e){
            LOGGER.error("Error when sending message to queue" + this.getQUEUE_NAME());
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
