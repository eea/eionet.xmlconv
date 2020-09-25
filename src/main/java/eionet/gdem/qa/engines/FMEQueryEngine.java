package eionet.gdem.qa.engines;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import eionet.gdem.SpringApplicationContext;
import eionet.gdem.services.fme.FmeServerCommunicator;
import eionet.gdem.services.fme.FmeServerCommunicatorImpl;
import eionet.gdem.services.fme.request.SynchronousSubmitJobRequest;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.ZipUtil;
import net.sf.json.JSONArray;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
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

    private String fmeUrl = null;

    private Integer retries = 0;

    private String randomStr = RandomStringUtils.randomAlphanumeric(5);


    /* Variables for eionet.gdem.Properties*/
    private Integer fmeTimeoutProperty = Properties.fmeTimeout;
    private String fmeHostProperty = Properties.fmeHost;
    private String fmePortProperty = Properties.fmePort;
    private String fmeTokenExpirationProperty = Properties.fmeTokenExpiration;
    private String fmeTokenTimeunitProperty = Properties.fmeTokenTimeunit;
    private String fmePollingUrlProperty = Properties.fmePollingUrl;
    private Integer fmeRetryHoursProperty = Properties.fmeRetryHours;
    private String fmeTokenProperty = Properties.fmeToken;
    private String fmeResultFolderUrlProperty = Properties.fmeResultFolderUrl;
    private String fmeResultFolderProperty = Properties.fmeResultFolder;
    private String fmeDeleteFolderUrlProperty = Properties.fmeDeleteFolderUrl;
    private String tmpFolderProperty = Properties.getTmpFolder() + File.separatorChar;

    /**
     * Default constructor.
     * @throws Exception If an error occurs.
     */
    public FMEQueryEngine() throws Exception {
        client_ = HttpClients.createDefault();

        requestConfigBuilder = RequestConfig.custom();
        requestConfigBuilder.setSocketTimeout(this.getFmeTimeoutProperty());
    }

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws IOException {

        try {
            String[] urlSegments = script.getOrigFileUrl().split("/");
            String fileNameWthXml = urlSegments[urlSegments.length-1];
            String[] fileNameSegments = fileNameWthXml.split("\\.");
            String fileName = fileNameSegments[0];
            String folderName = fileName + "_" +  getRandomStr();

            FmeServerCommunicator fmeServerCommunicator = this.getFmeServerCommunicator();
            String jobId =     fmeServerCommunicator.submitJob(script,new SynchronousSubmitJobRequest(script.getScriptSource(),folderName));
            //String jobId2 = submitJobToFME(script, folderName);

            getJobStatus(jobId, script);
            getResultFiles(folderName, result);
            deleteFolder(folderName);
        } catch (Exception e) {
            String message = "Generic Exception handling. FME request error: " + e.getMessage();
            LOGGER.error(message);
            IOUtils.copy(IOUtils.toInputStream("<div class=\"feedbacktext\"><span id=\"feedbackStatus\" class=\"BLOCKER\" style=\"display:none\">The QC process failed. Please try again. If the issue persists please contact the dataflow helpdesk.</span>The QC process failed. Please try again. If the issue persists please contact the dataflow helpdesk.</div>", "UTF-8"), result);
        }
    }

    private String submitJobToFME (XQScript script, String folderName) throws Exception {
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
            String headerAuthorizationValue = "fmetoken token="+this.getFmeTokenProperty();
            Header[] headers = {
                    new BasicHeader("Content-type", "application/json"),
                    new BasicHeader("Accept", "application/json"),
                    new BasicHeader(HttpHeaders.AUTHORIZATION, headerAuthorizationValue)
            };
            request.setHeaders(headers);
            JSONObject jsonParams = createJSONObjectForJobSubmission(script.getOrigFileUrl(), folderName);
            StringEntity params = new StringEntity(jsonParams.toString());
            request.setEntity(params);

            response = this.getClient_().execute(request);
            Integer statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED){
                throw new Exception("Unauthorized token");
            }
            else if (statusCode == HttpStatus.SC_NOT_FOUND){
                throw new Exception("The workspace or repository does not exist");
            }
            else if (statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY){
                throw new Exception("Some or all of the input parameters are invalid");
            }
            else {
                if (statusCode != HttpStatus.SC_ACCEPTED){
                    String message = "Received status code " + statusCode + " for job submission request";
                    throw new Exception(message);
                }
            }
            //status code is HttpStatus.SC_ACCEPTED (202)
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

    private JSONObject createJSONObjectForJobSubmission(String xmlSourceFile, String folderName){
        JSONObject folderParams = new JSONObject();
        folderParams.put("name", "folder");
        folderParams.put("value", this.getFmeResultFolderProperty() + "/" +folderName);

        JSONObject xmlParams = new JSONObject();
        xmlParams.put("name", "envelopepath");
        xmlParams.put("value", xmlSourceFile);

        JSONArray ja = new JSONArray();
        ja.add(folderParams);
        ja.add(xmlParams);

        JSONObject joPublishedParams = new JSONObject();
        joPublishedParams.put("publishedParameters", ja);
        return joPublishedParams;
    }

    private void getJobStatus(String jobId, XQScript script) throws Exception {
        LOGGER.info("Began polling for status of job #" + jobId);
        String url = this.getFmePollingUrlProperty() + jobId;
        String headerAuthorizationValue = "fmetoken token="+this.getFmeTokenProperty();
        HttpGet httpGet = new HttpGet(url);
        Header[] headers = {
                new BasicHeader("Accept", "application/json"),
                new BasicHeader(HttpHeaders.AUTHORIZATION, headerAuthorizationValue)
        };
        httpGet.setHeaders(headers);

        int count = 0;
        int retryMilisecs = this.getFmeRetryHoursProperty() * 60 * 60 * 1000;
        int timeoutMilisecs = this.getFmeTimeoutProperty();
        this.setRetries(retryMilisecs / timeoutMilisecs);
        LOGGER.debug(String.format("The number of retries of polling for status of job #%s is %d", jobId, this.getRetries()));

        while (count < this.getRetries()) {
            LOGGER.info(String.format("Retry %d for polling for status of job #%s", count, jobId));
            try (CloseableHttpResponse response = this.getClient_().execute(httpGet)) {

                Integer statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_UNAUTHORIZED){
                    throw new Exception("Unauthorized token");
                }
                else if (statusCode == HttpStatus.SC_OK) { // Valid Result
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

                        count = this.getRetries();
                    }
                    else {
                        String message = "Received wrong response status "+ jsonResponse.get("status");
                        throw new Exception(message);
                    }
                } else if(statusCode == HttpStatus.SC_NOT_FOUND){
                    throw new Exception("The job does not exist");
                }
                else { // NOT Valid status code
                    String message = "Error when polling for job status. Received status code: " + statusCode;
                    throw new Exception(message);
                }
            } catch (SocketTimeoutException ste) { // Timeout Exceeded
                LOGGER.error("Retries = "+count+" The FME request has exceeded the allotted timeout of :"+getFmeTimeoutProperty()+" -- Source file: " + script.getOrigFileUrl() + " -- FME workspace: " + script.getScriptSource());
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

    private void getResultFiles (String folderName, OutputStream result) throws Exception {
        LOGGER.info("Began downloading folder " + folderName);
        HttpPost request = null;
        CloseableHttpResponse response = null;
        try {
            java.net.URI uri = new URIBuilder(this.getFmeResultFolderUrlProperty() + this.getFmeResultFolderProperty() + "/" + folderName)
                    .build();
            request = new HttpPost(uri);
            String headerAuthorizationValue = "fmetoken token="+this.getFmeTokenProperty();
            Header[] headers = {
                    new BasicHeader("Content-type", "application/x-www-form-urlencoded"),
                    new BasicHeader("Accept", "application/zip"),
                    new BasicHeader(HttpHeaders.AUTHORIZATION, headerAuthorizationValue)
            };
            request.setHeaders(headers);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("folderNames","."));
            nameValuePairs.add(new BasicNameValuePair("zipFileName","htmlfiles.zip"));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            response = this.getClient_().execute(request);
            Integer statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED){
                throw new Exception("Unauthorized token");
            }
            else if (statusCode == HttpStatus.SC_NOT_FOUND){
                throw new Exception("The resource connection or directory does not exist");
            }
            else if (statusCode == HttpStatus.SC_CONFLICT){
                throw new Exception("The resource connection is not a type of resource that can be downloaded");
            }
            else {
                if (statusCode != HttpStatus.SC_OK){
                    String message = "Received status code " + statusCode + " for folder downloading";
                    throw new Exception(message);
                }
            }
            //status code is HttpStatus.SC_OK (200)
            LOGGER.info("Received status code 200 when downloading folder " + folderName);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            //Store zip file in tmp folder
            String folderPath = getTmpFolderProperty() + folderName;
            FileOutputStream fos = new FileOutputStream(new File(folderPath+".zip"));
            int inByte;
            while((inByte = is.read()) != -1)
                fos.write(inByte);
            fos.close();

            //Extract folder in tmp folder and delete zip file
            ZipUtil.unzip(folderPath+".zip", folderPath);
            File zipFile = new File(folderPath+".zip");
         //   zipFile.delete();
            LOGGER.info("Extracted and deleted " + folderPath + ".zip");

            // Get all the names of the files present in the given directory
            File folder = new File(folderPath);
            if(!folder.isDirectory()){
                String errorMsg = getTmpFolderProperty() + folderPath + " is not a directory";
                throw new Exception(errorMsg);
            }
            List<String> listFile = Arrays.asList(folder.list());
            Collections.sort(listFile);
            LOGGER.info("Files found in directory " + folderPath + " are: " + listFile);

            //Copy content of html file to OutputStream
        //    InputStream fileContent = new FileInputStream(getTmpFolderProperty() + folderName + "/" + listFile.get(0));
          //  IOUtils.copy(fileContent, result);
            IOUtils.copy(new FileInputStream(zipFile),result);
            LOGGER.info("Copied file " + listFile.get(0) + " to stream");

            //Delete created folder
            Utils.deleteFolder(folderPath);
            LOGGER.info("Deleted folder " + folderPath);
            LOGGER.info("Finished downloading folder " + folderName + " from FME");

        }  catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
        }
    }

    private void deleteFolder (String folderName) throws Exception {
        LOGGER.info("Began deleting folder " + folderName);
        HttpDelete request = null;
        CloseableHttpResponse response = null;
        try {
            java.net.URI uri = new URIBuilder(this.getFmeDeleteFolderUrlProperty() + this.getFmeResultFolderProperty() + "/" + folderName)
                    .build();
            request = new HttpDelete(uri);
            String headerAuthorizationValue = "fmetoken token="+this.getFmeTokenProperty();
            Header[] headers = {
                    new BasicHeader("Accept", "application/json"),
                    new BasicHeader(HttpHeaders.AUTHORIZATION, headerAuthorizationValue)
            };
            request.setHeaders(headers);

            response = this.getClient_().execute(request);
            Integer statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED){
                throw new Exception("Unauthorized token");
            }
            else if (statusCode == HttpStatus.SC_NOT_FOUND){
                throw new Exception("The resource connection or path does not exist");
            }
            else {
                if (statusCode != HttpStatus.SC_NO_CONTENT){
                    String message = "Received status code " + statusCode + " for folder deletion";
                    throw new Exception(message);
                }
            }
            //status code is HttpStatus.SC_NO_CONTENT (204)
            LOGGER.info("Deleted folder " + folderName);
        }  catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
        }
    }

    protected CloseableHttpClient getClient_() {
        return client_;
    }

    protected static Builder getRequestConfigBuilder() {
        return requestConfigBuilder;
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

    protected String getFmeTokenProperty() {
        return fmeTokenProperty;
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

    public String getFmeResultFolderUrlProperty() {
        return fmeResultFolderUrlProperty;
    }

    public String getFmeResultFolderProperty() {
        return fmeResultFolderProperty;
    }

    public String getFmeDeleteFolderUrlProperty() {
        return fmeDeleteFolderUrlProperty;
    }

    protected Integer getRetries() {
        return retries;
    }

    private void setRetries(Integer retries) {
        this.retries = (retries <= 0) ? 1 : retries;
    }

    public String getTmpFolderProperty() {
        return tmpFolderProperty;
    }

    public String getRandomStr() {
        return randomStr;
    }


    public FmeServerCommunicator getFmeServerCommunicator(){
       return (FmeServerCommunicator) SpringApplicationContext.getBean(FmeServerCommunicator.class);
    }
}
