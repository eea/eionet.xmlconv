package eionet.gdem.rabbitmq.service;

import eionet.gdem.Constants;
import eionet.gdem.rabbitMQ.enums.RabbitMQPriority;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import eionet.gdem.rabbitMQ.service.RabbitmqMsgPriorityService;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Testing RabbitmqMsgPriorityService methods.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class RabbitmqMsgPriorityServiceImplTest {

    @Autowired
    RabbitmqMsgPriorityService rabbitmqMsgPriorityService;

    private WorkerJobRabbitMQRequestMessage message = new WorkerJobRabbitMQRequestMessage();

    @Test
    public void getMsgPriorityBasedOnJobTypeOtherJobTest() {
        Integer priority = rabbitmqMsgPriorityService.getMsgPriorityBasedOnJobType(message);
        assertEquals(RabbitMQPriority.OTHER.getId(), priority);
    }

    @Test
    public void getMsgPriorityBasedOnJobTypeOnDemandApiTest() {
        message.setJobType(Constants.ON_DEMAND_TYPE);
        message.setApi(true);
        Integer priority = rabbitmqMsgPriorityService.getMsgPriorityBasedOnJobType(message);
        assertEquals(RabbitMQPriority.ON_DEMAND_API.getId(), priority);
    }

    @Test
    public void getMsgPriorityBasedOnJobTypeOnDemandUITest() {
        message.setJobType(Constants.ON_DEMAND_TYPE);
        message.setApi(false);
        Integer priority = rabbitmqMsgPriorityService.getMsgPriorityBasedOnJobType(message);
        assertEquals(RabbitMQPriority.ON_DEMAND_UI.getId(), priority);
    }
}










