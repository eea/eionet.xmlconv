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

    @Query(value = "select * from QUERY_HISTORY where QUERY_ID= :queryId", nativeQuery=true)
    List<QueryHistoryEntry> findEntriesByQueryId(@Param("queryId") Integer queryId);

    @Modifying
    @Query(value = "update QUERY_HISTORY set QUERY_ID= :newQueryId where QUERY_ID= :oldQueryId", nativeQuery=true)
    void updateQueryId(@Param("newQueryId") Integer newQueryId, @Param("oldQueryId") Integer oldQueryId);

    @Query(value = "select * from QUERY_HISTORY where QUERY_ID= :queryId order by DATE_MODIFIED DESC limit 1", nativeQuery=true)
    QueryHistoryEntry findLastEntryByQueryId(@Param("queryId") Integer queryId);
}
