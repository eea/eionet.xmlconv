package eionet.gdem.rabbitmq.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.listeners.WorkersJobsResultsMessageReceiver;
import eionet.gdem.rabbitMQ.model.WorkerJobInfoRabbitMQResponse;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class WorkersJobsResultsMessageReceiverTest {

    @Autowired
    private DataSource db;

    @Autowired
    JobService jobService;

    @Autowired
    private WorkersJobsResultsMessageReceiver receiver;

    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_QAJOBS_XML);
    }

    @Test
    public void testOnMessageReadyJob() throws JsonProcessingException {
        XQScript xqScript = createXqScript();

        WorkerJobInfoRabbitMQResponse response = createRabbitMQResponse(xqScript, false);
        Message message = convertObjectToByteArray(response);

        receiver.onMessage(message);
        JobEntry jobEntry = jobService.findById(1);
        assertTrue(jobEntry.getnStatus().equals(3));
    }

    @Test
    public void testOnMessageJobFailure() throws JsonProcessingException {
        XQScript xqScript = createXqScript();

        WorkerJobInfoRabbitMQResponse response = createRabbitMQResponse(xqScript, true);
        Message message = convertObjectToByteArray(response);

        receiver.onMessage(message);
        JobEntry jobEntry = jobService.findById(1);
        assertTrue(jobEntry.getnStatus().equals(4));
    }

    private XQScript createXqScript() {
        XQScript xqScript = new XQScript();
        xqScript.setJobId("1");
        xqScript.setSrcFileUrl("http://test.dev/test.xml");
        xqScript.setScriptFileName("test.xq");
        xqScript.setStrResultFile("test.html");
        xqScript.setScriptType("xquery 3.0+");
        return xqScript;
    }

    private Message convertObjectToByteArray(WorkerJobInfoRabbitMQResponse response) throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();
        String responseObject = jsonMapper.writeValueAsString(response);
        byte[] body = responseObject.getBytes();
        MessageProperties messageProperties = new MessageProperties();
        return new Message(body, messageProperties);
    }

    private WorkerJobInfoRabbitMQResponse createRabbitMQResponse(XQScript xqScript, boolean errorExists) {
        WorkerJobInfoRabbitMQResponse response = new WorkerJobInfoRabbitMQResponse();
        response.setScript(xqScript);
        response.setJobExecutorStatus(1);
        response.setJobExecutorName("demoJobExecutor");
        response.setHeartBeatQueue("demoJobExecutor-queue");
        response.setErrorExists(errorExists);
        return response;
    }

}




















