package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.QueryMetadataEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("queryMetadataRepository")
public interface QueryMetadataRepository extends JpaRepository<QueryMetadataEntry, Integer> {
}
