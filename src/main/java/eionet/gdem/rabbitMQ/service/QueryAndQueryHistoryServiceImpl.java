package eionet.gdem.rabbitMQ.service;

import eionet.gdem.data.scripts.HeavyScriptReasonEnum;
import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.Entities.QueryHistoryEntry;
import eionet.gdem.jpa.service.QueryHistoryService;
import eionet.gdem.jpa.service.QueryJpaService;
import eionet.gdem.qa.utils.ScriptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QueryAndQueryHistoryServiceImpl implements QueryAndQueryHistoryService {

    private QueryJpaService queryJpaService;
    private QueryHistoryService queryHistoryService;

    @Autowired
    public QueryAndQueryHistoryServiceImpl(QueryJpaService queryJpaService, QueryHistoryService queryHistoryService) {
        this.queryJpaService = queryJpaService;
        this.queryHistoryService = queryHistoryService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveQueryAndQueryHistoryEntries(QueryEntry queryEntry, QueryHistoryEntry queryHistoryEntry) {
        queryEntry = queryJpaService.save(queryEntry);
        queryHistoryEntry.setQueryEntry(queryEntry);
        queryHistoryService.save(queryHistoryEntry);
    }
}
