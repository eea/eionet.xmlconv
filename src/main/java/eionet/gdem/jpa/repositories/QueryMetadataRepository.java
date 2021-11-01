package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.QueryMetadataEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("queryMetadataRepository")
public interface QueryMetadataRepository extends JpaRepository<QueryMetadataEntry, Integer> {

    /* Retrieves the entry by its queryId */
    List<QueryMetadataEntry> findByQueryId(Integer queryId);

    @Query(value = "SELECT * FROM QUERY_METADATA WHERE T_QUERY_ID= :queryId AND VERSION = (SELECT MAX(VERSION) FROM QUERY_METADATA WHERE T_QUERY_ID = :queryId)", nativeQuery = true)
    List<QueryMetadataEntry> findByQueryIdAndMaxVersion(@Param("queryId") Integer queryId);

    @Query(value = "SELECT COUNT(ID) FROM QUERY_METADATA WHERE T_QUERY_ID= :queryId", nativeQuery = true)
    Integer findNumberOfEntriesByQueryId(@Param("queryId") Integer queryId);
}
