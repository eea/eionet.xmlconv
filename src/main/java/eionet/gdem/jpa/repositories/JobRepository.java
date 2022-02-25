package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Repository("jobRepository")
public interface JobRepository extends JpaRepository<JobEntry, Integer> {

    JobEntry findById(Integer id);

    List<JobEntry> findByIntSchedulingStatus(InternalSchedulingStatus intSchedulingStatus);

    List<JobEntry> findByIntSchedulingStatusAndIsHeavy(InternalSchedulingStatus intSchedulingStatus, boolean isHeavy);

    @Query(value = "select RETRY_COUNTER from T_XQJOBS where JOB_ID = :jobId", nativeQuery=true)
    Integer getRetryCounter(@Param("jobId") Integer jobId);

    @Modifying
    @Query(value = "update T_XQJOBS set N_STATUS = :nStatus, INSTANCE= :instance, TIME_STAMP= :timestamp where JOB_ID = :jobId", nativeQuery=true)
    void updateJobNStatus(@Param("nStatus") Integer nStatus, @Param("instance") String instance, @Param("timestamp") Timestamp timestamp, @Param("jobId") Integer jobId);

    @Query(value = "SELECT * FROM T_XQJOBS WHERE N_STATUS=2 AND INTERNAL_STATUS_ID=3", nativeQuery = true)
    List<JobEntry> findProcessingJobs();

    Page<JobEntry> findById(Integer id, Pageable pageable);

    Page<JobEntry> findByUrlContaining(String url, Pageable pageable);

    Page<JobEntry> findByFileContaining(String file, Pageable pageable);

    Page<JobEntry> findByResultFileContaining(String resultFile, Pageable pageable);

    Page<JobEntry> findByInstanceContaining(String instance, Pageable pageable);

    Page<JobEntry> findByJobTypeContaining(String jobType, Pageable pageable);

    Page<JobEntry> findByJobExecutorNameContaining(String jobExecutorName, Pageable pageable);

    Page<JobEntry> findByNStatus(Integer nStatus, Pageable pageable);

    Page<JobEntry> findByNStatusIn(Set<Integer> nStatus, Pageable pageable);
}
