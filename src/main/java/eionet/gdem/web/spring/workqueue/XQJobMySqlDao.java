package eionet.gdem.web.spring.workqueue;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.database.MySqlBaseDao;
import eionet.gdem.utils.Utils;
import org.basex.query.value.item.Dat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import static eionet.gdem.qa.QueryMySqlDao.JOB_RETRY_COUNTER;

/**
 * XQ job MySQL Dao class.
 * @author Unknown
 * @author George Sofianos
 */
@Repository("xqJobDao")
public class XQJobMySqlDao extends MySqlBaseDao implements IXQJobDao, Constants {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(XQJobMySqlDao.class);

    /*
     * T_XQJOBS.
     */
    public static final String JOB_ID_FLD = "JOB_ID";
    public static final String URL_FLD = "URL";
    public static final String RESULT_FILE_FLD = "RESULT_FILE";
    public static final String XQ_FILE_FLD = "XQ_FILE";
    public static final String STATUS_FLD = "N_STATUS";
    public static final String TIMESTAMP_FLD = "TIME_STAMP";
    public static final String XQ_ID_FLD = "QUERY_ID";
    public static final String SRC_FILE_FLD = "SRC_FILE";
    public static final String XQ_TYPE_FLD = "XQ_TYPE";
    public static final String INSTANCE = "INSTANCE";
    public static final String DURATION_FLD = "DURATION";
    /**
     * Table for XQuery Workqueue.
     */
    public static final String WQ_TABLE = "T_XQJOBS";

    /** Base query for getting all fields from WQ_TABLE */
    private static final String qXQJobDataBase = "SELECT " + URL_FLD + "," + XQ_FILE_FLD + "," + RESULT_FILE_FLD + ", "
            + STATUS_FLD + ", " + SRC_FILE_FLD + ", " + XQ_ID_FLD + ", " + JOB_ID_FLD + ", " + TIMESTAMP_FLD + ", " + XQ_TYPE_FLD + " FROM " + WQ_TABLE;

    private static final String qXQJobData = qXQJobDataBase + " WHERE " + JOB_ID_FLD + "= ?";

    private static final String qXQFinishedJobs = qXQJobDataBase + " WHERE " + STATUS_FLD + ">=" + Constants.XQ_READY
            + " ORDER BY " + JOB_ID_FLD;

    private static final String qInsertXQJob = "INSERT INTO " + WQ_TABLE + " (" + URL_FLD + "," + XQ_FILE_FLD + ", "
            + RESULT_FILE_FLD + "," + STATUS_FLD + "," + XQ_ID_FLD + "," + TIMESTAMP_FLD + "," + XQ_TYPE_FLD + ") " + "VALUES (?,?,?,?,?,{fn now()},?)";

    // use LAST_INSERT_ID to avoid duplicates in extreme cases http://stackoverflow.com/a/17112962/3771458
    private static final String qGetJobID = "SELECT LAST_INSERT_ID()";

    private static final String qChangeJobStatus = "UPDATE " + WQ_TABLE + " SET " + STATUS_FLD + "= ?" + ", " + INSTANCE + "= ?, " + TIMESTAMP_FLD
            + "= NOW() " + " WHERE " + JOB_ID_FLD + "= ?";

    private static final String qProcessXQJob = "UPDATE " + WQ_TABLE + " SET " + STATUS_FLD + "= ?" + ", " + INSTANCE + "= ?, " + TIMESTAMP_FLD
            + "= NOW() , " + JOB_RETRY_COUNTER + " = " + JOB_RETRY_COUNTER + " + 1  WHERE " + JOB_ID_FLD + "= ?";

    private static final String qMarkDeletedJob = "UPDATE " + WQ_TABLE + " SET " + JOB_RETRY_COUNTER + "= ?" + " WHERE " + JOB_ID_FLD + "= ?";

    private static final String qXQJobRetries = "SELECT " + JOB_RETRY_COUNTER + " FROM " + WQ_TABLE + " WHERE " + JOB_ID_FLD + "= ?";

    private static final String qChangeFileJobsStatus = "UPDATE " + WQ_TABLE + " SET " + STATUS_FLD + "= ?" + ", " + SRC_FILE_FLD
            + "= ? " + ", " + TIMESTAMP_FLD + "= NOW() " + " WHERE " + URL_FLD + "= ? " + " AND " + STATUS_FLD + "< ? ";

    private static final String qJobs = "SELECT " + JOB_ID_FLD + " FROM " + WQ_TABLE + " WHERE " + STATUS_FLD + "= ? ORDER BY "
            + JOB_ID_FLD;

    private static final String qJobsLimit = "SELECT " + JOB_ID_FLD + " FROM " + WQ_TABLE + " WHERE " + STATUS_FLD
            + "= ? ORDER BY " + JOB_ID_FLD + " LIMIT 0,?";

