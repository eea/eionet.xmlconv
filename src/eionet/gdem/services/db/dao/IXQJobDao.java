package eionet.gdem.services.db.dao;

import java.sql.SQLException;

public interface IXQJobDao extends IDbSchema{


	  /**
	  * Gets information about the received job in Workqueue
	  * @param String jobId
	  * @ return String[]
	  */
	  public String[] getXQJobData(String jobId) throws SQLException;

	  /**
	  * Creates a new job in the queue
	  * XQ Script is saved earlier in the db
	  * @param String url, String xqFile, String resultFile
	  * @param String xqID - query id from db
	  */
	  public String startXQJob(String url, String xqFile, String resultFile) throws SQLException;
	  public String startXQJob(String url, String xqFile, String resultFile, int xqID) throws SQLException;

	  /**
	   * Changes the status of the job in the table
	   * also changes the time_stamp showing when the new task was started
	   */
	   public void changeJobStatus(String jobId, int status) throws SQLException;
	   /**
	   * Changes the status of the jobs in the table and sets the downloaded file local src
	   * THe jobs should have the sam source url.
	   * also changes the time_stamp showing when the new task was started
	   */
	   public void changeFileJobsStatus(String url, String savedFile, int status) throws SQLException;
	   /**
	   * Returns job IDs in the Workqueue with the given status
	   * @return String[]
	   */
	   public String[] getJobs(int status) throws SQLException;
	   /**
	   * Returns job IDs in the Workqueue with the given status and limits the rows with the given limit
	   * @return String[]
	   */
	   public String[] getJobsLimit(int status, int max_rows) throws SQLException;

	   /**
	   * Removes the XQJob
	   * No checking performed by this method
	   */
	   public void endXQJob(String jobId) throws SQLException;

	   /**
	   * returns all root element mappings for schema ID
	   * @param - schema ID
	   * @return Vector containing HashMaps with root element info
	   */
	   /**
	    * returns all the job data in the WQ table
	    * @return String[][] containing all fields as HashMap from T_CONVTYPE table
	    */

	    public String[][] getJobData() throws SQLException;

}
