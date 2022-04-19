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

    Long countById(Integer id);

    Page<JobEntry> findByUrlContainingIgnoreCase(String url, Pageable pageable);

    Long countByUrlContainingIgnoreCase(String url);

    Page<JobEntry> findByFileContainingIgnoreCase(String file, Pageable pageable);

    Long countByFileContainingIgnoreCase(String file);

    Page<JobEntry> findByResultFileContainingIgnoreCase(String resultFile, Pageable pageable);

    Long countByResultFileContainingIgnoreCase(String resultFile);

    Page<JobEntry> findByInstanceContainingIgnoreCase(String instance, Pageable pageable);

    Long countByInstanceContainingIgnoreCase(String instance);

    Page<JobEntry> findByJobTypeContainingIgnoreCase(String jobType, Pageable pageable);

    Long countByJobTypeContainingIgnoreCase(String jobType);

    Page<JobEntry> findByJobExecutorNameContainingIgnoreCase(String jobExecutorName, Pageable pageable);

    Long countByJobExecutorNameContainingIgnoreCase(String jobExecutorName);

    Page<JobEntry> findByNStatus(Integer nStatus, Pageable pageable);

    Long countByNStatus(Integer nStatus);

    Page<JobEntry> findByNStatusIn(Set<Integer> nStatus, Pageable pageable);

    Long countByNStatusIn(Set<Integer> nStatus);

    @Query(value = "UPDATE T_XQJOBS SET N_STATUS= :newStatus AND TIMESTAMP= :timestamp WHERE N_STATUS= :oldStatus", nativeQuery = true)
    void changeJobStatusAndTimestampByStatus(@Param("newStatus") Integer newStatus, @Param("timestamp") Timestamp timestamp, @Param("oldStatus") Integer oldStatus);

    @Query(value = "UPDATE T_XQJOBS SET DURATION= :duration WHERE JOB_ID= :jobId", nativeQuery=true)
    Integer updateDurationByJobId(@Param("duration") Long duration, @Param("jobId") Integer jobId);
    
    @Query(value = "SELECT COUNT(*) FROM T_XQJOBS where TIME_STAMP like :containedDate", nativeQuery = true)
    Long countByTimestampContaining(@Param("containedDate") String containedDate);

    @Query(value = "SELECT * FROM T_XQJOBS WHERE DUPLICATE_IDENTIFIER = :duplicateIdentifier and N_STATUS in :nStatus ORDER BY JOB_ID DESC LIMIT 1", nativeQuery = true)
    JobEntry findByDuplicateIdentifierAndStatus(@Param("duplicateIdentifier") String duplicateIdentifier, @Param("nStatus") Set<Integer> nStatus);
}
