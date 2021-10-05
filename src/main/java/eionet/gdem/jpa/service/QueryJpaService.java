package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryEntry;

public interface QueryJpaService {

    QueryEntry findByQueryId(Integer queryId);

    Integer findMaxVersion(Integer queryId);

    QueryEntry save(QueryEntry queryEntry);

    void updateVersion(Integer version, Integer queryId);
}
