package eionet.gdem.rabbitmq.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Constants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.WorkerHeartBeatMsgRepository;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.listeners.WorkerHeartBeatResponseReceiver;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessageInfo;
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
import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class WorkerHeartBeatResponseReceiverTest {

    @Mock
    private JobService jobService;

    @Mock
    private JobHistoryService jobHistoryService;

    @Mock
    WorkerHeartBeatMsgRepository workerHeartBeatMsgRepository;

    @Spy
    @InjectMocks
    private WorkerHeartBeatResponseReceiver receiver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doNothing().when(jobService).changeStatusesAndJobExecutorName(anyInt(), any(InternalSchedulingStatus.class), anyString(), any(Timestamp.class), anyInt());
        doNothing().when(jobHistoryService).updateStatusesAndJobExecutorName(anyInt(), anyInt(), any(JobEntry.class));
    }

    @Test
    public void testOnMessage() throws JsonProcessingException, DatabaseException {
        String[] scriptParams = new String[0];
        XQScript xqScript = new XQScript(null, scriptParams, "HTML");
        xqScript.setJobId("12452");

        WorkerHeartBeatMessageInfo response = new WorkerHeartBeatMessageInfo("demoJobExecutor", 12453).setJobStatus(Constants.JOB_NOT_FOUND_IN_WORKER).setId(35);
        Message message = convertObjectToByteArray(response);

        WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry = new WorkerHeartBeatMsgEntry(response.getJobId(), response.getJobExecutorName(), new Timestamp(new Date().getTime()));
        when(workerHeartBeatMsgRepository.findOne(anyInt())).thenReturn(workerHeartBeatMsgEntry);
        when(workerHeartBeatMsgRepository.save(any(WorkerHeartBeatMsgEntry.class))).thenReturn(workerHeartBeatMsgEntry);

        JobEntry jobEntry = new JobEntry().setId(12452).setUrl("https://cdrtest.eionet.europa.eu/ro/colwkcutw/envxxyxia/REP_D-RO_ANPM_20170929_C-001.xml")
                .setFile("/opt/xmlconv/tmp/gdem_1615903208066.xquery").setResultFile("/opt/xmlconv/eearun/xmlconv/tmp/gdem_1615903208071.html").setScriptType("xquery 3.0+")
                .setnStatus(Constants.XQ_PROCESSING).setJobExecutorName("demoJobExecutor");
        when(jobService.findById(anyInt())).thenReturn(jobEntry);
        receiver.onMessage(message);
        verify(jobService).changeNStatus(anyInt(),anyInt());
    }

    private Message convertObjectToByteArray(WorkerHeartBeatMessageInfo response) throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();
        String responseObject = jsonMapper.writeValueAsString(response);
        byte[] body = responseObject.getBytes();
        MessageProperties messageProperties = new MessageProperties();
        return new Message(body, messageProperties);
    }

}




















