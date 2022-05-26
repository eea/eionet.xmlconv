package eionet.gdem.rabbitMQ.listeners;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class CdrRequestMessageReceiver implements MessageListener {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(CdrRequestMessageReceiver.class);

    @Override
    public void onMessage(Message message) {
        String messageBody = new String(message.getBody());

        ObjectMapper mapper =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
        try {
            String request = mapper.readValue(messageBody, String.class);
        } catch (Exception e) {
            LOGGER.error("Error during cdr request message processing");
        }
    }
}
