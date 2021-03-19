package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface WorkerHeartBeatMsgRepository extends JpaRepository<WorkerHeartBeatMsgEntry, Integer> {

    @Query(value = "SELECT * FROM WORKER_HEART_BEAT_MSG WHERE JOB_ID= :jobId AND RESPONSE_TIMESTAMP IS NULL ORDER BY REQUEST_TIMESTAMP DESC", nativeQuery = true)
    List<WorkerHeartBeatMsgEntry> findUnAnsweredHeartBeatMessages(@Param("jobId") Integer jobId);

    @Modifying
    @Query(value = "update WORKER_HEART_BEAT_MSG set RESPONSE_TIMESTAMP = :responseTimestamp, JOB_STATUS= :jobStatus where JOB_ID = :jobId and REQUEST_TIMESTAMP= :requestTimestamp", nativeQuery=true)
    void updateResponseTimestampAndJobStatus(@Param("responseTimestamp") Timestamp responseTimestamp, @Param("jobStatus") Integer jobStatus, @Param("jobId") Integer jobId, @Param("requestTimestamp") Timestamp requestTimestamp);
}
