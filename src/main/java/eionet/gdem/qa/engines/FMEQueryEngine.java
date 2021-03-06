package eionet.gdem.qa.engines;

import java.io.*;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import eionet.gdem.SpringApplicationContext;
import eionet.gdem.XMLConvException;
import eionet.gdem.services.fme.FmeJobStatus;
import eionet.gdem.services.fme.FmeServerCommunicator;
import eionet.gdem.services.fme.exceptions.*;
import eionet.gdem.services.fme.request.SynchronousSubmitJobRequest;
import eionet.gdem.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private String synchronousToken = null;


    /* Variables for eionet.gdem.Properties*/
    private Integer fmeTimeoutProperty = Properties.fmeTimeout;
    private String fmeHostProperty = Properties.fmeHost;
    private String fmePortProperty = Properties.fmePort;
    private String fmeTokenExpirationProperty = Properties.fmeTokenExpiration;
    private String fmeTokenTimeunitProperty = Properties.fmeTokenTimeunit;
    private String fmePollingUrlProperty = Properties.fmePollingUrl;
    private Integer fmeRetryHoursProperty = Properties.fmeRetryHours;
    private String fmeTokenProperty = Properties.fmeToken;

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

        if(script.getAsynchronousExecution()){
            LOGGER.info("The script " + script.getScriptFileName() + " will be run asynchronously");
            runQueryAsynchronous(script, result);
        }
        else{
            LOGGER.info("The script " + script.getScriptFileName() + " will be run synchronously");
            runQuerySynchronous(script, result);
        }
    }

    protected void runQuerySynchronous(XQScript script, OutputStream result) {

        HttpPost runMethod = null;
        CloseableHttpResponse response = null;
        int count = 0;
        int retryMilisecs = Properties.fmeRetryHours * 60 * 60 * 1000;
        int timeoutMilisecs = Properties.fmeTimeout;
        int retries = retryMilisecs / timeoutMilisecs;
        retries = (retries <= 0) ? 1 : retries;
        String jobId = script.getJobId();
        while (count < retries) {
            try {
                java.net.URI uri = new URIBuilder(script.getScriptSource())
                        .addParameter("token", synchronousToken)
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
                    String logMessage = FMEQueryEngine.class.getName() + ": Synchronous job exeuction ";
                    if (!Utils.isNullStr(jobId)){
                        logMessage += " for job id " + jobId;
                    }
                    logMessage += " got response 200 OK From FME SERVER in :"+ count +"retry";
                    LOGGER.info(logMessage);
                    IOUtils.copy(entity.getContent(), result);
                } else { // NOT Valid Result
                    // If the last retry fails a BLOCKER predefined error is returned
                    if (count + 1 == retries){
                        String logMessage = FMEQueryEngine.class.getName() + ": Synchronous job exeuction ";
                        if (!Utils.isNullStr(jobId)){
                            logMessage += " for job id " + jobId;
                        }
                        logMessage += " failed with response code " + response.getStatusLine().getStatusCode() + " for last Retry  number :"+ count;
                        LOGGER.error(logMessage);
                        IOUtils.copy(IOUtils.toInputStream("<div class=\"feedbacktext\"><span id=\"feedbackStatus\" class=\"BLOCKER\" style=\"display:none\">The QC Process failed, please allow some time and re-run the process. If the issue persists please contact the dataflow helpdesk.</span>The QC Process failed, please allow some time and re-run the process. Please try again. If the issue persists please contact the dataflow helpdesk.</div>", "UTF-8"), result);
                    } else {

                        String logMessage = FMEQueryEngine.class.getName() + ": Synchronous job exeuction ";
                        if (!Utils.isNullStr(jobId)){
                            logMessage += " for job id " + jobId;
                        }
                        logMessage += " failed with response code " + response.getStatusLine().getStatusCode() + ". The application has encountered an error. The FME QC process request failed. -- Source file: " + script.getOrigFileUrl() + " -- FME workspace: " + script.getScriptSource() + " -- Response: " + response.toString() + "-- #Retry: " + count;
                        LOGGER.error(logMessage);
                        Thread.sleep(timeoutMilisecs); // The thread is forced to wait 'timeoutMilisecs' before trying to retry the FME call
                        throw new Exception("The application has encountered an error. The FME QC process request failed.");
                    }
                }
                count = retries;
            } catch (SocketTimeoutException e) { // Timeout Exceeded
                String logMessage = FMEQueryEngine.class.getName() + ": Synchronous job exeuction ";
                if (!Utils.isNullStr(jobId)){
                    logMessage += " for job id " + jobId;
                }
                logMessage += " failed with SocketTimeoutException. Retries = "+count+"\n The FME request has exceeded the allotted timeout of :"+Properties.fmeTimeout+" -- Source file: " + script.getOrigFileUrl() + " -- FME workspace: " + script.getScriptSource();
                LOGGER.error(logMessage);
            } catch (Exception e) {
                String logMessage = FMEQueryEngine.class.getName() + ": Synchronous job exeuction ";
                if (!Utils.isNullStr(jobId)){
                    logMessage += " for job id " + jobId;
                }
                logMessage += " failed. Generic Exception handling. . FME request error: " + e.getMessage();
                LOGGER.error(logMessage);
            } finally {
                if (runMethod != null) {
                    runMethod.releaseConnection();
                }
                count++;
            }
        }

    }

    protected void runQueryAsynchronous(XQScript script, OutputStream result) throws IOException {
        String[] urlSegments = script.getOrigFileUrl().split("/");
        String fileNameWthXml = urlSegments[urlSegments.length-1];
        String[] fileNameSegments = fileNameWthXml.split("\\.");
        String fileName = fileNameSegments[0];
        String folderName = fileName + "_" +  getRandomStr();
        String jobId="";
        String convertersJobId = script.getJobId();
        try {


            FmeServerCommunicator fmeServerCommunicator = this.getFmeServerCommunicator();
            jobId = fmeServerCommunicator.submitJob(script,new SynchronousSubmitJobRequest(script.getOrigFileUrl(),folderName));


            this.pollFmeServerWithRetries(jobId,script,fmeServerCommunicator);

            fmeServerCommunicator.getResultFiles(folderName, result);
            fmeServerCommunicator.deleteFolder(folderName);
        } catch (FmeAuthorizationException | FmeCommunicationException | GenericFMEexception | FMEBadRequestException |RetryCountForGettingJobResultReachedException | InterruptedException e) {
            String message = "Generic Exception handling ";
            if (!Utils.isNullStr(convertersJobId)){
                message += " for job id " + convertersJobId;
            }
            message += " FME request error: " + e.getMessage();
            LOGGER.error(message);

            String resultStr = createErrorMessage(jobId, script.getScriptSource(), script.getOrigFileUrl(), e.getMessage());

            ZipOutputStream out = new ZipOutputStream(result);
            ZipEntry entry = new ZipEntry("output.html");
            out.putNextEntry(entry);
            byte[] data = resultStr.getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();
            out.close();
        }
    }

    private String createErrorMessage (String fmeJobId, String scriptUrl, String sourceUrl, String exception){
        String resultStringHtml = "<div class=\"feedbacktext\"><span id=\"feedbackStatus\" class=\"BLOCKER\" style=\"display:none\">";
        String resultStringMsg ="The QC Process failed, please allow some time and re-run the process. If the issue persists please contact the dataflow helpdesk. ";
        String resultStringSpecificMsg;
        if (Utils.isNullStr(fmeJobId)){
            resultStringSpecificMsg = "Job submission for script: " + scriptUrl + " and xml url " + sourceUrl + " has failed. ";
        }
        else{
            resultStringSpecificMsg = "The id in the FME server is #" + fmeJobId + ". ";
        }
        String exceptionMsg = "Exception message is: " + exception;
        String fullResultString = resultStringHtml + resultStringMsg + resultStringSpecificMsg + exceptionMsg +  "</span>" ;
        fullResultString += resultStringMsg + resultStringSpecificMsg + exceptionMsg + "</div>";
        return fullResultString;
    }


    protected void pollFmeServerWithRetries(String jobId, XQScript script,FmeServerCommunicator fmeServerCommunicator) throws RetryCountForGettingJobResultReachedException, FMEBadRequestException, FmeCommunicationException, GenericFMEexception, FmeAuthorizationException, InterruptedException {
        int count = 0;
        int retryMilisecs = this.getFmeRetryHoursProperty() * 60 * 60 * 1000;
        int timeoutMilisecs = this.getFmeTimeoutProperty();
        this.setRetries(retryMilisecs / timeoutMilisecs);
        String convertersJobId = script.getJobId();
        while (count < this.getRetries()) {
            String logMessage = "Retry " + count + " for polling for status of FME job " + jobId;
            if (!Utils.isNullStr(convertersJobId)){
                logMessage += " Converters job id is " + convertersJobId;
            }
            LOGGER.info(logMessage);
            FmeJobStatus jobStatus = fmeServerCommunicator.getJobStatus(jobId,script);
            switch (jobStatus){
                case SUBMITTED:
                case PULLED:
                case QUEUED: {
                    if (count + 1 == this.getRetries()) {
                        String message = "Failed for last Retry  number: " + count + ". Received status " + jobStatus.toString();
                        if (!Utils.isNullStr(convertersJobId)){
                            message += " Converters job id is " + convertersJobId;
                        }
                        throw new RetryCountForGettingJobResultReachedException(message);
                    } else {
                        String message = "Fme Request Process is still in progress for  -- Source file: " + script.getOrigFileUrl() + " -- FME workspace: " + script.getScriptSource() + " -- Response: " + jobStatus.toString() + "-- #Retry: " + count;
                        if (!Utils.isNullStr(convertersJobId)){
                            message += " Converters job id is " + convertersJobId;
                        }
                        LOGGER.error(message);
                        Thread.sleep(timeoutMilisecs); // The thread is forced to wait 'timeoutMilisecs' before trying to retry the FME call

                    }
                    count++;
                    LOGGER.info("Retry checking");
                    break;
                }
                case ABORTED:
                case FME_FAILURE:{
                    throw new GenericFMEexception("Received result status FME_FAILURE for job Id #" + jobId);}

                case SUCCESS:
                    return;
            }
        }
        throw new RetryCountForGettingJobResultReachedException("Retry count reached with no result");
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
            fmeUrl = "https://" + Properties.fmeHost + ":" + Properties.fmePort
                    + "/fmetoken/generate";

            java.net.URI uri = new URIBuilder(fmeUrl)
                    .addParameter("user", Properties.fmeUser)
                    .addParameter("password", Properties.fmePassword)
                    .addParameter("expiration", this.getFmeTokenExpirationProperty())
                    .addParameter("timeunit", this.getFmeTokenTimeunitProperty()).build();
            method = new HttpPost(uri);
            response = client_.execute(method);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                synchronousToken = new String(IOUtils.toByteArray(stream), StandardCharsets.UTF_8);
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


    protected Integer getRetries() {
        return retries;
    }

    private void setRetries(Integer retries) {
        this.retries = (retries <= 0) ? 1 : retries;
    }



    public String getRandomStr() {
        return randomStr;
    }


    public FmeServerCommunicator getFmeServerCommunicator(){
       return (FmeServerCommunicator) SpringApplicationContext.getBean(FmeServerCommunicator.class);
    }

}
