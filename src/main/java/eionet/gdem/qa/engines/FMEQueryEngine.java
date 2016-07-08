package eionet.gdem.qa.engines;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

import org.apache.commons.io.IOUtils;
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

import eionet.gdem.GDEMException;
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
            throw new GDEMException(e.toString(), e);
        }
    }

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws GDEMException {

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
                if (response.getStatusLine().getStatusCode() != 200) { // HTTP status code is not 200
                    LOGGER.error("The application has encountered an error. The FME QC process request failed. -- Source file: " + script.getOrigFileUrl() + " -- FME workspace: " + script.getScriptSource() + " -- Response: " + response.toString());
                    throw new Exception("The application has encountered an error. The FME QC process request failed.");
                } else {
                    HttpEntity entity = response.getEntity();
                    // We get an InputStream and copy it to the 'result' OutputStream
                    IOUtils.copy(entity.getContent(), result);
                }
                count = retries;
            } catch (SocketTimeoutException e) { // Timeout Exceeded
                LOGGER.warn("The FME request has exceeded the allotted timeout. -- Source file: " + script.getOrigFileUrl() + " -- FME workspace: " + script.getScriptSource());
            } catch (Exception e) {
                LOGGER.warn("FME request error: " + e.getMessage());
            } finally {
                if (runMethod != null) {
                    runMethod.releaseConnection();
                }
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
                token_ = entity.getContent().toString();
            } else {
                LOGGER.error("FME authentication failed. Could not retrieve a Token");
                throw new GDEMException("FME authentication failed");
            }        	
        } catch (Exception e) {
            throw new GDEMException(e.toString(), e);
        } finally {
            if (method != null) {
            	method.releaseConnection();
            }
        }
        
    }

}
