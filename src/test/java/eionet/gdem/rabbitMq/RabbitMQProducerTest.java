package eionet.gdem.rabbitMq;

import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class RabbitMQProducerTest {

    @Mock
    RabbitMQProducer producer = new RabbitMQProducer();


    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(producer.getQUEUE_NAME()).thenReturn("testQueue");

    }

    /* Test case: send notification to queue */
    @Test
    public void testSendMessageToQueue() throws Exception {
        producer.sendMessageToQueue("hello world");
    }
}