    private static final String qEndXQJob = "DELETE FROM " + WQ_TABLE + " WHERE " + JOB_ID_FLD + "= ?";

    private static final String qEndXQJobs = "DELETE FROM " + WQ_TABLE + " WHERE " + JOB_ID_FLD + " IN ";

    private static final String qJobData = "SELECT " + JOB_ID_FLD + ", " + URL_FLD + "," + XQ_FILE_FLD + ", " + RESULT_FILE_FLD
            + ", " + STATUS_FLD + ", " + TIMESTAMP_FLD + ", " + XQ_ID_FLD +  ", " + INSTANCE + " FROM " + WQ_TABLE + " ORDER BY " + JOB_ID_FLD;

    private static final String qChangeJobsStatuses = "UPDATE " + WQ_TABLE + " SET " + STATUS_FLD + "= ?" + ", " + TIMESTAMP_FLD
            + "= NOW() " + " WHERE " + JOB_ID_FLD + " IN  ";

    private static final String qRestartActiveXQJobs = "UPDATE " + WQ_TABLE + " SET " + STATUS_FLD + "= ?" + ", " + TIMESTAMP_FLD
            + "= NOW() " + " WHERE " + STATUS_FLD + "= ?";

    private static final String qCountActiveJobs = "SELECT COUNT(*) " + " FROM " + WQ_TABLE + " WHERE " + STATUS_FLD + "="
            + Constants.XQ_DOWNLOADING_SRC + " OR " + STATUS_FLD + "=" + Constants.XQ_PROCESSING;

    
    private static final String qLastActiveJobTime = qXQJobDataBase + " WHERE "+ STATUS_FLD + "=" + Constants.XQ_PROCESSING + " ORDER BY TIME_STAMP desc limit 1";
    
    private static final String qJobsByInstanceAndStatus = "SELECT INSTANCE, N_STATUS, COUNT(*) as JOBS_SUM FROM T_XQJOBS GROUP BY INSTANCE, N_STATUS";

    private static final String qJobsObject = "SELECT *" + " FROM " + WQ_TABLE + " WHERE " + STATUS_FLD + "= ?";

    private static final String qJobsUpdateDuration = "UPDATE " + WQ_TABLE + " SET " + DURATION_FLD + "= ?" + " WHERE " + JOB_ID_FLD + " = ?  ";

