package eionet.gdem.qa.engines;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.qa.XQScript;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Execute an FME query. Runs synchronously.
 *
 * @author Bilbomatica
 */
public class FMEQueryEngine extends QAScriptEngineStrategy {

    private static final Log LOGGER = LogFactory.getLog(FMEQueryEngine.class);

    private CloseableHttpClient client_ = null;

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

        try {
            getConnectionInfo();
        } catch (IOException e) {
            throw new GDEMException(e.toString(), e);
        }
    }

    @Override
    protected void runQuery(XQScript script, OutputStream result)
            throws GDEMException {

        HttpPost runMethod = null;
        CloseableHttpResponse response = null;
        try {
            java.net.URI uri = new URIBuilder(script.getScriptSource())
                .addParameter("token", token_)
                .addParameter("opt_showresult", "true")
                .addParameter("opt_servicemode", "sync")
                .addParameter("source_xml", script.getOrigFileUrl()) // XML file
                .addParameter("format", script.getOutputType())
                .build(); // Output format
            runMethod = new HttpPost(uri);
             response = client_.execute(runMethod);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOGGER.error("FME workspace failed: " + script.getScriptSource());
                throw new Exception("FME workspace failed");
            } else {
                HttpEntity entity = response.getEntity();
                // We get an InputStream and copy it to the 'result' OutputStream
                IOUtils.copy(entity.getContent(), result);
            }

        } catch (Exception e) {
            throw new GDEMException(e.toString(), e);
        } finally {
            if (runMethod != null) {
                runMethod.releaseConnection();
            }
        }

    }

    /**
     * Gets a user token from the FME server.
     *
     * @throws Exception If an error occurs.
     */
    private void getConnectionInfo() throws Exception {

        // We must first generate a security token for authentication
        // purposes
        fmeUrl = "http://" + Properties.fmeHost + ":" + Properties.fmePort
                + "/fmetoken/generate";

        java.net.URI uri = new URIBuilder(fmeUrl)
            .addParameter("user", Properties.fmeUser)
            .addParameter("password", Properties.fmePassword)
            .addParameter("expiration", Properties.fmeTokenExpiration)
            .addParameter("timeunit", Properties.fmeTokenTimeunit).build();
        HttpPost method = new HttpPost(uri);
        CloseableHttpResponse response = client_.execute(method);
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            token_ = entity.getContent().toString();
        } else {
            LOGGER.error("FME authentication failed. Could not retrieve a Token");
            throw new GDEMException("FME authentication failed");
        }

    }

}
