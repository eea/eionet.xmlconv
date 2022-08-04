package eionet.gdem.rabbitMQ.listeners;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.jpa.Entities.CdrRequestEntry;
import eionet.gdem.jpa.service.CdrRequestsService;
import eionet.gdem.rabbitMQ.model.CdrJobRequestMessage;
import eionet.gdem.rabbitMQ.service.CdrResponseMessageFactoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CdrRequestMessageReceiver implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdrRequestMessageReceiver.class);

    @Autowired
    private QaService qaService;

    @Autowired
    private CdrRequestsService cdrRequestsService;

    @Autowired
    private CdrResponseMessageFactoryService cdrResponseMessageFactoryService;

    @Override
    public void onMessage(Message message) {
        String messageBody = new String(message.getBody());
        String uuid = null;
        List<QaResultsWrapper> qaResults =  new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            CdrJobRequestMessage cdrMessage = mapper.readValue(messageBody, CdrJobRequestMessage.class);
            uuid = cdrMessage.getUUID();
            LOGGER.info("Jobs from cdr queue for envelope url " + cdrMessage.getEnvelopeUrl() + " and UUID " + uuid + " will be scheduled");
            qaResults = qaService.scheduleJobs(cdrMessage.getEnvelopeUrl(), false, true, uuid);
            CdrRequestEntry cdrRequestEntry = new CdrRequestEntry(uuid, cdrMessage.getEnvelopeUrl(), qaResults.size(), new Timestamp(new Date().getTime()));
            cdrRequestsService.save(cdrRequestEntry);
            cdrResponseMessageFactoryService.createCdrSummaryResponseMessageAndSendToQueue(uuid, cdrMessage.getEnvelopeUrl(), qaResults);
            LOGGER.info("There are " + qaResults.size() + " jobs that have been scheduled for envelope url " + cdrMessage.getEnvelopeUrl() + " and UUID " + uuid);
        } catch (Exception e) {
            LOGGER.error("Error during cdr message processing. Exception message is:  " + e.getMessage());
            throw new AmqpRejectAndDontRequeueException(e.getMessage());
        }
    }
}
