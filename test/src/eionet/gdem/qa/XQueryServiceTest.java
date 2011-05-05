/*
 * Created on 29.10.2008
 */
package eionet.gdem.qa;

import java.util.Hashtable;
import java.util.Vector;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IXQJobDao;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * XQueryServiceTst
 */

public class XQueryServiceTest  extends DBTestCase{
     private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();

     /**
     * Provide a connection to the database.
     */
    public XQueryServiceTest(String name)
    {
        super( name );
        DbHelper.setUpConnectionProperties();
    }
    /**
     * Set up test case properties
     */
    protected void setUp()throws Exception{
        try {
        super.setUp();
        TestUtils.setUpProperties(this);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    /**
     * Load the data which will be inserted for the test
     */
    protected IDataSet getDataSet() throws Exception {
        IDataSet loadedDataSet = new FlatXmlDataSet(
                getClass().getClassLoader().getResourceAsStream(
                        TestConstants.SEED_DATASET_QA_XML));
        return loadedDataSet;
    }
    /**
     * Tests that the added QA job contains the qa account data for QA engine
     */
    public void testAnalyzeXMLProtectedFiles() throws Exception {

        XQueryService qs = new XQueryService();

        String schema ="http://biodiversity.eionet.europa.eu/schemas/dir9243eec/habitats.xsd";
        String fileName="http://cdr.eionet.europa.eu/test.xml";
        Hashtable hash = new Hashtable();
        Vector files = new Vector();
        files.add(fileName);
        hash.put(schema,files);

        Vector v = qs.analyzeXMLFiles(hash);
        assertTrue(v.size()==1);
        Vector v2 = (Vector)v.get(0);
        String jobId = (String)v2.get(0);

        String jobdata[] = xqJobDao.getXQJobData(jobId);
        String urlField = jobdata[0];

        //check if url field containts ticket parameter
        assertTrue(urlField.contains("getsource?ticket="));
    }

    public void testExpiredQAResultHTML() throws Exception {
        XQueryService qs = new XQueryService();

        Vector queryResult = qs.runQAScript("http://localhost/notexist.xml", "51");

        String qyeryResultHtml = new String( (byte[]) queryResult.elementAt(1));
        boolean ok = false;

        ok = qyeryResultHtml != null && qyeryResultHtml.startsWith("<html>") && qyeryResultHtml.endsWith("</html>");
//		//must contain red warning <span class="warning">The stylesheet expired on 11.11.2010</span>
        ok = ok && qyeryResultHtml.indexOf("<div class=\"error-msg\">The reported XML file uses an expired XML Schema.") != -1;

        assertTrue(ok);

    }

    public void testNotExpiredQAResultHtml() throws Exception {
        XQueryService qs = new XQueryService();

        Vector queryResult = qs.runQAScript("http://localhost/notexist.xml", "52");

        String qyeryResultHtml = new String( (byte[]) queryResult.elementAt(1));
        boolean ok = false;

        ok = qyeryResultHtml != null && qyeryResultHtml.startsWith("<html>") && qyeryResultHtml.endsWith("</html>");
        //must NOT contain red warning <span class="warning">The stylesheet expired on 11.11.2010</span>
        ok = ok && qyeryResultHtml.indexOf("expired") == -1;

        assertTrue(ok);

    }
    public void testExpiredQAResultXml() throws Exception {
        XQueryService qs = new XQueryService();

        Vector queryResult = qs.runQAScript("http://localhost/notexist.xml", "53");

        String qyeryResultXml = new String( (byte[]) queryResult.elementAt(1));
        boolean ok = false;

        ok = qyeryResultXml != null && qyeryResultXml.startsWith("<?xml");
        //must NOT contain red warning - it is XML
        ok = ok && qyeryResultXml.indexOf("expired") == -1;

        assertTrue(ok);

    }
}
