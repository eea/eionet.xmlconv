package eionet.gdem.qa.engines;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.qa.XQScript;

/**
 * Execute an FME query. Runs synchronously.
 *
 * @author Bilbomatica
 */
public class FMEQueryEngine extends QAScriptEngineStrategy {

    private static final Log LOGGER = LogFactory.getLog(FMEQueryEngine.class);

    private HttpClient client_ = null;

    /** Security token for authentication. */
    private String token_ = null;

    private String fmeUrl = null;

    public FMEQueryEngine() throws Exception {
        client_ = new HttpClient();

        try {
            getConnectionInfo();
        } catch (IOException e) {
            throw new GDEMException(e.toString(), e);
        }
    }

    @Override
    protected void runQuery(XQScript script, OutputStream result)
            throws GDEMException {

        try {
            // for the request to run the service
            PostMethod runMethod = new PostMethod(script.getScriptSource());

            runMethod.addParameter("token", token_);

            runMethod.addParameter("opt_showresult", "true");
            runMethod.addParameter("opt_servicemode", "sync");

            runMethod.addParameter("source_xml", script.getOrigFileUrl()); // XML file
            runMethod.addParameter("format", script.getOutputType()); // Output format

            if (client_.executeMethod(runMethod) != 200) {
                LOGGER.error("FME workspace failed: " + script.getScriptSource());
                throw new Exception("FME workspace failed");
            } else {
                // We get an InputStream and copy it to the 'result' OutputStream
                IOUtils.copy(runMethod.getResponseBodyAsStream(), result);
            }

        } catch (Exception e) {
            throw new GDEMException(e.toString(), e);
        }

    }

    /**
     * Gets a user token from the FME server.
     *
     * @throws Exception
     */
    private void getConnectionInfo() throws Exception {

        // We must first generate a security token for authentication
        // purposes
        fmeUrl = "http://" + Properties.fmeHost + ":" + Properties.fmePort
                + "/fmetoken/generate";

        PostMethod method = new PostMethod(fmeUrl);
        method.addParameter("user", Properties.fmeUser);
        method.addParameter("password", Properties.fmePassword);
        method.addParameter("expiration", Properties.fmeTokenExpiration);
        method.addParameter("timeunit", Properties.fmeTokenTimeunit);

        if (client_.executeMethod(method) == 200) {
            token_ = method.getResponseBodyAsString();
        } else {
            LOGGER.error("FME authentication failed. Could not retrieve a Token");
            throw new GDEMException("FME authentication failed");
        }

    }

}
