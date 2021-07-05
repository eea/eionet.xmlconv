package eionet.gdem.services.fme;

import eionet.gdem.Properties;
import eionet.gdem.qa.XQScript;
import eionet.gdem.services.fme.exceptions.*;
import eionet.gdem.services.fme.request.HttpRequestHeader;
import eionet.gdem.services.fme.request.SubmitJobRequest;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.ZipUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class FmeServerCommunicatorImpl implements FmeServerCommunicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FmeServerCommunicatorImpl.class);
    private String fmeTokenProperty = Properties.fmeToken;
    private String fmePollingUrl = Properties.fmePollingUrl;
    private String fmeResultFolderUrlProperty = Properties.fmeResultFolderUrl;
    private String fmeResultFolderProperty = Properties.fmeResultFolder;
    private String tmpFolderProperty = Properties.getTmpFolder() + File.separatorChar;
    private String fmeDeleteFolderUrlProperty = Properties.fmeDeleteFolderUrl;

    private ApacheHttpClientWrapper clientWrapper;
    private static final String JSON_STATUS_PARAM="status";
    private static final String FME_TOKEN_HEADER="fmetoken token=";
    private static final String JSON_JOB_ID_PARAM="id";
    private static final String MEDIA_TYPE_JSON="application/json";
    private static final String APPLICATION_ZIP="application/zip";
    private static final String CONTENT_TYPE_FORM_URLENCODED="application/x-www-form-urlencoded";

    @Autowired
    public FmeServerCommunicatorImpl(@Qualifier("fmeApacheHttpClient") ApacheHttpClientWrapper clientWrapper) {
        this.clientWrapper = clientWrapper;
    }

    @Override
    public String submitJob(XQScript script, SubmitJobRequest submitJobRequest) throws FmeAuthorizationException, FmeCommunicationException {
        if (script == null) {
            throw new IllegalArgumentException("XQScript is empty");
        } else {
            if (script.getScriptSource() == null) {
                throw new IllegalArgumentException("XQScript source file is empty");
            }
            if (script.getOrigFileUrl() == null) {
                throw new IllegalArgumentException("XML file was not provided");
            }
        }
        String convertersJobId = script.getJobId();
        String message = "Began asynchronous job submission in FME for script " + script.getScriptSource();
        if (!Utils.isNullStr(convertersJobId)){
            message += " Converters JobId is " + convertersJobId;
        }
        LOGGER.info(message);
        HttpPost postMethod = null;
        CloseableHttpResponse response = null;
        String jobId = null;
        try {

            postMethod = new HttpPost(new URI(script.getScriptSource()));
            Header[] headers = new HttpRequestHeader.Builder().createHeader(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_JSON).
                    createHeader(HttpHeaders.ACCEPT, MEDIA_TYPE_JSON).createHeader(HttpHeaders.AUTHORIZATION, FME_TOKEN_HEADER + fmeTokenProperty).build().getHeaders();
            postMethod.setHeaders(headers);

            StringEntity params6 = new StringEntity(submitJobRequest.buildBody());
            postMethod.setEntity(params6);

            response = this.clientWrapper.getClient().execute(postMethod);

            Integer statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new FmeAuthorizationException("Unauthorized token");
            } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                throw new FmeCommunicationException("The workspace or repository does not exist");
            } else if (statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
                throw new FmeCommunicationException("Some or all of the input parameters are invalid");
            } else {
                if (statusCode != HttpStatus.SC_ACCEPTED) {
                    String errorMsg = "Received status code " + statusCode + " for job submission request.";
                    if (!Utils.isNullStr(convertersJobId)){
                        errorMsg += " Converters JobId is " + convertersJobId;
                    }
                    throw new FmeCommunicationException(errorMsg);
                }
            }

            JSONObject jsonResponse = ApacheHttpClientUtils.getJsonFromResponseEntity(response.getEntity());
            jobId = jsonResponse.get(JSON_JOB_ID_PARAM).toString();
            if (jobId == null || jobId.isEmpty() || jobId.equals("null")) {
                throw new FmeCommunicationException("Valid status code but no job ID was retrieved");
            }
            String logMessage = "Job was submitted in FME for script " + script.getScriptSource() + " with FME job id " + jobId;
            if (!Utils.isNullStr(convertersJobId)){
                logMessage += " Converters JobId is " + convertersJobId;
            }
            LOGGER.info(logMessage);

        } catch (URISyntaxException | HttpRequestHeaderInitializationException |IOException e) {
            throw new FmeCommunicationException(e.getMessage());
        } finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
        }
        return jobId;
    }

    @Override
    public FmeJobStatus getJobStatus(String jobId, XQScript script) throws FmeAuthorizationException, FmeCommunicationException ,GenericFMEexception ,FMEBadRequestException{
        HttpGet getMethod = null;
        CloseableHttpResponse response = null;

        try {
            getMethod = new HttpGet(new URI(this.fmePollingUrl + jobId));
            Header[] headers = new HttpRequestHeader.Builder().createHeader(HttpHeaders.ACCEPT, MEDIA_TYPE_JSON).
                    createHeader(HttpHeaders.AUTHORIZATION, FME_TOKEN_HEADER + fmeTokenProperty).build().getHeaders();
            getMethod.setHeaders(headers);

            response = this.clientWrapper.getClient().execute(getMethod);
            Integer statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new FmeAuthorizationException("Unauthorized token");
            }else if(statusCode == HttpStatus.SC_NOT_FOUND){
                throw new FMEBadRequestException("The job does not exist");

            } else if (statusCode == HttpStatus.SC_OK) {
                JSONObject jsonResponse = ApacheHttpClientUtils.getJsonFromResponseEntity(response.getEntity());
                if (jsonResponse.get(JSON_STATUS_PARAM) != null) {
                    return FmeJobStatus.valueOf(jsonResponse.get(JSON_STATUS_PARAM).toString());
                } else {
                    String message = "Received wrong response status " + jsonResponse.get("status");
                    throw new FmeCommunicationException(message);
                }
            }else {
                // NOT Valid status code
                String message = "Error when polling for job status. Received status code: " + statusCode;
                throw new GenericFMEexception(message);
            }
        } catch (URISyntaxException | HttpRequestHeaderInitializationException | IOException e) {
            LOGGER.error(e.getMessage());
            throw new GenericFMEexception(e);
        } finally {
            if (getMethod != null) {
                getMethod.releaseConnection();
            }
        }
    }

    @Override
    public void getResultFiles(String folderName, OutputStream result) throws FmeAuthorizationException  , FMEBadRequestException , GenericFMEexception {
        LOGGER.info("Began downloading folder " + folderName);
        HttpPost postMethod = null;
        CloseableHttpResponse response = null;
        try {

            postMethod = new HttpPost(new URI(fmeResultFolderUrlProperty + fmeResultFolderProperty + "/" + folderName));

            Header[] headers = new HttpRequestHeader.Builder().createHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_FORM_URLENCODED).createHeader(HttpHeaders.ACCEPT, APPLICATION_ZIP).
                    createHeader(HttpHeaders.AUTHORIZATION, FME_TOKEN_HEADER + fmeTokenProperty).build().getHeaders();
            postMethod.setHeaders(headers);


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("folderNames","."));
            nameValuePairs.add(new BasicNameValuePair("zipFileName","htmlfiles.zip"));
            postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            response = this.clientWrapper.getClient().execute(postMethod);
            Integer statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED){
                throw new FmeAuthorizationException("Unauthorized token");
            }
            else if (statusCode == HttpStatus.SC_NOT_FOUND){
                throw new FMEBadRequestException("The resource connection or directory does not exist");
            }
            else if (statusCode == HttpStatus.SC_CONFLICT){
                throw new FMEBadRequestException("The resource connection is not a type of resource that can be downloaded");
            }
            else {
                if (statusCode != HttpStatus.SC_OK){
                    String message = "Received status code " + statusCode + " for folder downloading";
                    throw new FMEBadRequestException(message);
                }
            }
            //status code is HttpStatus.SC_OK (200)
            LOGGER.info("Received status code 200 when downloading folder " + folderName);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            //Store zip file in tmp folder
            String folderPath =  tmpFolderProperty + folderName;
            FileOutputStream fos = new FileOutputStream(new File(folderPath+".zip"));
            int inByte;
            while((inByte = is.read()) != -1)
                fos.write(inByte);
            fos.close();

            File zipFile = new File(folderPath+".zip");
            IOUtils.copy(new FileInputStream(zipFile),result);
            LOGGER.info("Copied content of " + folderName + ".zip" + " to stream");
            Utils.deleteFolder(folderPath+".zip");
            LOGGER.info("Deleted folder " + folderPath + ".zip");
            LOGGER.info("Finished downloading folder " + folderName + " from FME");

        }  catch (URISyntaxException | HttpRequestHeaderInitializationException | IOException e) {
            throw new GenericFMEexception(e.getMessage());
        } finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
        }
    }

    @Override
    public void deleteFolder (String folderName) throws FmeAuthorizationException , GenericFMEexception  ,FMEBadRequestException{
        LOGGER.info("Began deleting folder " + folderName);
        HttpDelete request = null;
        CloseableHttpResponse response = null;
        try {
            java.net.URI uri = new URIBuilder(fmeDeleteFolderUrlProperty + fmeResultFolderProperty + "/" + folderName)
                    .build();
            request = new HttpDelete(uri);
            Header[] headers = new HttpRequestHeader.Builder().createHeader(HttpHeaders.ACCEPT, MEDIA_TYPE_JSON).
                    createHeader(HttpHeaders.AUTHORIZATION, FME_TOKEN_HEADER + fmeTokenProperty).build().getHeaders();
            request.setHeaders(headers);

            response = this.clientWrapper.getClient().execute(request);
            Integer statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED){
                throw new FmeAuthorizationException("Unauthorized token");
            }
            else if (statusCode == HttpStatus.SC_NOT_FOUND){
                throw new FMEBadRequestException("The resource connection or path does not exist");
            }
            else {
                if (statusCode != HttpStatus.SC_NO_CONTENT){
                    String message = "Received status code " + statusCode + " for folder deletion";
                    throw new GenericFMEexception(message);
                }
            }
            //status code is HttpStatus.SC_NO_CONTENT (204)
            LOGGER.info("Deleted folder " + folderName);
        }  catch (GenericFMEexception | URISyntaxException | HttpRequestHeaderInitializationException |IOException e) {
            throw new GenericFMEexception(e.getMessage());
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
        }
    }
}
