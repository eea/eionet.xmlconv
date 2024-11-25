package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobExecutorHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobExecutorHistoryRepository extends JpaRepository<JobExecutorHistory, Integer> {

    List<JobExecutorHistory> findByName(String name);

    List<JobExecutorHistory> findByJobId(Integer jobId);

}
