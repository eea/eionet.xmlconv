package eionet.gdem.services;

import java.sql.SQLException;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public interface QueueJobsService {

    public String[] getJobById(String jobId)throws SQLException;
    
    public String getLatestProcessingJobStartTime() throws SQLException;
}
