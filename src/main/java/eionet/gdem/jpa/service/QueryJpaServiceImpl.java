package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.repositories.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("queryJpaServiceImpl")
public class QueryJpaServiceImpl implements QueryJpaService {

    private QueryRepository queryRepository;

    @Autowired
    public QueryJpaServiceImpl(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Override
    public Integer findMaxVersion(Integer queryId) {
        return queryRepository.findMaxVersion(queryId);
    }

    @Override
    public QueryEntry findByQueryId(Integer queryId) {
        return queryRepository.findByQueryId(queryId);
    }

    @Override
    public QueryEntry save(QueryEntry queryEntry) {
        return queryRepository.save(queryEntry);
    }

    @Transactional
    @Override
    public void updateVersion(Integer version, Integer queryId) {
        queryRepository.updateVersion(version, queryId);
    }

    @Override
    public String getShortName(Integer queryId) {
        return queryRepository.getShortName(queryId);
    }
}
