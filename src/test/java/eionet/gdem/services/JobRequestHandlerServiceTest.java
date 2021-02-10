package eionet.gdem.services;

import eionet.gdem.services.impl.JobRequestHandlerServiceImpl;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class JobRequestHandlerServiceTest {

    JobRequestHandlerService jobRequestHandlerService = new JobRequestHandlerServiceImpl();

    /**
     * Tests that the added QA job contains the qa account data for QA engine
     */
    @Test
    public void testAnalyzeXMLProtectedFiles() throws Exception {
        String schema = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/habitats.xsd";
        String fileName = "http://cdr.eionet.europa.eu/test.xml";
        HashMap map = new HashMap();
        List<String> fileList = new ArrayList<>();
        fileList.add(fileName);
        map.put(schema, fileList);

        Vector v = jobRequestHandlerService.analyzeMultipleXMLFiles(map);
        assertTrue(v.size() == 1);
        Vector v2 = (Vector) v.get(0);
        String jobId = (String) v2.get(0);
        System.out.println("---------------------------------- jobId: " + jobId);
    }
}
