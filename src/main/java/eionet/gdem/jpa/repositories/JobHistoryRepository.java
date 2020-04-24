package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobHistoryEntry;
import net.xqj.basex.bin.T;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface JobHistoryRepository extends JpaRepository<JobHistoryEntry, Long> {

    /* Retrieves the job history entry by its id */
    JobHistoryEntry findById(Integer id);

    /* Retrieves the job history entry by its status */
    List<JobHistoryEntry> findAllByStatus(Integer status);

}
