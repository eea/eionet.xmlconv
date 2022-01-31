package eionet.gdem.rabbitmq.service;

import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.WorkerHeartBeatMsgService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessage;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import eionet.gdem.rabbitMQ.service.HandleHeavyJobsServiceImpl;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageSender;
import eionet.gdem.services.JobHistoryService;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class HandleHeavyJobsServiceImplTest {

    @Mock
    private JobService jobService;
    @Mock
    private JobHistoryService jobHistoryService;
    @Mock
    private WorkerHeartBeatMsgService workerHeartBeatMsgService;
    @Mock
    private RabbitMQMessageSender rabbitMQMessageSender;
    @Spy
    @InjectMocks
    private HandleHeavyJobsServiceImpl handleHeavyJobsService;

    @Before
    public void setUp() throws DatabaseException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHandleNoHeavyRetriesOnFailure() throws DatabaseException {
        XQScript script = new XQScript();
        script.setJobId("44");
        WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage = new WorkerJobRabbitMQRequestMessage().setScript(script);
        JobEntry jobEntry = new JobEntry().setId(1).setHeavyRetriesOnFailure(0);
        JobHistoryEntry jobHistoryEntry = new JobHistoryEntry().setId(1);
        when(jobHistoryService.save(any(JobHistoryEntry.class))).thenReturn(jobHistoryEntry);
        doNothing().when(rabbitMQMessageSender).sendMessageToRabbitMQ(any(WorkerHeartBeatMessage.class));
        handleHeavyJobsService.handle(workerJobRabbitMQRequestMessage, jobEntry, jobHistoryEntry);
        verify(handleHeavyJobsService).handle(workerJobRabbitMQRequestMessage, jobEntry, jobHistoryEntry);
    }

    @Test
    public void testHandleWithHeavyRetriesOnFailure() throws DatabaseException {
        XQScript script = new XQScript();
        script.setJobId("45");
        WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage = new WorkerJobRabbitMQRequestMessage().setScript(script);
        JobEntry jobEntry = new JobEntry().setId(1).setHeavyRetriesOnFailure(1);
        JobHistoryEntry jobHistoryEntry = new JobHistoryEntry().setId(1);
        when(jobHistoryService.save(any(JobHistoryEntry.class))).thenReturn(jobHistoryEntry);
        doNothing().when(rabbitMQMessageSender).sendMessageToRabbitMQ(any(WorkerHeartBeatMessage.class));
        List<WorkerHeartBeatMsgEntry> workerHeartBeatMsgEntries = new ArrayList<>();
        when(workerHeartBeatMsgService.findUnAnsweredHeartBeatMessages(anyInt())).thenReturn(workerHeartBeatMsgEntries);
        handleHeavyJobsService.handle(workerJobRabbitMQRequestMessage, jobEntry, jobHistoryEntry);
        verify(handleHeavyJobsService).handle(workerJobRabbitMQRequestMessage, jobEntry, jobHistoryEntry);
    }
}








