package eionet.gdem.services;

import eionet.gdem.XMLConvException;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.QueryJpaService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import eionet.gdem.rabbitMQ.service.DefineJobQueueAndSendToRabbitMQTemplate;
import eionet.gdem.services.impl.JobOnDemandHandlerServiceImpl;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class JobOnDemandHandlerServiceTest {

    @Mock
    private JobService jobService;

    @Mock
    private JobHistoryService jobHistoryService;

    @Mock
    private QueryJpaService queryJpaService;

    @Mock
    DefineJobQueueAndSendToRabbitMQTemplate defineJobQueueAndSendToRabbitMQ;

    @InjectMocks
    private JobOnDemandHandlerServiceImpl jobOnDemandHandlerService;

    private JobEntry jobEntry;
    private JobHistoryEntry jobHistoryEntry;
    private XQScript script;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        String[] scriptParams = new String[0];
        script = new XQScript(null, scriptParams, "HTML");
        script.setJobId("627015");
        script.setResulFile("resultFile");
        script.setScriptFileName("xqFile");
        InternalSchedulingStatus internalStatus = new InternalSchedulingStatus().setId(3);
        jobEntry = new JobEntry("xmlUrl", "xqFile", "resultFile",0,1246, new Timestamp(new Date().getTime()),"xquery 3.0+", internalStatus)
                .setId(627015).setSrcFile("srcFile");
        jobHistoryEntry = new JobHistoryEntry(7, "627015", 1, new Timestamp(new Date().getTime()),null, null, null , null);
    }

    @Test
    public void testCreateJobAndSendToRabbitMQ() throws XMLConvException, DatabaseException {
        when(jobService.saveOrUpdate(any(JobEntry.class))).thenReturn(jobEntry);
        when(jobHistoryService.save(any(JobHistoryEntry.class))).thenReturn(jobHistoryEntry);
        QueryEntry queryEntry = new QueryEntry().setQueryId(1).setShortName("shortName").setSchemaId(742).setResultType("HTML").setScriptType("xquery 3.0+").setUrl("test").setUpperLimit(10).setActive(true).setVersion(1);
        when(queryJpaService.findByQueryId(anyInt())).thenReturn(queryEntry);
        doNothing().when(defineJobQueueAndSendToRabbitMQ).execute(any(QueryEntry.class), any(JobEntry.class), any(WorkerJobRabbitMQRequestMessage.class));
        JobEntry jobEntryResult = jobOnDemandHandlerService.createJobAndSendToRabbitMQ(script, 0, false, null);
        Assert.assertEquals(jobEntry.getId(), jobEntryResult.getId());
    }

}










