package eionet.gdem.services.fme;

import eionet.gdem.qa.XQScript;
import eionet.gdem.services.fme.exceptions.FmeAuthorizationException;
import eionet.gdem.services.fme.exceptions.FmeCommunicationException;
import eionet.gdem.services.fme.request.SubmitJobRequest;

import java.io.OutputStream;

public interface FmeServerCommunicator {


    String submitJob(XQScript script, SubmitJobRequest request) throws FmeAuthorizationException, FmeCommunicationException;

    FmeJobStatus getJobStatus(String jobId, XQScript script) throws FmeAuthorizationException, FmeCommunicationException;

    void getResultFiles(String folderName, OutputStream result) throws Exception;

    public void deleteFolder(String folderName) throws Exception;


}