package eionet.gdem.rabbitmq.service;

import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.service.WorkerHeartBeatMsgService;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessageInfo;
import eionet.gdem.rabbitMQ.service.HeartBeatMsgHandlerServiceImpl;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageSender;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class HeartBeatMsgHandlerServiceImplTest {

    @Mock
    WorkerHeartBeatMsgService workerHeartBeatMsgService;

    @Mock
    RabbitMQMessageSender rabbitMQMessageSender;

    @InjectMocks
    HeartBeatMsgHandlerServiceImpl heartBeatMsgHandlerService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveMsgAndSendToRabbitMQ() {
        WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry = new WorkerHeartBeatMsgEntry();
        workerHeartBeatMsgEntry.setId(1);
        when(workerHeartBeatMsgService.save(any(WorkerHeartBeatMsgEntry.class))).thenReturn(workerHeartBeatMsgEntry);
        doNothing().when(rabbitMQMessageSender).sendMessageToRabbitMQ(any(WorkerHeartBeatMessageInfo.class));
        WorkerHeartBeatMessageInfo heartBeatMessageInfo = new WorkerHeartBeatMessageInfo();
        heartBeatMsgHandlerService.saveMsgAndSendToRabbitMQ(heartBeatMessageInfo, workerHeartBeatMsgEntry);
        verify(workerHeartBeatMsgService).save(any(WorkerHeartBeatMsgEntry.class));
    }
}






















