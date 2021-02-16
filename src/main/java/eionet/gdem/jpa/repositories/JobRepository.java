package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository("jobRepository")
public interface JobRepository extends JpaRepository<JobEntry, Integer> {

    JobEntry findById(Integer id);

    List<JobEntry> findByIntSchedulingStatus(InternalSchedulingStatus intSchedulingStatus);

    @Modifying
    @Query(value = "update T_XQJOBS set N_STATUS = :nStatus, INSTANCE= :instance, TIME_STAMP= :timestamp, RETRY_COUNTER= :retryCounter where JOB_ID = :jobId", nativeQuery=true)
    void updateJobInfo(@Param("nStatus") Integer nStatus, @Param("instance") String instance, @Param("timestamp") Timestamp timestamp, @Param("retryCounter") Integer retryCounter, @Param("jobId") Integer jobId);

    @Query(value = "select RETRY_COUNTER from T_XQJOBS where JOB_ID = :jobId", nativeQuery=true)
    Integer getRetryCounter(@Param("jobId") Integer jobId);

    @Modifying
    @Query(value = "update T_XQJOBS set N_STATUS = :nStatus, INSTANCE= :instance, TIME_STAMP= :timestamp where JOB_ID = :jobId", nativeQuery=true)
    void updateJobNStatus(@Param("nStatus") Integer nStatus, @Param("instance") String instance, @Param("timestamp") Timestamp timestamp, @Param("jobId") Integer jobId);

    @Modifying
    @Query(value = "update T_XQJOBS set INTERNAL_STATUS_ID= :intStatus where JOB_ID = :jobId", nativeQuery=true)
    void updateInternalStatus(@Param("intStatus") InternalSchedulingStatus intStatus, @Param("jobId") Integer jobId);
}
