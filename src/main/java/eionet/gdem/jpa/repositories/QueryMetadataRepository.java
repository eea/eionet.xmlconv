package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.QueryMetadataEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("queryMetadataRepository")
public interface QueryMetadataRepository extends JpaRepository<QueryMetadataEntry, Integer> {

    /* Retrieves the entry by its queryId */
    List<QueryMetadataEntry> findByQueryId(Integer queryId);
}
