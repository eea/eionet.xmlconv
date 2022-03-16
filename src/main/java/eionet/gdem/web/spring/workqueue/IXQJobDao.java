package eionet.gdem.web.spring.workqueue;

import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

/**
 * XQ Job Dao Interface.
 * @author Unknown
 * @author George Sofianos
 */
public interface IXQJobDao {

    /**
     * Gets information about the received job in Workqueue.
     *
     * @param jobId Job Id
     * @return jobs a array of Strings
     * @throws SQLException
     *             DB error occurred.
     */
    String[] getXQJobData(String jobId) throws SQLException;

    /**
     * Get all finished jobs from DB.
     *
     * @return Job data.
     * @throws SQLException
     *             DB error occurred.
     */
    String[][] getXQFinishedJobs() throws SQLException;



    /**
     *Get Latest Processing Job Start Time
     * @return Job data
     * @throws SQLException
     */
    String[] getLatestProcessingJobStartTime() throws SQLException;

    /**
     *Get Jobs Status By XMLCONV Instance
     * @return Job data
     * @throws SQLException
     */
    String[][] getJobsSumInstanceAndStatus() throws SQLException;

    /**
     *Get a map with job id as key and timestamp as value by status
     * @param status
     * @param internalStatusId
     * @return the hashmap
     * @throws SQLException
     */
    Map<String, Timestamp> getJobsWithTimestamps(int status, Integer internalStatusId) throws SQLException;

    /**
     * Get long running jobs
     *  @param duration the duration threshold
     *  @param status
     *  @param internalStatus
     * @throws SQLException
     */
    String[] getLongRunningJobs(Long duration, Integer status, Integer internalStatus) throws SQLException;

    /**
     * Get usernname from T_API_USER
     * @throws SQLException
     */
    String getAPIUsername() throws SQLException;

    /**
     * Get all running jobs from DB
     * @return Job data
     * @throws SQLException
     */
    String[][] getRunningJobs() throws  SQLException;
}
