package eionet.gdem.services.impl;

import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.api.qa.service.impl.QaServiceImpl;
import eionet.gdem.qa.QueryService;
import eionet.gdem.services.JobRequestHandlerService;
import eionet.gdem.services.JobResultHandlerService;
import eionet.gdem.services.RunScriptAutomaticService;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class JobResultHandlerServiceTest {


    private QaServiceImpl qaService;

    @Mock
    private JobRequestHandlerService jobRequestHandlerService;

    @Spy
    @Autowired
    private JobResultHandlerServiceImpl jobResultHandlerService;

    @Mock
    private RunScriptAutomaticService runScriptAutomaticService;

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
    public void testSuccessGetJobResults() throws XMLConvException {
        String jobId = "22";
        Hashtable<String, Object> results = new Hashtable<String, Object>();
        results.put(Constants.RESULT_CODE_PRM, "0");
        when(jobResultHandlerService.getResult(jobId)).thenReturn(results);
        Hashtable<String, Object> realResults = this.qaService.getJobResults(jobId);
        verify(jobResultHandlerService, times(2)).getResult(jobId);
        Assert.assertEquals(results, realResults);
    }

    @Test
    public void testJobFatalError() throws Exception {
        Hashtable table = jobResultHandlerService.prepareResult(Constants.XQ_FATAL_ERR, new String[]{"", "", "src/test/resources/seed-gw-valid.xml", "", "", "-1"}, new HashMap(), "-1");
        assertEquals("Wrong result code", "0", table.get(Constants.RESULT_CODE_PRM));
    }

    @Test
    public void testJobLightError() throws Exception {
        Hashtable table = jobResultHandlerService.prepareResult(Constants.XQ_LIGHT_ERR, new String[]{"", "", "src/test/resources/seed-gw-valid.xml", "", "", "-1"}, new HashMap(), "-1");
        assertEquals("Wrong result code", "0", table.get(Constants.RESULT_CODE_PRM));
    }

    @Test
    public void testJobReady() throws Exception {
        Hashtable table = jobResultHandlerService.prepareResult(Constants.XQ_READY, new String[]{"", "", "src/test/resources/seed-gw-valid.xml", "", "", "-1"}, new HashMap(), "-1");
        assertEquals("Wrong result code", "0", table.get(Constants.RESULT_CODE_PRM));
    }

    @Test
    public void testJobNotFound() throws Exception {
        Hashtable table = jobResultHandlerService.prepareResult(Constants.XQ_JOBNOTFOUND_ERR, new String[]{"", "", "src/test/resources/seed-gw-valid.xml", "", "", "-1"}, new HashMap(), "-1");
        assertEquals("Wrong result code", "3", table.get(Constants.RESULT_CODE_PRM));
    }

    @Test
    public void testJobDownloading() throws Exception {
        Hashtable table = jobResultHandlerService.prepareResult(Constants.XQ_DOWNLOADING_SRC, new String[]{"", "", "src/test/resources/seed-gw-valid.xml", "", "", "-1"}, new HashMap(), "-1");
        assertEquals("Wrong result code", "1", table.get(Constants.RESULT_CODE_PRM));
    }

    @Test
    public void testJobReceived() throws Exception {
        Hashtable table = jobResultHandlerService.prepareResult(Constants.XQ_RECEIVED, new String[]{"", "", "src/test/resources/seed-gw-valid.xml", "", "", "-1"}, new HashMap(), "-1");
        assertEquals("Wrong result code", "1", table.get(Constants.RESULT_CODE_PRM));
    }
}
