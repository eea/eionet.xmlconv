package eionet.gdem.services.db.dao;

import java.sql.SQLException;

/**
 * XQ Job Dao Interface.
 * @author Unknown
 * @author George Sofianos
 */
public interface IXQJobDao extends IDbSchema {

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
     * Creates a new job in the queue XQ Script is saved earlier in the db.
     *
     * @param url URL
     * @param xqFile XQ File
     * @param resultFile Result file
     * @param xqType Script type
     * @throws SQLException
     *             DB error occurred.
     */
    String startXQJob(String url, String xqFile, String resultFile, String xqType) throws SQLException;

    /**
     * Creates a new job in the queue XQ Script is saved earlier in the db.
     *
     * @param url URL
     * @param xqFile XQ File
     * @param resultFile Result file
     * @param xqID - query id from db
     * @param xqType Script Type
     * @throws SQLException
     *             DB error occurred.
     */
    String startXQJob(String url, String xqFile, String resultFile, int xqID, String xqType) throws SQLException;

    /**
     * Changes the status of the job in the table also changes the time_stamp showing when the new task was started.
     * @param jobId Job Id
     * @param status Status
     * @throws SQLException
     *             DB error occurred.
     */
    void changeJobStatus(String jobId, int status) throws SQLException;

    /**
     * Changes the status of the jobs in the table and sets the downloaded file local src THe jobs should have the sam source url.
     * also changes the time_stamp showing when the new task was started
     * @param url URL
     * @param savedFile Saved file
     * @param status Status
     * @throws SQLException
     *             DB error occurred.
     */
    void changeFileJobsStatus(String url, String savedFile, int status) throws SQLException;

    /**
     * Returns job IDs in the Workqueue with the given status.
     * @param status Status
     * @return Jobs array
     * @throws SQLException
     *             DB error occurred.
     */
    String[] getJobs(int status) throws SQLException;

    /**
     * Returns job IDs in the Workqueue with the given status and limits the rows with the given limit.
     * @param status Status
     * @param max_rows Maximum Rows
     * @return Jobs limit
     * @throws SQLException
     *             DB error occurred.
     */
    String[] getJobsLimit(int status, int max_rows) throws SQLException;

    /**
     * Removes the XQJob No checking performed by this method.
     * @param jobId Job Id
     * @throws SQLException
     *             DB error occurred.
     */
    void endXQJob(String jobId) throws SQLException;

    /**
     * Removes the XQJobs. No checking performed by this method
     * @param jobIds Job IDs
     * @throws SQLException
     *             DB error occurred.
     */
    void endXQJobs(String[] jobIds) throws SQLException;

    /**
     * changes the job status for several jobs. No checking performed by this method
     * @param jobIds Job Ids
     * @param status Status
     * @throws SQLException
     *             DB error occurred.
     */
    void changeXQJobsStatuses(String[] jobIds, int status) throws SQLException;

    /**
     * Returns all the job data in the WQ table.
     *
     * @return String[][] containing all fields as HashMap from T_CONVTYPE table
     * @throws SQLException
     *             DB error occurred.
     */
    String[][] getJobData() throws SQLException;

    /**
     * Removes the XQJobs. No checking performed by this method
     * @param currentStatus Current status
     * @param newStatus New Status
     * @throws SQLException
     *             DB error occurred.
     */
    void changeJobStatusByStatus(int currentStatus, int newStatus) throws SQLException;

    /**
     * Countr the active jobs (N_STATUS=1 or 2) in DB.
     *
     * @return
     * @throws SQLException
     *             DB error occurred.
     */
    int countActiveJobs() throws SQLException;

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

}
