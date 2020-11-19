package eionet.gdem.services.impl;

import com.rabbitmq.client.ConnectionFactory;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class RabbitMQProducerServiceTest {
    @Mock
    RabbitMQProducerServiceImpl producer;

    private ConnectionFactory setupConnectionFactory(String host, Integer port, String username, String password) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }


    @Before
    public void setup() throws Exception {
        TestUtils.setUpProperties(this);
        MockitoAnnotations.initMocks(this);
        when(producer.getQUEUE_NAME()).thenReturn("testConsumingConnection");
        doCallRealMethod().when(producer).sendMessageToQueue(Mockito.anyString());
    }

    /* Test case: send notification to queue */
    @Test
    public void testSendMessageToQueue() throws Exception {
        //when(producer.setupConnectionFactory()).thenReturn(setupConnectionFactory("localhost", 5672, "user", "password"));
        producer.sendMessageToQueue("hello world");
    }
}
