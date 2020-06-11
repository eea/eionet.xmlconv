package eionet.gdem.qa.engines;

import eionet.gdem.XMLConvException;
import eionet.gdem.qa.XQScript;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jooq.tools.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FMEQueryEngine.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*", "org.w3c.dom.*", "sun.security.*", "javax.net.ssl.*"})
public class FMEQueryEngineTest {

    @Mock
    private FMEQueryEngine fmeQueryEngine;
    @Mock
    private CloseableHttpClient client_;
    @Mock
    private CloseableHttpResponse response;
    @Mock
    private HttpEntity entity;
    @Mock
    private StatusLine statusLine;

    private static RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

    private Integer fmeTimeoutProperty = 1;
    private String fmeHostProperty = "fme.discomap.eea.europa.eu";
    private String fmePortProperty = "80";
    private String fmeUserProperty = "notValidUsername";
    private String fmePasswordProperty = "notValidPassword";
    private String fmeTokenExpirationProperty = "1";
    private String fmeTokenTimeunitProperty = "hour";
    private String fmePollingUrlProperty = "https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/jobs/id/";
    private Integer fmeRetryHoursProperty = 1;
    private String failedOutputString = "<div class=\"feedbacktext\"><span id=\"feedbackStatus\" class=\"BLOCKER\" style=\"display:none\">The QC process failed. Please try again. If the issue persists please contact the dataflow helpdesk.</span>The QC process failed. Please try again. If the issue persists please contact the dataflow helpdesk.</div>";


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(fmeQueryEngine.getFmeTimeoutProperty()).thenReturn(fmeTimeoutProperty);
        when(fmeQueryEngine.getFmeHostProperty()).thenReturn(fmeHostProperty);
        when(fmeQueryEngine.getFmePortProperty()).thenReturn(fmePortProperty);
        when(fmeQueryEngine.getFmeUserProperty()).thenReturn(fmeUserProperty);
        when(fmeQueryEngine.getFmePasswordProperty()).thenReturn(fmePasswordProperty);
        when(fmeQueryEngine.getFmeTokenExpirationProperty()).thenReturn(fmeTokenExpirationProperty);
        when(fmeQueryEngine.getFmeTokenTimeunitProperty()).thenReturn(fmeTokenTimeunitProperty);
        when(fmeQueryEngine.getFmePollingUrlProperty()).thenReturn(fmePollingUrlProperty);
        when(fmeQueryEngine.getFmeRetryHoursProperty()).thenReturn(fmeRetryHoursProperty);
        when(fmeQueryEngine.getRetries()).thenReturn(3);
        when(fmeQueryEngine.getClient_()).thenReturn(client_);
       // when(fmeQueryEngine.getToken_()).thenCallRealMethod();
        requestConfigBuilder.setSocketTimeout(fmeTimeoutProperty);
        PowerMockito.mockStatic(FMEQueryEngine.class);
        when(fmeQueryEngine.getRequestConfigBuilder()).thenReturn(requestConfigBuilder);
        when(client_.execute(ArgumentMatchers.any(HttpUriRequest.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(entity);
        doCallRealMethod().when(fmeQueryEngine).runQuery(Mockito.any(), Mockito.any());

    }

    /* Test case: successful retrieval of token */
    @Test
    public void testGetConnectionInfoSuccessful() throws Exception {
        String tokenValue = "token";
        when(statusLine.getStatusCode()).thenReturn(200);
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( tokenValue.getBytes() ));
        Whitebox.invokeMethod(fmeQueryEngine, "getConnectionInfo");
       // Assert.assertThat(fmeQueryEngine.getToken_(), is(tokenValue));
    }

    /* Test case: not successful */
    @Test(expected = Exception.class)
    public void testGetConnectionNotSuccessful() throws Exception {
        when(statusLine.getStatusCode()).thenReturn(400);
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getConnectionInfo");
        }
        catch(Exception e)
        {
            String expectedMessage = "FME authentication failed. Could not retrieve a Token";
            Assert.assertThat(e.getMessage(), containsString(expectedMessage));
            throw e;
        }
        fail("Unsuccessful retrieval of token - exception did not throw!");
    }

