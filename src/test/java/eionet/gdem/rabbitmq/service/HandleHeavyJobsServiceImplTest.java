package eionet.gdem.rabbitmq.service;

import eionet.gdem.jpa.Entities.*;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.QueryJpaService;
import eionet.gdem.jpa.service.WorkerHeartBeatMsgService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessage;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import eionet.gdem.rabbitMQ.service.HandleHeavyJobsServiceImpl;
import eionet.gdem.rabbitMQ.service.QueryAndQueryHistoryService;
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
    JobService jobService;
    @Mock
    private JobHistoryService jobHistoryService;
    @Mock
    private WorkerHeartBeatMsgService workerHeartBeatMsgService;
    @Mock
    private RabbitMQMessageSender rabbitMQMessageSender;
    @Mock
    private QueryJpaService queryJpaService;
    @Mock
    private QueryAndQueryHistoryService queryAndQueryHistoryService;
    @Spy
    @InjectMocks
    private HandleHeavyJobsServiceImpl handleHeavyJobsService;

    @Before
    public void setUp() throws DatabaseException {
        MockitoAnnotations.initMocks(this);
        QueryEntry queryEntry = new QueryEntry().setQueryId(1).setShortName("shortName").setSchemaId(742).setResultType("HTML").setScriptType("xquery 3.0+").setUrl("test").setUpperLimit(10).setActive(true).setVersion(1);
        when(queryJpaService.findByQueryId(anyInt())).thenReturn(queryEntry);
        doNothing().when(rabbitMQMessageSender).sendMessageToRabbitMQ(any(WorkerHeartBeatMessage.class));
        doNothing().when(queryAndQueryHistoryService).saveQueryAndQueryHistoryEntries(any(QueryEntry.class), any(QueryHistoryEntry.class));
    }

    @Test
    public void testHandleNoHeavyRetriesOnFailure() throws DatabaseException {
        XQScript script = new XQScript();
        script.setJobId("44");
        WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage = new WorkerJobRabbitMQRequestMessage().setScript(script);
        JobEntry jobEntry = new JobEntry().setId(1).setHeavyRetriesOnFailure(0);
        JobHistoryEntry jobHistoryEntry = new JobHistoryEntry().setId(1);
        when(jobService.saveOrUpdate(any(JobEntry.class))).thenReturn(jobEntry);
        when(jobHistoryService.save(any(JobHistoryEntry.class))).thenReturn(jobHistoryEntry);
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
        when(jobService.saveOrUpdate(any(JobEntry.class))).thenReturn(jobEntry);
        when(jobHistoryService.save(any(JobHistoryEntry.class))).thenReturn(jobHistoryEntry);
        doNothing().when(rabbitMQMessageSender).sendMessageToRabbitMQ(any(WorkerHeartBeatMessage.class));
        List<WorkerHeartBeatMsgEntry> workerHeartBeatMsgEntries = new ArrayList<>();
        when(workerHeartBeatMsgService.findUnAnsweredHeartBeatMessages(anyInt())).thenReturn(workerHeartBeatMsgEntries);
        handleHeavyJobsService.handle(workerJobRabbitMQRequestMessage, jobEntry, jobHistoryEntry);
        verify(handleHeavyJobsService).handle(workerJobRabbitMQRequestMessage, jobEntry, jobHistoryEntry);
    }
}








