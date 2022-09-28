package eionet.gdem.api.qa.service;

import eionet.gdem.XMLConvException;
import eionet.gdem.api.qa.service.impl.QaServiceImpl;
import eionet.gdem.dto.Schema;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.QueryMetadataService;
import eionet.gdem.qa.QueryService;
import eionet.gdem.rabbitMQ.service.CdrResponseMessageFactoryService;
import eionet.gdem.services.JobHistoryService;
import eionet.gdem.services.JobRequestHandlerService;
import eionet.gdem.services.JobResultHandlerService;
import eionet.gdem.services.RunScriptAutomaticService;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class QaServiceTest {

    private QaServiceImpl qaService;

    @Mock
    private QaServiceImpl qaServiceMocked;

    @Mock
    private JobRequestHandlerService jobRequestHandlerService;

    @Mock
    private JobResultHandlerService jobResultHandlerService;

    @Mock
    private RunScriptAutomaticService runScriptAutomaticService;

    @Mock
    private QueryService queryServiceMock;

    @Mock
    private JobService jobService;

    @Mock
    private JobHistoryService jobHistoryService;

    @Mock
    private JobExecutorHistoryService jobExecutorHistoryService;

    @Mock
    private QueryMetadataService queryMetadataService;

    @Mock
    private CdrResponseMessageFactoryService cdrResponseMessageFactoryService;

    DocumentBuilder documentBuilder;

    @Autowired
    private DataSource db;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.qaService = new QaServiceImpl(queryServiceMock, jobRequestHandlerService, jobResultHandlerService, runScriptAutomaticService, jobService, jobHistoryService, jobExecutorHistoryService, queryMetadataService, cdrResponseMessageFactoryService);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_QA_XML);
    }

    @Test
    public void testExtractLinksAndSchemasFromEnvelopeUrl() throws URISyntaxException, ParserConfigurationException, SAXException, IOException, XMLConvException {
        URL url = this.getClass().getResource("/envelope-xml.xml");
        String envelopeUrl = "https://cdrtest.eionet.europa.eu/gr/colvjazdw/envvkyrww/AutomaticQA_70556";
        File XmlFile = new File(url.toURI());
        QaServiceImpl spy = spy(qaService);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(XmlFile);
        when(spy.getXMLFromEnvelopeURL(envelopeUrl)).thenReturn(doc);
        HashMap<String, String> fileLinksAndSchemas = new HashMap<String, String>();
        fileLinksAndSchemas.put("http://cdrtest.eionet.europa.eu/gr/sample.xml", "http://dd.eionet.europa.eu/GetSchema?id=1234");
        HashMap<String, String> realResults = spy.extractFileLinksAndSchemasFromEnvelopeUrl(envelopeUrl);
        Assert.assertEquals(fileLinksAndSchemas.get("http://cdrtest.eionet.europa.eu/gr/sample.xml"), realResults.get("http://cdrtest.eionet.europa.eu/gr/sample.xml"));
    }


    /* Test case: get schema by url successful */
    @Test
    public void testGetSchemaBySchemaUrlSuccessful() throws Exception {
        Schema result = qaService.getSchemaBySchemaUrl("http://localhost/not_existing.xsd");
        MatcherAssert.assertThat(result.getId(), is("58"));
        MatcherAssert.assertThat(result.getSchema(), is("http://localhost/not_existing.xsd"));
        MatcherAssert.assertThat(result.getDescription(), is("Expired dummy"));
        MatcherAssert.assertThat(result.getDtdPublicId(), is(""));
        MatcherAssert.assertThat(result.isDoValidation(), is(false));
        MatcherAssert.assertThat(result.getSchemaLang(), is("XSD"));
        MatcherAssert.assertThat(result.isBlocker(), is(false));
        MatcherAssert.assertThat(result.getMaxExecutionTime(), is(nullValue()));
    }

    /* Test case: content is not empty */
    @Test
    public void testHandleOnDemandJobsResultsNotEmptyContent() throws Exception {
        Vector results = new Vector();
        results.add(("testContentType"));
        results.add(("testContent").getBytes());
        results.add(("testFeedbackStatus").getBytes());
        results.add(("testMessage").getBytes());
        String sourceUrl = "testXml";
        String scriptId = "1";
        Long maxMs = 5L;
        Long timeoutMs = 1L;

        when(qaServiceMocked.getMaxMsToWaitForEmptyFileForOnDemandJobs()).thenReturn(maxMs);
        when(qaServiceMocked.getTimeoutToWaitForEmptyFileForOnDemandJobs()).thenReturn(timeoutMs);
        when(qaServiceMocked.ConvertByteArrayToString(any(byte[].class))).thenCallRealMethod();
        when(qaServiceMocked.handleOnDemandJobsResults(results, sourceUrl, scriptId)).thenCallRealMethod();

        LinkedHashMap<String, String> jsonResults = qaServiceMocked.handleOnDemandJobsResults(results, sourceUrl, scriptId);
        MatcherAssert.assertThat(jsonResults.get("feedbackStatus"), is("testFeedbackStatus"));
        MatcherAssert.assertThat(jsonResults.get("feedbackMessage"), is("testMessage"));
        MatcherAssert.assertThat(jsonResults.get("feedbackContent"), is("testContent"));
        MatcherAssert.assertThat(jsonResults.get("feedbackContentType"), is("testContentType"));
    }

    /* Test case: content is empty */
    @Test
    public void testHandleOnDemandJobsResultsEmptyContent() throws Exception {
        Vector results = new Vector();
        results.add(("testContentType"));
        results.add(("").getBytes());
        results.add(("testFeedbackStatus").getBytes());
        results.add(("testMessage").getBytes());
        String sourceUrl = "testXml";
        String scriptId = "1";
        Long maxMs = 5L;
        Long timeoutMs = 1L;

        when(qaServiceMocked.getMaxMsToWaitForEmptyFileForOnDemandJobs()).thenReturn(maxMs);
        when(qaServiceMocked.getTimeoutToWaitForEmptyFileForOnDemandJobs()).thenReturn(timeoutMs);
        when(qaServiceMocked.ConvertByteArrayToString(any(byte[].class))).thenCallRealMethod();
        when(qaServiceMocked.handleOnDemandJobsResults(results, sourceUrl, scriptId)).thenCallRealMethod();

        LinkedHashMap<String, String> jsonResults = qaServiceMocked.handleOnDemandJobsResults(results, sourceUrl, scriptId);
        MatcherAssert.assertThat(jsonResults.get("feedbackStatus"), is(TestConstants.ON_DEMAND_JOBS_EMPTY_CONTENT_FEEDBACK_STATUS));
        MatcherAssert.assertThat(jsonResults.get("feedbackMessage"), is(TestConstants.ON_DEMAND_JOBS_EMPTY_CONTENT_FEEDBACK_MESSAGE));
        MatcherAssert.assertThat(jsonResults.get("feedbackContent"), is(TestConstants.ON_DEMAND_JOBS_EMPTY_CONTENT_FEEDBACK_CONTENT));
        MatcherAssert.assertThat(jsonResults.get("feedbackContentType"), is(TestConstants.ON_DEMAND_JOBS_EMPTY_CONTENT_FEEDBACK_CONTENT_TYPE));
    }

}
