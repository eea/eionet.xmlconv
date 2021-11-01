package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.QueryMetadataHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("queryMetadataHistoryRepository")
public interface QueryMetadataHistoryRepository extends JpaRepository<QueryMetadataHistoryEntry, Integer> {

    /* Retrieves the entries by the queryId */
    List<QueryMetadataHistoryEntry> findByQueryId(Integer queryId);

    @Query(value = "SELECT COUNT(ID) FROM QUERY_METADATA_HISTORY WHERE T_QUERY_ID= :queryId", nativeQuery = true)
    Integer findNumberOfEntriesByQueryId(@Param("queryId") Integer queryId);
}
