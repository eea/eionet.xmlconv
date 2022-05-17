package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryMetadataEntry;
import eionet.gdem.jpa.Entities.QueryMetadataHistoryEntry;
import eionet.gdem.paging.Paged;

import java.util.List;

public interface QueryMetadataService {

    void storeScriptInformation(Integer queryID, String scriptFile, String scriptType, Long durationOfJob, Integer jobStatus, Integer jobId, Long fmeJobId, String xmlUrl, Long xmlSize);

    List<QueryMetadataHistoryEntry> fillQueryHistoryMetadataAdditionalInfo(List<QueryMetadataHistoryEntry> historyEntries);

    List<QueryMetadataEntry> fillQueryMetadataAdditionalInfo(List<QueryMetadataEntry> entries);

    Paged<QueryMetadataHistoryEntry> getQueryMetadataHistoryEntries(Integer pageNumber, Integer size, Integer scriptId);

    Paged<QueryMetadataEntry> getQueryMetadataEntries(Integer pageNumber, Integer size, Integer scriptId);

    Integer getCountOfHistoryEntriesByScript(Integer scriptId);

    Integer getCountOfEntriesByScript(Integer scriptId);

    List<QueryMetadataHistoryEntry> findByJobId(Integer jobId);

    QueryMetadataHistoryEntry saveQueryMetadataHistoryEntry(QueryMetadataHistoryEntry entry);

    List<QueryMetadataHistoryEntry> getAllQueryMetadataHistoryEntries(Integer scriptId);

    List<QueryMetadataEntry> getAllQueryMetadataEntries(Integer scriptId);
}
