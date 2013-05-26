package eionet.gdem.services.db.dao;

import java.sql.SQLException;

public interface IXQJobDao extends IDbSchema {

    /**
     * Gets information about the received job in Workqueue.
     *
     * @param jobId
     * @return jobs a array of Strings
     * @throws SQLException
     *             DB error occurred.
     */
    String[] getXQJobData(String jobId) throws SQLException;

    /**
     * Creates a new job in the queue XQ Script is saved earlier in the db.
     *
     * @param url
     * @param xqFile
     * @param resultFile
     * @throws SQLException
     *             DB error occurred.
     */
    String startXQJob(String url, String xqFile, String resultFile) throws SQLException;

    /**
     * Creates a new job in the queue XQ Script is saved earlier in the db.
     *
     * @param url
     * @param xqFile
     * @param resultFile
     * @param xqID - query id from db
     * @throws SQLException
     *             DB error occurred.
     */
    String startXQJob(String url, String xqFile, String resultFile, int xqID) throws SQLException;

    /**
     * Changes the status of the job in the table also changes the time_stamp showing when the new task was started.
     * @throws SQLException
     *             DB error occurred.
     */
    void changeJobStatus(String jobId, int status) throws SQLException;

    /**
     * Changes the status of the jobs in the table and sets the downloaded file local src THe jobs should have the sam source url.
     * also changes the time_stamp showing when the new task was started
     * @throws SQLException
     *             DB error occurred.
     */
    void changeFileJobsStatus(String url, String savedFile, int status) throws SQLException;

    /**
     * Returns job IDs in the Workqueue with the given status.
     *
     * @return
     * @throws SQLException
     *             DB error occurred.
     */
    String[] getJobs(int status) throws SQLException;

    /**
     * Returns job IDs in the Workqueue with the given status and limits the rows with the given limit.
     *
     * @return
     * @throws SQLException
     *             DB error occurred.
     */
    String[] getJobsLimit(int status, int max_rows) throws SQLException;

    /**
     * Removes the XQJob No checking performed by this method.
     * @throws SQLException
     *             DB error occurred.
     */
    void endXQJob(String jobId) throws SQLException;

    /**
     * Removes the XQJobs. No checking performed by this method
     * @throws SQLException
     *             DB error occurred.
     */
    void endXQJobs(String[] jobIds) throws SQLException;

    /**
     * changes the job status for several jobs. No checking performed by this method
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
}
