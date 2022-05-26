package eionet.gdem.rabbitMQ.listeners;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.rabbitMQ.model.CdrJobRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CdrJobRequestMessageReceiver implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdrJobRequestMessageReceiver.class);

    @Autowired
    private QaService qaService;

    @Override
    public void onMessage(Message message) {
        String messageBody = new String(message.getBody());
        try {
            ObjectMapper mapper =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //the following class may need changes.
            CdrJobRequestMessage cdrMessage = mapper.readValue(messageBody, CdrJobRequestMessage.class);
            LOGGER.info("Jobs from cdr queue for envelope url " + cdrMessage.getEnvelopeUrl() + " and UUID " + cdrMessage.getUUID() + " will be scheduled");
            List<QaResultsWrapper> qaResults = qaService.scheduleJobs(cdrMessage.getEnvelopeUrl(), true, true, cdrMessage.getUUID());
            LOGGER.info("Jobs from cdr queue for envelope url " + cdrMessage.getEnvelopeUrl() + " and UUID " + cdrMessage.getUUID() + " have been scheduled");
        } catch (Exception e) {
            LOGGER.error("Error during cdr message processing. Exception message is:  " + e.getMessage());
        }
    }
}
