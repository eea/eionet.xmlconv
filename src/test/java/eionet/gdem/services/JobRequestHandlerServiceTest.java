package eionet.gdem.services;

import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class JobRequestHandlerServiceTest {

    @Qualifier("jobRequestHandlerService")
    @Autowired
    JobRequestHandlerService jobRequestHandlerService;

    @Autowired
    private DataSource db;

    @Autowired
    private IXQJobDao xqJobDao;

    /**
     * Set up test case properties
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_QA_XML);
    }


    /**
     * Test for one xml file and one schema
     */
    @Test
    public void testAnalyzeXMLProtectedFiles() throws Exception {
        String schema = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/habitats.xsd";
        String fileName = "http://cdr.eionet.europa.eu/test.xml";
        HashMap map = new HashMap();
        List<String> fileList = new ArrayList<>();
        fileList.add(fileName);
        map.put(schema, fileList);

        HashMap<String, String> result = jobRequestHandlerService.analyzeMultipleXMLFiles(map);
        assertTrue(result.size() == 1);

        for (Map.Entry<String, String> entry : result.entrySet()) {
            String jobId = entry.getKey();
            assertUrlField(jobId);
        }
    }

    /**
     * Tests for one schema and multiple files
     */
    @Test
    public void testAnalyzeXMLProtectedMultipleFiles() throws Exception {
        String schema = "http://dd.eionet.eu.int/namespace.jsp?ns_id=200 http://dd.eionet.eu.int/GetSchema?id=TBL1920";
        HashMap map = new HashMap();
        List<String> fileList = new ArrayList<>();
        fileList.add("http://cdr.eionet.europa.eu/test.xml");
        fileList.add("http://cdr.eionet.europa.eu/test2.xml");
        fileList.add("http://cdr.eionet.europa.eu/test3.xml");
        map.put(schema, fileList);

        HashMap<String, String> result = jobRequestHandlerService.analyzeMultipleXMLFiles(map);
        assertTrue(result.size() == 9);
        for (Map.Entry<String, String> entry : result.entrySet()) {
            String jobId = entry.getKey();
            assertUrlField(jobId);
        }
    }

    /**
     * Tests for multiple schemas and multiple files
     */
    @Test
    public void testAnalyzeXMLProtectedMMultipleSchemasAndFiles() throws Exception {
        String schema1 = "http://dd.eionet.eu.int/namespace.jsp?ns_id=200 http://dd.eionet.eu.int/GetSchema?id=TBL1920";
        String schema2 = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/habitats.xsd";
        HashMap map = new HashMap();
        List<String> fileList1 = new ArrayList<>();
        fileList1.add("http://cdr.eionet.europa.eu/test.xml");
        fileList1.add("http://cdr.eionet.europa.eu/test2.xml");
        fileList1.add("http://cdr.eionet.europa.eu/test3.xml");
        map.put(schema1, fileList1);

        List<String> fileList2 = new ArrayList<>();
        fileList2.add("http://cdr.eionet.europa.eu/test2.xml");
        fileList2.add("http://cdr.eionet.europa.eu/test4.xml");
        map.put(schema2, fileList2);

        HashMap<String, String> result = jobRequestHandlerService.analyzeMultipleXMLFiles(map);
        assertTrue(result.size() == 11);
        for (Map.Entry<String, String> entry : result.entrySet()) {
            String jobId = entry.getKey();
            assertUrlField(jobId);
        }
    }

    private void assertUrlField(String jobId) throws SQLException {
        String jobdata[] = xqJobDao.getXQJobData(jobId);
        String urlField = jobdata[0];

        // check if url field containts ticket parameter
        assertTrue(urlField.contains("getsource?ticket="));
    }
}
