package eionet.gdem.rabbitMQ.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterQueueMessageReceiver implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeadLetterQueueMessageReceiver.class);

    @Override
    public void onMessage(Message message) {
        String messageBody = new String(message.getBody());
        try {
            if(messageBody == null){
                LOGGER.info("Received NULL message in DEAD LETTER QUEUE");
            }
            else {
                LOGGER.info("Received message in DEAD LETTER QUEUE: " + messageBody);
            }
        } catch (Exception e) {
            LOGGER.info("Error during dead letter queue message processing: ", e.getMessage());
        }
    }
}
