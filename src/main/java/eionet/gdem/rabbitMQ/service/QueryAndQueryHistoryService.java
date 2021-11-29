package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.Entities.QueryHistoryEntry;

public interface QueryAndQueryHistoryService {

    void saveQueryAndQueryHistoryEntries(QueryEntry queryEntry, QueryHistoryEntry queryHistoryEntry);
}
