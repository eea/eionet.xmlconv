package eionet.gdem.qa.engines;

import eionet.gdem.qa.XQScript;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jooq.tools.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


import java.io.OutputStream;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FMEQueryEngine.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*", "org.w3c.dom.*", "sun.security.*", "javax.net.ssl.*"})
public class FMEQueryEngineTest {

    @Mock
    private FMEQueryEngine fmeQueryEngine;

    private CloseableHttpClient client_ = HttpClients.createDefault();

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

    }

    /* Test case: successful retrieval of token */
    @Test
    public void testGetConnectionInfoSuccessful() throws Exception {
        Whitebox.invokeMethod(fmeQueryEngine, "getConnectionInfo");
        Assert.assertThat(fmeQueryEngine.getToken_(), is(notNullValue()));
    }

    /* Test case: Wrong FME host */
    @Test(expected = Exception.class)
    public void testGetConnectionInfoWrongFMEHost() throws Exception {
        when(fmeQueryEngine.getFmeHostProperty()).thenReturn("wrongFmeHost");
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getConnectionInfo");
        }
        catch(Exception e)
        {
            String expectedMessage = "wrongFmeHost: Name or service not known";
            Assert.assertThat(e.getMessage(), containsString(expectedMessage));
            throw e;
        }
        fail("Wrong fme host - exception did not throw!");
    }

    /* Test case: Wrong FME username */
    @Test(expected = Exception.class)
    public void testGetConnectionInfoWrongFMEUser() throws Exception {
        when(fmeQueryEngine.getFmeUserProperty()).thenReturn("wrongUsername");
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getConnectionInfo");
        }
        catch(Exception e)
        {
            String expectedMessage = "FME authentication failed";
            Assert.assertThat(e.getMessage(), containsString(expectedMessage));
            throw e;
        }
        fail("Wrong fme host - exception did not throw!");
    }

    /* Test case: Wrong FME password */
    @Test(expected = Exception.class)
    public void testGetConnectionInfoWrongFMEPassword() throws Exception {
        when(fmeQueryEngine.getFmePasswordProperty()).thenReturn("wrongPassword");
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getConnectionInfo");
        }
        catch(Exception e)
        {
            String expectedMessage = "FME authentication failed";
            Assert.assertThat(e.getMessage(), containsString(expectedMessage));
            throw e;
        }
        fail("Wrong fme host - exception did not throw!");
    }

    /* Test case: Wrong FME token expiration value */
    @Test(expected = Exception.class)
    public void testGetConnectionInfoWrongFMEExpirationValue() throws Exception {
        when(fmeQueryEngine.getFmeTokenExpirationProperty()).thenReturn("wrongValue");
        try
        {
            Whitebox.invokeMethod(fmeQueryEngine, "getConnectionInfo");
        }
        catch(Exception e)
        {
            String expectedMessage = "FME authentication failed";
            Assert.assertThat(e.getMessage(), containsString(expectedMessage));
            throw e;
        }
        fail("Wrong fme token expiration value - exception did not throw!");
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

    /* Test case: job submission with null token */
    @Test(expected = Exception.class)
    public void testSubmitJobToFMENullToken() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        when(fmeQueryEngine.getFmeTokenExpirationProperty()).thenReturn(null);
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

    /*This was commented due to not inserting a previously valid token */
    /*@Test(expected = Exception.class)
    public void testsubmitJobToFMECExpiredToken() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        when(fmeQueryEngine.getToken_()).thenReturn("");
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
    }*/

    /* Test case: job submission with wrong token */
    @Test(expected = Exception.class)
    public void testSubmitJobToFMEWrongToken() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        when(fmeQueryEngine.getToken_()).thenReturn("wrongToken");
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

    /*This was commented due to not inserting a valid token */
    /* Test case: successful job submission */
    /*@Test
    public void testsubmitJobToFMECSuccessful() throws Exception {
        XQScript script = new XQScript("https://fme.discomap.eea.europa.eu/fmerest/v3/transformations/submit/ReportNetTesting/sample_call2.fmw", null, null);
        script.setSrcFileUrl("https://cdr.eionet.europa.eu/se/eu/dwd/envw9mv4a/WISE_DWD_SE_2014_DWD_MS.xml");
        when(fmeQueryEngine.getToken_()).thenReturn("");

        String jobId = Whitebox.invokeMethod(fmeQueryEngine, "submitJobToFME", script);
        Assert.assertThat(jobId, is(notNullValue()));
        Assert.assertThat(StringUtils.isNumeric(jobId), is(true));
    }*/

    /* Test case: get job status for wrong job id */
    @Test(expected = Exception.class)
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
    }

    /* Test case: get job status for not inserted id */
    @Test(expected = Exception.class)
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
    }


}
