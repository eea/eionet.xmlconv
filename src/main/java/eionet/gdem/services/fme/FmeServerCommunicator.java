package eionet.gdem.services.fme;

import eionet.gdem.qa.XQScript;
import eionet.gdem.services.fme.request.SynchronousSubmitJobRequest;
import org.apache.http.impl.client.CloseableHttpClient;

public interface FmeServerCommunicator {



    String submitJob(XQScript script, SynchronousSubmitJobRequest request) throws Exception;


}
