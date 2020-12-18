package eionet.gdem.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.WorkersJobsResultsMessageReceiver;
import eionet.gdem.rabbitMQ.model.WorkersRabbitMQResponse;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class WorkersJobsResultsMessageReceiverTest {

    @Mock
    private IXQJobDao xqJobDao;

    @Mock
    private JobHistoryRepository jobHistoryRepository;

    @Spy
    @InjectMocks
    private WorkersJobsResultsMessageReceiver receiver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnMessage() throws JsonProcessingException, SQLException {
        String[] scriptParams = new String[0];
        XQScript xqScript = new XQScript(null, scriptParams, "HTML");
        xqScript.setJobId("12452");

        WorkersRabbitMQResponse response = createRabbitMQResponse(xqScript);
        Message message = convertObjectToByteArray(response);

        doNothing().when(xqJobDao).changeJobStatus(anyString(),anyInt());
        JobHistoryEntry jobHistoryEntry = new JobHistoryEntry(7, null, 0, new Timestamp(new Date().getTime()),null, null, null , null);
        when(jobHistoryRepository.save(any(JobHistoryEntry.class))).thenReturn(jobHistoryEntry);
        receiver.onMessage(message);
        verify(xqJobDao).changeJobStatus(anyString(),anyInt());
    }

    private Message convertObjectToByteArray(WorkersRabbitMQResponse response) throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();
        String responseObject = jsonMapper.writeValueAsString(response);
        byte[] body = responseObject.getBytes();
        MessageProperties messageProperties = new MessageProperties();
        return new Message(body, messageProperties);
    }

    private WorkersRabbitMQResponse createRabbitMQResponse(XQScript xqScript) {
        WorkersRabbitMQResponse response = new WorkersRabbitMQResponse();
        response.setXqScript(xqScript);
        response.setJobStatus(0);
        response.setContainerName("demoJobExecutor");
        response.setErrorExists(false);
        return response;
    }

}




















