package eionet.gdem.qa.engines;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import org.jooq.tools.json.JSONParser;
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

    /**
     * Default constructor.
     * @throws Exception If an error occurs.
     */
    public FMEQueryEngine() throws Exception {
        client_ = HttpClients.createDefault();        
        
        requestConfigBuilder = RequestConfig.custom();
        requestConfigBuilder.setSocketTimeout(Properties.fmeTimeout);

        try {
            getConnectionInfo();
        } catch (IOException e) {
            throw new XMLConvException(e.toString(), e);
        }
    }

    /*@Override
    protected void runQuery(XQScript script, OutputStream result) throws XMLConvException {

        HttpPost runMethod = null;
        CloseableHttpResponse response = null;
        int count = 0;
        int retryMilisecs = Properties.fmeRetryHours * 60 * 60 * 1000;
        int timeoutMilisecs = Properties.fmeTimeout;
        int retries = retryMilisecs / timeoutMilisecs;
        retries = (retries <= 0) ? 1 : retries;
        while (count < retries) {
            try {
                java.net.URI uri = new URIBuilder(script.getScriptSource())
                        .addParameter("token", token_)
                        .addParameter("opt_showresult", "true")
                        .addParameter("opt_servicemode", "sync")
                        .addParameter("source_xml", script.getOrigFileUrl()) // XML file
                        .addParameter("format", script.getOutputType())
                        .build(); // Output format
                runMethod = new HttpPost(uri);

                // Request Config (Timeout)
                runMethod.setConfig(requestConfigBuilder.build());
                response = client_.execute(runMethod);
                if (response.getStatusLine().getStatusCode() == 200) { // Valid Result: 200 HTTP status code
                	HttpEntity entity = response.getEntity();
                    // We get an InputStream and copy it to the 'result' OutputStream
                    LOGGER.info(FMEQueryEngine.class.getName() +": Response 200 OK From FME SERVER in :"+ count +"retry");
                    IOUtils.copy(entity.getContent(), result);
                } else { // NOT Valid Result
                    // If the last retry fails a BLOCKER predefined error is returned
                    if (count + 1 == retries){
                        LOGGER.error(FMEQueryEngine.class.getName() +" Failed for last Retry  number :"+ count );

                        IOUtils.copy(IOUtils.toInputStream("<div class=\"feedbacktext\"><span id=\"feedbackStatus\" class=\"BLOCKER\" style=\"display:none\">The QC process failed. Please try again. If the issue persists please contact the dataflow helpdesk.</span>The QC process failed. Please try again. If the issue persists please contact the dataflow helpdesk.</div>", "UTF-8"), result);
                    } else {                    	

                        LOGGER.error("The application has encountered an error. The FME QC process request failed. -- Source file: " + script.getOrigFileUrl() + " -- FME workspace: " + script.getScriptSource() + " -- Response: " + response.toString() + "-- #Retry: " + count);
                        Thread.sleep(timeoutMilisecs); // The thread is forced to wait 'timeoutMilisecs' before trying to retry the FME call
                        throw new Exception("The application has encountered an error. The FME QC process request failed.");
                    }
                }
                count = retries;
            } catch (SocketTimeoutException e) { // Timeout Exceeded
                LOGGER.error("Retries = "+count+"\n The FME request has exceeded the allotted timeout of :"+Properties.fmeTimeout+" -- Source file: " + script.getOrigFileUrl() + " -- FME workspace: " + script.getScriptSource());
            } catch (Exception e) {
                LOGGER.error("Generic Exception handling. FME request error: " + e.getMessage());
            } finally {
                if (runMethod != null) {
                    runMethod.releaseConnection();
                }
                count++;
            }
        }

    }*/

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws Exception {

        String jobId = submitJobToFME(script);
        getJobStatus(jobId, result, script);

    }

    private String submitJobToFME (XQScript script){
        LOGGER.info("Began asynchronous job submission in FME for script " + script.getScriptSource());
        HttpPost request = null;
        CloseableHttpResponse response = null;
        String jobId = null;
        try {
            java.net.URI uri = new URIBuilder(script.getScriptSource())
                    .build(); // Output format
            request = new HttpPost(uri);
            String headerAuthorizationValue = "fmetoken token="+token_;
            Header[] headers = {
                    new BasicHeader("Content-type", "application/json"),
                    new BasicHeader("Accept", "application/json"),
                    new BasicHeader("Authorization", headerAuthorizationValue)
            };
            request.setHeaders(headers);
            JSONObject jsonParams = createJSONObjectForJobSubmission(script.getOrigFileUrl());
            StringEntity params =new StringEntity(jsonParams.toString());
            request.setEntity(params);

            response = client_.execute(request);
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(new BasicResponseHandler().handleResponse(response));
            jobId = (String) jsonResponse.get("id");

            LOGGER.info(String.format("Job was submitted in FME for script %s with id %s", script.getScriptSource(), jobId));

        }  catch (Exception e) {
            LOGGER.error("Generic Exception handling. FME request error: " + e.getMessage());
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
        }
        return jobId;
    }

    private JSONObject createJSONObjectForJobSubmission(String xmlSourceFile){
        //TODO the following values will be changed
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
        /*
            TODO create a loop where we send request to find out job status as many times as the property in default.properties
                if status is valid then we handle the response the same way it is handled above and we copy it to the result else we have a timeout.
         */

        LOGGER.info("Began polling for status of job #" + jobId);
        String url = Properties.fmePollingUrl + jobId;
        String encoding = Base64.getEncoder().encodeToString((Properties.fmeUser + ":" + Properties.fmePassword).getBytes());
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);

        int count = 0;
        int retryMilisecs = Properties.fmeRetryHours * 60 * 60 * 1000;
        int timeoutMilisecs = Properties.fmeTimeout;
        int retries = retryMilisecs / timeoutMilisecs;
        retries = (retries <= 0) ? 1 : retries;
        LOGGER.debug(String.format("The number of retries of polling for status of job #%s is %d", jobId, retries));

        ArrayList<Integer> acceptedStatuses = new ArrayList<Integer>();
        acceptedStatuses.add(HttpStatus.SC_OK);
        acceptedStatuses.add(HttpStatus.SC_ACCEPTED);

        while (count < retries) {
            LOGGER.info(String.format("Retry %d for polling for status of job #%s", count, jobId));
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(httpPost)) {

                Integer statusCode = response.getStatusLine().getStatusCode();
                if (acceptedStatuses.contains(statusCode)) { // Valid Result
                    HttpEntity entity = response.getEntity();
                    // We get an InputStream and copy it to the 'result' OutputStream
                    LOGGER.info(String.format("Retrieved status %d for polling of job #%d", statusCode, jobId));
                    IOUtils.copy(entity.getContent(), result);
                } else { // NOT Valid Result
                    // If the last retry fails a BLOCKER predefined error is returned
                    if (count + 1 == retries){
                        LOGGER.error("Failed for last Retry  number :"+ count );
                        IOUtils.copy(IOUtils.toInputStream("<div class=\"feedbacktext\"><span id=\"feedbackStatus\" class=\"BLOCKER\" style=\"display:none\">The QC process failed. Please try again. If the issue persists please contact the dataflow helpdesk.</span>The QC process failed. Please try again. If the issue persists please contact the dataflow helpdesk.</div>", "UTF-8"), result);
                    } else {
                        LOGGER.error("The application has encountered an error. The FME QC process request failed. -- Source file: " + script.getOrigFileUrl() + " -- FME workspace: " + script.getScriptSource() + " -- Response: " + response.toString() + "-- #Retry: " + count);
                        Thread.sleep(timeoutMilisecs); // The thread is forced to wait 'timeoutMilisecs' before trying to retry the FME call
                        throw new Exception("The application has encountered an error. The FME QC process request failed.");
                    }
                }
                count = retries;

            } catch (Exception e) {
                throw (e);
            }
            finally {
                count++;
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
            // We must first generate a security token for authentication
            // purposes
            fmeUrl = "http://" + Properties.fmeHost + ":" + Properties.fmePort
                    + "/fmetoken/generate";

            java.net.URI uri = new URIBuilder(fmeUrl)
                .addParameter("user", Properties.fmeUser)
                .addParameter("password", Properties.fmePassword)
                .addParameter("expiration", Properties.fmeTokenExpiration)
                .addParameter("timeunit", Properties.fmeTokenTimeunit).build();
            method = new HttpPost(uri);
            response = client_.execute(method);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                token_ = new String(IOUtils.toByteArray(stream), StandardCharsets.UTF_8);
                IOUtils.closeQuietly(stream);
            } else {
                LOGGER.error("FME authentication failed. Could not retrieve a Token");
                throw new XMLConvException("FME authentication failed");
            }        	
        } catch (Exception e) {
            throw new XMLConvException(e.toString(), e);
        } finally {
            if (method != null) {
            	method.releaseConnection();
            }
        }
        
    }

}
