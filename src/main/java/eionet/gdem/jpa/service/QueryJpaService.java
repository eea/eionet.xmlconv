package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryEntry;

public interface QueryJpaService {

    QueryEntry findByQueryId(Integer queryId);

    QueryEntry save(QueryEntry queryEntry);

    void delete(Integer id);
}
