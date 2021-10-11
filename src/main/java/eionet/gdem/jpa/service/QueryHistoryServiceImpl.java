package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryHistoryEntry;
import eionet.gdem.jpa.repositories.QueryHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("queryHistoryServiceImpl")
public class QueryHistoryServiceImpl implements QueryHistoryService {

    private QueryHistoryRepository queryHistoryRepository;

    @Autowired
    public QueryHistoryServiceImpl(QueryHistoryRepository queryHistoryRepository) {
        this.queryHistoryRepository = queryHistoryRepository;
    }

    @Override
    public List<QueryHistoryEntry> findEntriesByQueryId(Integer queryId) {
        return queryHistoryRepository.findEntriesByQueryId(queryId);
    }

    @Override
    public QueryHistoryEntry save(QueryHistoryEntry entry) {
        return queryHistoryRepository.save(entry);
    }

    @Transactional
    @Override
    public void updateQueryId(Integer newQueryId, Integer oldQueryId) {
        queryHistoryRepository.updateQueryId(newQueryId, oldQueryId);
    }
}
