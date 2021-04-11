package eionet.gdem.rabbitmq.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.listeners.WorkersStatusMessageReceiver;
import eionet.gdem.rabbitMQ.model.WorkerStateRabbitMQResponse;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class WorkersStatusMessageReceiverTest {

    @Mock
    WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;

    @InjectMocks
    private WorkersStatusMessageReceiver receiver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doNothing().when(workerAndJobStatusHandlerService).saveOrUpdateJobExecutor(any(JobExecutor.class), any(JobExecutorHistory.class));
    }

    @Test
    public void testOnMessage() throws JsonProcessingException, DatabaseException {
        String[] scriptParams = new String[0];
        XQScript xqScript = new XQScript(null, scriptParams, "HTML");
        xqScript.setJobId("12452");

        WorkerStateRabbitMQResponse response = new WorkerStateRabbitMQResponse("demoJobExecutor", SchedulingConstants.WORKER_READY).setHeartBeatQueue("demoJobExecutor-queue");
        Message message = convertObjectToByteArray(response);

        receiver.onMessage(message);
        verify(workerAndJobStatusHandlerService).saveOrUpdateJobExecutor(any(JobExecutor.class), any(JobExecutorHistory.class));
    }

    private Message convertObjectToByteArray(WorkerStateRabbitMQResponse response) throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();
        String responseObject = jsonMapper.writeValueAsString(response);
        byte[] body = responseObject.getBytes();
        MessageProperties messageProperties = new MessageProperties();
        return new Message(body, messageProperties);
    }

}




















