package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobExecutorHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobExecutorHistoryRepository extends JpaRepository<JobExecutorHistory, Integer> {

    /* Retrieves the entry by its containerId */
    List<JobExecutorHistory> findByContainerId(String containerId);

    /* Retrieves the entry by its jobId */
    List<JobExecutorHistory> findByJobId(Integer jobId);

}
