package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.repositories.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("queryJpaServiceImpl")
public class QueryJpaServiceImpl implements QueryJpaService {

    private QueryRepository queryRepository;

    @Autowired
    public QueryJpaServiceImpl(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }


    @Override
    public QueryEntry save(QueryEntry queryEntry) {
        return queryRepository.save(queryEntry);
    }

    @Override
    public void delete(Integer id) {
        queryRepository.delete(id);
    }
}
