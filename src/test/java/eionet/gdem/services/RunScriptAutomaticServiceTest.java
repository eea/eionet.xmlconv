package eionet.gdem.services;

import eionet.gdem.api.qa.service.impl.QaServiceImpl;
import eionet.gdem.qa.QueryService;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.util.Vector;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })

//These tests were used for saxon engine which has been moved to jobExecutor application
@Ignore
public class RunScriptAutomaticServiceTest {

    @Autowired
    RunScriptAutomaticService runScriptAutomaticService;

    private QaServiceImpl qaService;

    @Mock
    private JobRequestHandlerService jobRequestHandlerService;

    @Mock
    private JobResultHandlerService jobResultHandlerService;

    @Mock
    private QueryService queryServiceMock;

    @Autowired
    private DataSource db;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.qaService = new QaServiceImpl(queryServiceMock, jobRequestHandlerService, jobResultHandlerService, runScriptAutomaticService);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_QA_XML);
    }

    @Test
    public void testExpiredQAResultHTML() throws Exception {
        QueryService qs = new QueryService();

        Vector queryResult = runScriptAutomaticService.runQAScript("http://localhost/notexist.xml", "51");

        String qyeryResultHtml = new String((byte[]) queryResult.elementAt(1));
        boolean ok = false;

        ok = qyeryResultHtml != null && qyeryResultHtml.startsWith("<html>") && qyeryResultHtml.endsWith("</html>");
        // //must contain red warning <span class="warning">The stylesheet expired on 11.11.2010</span>
        ok = ok && qyeryResultHtml.indexOf("<div class=\"error-msg\">The reported XML file uses an expired XML Schema.") != -1;

        assertTrue(ok);

    }

    @Test
    public void testNotExpiredQAResultHtml() throws Exception {
        QueryService qs = new QueryService();

        Vector queryResult = runScriptAutomaticService.runQAScript("http://localhost/notexist.xml", "52");

        String qyeryResultHtml = new String((byte[]) queryResult.elementAt(1));
        boolean ok = false;

        ok = qyeryResultHtml != null && qyeryResultHtml.startsWith("<html>") && qyeryResultHtml.endsWith("</html>");
        // must NOT contain red warning <span class="warning">The stylesheet expired on 11.11.2010</span>
        ok = ok && qyeryResultHtml.indexOf("expired") == -1;

        assertTrue(ok);

    }

    @Test
    public void testExpiredQAResultXml() throws Exception {
        QueryService qs = new QueryService();

        Vector queryResult = runScriptAutomaticService.runQAScript("http://localhost/notexist.xml", "53");

        String qyeryResultXml = new String((byte[]) queryResult.elementAt(1));
        boolean ok = false;

        ok = qyeryResultXml != null && qyeryResultXml.startsWith("<?xml");
        // must NOT contain red warning - it is XML
        ok = ok && qyeryResultXml.indexOf("expired") == -1;

        assertTrue(ok);

    }
}
