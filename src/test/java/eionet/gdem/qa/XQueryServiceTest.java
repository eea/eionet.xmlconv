/*
 * Created on 29.10.2008
 */
package eionet.gdem.qa;

import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.Vector;

import org.dbunit.IDatabaseTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.services.db.dao.IXQJobDao;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS XQueryServiceTst
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class XQueryServiceTest {

    @Autowired
    private IDatabaseTester databaseTester;

    @Autowired
    private IXQJobDao xqJobDao;

    /**
     * Set up test case properties and databaseTester.
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDefaultDatabaseTester(databaseTester, TestConstants.SEED_DATASET_QA_XML);
    }


    /**
     * Tests that the added QA job contains the qa account data for QA engine
     */
    @Test
    public void testAnalyzeXMLProtectedFiles() throws Exception {

        XQueryService qs = new XQueryService();

        String schema = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/habitats.xsd";
        String fileName = "http://cdr.eionet.europa.eu/test.xml";
        Hashtable hash = new Hashtable();
        Vector files = new Vector();
        files.add(fileName);
        hash.put(schema, files);

        Vector v = qs.analyzeXMLFiles(hash);
        assertTrue(v.size() == 1);
        Vector v2 = (Vector) v.get(0);
        String jobId = (String) v2.get(0);

        String jobdata[] = xqJobDao.getXQJobData(jobId);
        String urlField = jobdata[0];

        // check if url field containts ticket parameter
        assertTrue(urlField.contains("getsource?ticket="));
    }

    @Test
    public void testExpiredQAResultHTML() throws Exception {
        XQueryService qs = new XQueryService();

        Vector queryResult = qs.runQAScript("http://localhost/notexist.xml", "51");

        String qyeryResultHtml = new String((byte[]) queryResult.elementAt(1));
        boolean ok = false;

        ok = qyeryResultHtml != null && qyeryResultHtml.startsWith("<html>") && qyeryResultHtml.endsWith("</html>");
        // //must contain red warning <span class="warning">The stylesheet expired on 11.11.2010</span>
        ok = ok && qyeryResultHtml.indexOf("<div class=\"error-msg\">The reported XML file uses an expired XML Schema.") != -1;

        assertTrue(ok);

    }

    @Test
    public void testNotExpiredQAResultHtml() throws Exception {
        XQueryService qs = new XQueryService();

        Vector queryResult = qs.runQAScript("http://localhost/notexist.xml", "52");

        String qyeryResultHtml = new String((byte[]) queryResult.elementAt(1));
        boolean ok = false;

        ok = qyeryResultHtml != null && qyeryResultHtml.startsWith("<html>") && qyeryResultHtml.endsWith("</html>");
        // must NOT contain red warning <span class="warning">The stylesheet expired on 11.11.2010</span>
        ok = ok && qyeryResultHtml.indexOf("expired") == -1;

        assertTrue(ok);

    }

    @Test
    public void testExpiredQAResultXml() throws Exception {
        XQueryService qs = new XQueryService();

        Vector queryResult = qs.runQAScript("http://localhost/notexist.xml", "53");

        String qyeryResultXml = new String((byte[]) queryResult.elementAt(1));
        boolean ok = false;

        ok = qyeryResultXml != null && qyeryResultXml.startsWith("<?xml");
        // must NOT contain red warning - it is XML
        ok = ok && qyeryResultXml.indexOf("expired") == -1;

        assertTrue(ok);

    }
}
