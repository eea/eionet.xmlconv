package eionet.gdem.rabbitmq.service;

import eionet.gdem.dto.Schema;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.IQueryDao;
import eionet.gdem.rabbitMQ.errors.CreateRabbitMQMessageException;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageFactoryImpl;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageSender;
import eionet.gdem.services.JobHistoryService;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testing CreateRabbitMQMessage methods.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class RabbitMQMessageFactoryImplTest {

    @Mock
    JobService jobService;

    @Mock
    JobHistoryService jobHistoryService;

    @Mock
    IQueryDao queryDao;

    @Mock
    RabbitMQMessageSender rabbitMQMessageSender;

    @InjectMocks
    RabbitMQMessageFactoryImpl createRabbitMQMessage;

    JobEntry jobEntry;
    Schema schema;
    HashMap queryMap;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        InternalSchedulingStatus intStatus = new InternalSchedulingStatus().setId(0);
        jobEntry = new JobEntry("xmlUrl", "xqFile", "resultFile",0,1246,new Timestamp(new Date().getTime()),"xquery 3.0+",intStatus)
            .setId(627015).setSrcFile("srcFile");
        schema = new Schema();
        createQueryMap();
    }

    private void createQueryMap() {
        queryMap = new HashMap();
        queryMap.put("schemaId","742");
        queryMap.put("query", "aqd_schema_validation_proxy-1.0.xquery");
        queryMap.put("scriptType", "xquery 3.0+");
        queryMap.put("name", "XML Schema Validation");
        queryMap.put("desciption", "");
        queryMap.put("metaType", "text/html;charset=UTF-8");
        queryMap.put("id", "849");
        queryMap.put("runOnDemanMaxFileSizeMB", "5");
        queryMap.put("isActive", "1");
        queryMap.put("contentType", "HTML");
        queryMap.put("url", "xqueryUrl");
        queryMap.put("schemaUrl", "schemaUrl");
    }

    @Test
    public void createScriptAndSendMessageToRabbitMQTest() throws SQLException, CreateRabbitMQMessageException, DatabaseException {
        JobHistoryEntry jobHistoryEntry = new JobHistoryEntry(7, "627015", 1, new Timestamp(new Date().getTime()),null, null, null , null);
        when(jobService.findById(anyInt())).thenReturn(jobEntry);
        when(jobService.getRetryCounter(anyInt())).thenReturn(0);
        when(jobService.saveOrUpdate(any(JobEntry.class))).thenReturn(jobEntry);
        when(jobHistoryService.save(any(JobHistoryEntry.class))).thenReturn(jobHistoryEntry);
        when(queryDao.getQueryInfo(anyString())).thenReturn(queryMap);
        doNothing().when(rabbitMQMessageSender).sendMessageToRabbitMQ(any(WorkerJobRabbitMQRequestMessage.class));
        createRabbitMQMessage.createScriptAndSendMessageToRabbitMQ("627015");
        verify(queryDao).getQueryInfo(anyString());
    }

}














