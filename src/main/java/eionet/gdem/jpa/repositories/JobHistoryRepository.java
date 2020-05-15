package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("jobHistoryRepository")
public interface JobHistoryRepository extends JpaRepository<JobHistoryEntry, Long> {

    /* Retrieves the job history entry by its id */
    JobHistoryEntry findById(Integer id);

    /* Retrieves the job history entry by its status */
    List<JobHistoryEntry> findAllByStatus(Integer status);

    /* Retrieves job history entries by the job's name */
    List<JobHistoryEntry> findByJobName(String jobName);

}
