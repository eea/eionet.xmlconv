package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.CdrRequestEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CdrRequestsRepository extends JpaRepository<CdrRequestEntry, Integer> {
    List<CdrRequestEntry> findByUuidOrderByDateAddedDesc(String uuid);
}
