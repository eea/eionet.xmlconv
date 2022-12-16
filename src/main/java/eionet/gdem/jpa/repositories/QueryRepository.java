package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.QueryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryRepository extends JpaRepository<QueryEntry, Integer> {

    QueryEntry findByQueryId(Integer queryId);

    @Query(value = "SELECT MAX(VERSION) FROM T_QUERY WHERE QUERY_ID= :queryId", nativeQuery = true)
    Integer findMaxVersion(@Param("queryId") Integer queryId);

    @Modifying
    @Query(value = "update T_QUERY set VERSION= :version where QUERY_ID= :queryId", nativeQuery=true)
    void updateVersion(@Param("version") Integer version, @Param("queryId") Integer queryId);

    @Query(value = "SELECT SHORT_NAME FROM T_QUERY WHERE QUERY_ID= :queryId", nativeQuery = true)
    String getShortName(@Param("queryId") Integer queryId);
}
