package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Modifying
    @Query(value = "update JOB_HISTORY set DURATION = :duration where JOB_NAME = :jobName and STATUS = :status and STATUS = :internalStatusId ORDER BY ID DESC LIMIT 1", nativeQuery=true)
    void setDurationForJobHistory(@Param("duration") Long duration, @Param("jobName") String jobName, @Param("status") Integer status, Integer internalStatusId);

}
