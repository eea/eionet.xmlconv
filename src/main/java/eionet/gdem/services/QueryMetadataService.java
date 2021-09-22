package eionet.gdem.services;

public interface QueryMetadataService {

    void storeScriptInformation(Integer queryID, String scriptFile, String scriptType, Long durationOfJob, Integer jobStatus);
}
