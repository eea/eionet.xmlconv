//package eionet.gdem.rabbitmq.service;
//
//import eionet.gdem.qa.XQScript;
//import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;
//import eionet.gdem.rabbitMQ.service.RabbitMQMessageSenderImpl;
//import eionet.gdem.test.ApplicationTestContext;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.verify;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = { ApplicationTestContext.class })
//public class RabbitMQMessageSenderImplTest {
//
//    @Mock
//    RabbitTemplate rabbitTemplate;
//
//    @InjectMocks
//    RabbitMQMessageSenderImpl workersJobMessageSender;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    public void testSendJobInfoToRabbitMQ() {
//        String[] scriptParams = new String[0];
//        XQScript xqScript = new XQScript(null, scriptParams, "HTML");
//        doNothing().when(rabbitTemplate).convertAndSend(anyString(), any(WorkerJobRabbitMQRequest.class));
//        WorkerJobRabbitMQRequest workerJobRabbitMQRequest = new WorkerJobRabbitMQRequest(xqScript);
//        workersJobMessageSender.sendJobInfoToRabbitMQ(workerJobRabbitMQRequest);
//        verify(rabbitTemplate).convertAndSend(anyString(), any(WorkerJobRabbitMQRequest.class));
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
