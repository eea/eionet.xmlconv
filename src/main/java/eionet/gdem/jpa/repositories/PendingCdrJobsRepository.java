package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.PendingCdrJobEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingCdrJobsRepository extends JpaRepository<PendingCdrJobEntry, Integer> {

}
