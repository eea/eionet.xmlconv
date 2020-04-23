package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobHistoryEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobHistoryEntryRepository extends CrudRepository<JobHistoryEntry, Long>{

    /* Retrieves the job history entry by its id */
    JobHistoryEntry findById(Integer id);
}
