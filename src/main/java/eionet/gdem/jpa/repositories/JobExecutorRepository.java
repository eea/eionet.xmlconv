package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.utils.JobExecutorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface JobExecutorRepository extends JpaRepository<JobExecutor, Integer> {

    List<JobExecutor> findByStatus(Integer status);

    List<JobExecutor> findByStatusAndJobExecutorType(Integer status, JobExecutorType jobExecutorType);

    JobExecutor findByName(String name);

    void deleteByName(String name);

    void deleteByContainerId(String containerId);

    @Query(value = "SELECT * FROM JOB_EXECUTOR WHERE JOB_ID= :jobId", nativeQuery=true)
    List<JobExecutor> findJobExecutorsByJobId(@Param("jobId") Integer jobId);

    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="5000")})
    JobExecutor save(JobExecutor jobExecutor);

}


