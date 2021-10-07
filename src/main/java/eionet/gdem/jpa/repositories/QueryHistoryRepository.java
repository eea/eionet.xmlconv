package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.QueryHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryHistoryRepository extends JpaRepository<QueryHistoryEntry, Integer> {

    @Modifying
    @Query(value = "update QUERY_HISTORY set QUERY_ID= :newQueryId where QUERY_ID= :oldQueryId", nativeQuery=true)
    void updateQueryId(@Param("newQueryId") Integer newQueryId, @Param("oldQueryId") Integer oldQueryId);
}