    @Override
    public String[] getXQJobData(String jobId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] s;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qXQJobData);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qXQJobData);
            pstmt.setInt(1, Integer.parseInt(jobId));
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            if (r.length == 0) {
                s = null;
            } else {
                s = r[0];
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return s;
    }

    @Override
    public String[][] getXQFinishedJobs() throws SQLException {
        return executeSimpleQuery(qXQFinishedJobs);
    }

    @Override
    public String startXQJob(String url, String xqFile, String resultFile, String scriptType) throws SQLException {
        return startXQJob(url, xqFile, resultFile, JOB_FROMSTRING, scriptType);
    }

    @Override
    public String startXQJob(String url, String xqFile, String resultFile, int xqID, String xqType) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[][] r = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qInsertXQJob);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qInsertXQJob);
            pstmt.setString(1, url);
            pstmt.setString(2, xqFile);
            pstmt.setString(3, resultFile);
            pstmt.setInt(4, XQ_RECEIVED);
            pstmt.setInt(5, xqID);
            pstmt.setString(6, xqType);
            pstmt.executeUpdate();
            pstmt.close();

            pstmt = conn.prepareStatement(qGetJobID);
            rs = pstmt.executeQuery();
            r = getResults(rs);
        } finally {
            closeAllResources(null, pstmt, conn);
        }

        return r[0][0];

    }

    @Override
    public void changeJobStatus(String jobId, int status) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qChangeJobStatus);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qChangeJobStatus);
            pstmt.setInt(1, status);
            pstmt.setString(2, Properties.getHostname() );
            pstmt.setInt(3, Integer.parseInt(jobId));
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    @Override
    public void processXQJob(String jobId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qProcessXQJob);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qProcessXQJob);
            pstmt.setInt(1, Constants.XQ_PROCESSING);
            pstmt.setString(2, Properties.getHostname() );
            pstmt.setInt(3, Integer.parseInt(jobId));
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    @Override
    public void markDeleted(String jobId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qMarkDeletedJob);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qMarkDeletedJob);
            pstmt.setInt(1, 1000);
            pstmt.setInt(2, Integer.parseInt(jobId));
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    @Override
    public int getJobRetries(String jobId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[][] r = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qXQJobRetries);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qXQJobRetries);
            pstmt.setInt(1, Integer.parseInt(jobId));
            rs = pstmt.executeQuery();
            r = getResults(rs);
        } finally {
            closeAllResources(null, pstmt, conn);
        }

        return Integer.parseInt(r[0][0]);
    }

    @Override
    public void changeFileJobsStatus(String url, String savedFile, int status) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qChangeFileJobsStatus);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qChangeFileJobsStatus);
            pstmt.setInt(1, status);
            pstmt.setString(2, savedFile);
            pstmt.setString(3, url);
            pstmt.setInt(4, status);
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    @Override
    public String[] getJobs(int status) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] s = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qJobs);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qJobs);
            pstmt.setInt(1, status);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            if (r.length > 0) {
                s = new String[r.length];

                for (int i = 0; i < r.length; i++) {
                    s[i] = r[i][0];
                }
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return s;
    }

    @Override
    public String[] getJobsLimit(int status, int max_rows) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] s = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qJobsLimit);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qJobsLimit);
            pstmt.setInt(1, status);
            pstmt.setInt(2, max_rows);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            if (r.length > 0) {
                s = new String[r.length];

                for (int i = 0; i < r.length; i++) {
                    s[i] = r[i][0];
                }
            }
            if (isDebugMode) {
                LOGGER.debug("number of jobs in result: " + r.length);
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return s;
    }

    @Override
    public void endXQJob(String jobId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qEndXQJob);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qEndXQJob);
            pstmt.setInt(1, Integer.parseInt(jobId));
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    @Override
    public String[][] getJobData() throws SQLException {
        return executeSimpleQuery(qJobData);
    }

    @Override
    public void endXQJobs(String[] jobIds) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        // build the query IN()
        String strJobIDs = Utils.stringArray2String(jobIds, ",");

        StringBuffer queryBuf = new StringBuffer(qEndXQJobs);
        queryBuf.append("(");
        queryBuf.append(strJobIDs);
        queryBuf.append(")");
        if (isDebugMode) {
            LOGGER.debug("Query is " + queryBuf.toString());
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(queryBuf.toString());
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    @Override
    public void changeXQJobsStatuses(String[] jobIds, int status) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        // build the query IN()
        String strJobIDs = Utils.stringArray2String(jobIds, ",");
        StringBuffer queryBuf = new StringBuffer(qChangeJobsStatuses);
        queryBuf.append("(");
        queryBuf.append(strJobIDs);
        queryBuf.append(")");
        if (isDebugMode) {
            LOGGER.debug("Query is " + queryBuf.toString());
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(queryBuf.toString());
            pstmt.setInt(1, status);
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    @Override
    public void changeJobStatusByStatus(int currentStatus, int newStatus) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qRestartActiveXQJobs);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qRestartActiveXQJobs);
            pstmt.setInt(1, newStatus);
            pstmt.setInt(2, currentStatus);
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    @Override
    public int countActiveJobs() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[][] r = null;
        int ret = 0;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qCountActiveJobs);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qCountActiveJobs);
            rs = pstmt.executeQuery();
            r = getResults(rs);
            if (r.length > 0) {
                String strC = r[0][0];
                try {
                    ret = Integer.parseInt(strC);
                } catch (Exception e) {

                }

            }
            if (isDebugMode) {
                LOGGER.debug("number of active jobs: " + ret);
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return ret;
    }

    
    @Override
    public String[] getLatestProcessingJobStartTime() throws SQLException {
              
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] s;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qLastActiveJobTime);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qLastActiveJobTime);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            if (r.length == 0) {
                s = null;
            } else {
                s = r[0];
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return s;
    }

    @Override
    public String[][] getJobsSumInstanceAndStatus() throws SQLException{
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[][] s;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qJobsByInstanceAndStatus);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qJobsByInstanceAndStatus);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            if (r.length == 0) {
                s = null;
            } else {
                s = r;
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return s;
    }

    @Override
    public Map<String, Date> getJobsWithTimestamps(int status) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, Date> jobIdTimeStampMap = new HashMap();

        if (isDebugMode) {
            LOGGER.debug("Query is " + qJobsObject);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qJobsObject);
            pstmt.setInt(1, status);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                jobIdTimeStampMap.put(rs.getString("JOB_ID"), rs.getDate("TIME_STAMP"));
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return jobIdTimeStampMap;
    }

    @Override
    public void updateXQJobsDuration(Map<String, Long> jobHashmap) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        conn = getConnection();
        StringBuffer queryBuf = new StringBuffer(qJobsUpdateDuration);
        pstmt = conn.prepareStatement(queryBuf.toString());

        try {
            for (Map.Entry<String,Long> entry : jobHashmap.entrySet()) {
                pstmt.setLong(1, entry.getValue());
                pstmt.setString(2, entry.getKey());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

}
