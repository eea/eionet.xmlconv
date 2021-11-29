package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.utils.JobExecutorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobExecutorRepository extends JpaRepository<JobExecutor, Integer> {

    List<JobExecutor> findByStatus(Integer status);

    List<JobExecutor> findByStatusAndJobExecutorType(Integer status, JobExecutorType jobExecutorType);

    JobExecutor findByName(String name);

    void deleteByName(String name);

    void deleteByContainerId(String containerId);

    @Modifying
    @Query(value = "update JOB_EXECUTOR set STATUS= :status, JOB_ID= :jobId, JOB_EXECUTOR_TYPE= :jobExecutorType where NAME= :name", nativeQuery=true)
    void updateJobExecutor(@Param("status") Integer status, @Param("jobId") Integer jobId, @Param("jobExecutorType") Integer jobExecutorType, @Param("name") String name);

    @Query(value = "SELECT * FROM JOB_EXECUTOR WHERE JOB_ID= :jobId", nativeQuery=true)
    List<JobExecutor> findJobExecutorsByJobId(@Param("jobId") Integer jobId);

}


