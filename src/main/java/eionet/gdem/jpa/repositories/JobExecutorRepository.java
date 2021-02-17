package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobExecutorRepository extends JpaRepository<JobExecutor, Integer> {

    List<JobExecutor> findByStatus(Integer status);

    JobExecutor findByName(String name);

    void deleteByName(String name);

    @Modifying
    @Query(value = "update JOB_EXECUTOR set STATUS= :status, JOB_ID= :jobId where NAME= :name", nativeQuery=true)
    void updateStatus(@Param("status") Integer status, @Param("jobId") Integer jobId, @Param("name") String name);

}


