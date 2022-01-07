package eionet.gdem.rabbitmq.service;

import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessage;
import eionet.gdem.rabbitMQ.service.HeartBeatRabbitMessageSenderImpl;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class HeartBeatRabbitMessageSenderImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Spy
    @InjectMocks
    private HeartBeatRabbitMessageSenderImpl heartBeatRabbitMessageSenderImpl;

    @Before
    public void setUp() throws DatabaseException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendMessageToRabbitMQ() {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(WorkerHeartBeatMessage.class));
        WorkerHeartBeatMessage workerHeartBeatMessage = new WorkerHeartBeatMessage();
        heartBeatRabbitMessageSenderImpl.sendMessageToRabbitMQ(workerHeartBeatMessage);
        verify(heartBeatRabbitMessageSenderImpl).sendMessageToRabbitMQ(workerHeartBeatMessage);
    }
}










