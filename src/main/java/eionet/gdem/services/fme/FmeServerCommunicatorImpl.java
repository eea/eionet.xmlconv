package eionet.gdem.services.fme;

import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.qa.engines.FMEQueryEngine;
import eionet.gdem.services.fme.request.HttpRequestHeader;
import eionet.gdem.services.fme.request.SynchronousSubmitJobRequest;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.jooq.tools.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.Basic;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Service
public class FmeServerCommunicatorImpl implements FmeServerCommunicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FmeServerCommunicatorImpl.class);
    private String fmeTokenProperty = Properties.fmeToken;

    private ApacheHttpClientWrapper clientWrapper;

    @Autowired
    public FmeServerCommunicatorImpl(@Qualifier("fmeApacheHttpClient")ApacheHttpClientWrapper clientWrapper) {
        this.clientWrapper = clientWrapper;
    }

    @Override
    public String submitJob(XQScript script, SynchronousSubmitJobRequest synchronousSubmitJobRequest) throws Exception {
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
        HttpPost postMethod = null;
        CloseableHttpResponse response = null;
        String jobId = null;
        try {

            postMethod = new HttpPost(new URI(script.getScriptSource()));
            Header[] headers =     new HttpRequestHeader.Builder<BasicHeader>(BasicHeader.class).createHeader("Content-type","application/json").
                   createHeader("Accept", "application/json").createHeader(HttpHeaders.AUTHORIZATION, "fmetoken token="+fmeTokenProperty).build().getHeaders();
            postMethod.setHeaders(headers);
            postMethod.setEntity(synchronousSubmitJobRequest.build());

            response = this.clientWrapper.getClient().execute(postMethod);

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

            Map<String,String> jsonResultMap = ApacheHttpClientUtils.convertHttpEntityToMap(response.getEntity());
            String jsonStr = EntityUtils.toString(response.getEntity());
            org.json.JSONObject jsonResponse = new org.json.JSONObject(jsonStr);
            jobId = jsonResultMap.get("id");
            if(jobId == null || jobId.isEmpty()|| jobId.equals("null")){
                throw new Exception("Valid status code but no job ID was retrieved");
            }
            LOGGER.info(String.format("Job was submitted in FME for script %s with id %s", script.getScriptSource(), jobId));

        }  catch (IOException e) {
            throw new Exception(e.getMessage());
        } finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
        }
        return jobId;
    }
}
