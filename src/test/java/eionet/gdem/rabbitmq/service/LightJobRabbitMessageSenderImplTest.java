package eionet.gdem.rabbitmq.service;

import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import eionet.gdem.rabbitMQ.service.LightJobRabbitMessageSenderImpl;
import eionet.gdem.rabbitMQ.service.RabbitmqMsgPriorityService;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class LightJobRabbitMessageSenderImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private RabbitmqMsgPriorityService rabbitmqMsgPriorityService;
    @Spy
    @InjectMocks
    private LightJobRabbitMessageSenderImpl lightJobRabbitMessageSender;

    private WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage;
    private MessagePostProcessor messagePostProcessor;

    @Before
    public void setUp() throws DatabaseException {
        MockitoAnnotations.initMocks(this);
        XQScript script = new XQScript();
        script.setJobId("45");
        workerJobRabbitMQRequestMessage = new WorkerJobRabbitMQRequestMessage().setScript(script);
        messagePostProcessor = mock(MessagePostProcessor.class);
        MessageProperties messageProperties = mock(MessageProperties.class);
        byte[] body = new byte[10];
        Message message = new Message(body, messageProperties);
        when(messagePostProcessor.postProcessMessage(any(Message.class))).thenReturn(message);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), any(WorkerJobRabbitMQRequestMessage.class), ArgumentMatchers.eq(messagePostProcessor));
        when(rabbitmqMsgPriorityService.getMsgPriorityBasedOnJobType(any(WorkerJobRabbitMQRequestMessage.class))).thenReturn(1);
    }

    @Test
    public void testSendMessageToRabbitMQNullWorkerRetries() {
        lightJobRabbitMessageSender.sendMessageToRabbitMQ(workerJobRabbitMQRequestMessage);
        verify(lightJobRabbitMessageSender).sendMessageToRabbitMQ(workerJobRabbitMQRequestMessage);
    }

    @Test
    public void testSendMessageToRabbitMQWithWorkerRetries() {
        workerJobRabbitMQRequestMessage.setJobExecutionRetries(1);
        lightJobRabbitMessageSender.sendMessageToRabbitMQ(workerJobRabbitMQRequestMessage);
        verify(lightJobRabbitMessageSender).sendMessageToRabbitMQ(workerJobRabbitMQRequestMessage);
    }
}







