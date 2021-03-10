package eionet.gdem.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class WorkersJobsResultsMessageReceiverTest {

    @Mock
    private JobService jobService;

    @Mock
    private JobExecutorService jobExecutorService;

    @Mock
    private JobHistoryService jobHistoryService;

    @Mock
    JobExecutorHistoryService jobExecutorHistoryService;

    @Spy
    @InjectMocks
    private WorkersJobsResultsMessageReceiver receiver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doNothing().when(jobExecutorService).saveJobExecutor(any(JobExecutor.class));
        doNothing().when(jobService).changeNStatus(anyInt(),anyInt());
        doNothing().when(jobHistoryService).updateStatusesAndJobExecutorName(any(XQScript.class), anyInt(), anyInt(), anyString(), anyString());
        doNothing().when(jobExecutorService).updateJobExecutor(anyInt(), anyInt(), anyString(), anyString());
        doNothing().when(jobExecutorHistoryService).saveJobExecutorHistoryEntry(any(JobExecutorHistory.class));
        doNothing().when(jobService).changeIntStatusAndJobExecutorName(any(InternalSchedulingStatus.class), anyString(), any(Timestamp.class), anyInt());
    }

    @Test
    public void testOnMessageReadyJob() throws JsonProcessingException {
        String[] scriptParams = new String[0];
        XQScript xqScript = new XQScript(null, scriptParams, "HTML");
        xqScript.setJobId("12452");

        WorkersRabbitMQResponse response = createRabbitMQResponse(xqScript, false);
        Message message = convertObjectToByteArray(response);

        JobEntry jobEntry = new JobEntry().setId(12452);
        when(jobService.findById(anyInt())).thenReturn(jobEntry);
        receiver.onMessage(message);
        verify(jobService).changeNStatus(anyInt(),anyInt());
    }

    @Test
    public void testOnMessageJobFailure() throws JsonProcessingException {
        String[] scriptParams = new String[0];
        XQScript xqScript = new XQScript(null, scriptParams, "HTML");
        xqScript.setJobId("12453");

        WorkersRabbitMQResponse response = createRabbitMQResponse(xqScript, true);
        Message message = convertObjectToByteArray(response);

        JobEntry jobEntry = new JobEntry().setId(12453);
        when(jobService.findById(anyInt())).thenReturn(jobEntry);
        receiver.onMessage(message);
        verify(jobService).changeNStatus(anyInt(),anyInt());
    }

    private Message convertObjectToByteArray(WorkersRabbitMQResponse response) throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();
        String responseObject = jsonMapper.writeValueAsString(response);
        byte[] body = responseObject.getBytes();
        MessageProperties messageProperties = new MessageProperties();
        return new Message(body, messageProperties);
    }

    private WorkersRabbitMQResponse createRabbitMQResponse(XQScript xqScript, boolean errorExists) {
        WorkersRabbitMQResponse response = new WorkersRabbitMQResponse();
        response.setScript(xqScript);
        response.setJobExecutorStatus(1);
        response.setContainerName("demoJobExecutor");
        response.setErrorExists(errorExists);
        return response;
    }

}




















