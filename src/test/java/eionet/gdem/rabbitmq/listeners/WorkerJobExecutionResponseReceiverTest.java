package eionet.gdem.rabbitmq.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Constants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.listeners.WorkerJobExecutionResponseReceiver;
import eionet.gdem.rabbitMQ.model.WorkerJobExecutionInfo;
import eionet.gdem.services.JobHistoryService;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class WorkerJobExecutionResponseReceiverTest {

    @Mock
    private JobService jobService;

    @Mock
    private JobHistoryService jobHistoryService;

    @Spy
    @InjectMocks
    private WorkerJobExecutionResponseReceiver receiver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doNothing().when(jobService).changeNStatus(anyInt(),anyInt());
        doNothing().when(jobService).changeIntStatusAndJobExecutorName(any(InternalSchedulingStatus.class), anyString(), any(Timestamp.class), anyInt());
        doNothing().when(jobHistoryService).updateStatusesAndJobExecutorName(any(XQScript.class), anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    public void testOnMessage() throws JsonProcessingException {
        String[] scriptParams = new String[0];
        XQScript xqScript = new XQScript(null, scriptParams, "HTML");
        xqScript.setJobId("12452");

        WorkerJobExecutionInfo response = new WorkerJobExecutionInfo("demoJobExecutor", 12453).setExecuting(false);
        Message message = convertObjectToByteArray(response);

        JobEntry jobEntry = new JobEntry().setId(12452).setUrl("https://cdrtest.eionet.europa.eu/ro/colwkcutw/envxxyxia/REP_D-RO_ANPM_20170929_C-001.xml")
                .setFile("/opt/xmlconv/tmp/gdem_1615903208066.xquery").setResultFile("/opt/xmlconv/eearun/xmlconv/tmp/gdem_1615903208071.html").setScriptType("xquery 3.0+")
                .setnStatus(Constants.XQ_PROCESSING).setJobExecutorName("demoJobExecutor");
        when(jobService.findById(anyInt())).thenReturn(jobEntry);
        receiver.onMessage(message);
        verify(jobService).changeNStatus(anyInt(),anyInt());
    }

    private Message convertObjectToByteArray(WorkerJobExecutionInfo response) throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();
        String responseObject = jsonMapper.writeValueAsString(response);
        byte[] body = responseObject.getBytes();
        MessageProperties messageProperties = new MessageProperties();
        return new Message(body, messageProperties);
    }

}




















