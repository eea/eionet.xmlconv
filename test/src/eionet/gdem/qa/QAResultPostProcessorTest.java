package eionet.gdem.qa;

import java.util.HashMap;
import java.util.Map;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

public class QAResultPostProcessorTest extends DBTestCase {
    /**
     * Provide a connection to the database.
     */
    public QAResultPostProcessorTest(String name) {
        super(name);
        DbHelper.setUpConnectionProperties();
    }

    /**
     * Set up test case properties
     */
    protected void setUp() throws Exception {
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
        IDataSet loadedDataSet =
                new FlatXmlDataSet(getClass().getClassLoader().getResourceAsStream(TestConstants.SEED_DATASET_QA_XML));
        return loadedDataSet;
    }

    public void testExpiredSchemaQAResult() {
        QAResultPostProcessor postProcessor = new QAResultPostProcessor();
        String message = postProcessor.getWarningMessage("http://localhost/not_existing.xsd");

        assertTrue(message.indexOf("expired") > -1);

        String message2 = postProcessor.getWarningMessage("http://localhost/not_existing2.xsd");
        assertNull(message2);
    }

    public void testObsoleteDDSchemaQAResult() {
        MockQAResultPostProcessor qaPostProcessor = new MockQAResultPostProcessor();
        Map<String, String> dataset = new HashMap<String, String>();
        dataset.put("id", "1111");
        dataset.put("status", "Released");
        dataset.put("isLatestReleased", "true");
        dataset.put("dateOfLatestReleased", "1257138000000");
        dataset.put("idOfLatestReleased", "2222");
        qaPostProcessor.setDataset(dataset);

        // schema is latest released, no warning message
        String message = qaPostProcessor.getWarningMessage("http://dd.eionet.europa.eu/GetSchema?id=DST1111");
        assertNull(message);

        // schema is not latest released, warning message about obsolete schema
        dataset.put("status", "RELEASED");
        dataset.put("isLatestReleased", "false");
        dataset.put("dateOfLatestReleased", "1257138000000"); // 2 Nov 2009
        dataset.put("idOfLatestReleased", "2222");
        qaPostProcessor.setDataset(dataset);
        String message2 = qaPostProcessor.getWarningMessage("http://dd.eionet.europa.eu/GetSchema?id=DST1111");
        assertTrue(message2.indexOf("obsolete") > -1);

        // schema is not RELEASED, but OK
        dataset.put("status", "Incomplete");
        dataset.put("isLatestReleased", "false");
        qaPostProcessor.setDataset(dataset);
        String message3 = qaPostProcessor.getWarningMessage("http://dd.eionet.europa.eu/GetSchema?id=DST1111");
        assertNull(message3);

    }

    class MockQAResultPostProcessor extends QAResultPostProcessor {

        Map<String, String> datasetResult = null;

        protected Map getDataset(String xmlSchema) {
            return datasetResult;
        }

        public void setDataset(Map<String, String> dataset) {
            this.datasetResult = dataset;
        }
    }
}
