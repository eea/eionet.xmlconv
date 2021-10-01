package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryHistoryEntry;

import java.util.List;

public interface QueryHistoryService {

    List<QueryHistoryEntry> findById(Integer id);

    QueryHistoryEntry save(QueryHistoryEntry entry);

    Integer findQueryMaxVersion(Integer queryId);

    void updateQueryId(Integer newQueryId, Integer oldQueryId);
}
