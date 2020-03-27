package eionet.gdem.qa.engines;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import net.sf.json.JSONArray;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.jooq.tools.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eionet.gdem.XMLConvException;
import eionet.gdem.Properties;
import eionet.gdem.qa.XQScript;

/**
 * Execute an FME query. Runs synchronously.
 *
 * @author Bilbomatica
 */
public class FMEQueryEngine extends QAScriptEngineStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(FMEQueryEngine.class);

    private CloseableHttpClient client_ = null;

    private static Builder requestConfigBuilder = null;

    /**
     * Security token for authentication.
     */
    private String token_ = null;

    private String fmeUrl = null;

    private Integer retries = 0;


    /* Variables for eionet.gdem.Properties*/
    private Integer fmeTimeoutProperty = Properties.fmeTimeout;
    private String fmeHostProperty = Properties.fmeHost;
    private String fmePortProperty = Properties.fmePort;
    private String fmeUserProperty = Properties.fmeUser;
    private String fmePasswordProperty = Properties.fmePassword;
    private String fmeTokenExpirationProperty = Properties.fmeTokenExpiration;
    private String fmeTokenTimeunitProperty = Properties.fmeTokenTimeunit;
    private String fmePollingUrlProperty = Properties.fmePollingUrl;
    private Integer fmeRetryHoursProperty = Properties.fmeRetryHours;

    /**
     * Default constructor.
     * @throws Exception If an error occurs.
     */
    public FMEQueryEngine() throws Exception {
        client_ = HttpClients.createDefault();

        requestConfigBuilder = RequestConfig.custom();
        requestConfigBuilder.setSocketTimeout(this.getFmeTimeoutProperty());
        try {
            getConnectionInfo();
        } catch (IOException e) {
            throw new XMLConvException(e.toString(), e);
        }
    }

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws IOException {

        try {
            String jobId = submitJobToFME(script);
            getJobStatus(jobId, result, script);
        } catch (Exception e) {
            String message = "Generic Exception handling. FME request error: " + e.getMessage();
            LOGGER.error(message);
            IOUtils.copy(IOUtils.toInputStream("<div class=\"feedbacktext\"><span id=\"feedbackStatus\" class=\"BLOCKER\" style=\"display:none\">The QC process failed. Please try again. If the issue persists please contact the dataflow helpdesk.</span>The QC process failed. Please try again. If the issue persists please contact the dataflow helpdesk.</div>", "UTF-8"), result);
        }
    }

    private String submitJobToFME (XQScript script) throws Exception {
        if (script == null){
            throw new Exception("XQScript is empty");
        }
        else{
            if (script.getScriptSource() == null){
                throw new Exception("XQScript source file is empty");
            }
            if (script.getOrigFileUrl() == null){
                throw new Exception("XML file was not provided");
            }
        }

        LOGGER.info("Began asynchronous job submission in FME for script " + script.getScriptSource());
        HttpPost request = null;
        CloseableHttpResponse response = null;
        String jobId = null;
        try {
            java.net.URI uri = new URIBuilder(script.getScriptSource())
                    .build();
            request = new HttpPost(uri);
            String headerAuthorizationValue = "fmetoken token="+this.getToken_();
            Header[] headers = {
                    new BasicHeader("Content-type", "application/json"),
                    new BasicHeader("Accept", "application/json"),
                    new BasicHeader("Authorization", headerAuthorizationValue)
            };
            request.setHeaders(headers);
            JSONObject jsonParams = createJSONObjectForJobSubmission(script.getOrigFileUrl());
            StringEntity params = new StringEntity(jsonParams.toString());
            request.setEntity(params);

            response = this.getClient_().execute(request);
            Integer statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED){
                throw new Exception("Unauthorized token");
            }
            else if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_ACCEPTED){
                String message = "Received status code " + statusCode + " for job submission request";
                throw new Exception(message);
            }
            String jsonStr = EntityUtils.toString(response.getEntity());
            org.json.JSONObject jsonResponse = new org.json.JSONObject(jsonStr);
            jobId = jsonResponse.get("id").toString();
            if(jobId == null || jobId.isEmpty()|| jobId.equals("null")){
                throw new Exception("Valid status code but no job ID was retrieved");
            }

            LOGGER.info(String.format("Job was submitted in FME for script %s with id %s", script.getScriptSource(), jobId));

        }  catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
        }
        return jobId;
    }

    private JSONObject createJSONObjectForJobSubmission(String xmlSourceFile){
        JSONObject joReplyParams = new JSONObject();
        joReplyParams.put("name", "DestDataset_JSON");
        joReplyParams.put("value", "response.json");

        JSONObject joXMLParams = new JSONObject();
        joXMLParams.put("name", "SourceDataset_XML");
        JSONArray jaXMLParams = new JSONArray();
        jaXMLParams.add(xmlSourceFile);
        joXMLParams.put("value", jaXMLParams);
        JSONArray ja = new JSONArray();
        ja.add(joReplyParams);
        ja.add(joXMLParams);

        JSONObject joPublishedParams = new JSONObject();
        joPublishedParams.put("publishedParameters", ja);
        return joPublishedParams;
    }

    private void getJobStatus(String jobId, OutputStream result, XQScript script) throws Exception {
        LOGGER.info("Began polling for status of job #" + jobId);
        String url = this.getFmePollingUrlProperty() + jobId;
        String encoding = Base64.getEncoder().encodeToString((this.getFmeUserProperty() + ":" + this.getFmePasswordProperty()).getBytes());
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);

        int count = 0;
        int retryMilisecs = this.getFmeRetryHoursProperty() * 60 * 60 * 1000;
        int timeoutMilisecs = this.getFmeTimeoutProperty();
        this.setRetries(retryMilisecs / timeoutMilisecs);
        LOGGER.debug(String.format("The number of retries of polling for status of job #%s is %d", jobId, this.getRetries()));

        while (count < this.getRetries()) {
            LOGGER.info(String.format("Retry %d for polling for status of job #%s", count, jobId));
            try (CloseableHttpResponse response = this.getClient_().execute(httpGet)) {

                Integer statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) { // Valid Result
                    HttpEntity entity = response.getEntity();
                    LOGGER.info(String.format("Retrieved status %d for polling of job #%s", statusCode, jobId));

                    String jsonStr = EntityUtils.toString(entity);
                    org.json.JSONObject jsonResponse = new org.json.JSONObject(jsonStr);

                    if(jsonResponse.get("status").equals("SUBMITTED") || jsonResponse.get("status").equals("QUEUED") || jsonResponse.get("status").equals("PULLED")){
                        /* The request will be retried*/
                        if (count + 1 == this.getRetries()){
                            String message = "Failed for last Retry  number: " + count + ". Received status " + jsonResponse.get("status");
                            throw new Exception(message);
                        } else {
                            LOGGER.error("The application has encountered an error. The FME QC process request failed. -- Source file: " + script.getOrigFileUrl() + " -- FME workspace: " + script.getScriptSource() + " -- Response: " + response.toString() + "-- #Retry: " + count);
                            Thread.sleep(timeoutMilisecs); // The thread is forced to wait 'timeoutMilisecs' before trying to retry the FME call
                        }
                    }
                    else if (jsonResponse.get("status").equals("ABORTED") || jsonResponse.get("status").equals("FME_FAILURE")
                    || jsonResponse.get("status").equals("JOB_FAILURE")){
                        String message = "Received response status "+ jsonResponse.get("status");
                        throw new Exception(message);
                    }
                    else if (jsonResponse.get("status").equals("SUCCESS")){
                        //Result status will either be SUCCESS of FME_FAILURE
                        LOGGER.info(String.format("Result status for job id %s is %s", jobId, jsonResponse.getJSONObject("result").get("status")));
                        if (jsonResponse.getJSONObject("result").get("status").equals("FME_FAILURE")){
                            String errorMsg = "Received result status FME_FAILURE for job Id #" + jobId;
                            throw new Exception(errorMsg);
                        }
                        InputStream is = new ByteArrayInputStream(jsonStr.getBytes());
                        IOUtils.copy(is, result);
                        count = this.getRetries();
                    }
                    else {
                        String message = "Received wrong response status "+ jsonResponse.get("status");
                        throw new Exception(message);
                    }
                } else { // NOT Valid Result
                    String message = "Error when polling for job status. Received status code: " + statusCode;
                    throw new Exception(message);
                }
            } catch (Exception e) {
                throw e;
            }
            finally {
                count++;
                if (httpGet != null) {
                    httpGet.releaseConnection();
                }
            }
        }
    }

    /**
     * Gets a user token from the FME server.
     *
     * @throws Exception If an error occurs.
     */
    private void getConnectionInfo() throws Exception {

        HttpPost method = null;
        CloseableHttpResponse response = null;

        try {
            // We must first generate a security token for authentication purposes

            fmeUrl = "http://" + this.getFmeHostProperty() + ":" + this.getFmePortProperty()
                    + "/fmetoken/generate";

            java.net.URI uri = new URIBuilder(fmeUrl)
                    .addParameter("user", this.getFmeUserProperty())
                    .addParameter("password", this.getFmePasswordProperty())
                    .addParameter("expiration", this.getFmeTokenExpirationProperty())
                    .addParameter("timeunit", this.getFmeTokenTimeunitProperty()).build();
            method = new HttpPost(uri);
            response = this.getClient_().execute(method);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                token_ = new String(IOUtils.toByteArray(stream), StandardCharsets.UTF_8);
                IOUtils.closeQuietly(stream);
            } else {
                throw new XMLConvException("FME authentication failed. Could not retrieve a Token");
            }
        } catch (Exception e) {
            throw new XMLConvException(e.toString(), e);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }

    }


    protected CloseableHttpClient getClient_() {
        return client_;
    }

    protected static Builder getRequestConfigBuilder() {
        return requestConfigBuilder;
    }

    protected String getToken_() {
        return token_;
    }

    protected Integer getFmeTimeoutProperty() {
        return fmeTimeoutProperty;
    }

    protected String getFmeHostProperty() {
        return fmeHostProperty;
    }

    protected String getFmePortProperty() {
        return fmePortProperty;
    }

    protected String getFmeUserProperty() {
        return fmeUserProperty;
    }

    protected String getFmePasswordProperty() {
        return fmePasswordProperty;
    }

    protected String getFmeTokenExpirationProperty() {
        return fmeTokenExpirationProperty;
    }

    protected String getFmeTokenTimeunitProperty() {
        return fmeTokenTimeunitProperty;
    }

    protected String getFmePollingUrlProperty() {
        return fmePollingUrlProperty;
    }

    protected Integer getFmeRetryHoursProperty() {
        return fmeRetryHoursProperty;
    }

    protected Integer getRetries() {
        return retries;
    }

    private void setRetries(Integer retries) {
        this.retries = (retries <= 0) ? 1 : retries;
    }
}
