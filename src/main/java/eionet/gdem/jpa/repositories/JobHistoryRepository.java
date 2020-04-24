package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobHistoryRepository extends JpaRepository<JobHistoryEntry, Long> {

    /* Retrieves the job history entry by its id */
    JobHistoryEntry findById(Integer id);
}
