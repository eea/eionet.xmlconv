package eionet.gdem.rabbitmq.service;

import eionet.gdem.dto.Schema;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.qa.IQueryDao;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.errors.CreateMQMessageException;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageFactoryImpl;
import eionet.gdem.rabbitMQ.service.WorkersJobMessageSender;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
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
    IXQJobDao xqJobDao;

    @Mock
    IQueryDao queryDao;

    @Mock
    JobHistoryRepository jobHistoryRepository;

    @Mock
    WorkersJobMessageSender workersJobMessageSender;

    @InjectMocks
    RabbitMQMessageFactoryImpl createRabbitMQMessage;

    String[] jobData;
    Schema schema;
    HashMap queryMap;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        createRabbitMQMessage.setJobId("627015");
        jobData = new String[] {"xmlUrl", "xqFile", "resultFile", "0", "srcFile", "1246", "627015", "2020-12-09 14:07:02.0", "xquery 3.0+"};
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
    public void createScriptAndSendMessageToRabbitMQ() throws SQLException, CreateMQMessageException {
        JobHistoryEntry jobHistoryEntry = new JobHistoryEntry(7, null, 1, new Timestamp(new Date().getTime()),null, null, null , null);
        when(xqJobDao.getXQJobData(anyString())).thenReturn(jobData);
        doNothing().when(xqJobDao).processXQJob(anyString());
        when(jobHistoryRepository.save(any(JobHistoryEntry.class))).thenReturn(jobHistoryEntry);
        when(queryDao.getQueryInfo(anyString())).thenReturn(queryMap);
        doNothing().when(workersJobMessageSender).sendJobInfoToRabbitMQ(any(XQScript.class));
        createRabbitMQMessage.createScriptAndSendMessageToRabbitMQ();
        verify(queryDao).getQueryInfo(anyString());
    }

}














