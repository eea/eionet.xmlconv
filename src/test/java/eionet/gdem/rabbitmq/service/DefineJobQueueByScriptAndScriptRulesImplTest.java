package eionet.gdem.rabbitmq.service;

import eionet.gdem.SchedulingConstants;
import eionet.gdem.XMLConvException;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.Entities.ScriptRulesEntry;
import eionet.gdem.jpa.enums.ScriptRuleMatch;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import eionet.gdem.rabbitMQ.service.DefineJobQueueByScriptAndScriptRulesImpl;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageSender;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class DefineJobQueueByScriptAndScriptRulesImplTest {

    @Mock
    private WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;
    @Mock
    private JobService jobService;
    @Mock
    private RabbitMQMessageSender rabbitMQLightMessageSender;
    @Mock
    private RabbitMQMessageSender rabbitMQHeavyMessageSender;
    @Spy
    @InjectMocks
    DefineJobQueueByScriptAndScriptRulesImpl defineJobQueueByScriptAndScriptRules;

    QueryEntry queryEntry;
    JobEntry jobEntry;
    List<ScriptRulesEntry> rules;
    @Captor
    ArgumentCaptor<JobEntry> jobCaptor = ArgumentCaptor.forClass(JobEntry.class);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        queryEntry = new QueryEntry().setQueryId(1).setShortName("shortName").setSchemaId(742).setResultType("HTML").setScriptType("xquery 3.0+").setUrl("testUrl").setUpperLimit(10).setActive(true).setVersion(1).setMarkedHeavy(true);
        jobEntry = new JobEntry("testUrl", "xqFile", "resultFile",0,1246, new Timestamp(new Date().getTime()),"xquery 3.0+", new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_QUEUED))
                .setId(627015).setSrcFile("srcFile").setQueryId(1);
        rules = new ArrayList<>();
        ScriptRulesEntry rule1 = new ScriptRulesEntry().setQueryId(1).setField("collection path").setType("includes").setValue("test").setEnabled(true);
        ScriptRulesEntry rule2 = new ScriptRulesEntry().setQueryId(1).setField("xml file size (in MB)").setType("greater than").setValue("5").setEnabled(true);
        rules.add(rule1);
        rules.add(rule2);
    }

    @Test
    public void testCheckHeavyOrLightQueryId() {
        defineJobQueueByScriptAndScriptRules.checkHeavyOrLight(queryEntry, jobEntry);
        verify(defineJobQueueByScriptAndScriptRules).checkHeavyOrLight(any(QueryEntry.class), jobCaptor.capture());
        JobEntry result = jobCaptor.getValue();
        assertTrue(result.isHeavy());
    }

    @Test
    public void testCheckRulesAllMatchAllRulesApplyJobGetsHeavy() throws XMLConvException {
        queryEntry.setMarkedHeavy(false).setRuleMatch(ScriptRuleMatch.ALL.getValue()).setRulesEntryList(rules);
        doReturn(Long.valueOf(20000000)).when(defineJobQueueByScriptAndScriptRules).getXmlFileSize(anyString());
        defineJobQueueByScriptAndScriptRules.checkRules(queryEntry, jobEntry);
        verify(defineJobQueueByScriptAndScriptRules).checkRules(any(QueryEntry.class), jobCaptor.capture());
        JobEntry result = jobCaptor.getValue();
        assertTrue(result.isHeavy());
    }

    @Test
    public void testCheckRulesAllMatchAllNotAllRulesApplyJobRemainsLight() throws XMLConvException {
        queryEntry.setMarkedHeavy(false).setRuleMatch(ScriptRuleMatch.ALL.getValue()).setRulesEntryList(rules);
        doReturn(Long.valueOf(20)).when(defineJobQueueByScriptAndScriptRules).getXmlFileSize(anyString());
        defineJobQueueByScriptAndScriptRules.checkRules(queryEntry, jobEntry);
        verify(defineJobQueueByScriptAndScriptRules).checkRules(any(QueryEntry.class), jobCaptor.capture());
        JobEntry result = jobCaptor.getValue();
        assertTrue(!result.isHeavy());
    }

    @Test
    public void testCheckRulesAtLeastOneJobGetsHeavy() throws XMLConvException {
        queryEntry.setMarkedHeavy(false).setRuleMatch(ScriptRuleMatch.AT_LEAST_ONE.getValue()).setRulesEntryList(rules);
        doReturn(Long.valueOf(20)).when(defineJobQueueByScriptAndScriptRules).getXmlFileSize(anyString());
        defineJobQueueByScriptAndScriptRules.checkRules(queryEntry, jobEntry);
        verify(defineJobQueueByScriptAndScriptRules).checkRules(any(QueryEntry.class), jobCaptor.capture());
        JobEntry result = jobCaptor.getValue();
        assertTrue(result.isHeavy());
    }

    @Test
    public void testUpdateDatabase() throws DatabaseException {
        when(jobService.getRetryCounter(anyInt())).thenReturn(0);
        doNothing().when(workerAndJobStatusHandlerService).updateJobAndJobHistoryEntries(any(JobEntry.class));
        defineJobQueueByScriptAndScriptRules.updateDatabase(jobEntry);
        verify(defineJobQueueByScriptAndScriptRules).updateDatabase(jobEntry);
    }

    @Test
    public void testSendMsgToRabbitMQHeavy() {
        jobEntry.setHeavy(true);
        doNothing().when(rabbitMQHeavyMessageSender).sendMessageToRabbitMQ(any(WorkerJobRabbitMQRequestMessage.class));
        WorkerJobRabbitMQRequestMessage message = new WorkerJobRabbitMQRequestMessage();
        defineJobQueueByScriptAndScriptRules.sendMsgToRabbitMQ(queryEntry, jobEntry, message);
        verify(defineJobQueueByScriptAndScriptRules).sendMsgToRabbitMQ(queryEntry, jobEntry, message);
    }

    @Test
    public void testSendMsgToRabbitMQLight() {
        doNothing().when(rabbitMQLightMessageSender).sendMessageToRabbitMQ(any(WorkerJobRabbitMQRequestMessage.class));
        WorkerJobRabbitMQRequestMessage message = new WorkerJobRabbitMQRequestMessage();
        defineJobQueueByScriptAndScriptRules.sendMsgToRabbitMQ(queryEntry, jobEntry, message);
        verify(defineJobQueueByScriptAndScriptRules).sendMsgToRabbitMQ(queryEntry, jobEntry, message);
    }
}
















