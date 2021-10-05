package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.QueryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryRepository extends JpaRepository<QueryEntry, Integer> {

    QueryEntry findByQueryId(Integer queryId);
}
