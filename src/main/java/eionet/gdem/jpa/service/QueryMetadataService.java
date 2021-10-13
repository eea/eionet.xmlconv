package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryMetadataHistoryEntry;
import eionet.gdem.paging.Paged;

import java.util.List;

public interface QueryMetadataService {

    void storeScriptInformation(Integer queryID, String scriptFile, String scriptType, Long durationOfJob, Integer jobStatus);

    List<QueryMetadataHistoryEntry> fillQueryMetadataAdditionalInfo(List<QueryMetadataHistoryEntry> historyEntries);

    Paged<QueryMetadataHistoryEntry> getQueryMetadataHistoryEntries(Integer pageNumber, Integer size, Integer scriptId);
}
