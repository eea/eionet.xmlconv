package eionet.gdem.qa.engines;

import eionet.gdem.qa.XQScript;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicStatusLine;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
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
        when(fmeQueryEngine.getClient_()).thenReturn(client_);
        when(fmeQueryEngine.getToken_()).thenCallRealMethod();
        requestConfigBuilder.setSocketTimeout(fmeTimeoutProperty);
        PowerMockito.mockStatic(FMEQueryEngine.class);
        when(fmeQueryEngine.getRequestConfigBuilder()).thenReturn(requestConfigBuilder);
        when(client_.execute(ArgumentMatchers.any(HttpUriRequest.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(entity);
    }

    /* Test case: successful retrieval of token */
    @Test
    public void testGetConnectionInfoSuccessful() throws Exception {
        String tokenValue = "token";
        when(statusLine.getStatusCode()).thenReturn(200);
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( tokenValue.getBytes() ));
        Whitebox.invokeMethod(fmeQueryEngine, "getConnectionInfo");
        Assert.assertThat(fmeQueryEngine.getToken_(), is(tokenValue));
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

    /* Test case: get job status for wrong job id */
  /*  @Test(expected = Exception.class)
    public void testGetJobStatusWrongId() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        OutputStream result = null;
        String jobId = "testWrongId";
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getJobStatus", jobId, result, script);
        }
        catch(Exception e)
        {
            String expectedMessage = "Faulty value for job id was provided";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Wrong value for job id - exception did not throw!");
    }*/

    /* Test case: get job status for not inserted id */
    /*@Test(expected = Exception.class)
    public void testGetJobStatusNotInsertedId() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        OutputStream result = null;
        String jobId = "1";
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getJobStatus", jobId, result, script);
        }
        catch(Exception e)
        {
            String expectedMessage = "Faulty value for job id was provided";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Wrong value for job id - exception did not throw!");
    }*/


}
