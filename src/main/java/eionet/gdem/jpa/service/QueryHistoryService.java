package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryHistoryEntry;

import java.util.List;

public interface QueryHistoryService {

    List<QueryHistoryEntry> findEntriesByQueryId(Integer queryId);

    QueryHistoryEntry save(QueryHistoryEntry entry);

    void updateQueryId(Integer newQueryId, Integer oldQueryId);
}
