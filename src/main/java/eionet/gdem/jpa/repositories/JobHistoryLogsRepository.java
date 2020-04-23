package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobHistoryLogs;
import org.springframework.data.repository.CrudRepository;

public interface JobHistoryLogsRepository extends CrudRepository<JobHistoryLogs, Long>{
    JobHistoryLogs findById(Integer id);
}
