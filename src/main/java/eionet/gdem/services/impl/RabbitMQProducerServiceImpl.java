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

   // @Autowired
   // @Resource(name = "producerChannel")
  //  private Channel producerChannel;

    @Autowired
    public RabbitMQProducerServiceImpl() {
    }

    @Override
    public void sendMessageToQueue(String message) throws RabbitMQException {

    }

    protected String getQUEUE_NAME() {
        return QUEUE_NAME;
    }

    private void setQUEUE_NAME(String QUEUE_NAME) {
        this.QUEUE_NAME = QUEUE_NAME;
    }
}
