package eionet.gdem.rabbitmq.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Constants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.WorkerHeartBeatMsgRepository;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.listeners.WorkerHeartBeatResponseReceiver;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessage;
import eionet.gdem.rabbitMQ.service.HeartBeatMsgHandlerService;
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
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class WorkerHeartBeatResponseReceiverTest {

    @Mock
    HeartBeatMsgHandlerService heartBeatMsgHandlerService;

    @Mock
    WorkerHeartBeatMsgRepository workerHeartBeatMsgRepository;

    @Spy
    @InjectMocks
    private WorkerHeartBeatResponseReceiver receiver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doNothing().when(heartBeatMsgHandlerService).updateHeartBeatJobAndQueryTables(any(WorkerHeartBeatMsgEntry.class), any(WorkerHeartBeatMessage.class), anyInt(), any(InternalSchedulingStatus.class));
    }

    @Test
    public void testOnMessage() throws JsonProcessingException, DatabaseException {
        String[] scriptParams = new String[0];
        XQScript xqScript = new XQScript(null, scriptParams, "HTML");
        xqScript.setJobId("12452");

        WorkerHeartBeatMessage response = new WorkerHeartBeatMessage("demoJobExecutor", 12453).setJobStatus(Constants.JOB_NOT_FOUND_IN_WORKER).setId(35);
        Message message = convertObjectToByteArray(response);

        WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry = new WorkerHeartBeatMsgEntry(response.getJobId(), response.getJobExecutorName(), new Timestamp(new Date().getTime()));
        when(workerHeartBeatMsgRepository.findById(anyInt())).thenReturn(Optional.of(workerHeartBeatMsgEntry));
        receiver.onMessage(message);
        verify(workerHeartBeatMsgRepository).findById(anyInt());
    }

    private Message convertObjectToByteArray(WorkerHeartBeatMessage response) throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();
        String responseObject = jsonMapper.writeValueAsString(response);
        byte[] body = responseObject.getBytes();
        MessageProperties messageProperties = new MessageProperties();
        return new Message(body, messageProperties);
    }

}




















