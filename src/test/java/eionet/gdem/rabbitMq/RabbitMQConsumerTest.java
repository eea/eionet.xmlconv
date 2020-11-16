package eionet.gdem.rabbitMq;

import eionet.gdem.test.ApplicationTestContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class RabbitMQConsumerTest {

    RabbitMQConsumer receiver = new RabbitMQConsumer();

    /* Test case: receive notification from queue */
    @Test
    public void testReceiveMessageFromQueue() throws Exception {
        receiver.receiveMessageFromQueue();
    }
}
