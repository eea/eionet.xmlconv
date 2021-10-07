package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryHistoryEntry;

import java.util.List;

public interface QueryHistoryService {

    List<QueryHistoryEntry> findAll();

    QueryHistoryEntry save(QueryHistoryEntry entry);

    void updateQueryId(Integer newQueryId, Integer oldQueryId);
}