    /* Test case: creation of json object with not null xmlSourceFile */
    @Test
    public void testCreateJSONObjectForJobSubmissionNotNullFile() throws Exception {
        JSONObject actual = Whitebox.invokeMethod(fmeQueryEngine, "createJSONObjectForJobSubmission", "test");
        String expected = "{\"publishedParameters\":[{\"name\":\"DestDataset_JSON\",\"value\":\"response.json\"},{\"name\":\"SourceDataset_XML\",\"value\":[\"test\"]}]}";
        Assert.assertThat(actual, is(notNullValue()));
        Assert.assertThat(actual.toString(), is(expected));
    }

    /* Test case: creation of json object with null xmlSourceFile */
    @Test
    public void testCreateJSONObjectForJobSubmissionNullFile() throws Exception {
        JSONObject actual = Whitebox.invokeMethod(fmeQueryEngine, "createJSONObjectForJobSubmission", null);
        String expected = "{\"publishedParameters\":[{\"name\":\"DestDataset_JSON\",\"value\":\"response.json\"},{\"name\":\"SourceDataset_XML\",\"value\":[\"null\"]}]}";
        Assert.assertThat(actual, is(notNullValue()));
        Assert.assertThat(actual.toString(), is(expected));
    }

    /* Test case: job submission with null xqscript */
    @Test(expected = Exception.class)
    public void testSubmitJobToFMENullXQScript() throws Exception {
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "submitJobToFME", null);
        }
        catch(Exception e)
        {
            String expectedMessage = "XQScript is empty";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Null XQScript provided - exception did not throw!");
    }

    /* Test case: job submission with null script source file */
    @Test(expected = Exception.class)
    public void testSubmitJobToFMENullScriptSourceFile() throws Exception {
        XQScript script = new XQScript(null, null, null);
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "submitJobToFME", script);
        }
        catch(Exception e)
        {
            String expectedMessage = "XQScript source file is empty";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Null XQScript source file - exception did not throw!");
    }

    /* Test case: job submission with null xml source file */
    @Test(expected = Exception.class)
    public void testSubmitJobToFMENullXMLFile() throws Exception {
        XQScript script = new XQScript("testFmw", null, null);
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "submitJobToFME", script);
        }
        catch(Exception e)
        {
            String expectedMessage = "XML file was not provided";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Null XML source file - exception did not throw!");
    }

    /* Test case: job submission with unauthorized token */
    @Test(expected = Exception.class)
    public void testSubmitJobToFMEUnauthorized() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        when(statusLine.getStatusCode()).thenReturn(401);
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "submitJobToFME", script);
        }
        catch(Exception e)
        {
            String expectedMessage = "Unauthorized token";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Null XML source file - exception did not throw!");
    }

    /* Test case: job submission with unauthorized token */
    @Test(expected = Exception.class)
    public void testSubmitJobToFMEWrongStatus() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        when(statusLine.getStatusCode()).thenReturn(404);
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "submitJobToFME", script);
        }
        catch(Exception e)
        {
            String expectedMessage = "Received status code 404 for job submission request";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Null XML source file - exception did not throw!");
    }


    /* Test case: successful job submission */
    @Test
    public void testSubmitJobToFMECSuccessful() throws Exception {
        when(statusLine.getStatusCode()).thenReturn(200);
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":\"123\"}".getBytes() ));
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");

        String jobId = Whitebox.invokeMethod(fmeQueryEngine, "submitJobToFME", script);
        Assert.assertThat(jobId, is(notNullValue()));
        Assert.assertThat(jobId, is("123"));
    }

    /* Test case: job submission no job Id retrieved */
    @Test(expected = Exception.class)
    public void testSubmitJobToFMENoId() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"something\":\"somethingElse\"}".getBytes() ));
        when(statusLine.getStatusCode()).thenReturn(200);
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "submitJobToFME", script);
        }
        catch(Exception e)
        {
            String expectedMessage = "JSONObject[\"id\"] not found.";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Null XML source file - exception did not throw!");
    }

    /* Test case: job submission null job Id */
    @Test(expected = Exception.class)
    public void testSubmitJobToFMENullId() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":null}".getBytes() ));
        when(statusLine.getStatusCode()).thenReturn(200);
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "submitJobToFME", script);
        }
        catch(Exception e)
        {
            String expectedMessage = "Valid status code but no job ID was retrieved";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Null XML source file - exception did not throw!");
    }

    /* Test case: job submission empty job id */
    @Test(expected = Exception.class)
    public void testSubmitJobToFMEEmptyId() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":\"\"}".getBytes() ));
        when(statusLine.getStatusCode()).thenReturn(200);
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "submitJobToFME", script);
        }
        catch(Exception e)
        {
            String expectedMessage = "Valid status code but no job ID was retrieved";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Null XML source file - exception did not throw!");
    }

     /* Test case: not valid result */
    @Test(expected = Exception.class)
    public void testGetJobStatusNotSC_OK() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(400);
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getJobStatus", jobId, result, script);
        }
        catch(Exception e)
        {
            String expectedMessage = "Error when polling for job status. Received status code: 400";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Not valid response code - exception did not throw!");
    }

    /* Test case: status ABORTED */
    @Test(expected = Exception.class)
    public void testGetJobStatusABORTED() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"ABORTED\"},\"id\":\"testId\",\"status\":\"ABORTED\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ));
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getJobStatus", jobId, result, script);
        }
        catch(Exception e)
        {
            String expectedMessage = "Received response status ABORTED";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Status ABORTED - exception did not throw!");
    }

    /* Test case: status FME_FAILURE */
    @Test(expected = Exception.class)
    public void testGetJobStatusFME_FAILURE() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"FME_FAILURE\"},\"id\":\"testId\",\"status\":\"FME_FAILURE\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ));
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getJobStatus", jobId, result, script);
        }
        catch(Exception e)
        {
            String expectedMessage = "Received response status FME_FAILURE";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Status FME_FAILURE - exception did not throw!");
    }

    /* Test case: status JOB_FAILURE */
    @Test(expected = Exception.class)
    public void testGetJobStatusJOB_FAILURE() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"JOB_FAILURE\"},\"id\":\"testId\",\"status\":\"JOB_FAILURE\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ));
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getJobStatus", jobId, result, script);
        }
        catch(Exception e)
        {
            String expectedMessage = "Received response status JOB_FAILURE";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Status JOB_FAILURE - exception did not throw!");
    }

    /* Test case: status SUCCESS Result status SUCCESS */
    @Test
    public void testGetJobStatusSUCCESS_Result_SUCCESS() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"SUCCESS\"},\"id\":\"testId\",\"status\":\"SUCCESS\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ));
        Whitebox.invokeMethod(fmeQueryEngine, "getJobStatus", jobId, result, script);
    }

    /* Test case: status SUCCESS Result status FME_FAILURE*/
    @Test(expected = Exception.class)
    public void testGetJobStatusSUCCESS_Result_FME_FAILURE() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"FME_FAILURE\"},\"id\":\"testId\",\"status\":\"SUCCESS\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ));
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getJobStatus", jobId, result, script);
        }
        catch(Exception e)
        {
            String expectedMessage = "Received result status FME_FAILURE for job Id #testId";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Result Status FME_FAILURE - exception did not throw!");
    }

    /* Test case: status SUBMITTED never succeeded */
    @Test(expected = Exception.class)
    public void testGetJobStatusSUBMITTEDNeverSUCCESS() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"SUBMITTED\"},\"id\":\"testId\",\"status\":\"SUBMITTED\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ));
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getJobStatus", jobId, result, script);
        }
        catch(Exception e)
        {
            String expectedMessage = "Failed for last Retry  number: 2. Received status SUBMITTED";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Status SUBMITTED - exception did not throw!");
    }


    /* Test case: status QUEUED */
    @Test
    public void testGetJobStatusQUEUED_SUCCESS_2ndTime() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"QUEUED\"},\"id\":\"testId\",\"status\":\"QUEUED\"}";
        String JSONResponseSuccess = "{\"result\":{\"status\":\"SUCCESS\"},\"id\":\"testId\",\"status\":\"SUCCESS\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponseSuccess.getBytes() ));
        Whitebox.invokeMethod(fmeQueryEngine, "getJobStatus", jobId, result, script);
    }

    /* Test case: status PULLED */
    @Test(expected = Exception.class)
    public void testGetJobStatusPULLED_ABORTED_3rdTime() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"PULLED\"},\"id\":\"testId\",\"status\":\"PULLED\"}";
        String JSONResponseABORTED = "{\"result\":{\"status\":\"ABORTED\"},\"id\":\"testId\",\"status\":\"ABORTED\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponseABORTED.getBytes() ));
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getJobStatus", jobId, result, script);
        }
        catch(Exception e)
        {
            String expectedMessage = "Received response status ABORTED";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Status ABORTED - exception did not throw!");
    }

    /* Test case: other status */
    @Test(expected = Exception.class)
    public void testGetJobStatusOtherStatusCode() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"OTHER\"},\"id\":\"testId\",\"status\":\"OTHER\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ));
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getJobStatus", jobId, result, script);
        }
        catch(Exception e)
        {
            String expectedMessage = "Received wrong response status OTHER";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Status OTHER - exception did not throw!");
    }

    /* Test case: run query with null xqscript */
    @Test
    public void testRunQueryNullXQScript() throws Exception {
        FileOutputStream result = new FileOutputStream(new File("testFile"));

        fmeQueryEngine.runQuery(null, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));
    }

    /* Test case: run query with null script source file */
    @Test
    public void testRunQueryNullScriptSourceFile() throws Exception {
        XQScript script = new XQScript(null, null, null);
        FileOutputStream result = new FileOutputStream(new File("testFile"));

        fmeQueryEngine.runQuery(script, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));
    }

    /* Test case: run query with null xml source file */
    @Test
    public void testRunQueryNullXMLFile() throws Exception {
        XQScript script = new XQScript("testFmw", null, null);
        FileOutputStream result = new FileOutputStream(new File("testFile"));

        fmeQueryEngine.runQuery(script, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));
    }

    /* Test case: job submission with unauthorized token */
    @Test
    public void testRunQueryUnauthorized() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        when(statusLine.getStatusCode()).thenReturn(401);
        FileOutputStream result = new FileOutputStream(new File("testFile"));

        fmeQueryEngine.runQuery(script, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));
    }

    /* Test case: run query with unauthorized token */
    @Test
    public void testRunQueryWrongStatus() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        when(statusLine.getStatusCode()).thenReturn(404);
        FileOutputStream result = new FileOutputStream(new File("testFile"));

        fmeQueryEngine.runQuery(script, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));
    }

    /* Test case: run query no job Id retrieved */
    @Test
    public void testRunQueryNoId() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"something\":\"somethingElse\"}".getBytes() ));
        when(statusLine.getStatusCode()).thenReturn(200);
        FileOutputStream result = new FileOutputStream(new File("testFile"));

        fmeQueryEngine.runQuery(script, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));
    }

    /* Test case: run query null job Id */
    @Test
    public void testRunQueryNullId() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":null}".getBytes() ));
        when(statusLine.getStatusCode()).thenReturn(200);
        FileOutputStream result = new FileOutputStream(new File("testFile"));

        fmeQueryEngine.runQuery(script, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));
    }

    /* Test case: run query empty job id */
    @Test
    public void testRunQueryEmptyId() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":\"\"}".getBytes() ));
        when(statusLine.getStatusCode()).thenReturn(200);
        FileOutputStream result = new FileOutputStream(new File("testFile"));

        fmeQueryEngine.runQuery(script, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));

    }

    /* Test case: run query successful job submission */
    @Test
    public void testRunQueryJobStatusNotSC_OK() throws Exception {
        when(statusLine.getStatusCode()).thenReturn(200).thenReturn(400);
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":\"123\"}".getBytes() ));
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));

        fmeQueryEngine.runQuery(script, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));

    }

    /* Test case: run query status ABORTED */
    @Test
    public void testRunQueryStatusABORTED() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"ABORTED\"},\"id\":\"testId\",\"status\":\"ABORTED\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":\"123\"}".getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ));

        fmeQueryEngine.runQuery(script, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));
    }

    /* Test case: run query status FME_FAILURE */
    @Test
    public void testRunQueryStatusFME_FAILURE() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"FME_FAILURE\"},\"id\":\"testId\",\"status\":\"FME_FAILURE\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":\"123\"}".getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ));

        fmeQueryEngine.runQuery(script, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));
    }

    /* Test case: run query status JOB_FAILURE */
    @Test
    public void testRunQueryStatusJOB_FAILURE() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"JOB_FAILURE\"},\"id\":\"testId\",\"status\":\"JOB_FAILURE\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":\"123\"}".getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ));

        fmeQueryEngine.runQuery(script, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));
    }

    /* Test case: run query status SUCCESS Result status SUCCESS */
    @Test
    public void testRunQueryStatusSUCCESS_Result_SUCCESS() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"SUCCESS\"},\"id\":\"testId\",\"status\":\"SUCCESS\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":\"123\"}".getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ));
        fmeQueryEngine.runQuery(script, result);

        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is("{\"result\":{\"status\":\"SUCCESS\"},\"id\":\"testId\",\"status\":\"SUCCESS\"}"));
    }

    /* Test case: run query status SUCCESS Result status FME_FAILURE*/
    @Test
    public void testRunQueryStatusSUCCESS_Result_FME_FAILURE() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"FME_FAILURE\"},\"id\":\"testId\",\"status\":\"SUCCESS\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":\"123\"}".getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ));
        fmeQueryEngine.runQuery(script, result);

        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));
    }

    /* Test case: run query status SUBMITTED never succeeded */
    @Test
    public void testRunQueryStatusSUBMITTEDNeverSUCCESS() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"SUBMITTED\"},\"id\":\"testId\",\"status\":\"SUBMITTED\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":\"123\"}".getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ));

        fmeQueryEngine.runQuery(script, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));
    }


    /* Test case: run query status QUEUED */
    @Test
    public void testRunQueryStatusQUEUED_SUCCESS_2ndTime() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"QUEUED\"},\"id\":\"testId\",\"status\":\"QUEUED\"}";
        String JSONResponseSuccess = "{\"result\":{\"status\":\"SUCCESS\"},\"id\":\"testId\",\"status\":\"SUCCESS\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":\"123\"}".getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponseSuccess.getBytes() ));
        fmeQueryEngine.runQuery(script, result);

        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is("{\"result\":{\"status\":\"SUCCESS\"},\"id\":\"testId\",\"status\":\"SUCCESS\"}"));
    }

    /* Test case: run query status PULLED */
    @Test
    public void testRunQueryStatusPULLED_ABORTED_3rdTime() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"PULLED\"},\"id\":\"testId\",\"status\":\"PULLED\"}";
        String JSONResponseABORTED = "{\"result\":{\"status\":\"ABORTED\"},\"id\":\"testId\",\"status\":\"ABORTED\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":\"123\"}".getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponseABORTED.getBytes() ));

        fmeQueryEngine.runQuery(script, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));
    }

    /* Test case: run query other status */
    @Test
    public void testRunQueryOtherStatusCode() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        FileOutputStream result = new FileOutputStream(new File("testFile"));
        String jobId = "testId";
        when(statusLine.getStatusCode()).thenReturn(200);
        String JSONResponse = "{\"result\":{\"status\":\"OTHER\"},\"id\":\"testId\",\"status\":\"OTHER\"}";
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{\"id\":\"123\"}".getBytes() ))
                .thenReturn(new ByteArrayInputStream( JSONResponse.getBytes() ));

        fmeQueryEngine.runQuery(script, result);
        FileInputStream fis = new FileInputStream ("testFile");
        String text = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, is(failedOutputString));
    }

}
