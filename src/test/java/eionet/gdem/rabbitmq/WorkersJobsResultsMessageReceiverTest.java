package eionet.gdem.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.WorkersJobsResultsMessageReceiver;
import eionet.gdem.rabbitMQ.model.WorkersRabbitMQResponse;
import eionet.gdem.services.JobHistoryService;
import eionet.gdem.test.ApplicationTestContext;
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

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class WorkersJobsResultsMessageReceiverTest {

    @Mock
    private JobService jobService;

    @Mock
    private JobExecutorService jobExecutorService;

    @Mock
    private JobHistoryService jobHistoryService;

    @Spy
    @InjectMocks
    private WorkersJobsResultsMessageReceiver receiver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnMessage() throws JsonProcessingException {
        String[] scriptParams = new String[0];
        XQScript xqScript = new XQScript(null, scriptParams, "HTML");
        xqScript.setJobId("12452");

        WorkersRabbitMQResponse response = createRabbitMQResponse(xqScript);
        Message message = convertObjectToByteArray(response);

        doNothing().when(jobExecutorService).saveJobExecutor(any(JobExecutor.class));
        doNothing().when(jobService).changeNStatus(any(XQScript.class),anyInt());
        doNothing().when(jobHistoryService).updateStatusesAndJobExecutorName(any(XQScript.class), anyInt(), anyString());
        doNothing().when(jobExecutorService).updateJobExecutor(anyInt(), anyInt(), anyString());
        doNothing().when(jobService).changeIntStatusAndJobExecutorName(any(InternalSchedulingStatus.class), anyString(), any(Timestamp.class), anyInt());
        receiver.onMessage(message);
        verify(jobService).changeNStatus(any(XQScript.class),anyInt());
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
        response.setScript(xqScript);
        response.setJobStatus(0);
        response.setContainerName("demoJobExecutor");
        response.setErrorExists(false);
        return response;
    }

}




















