package eionet.gdem.web.spring.workqueue;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.database.MySqlBaseDao;
import eionet.gdem.utils.Utils;
import org.basex.query.value.item.Dat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Date;
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
    public static final String DURATION_FLD = "DURATION";
    public static final String API_USERNAME = "USERNAME";
    public static final String API_TABLE = "T_API_USER";
    public static final String INTERNAL_STATUS_ID_FLD = "INTERNAL_STATUS_ID";

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

    private static final String qXQRunningJobs = "SELECT " + JOB_ID_FLD + "," + URL_FLD + "," + DURATION_FLD + " FROM " + WQ_TABLE +
            " WHERE " + STATUS_FLD + "=" + Constants.XQ_PROCESSING  + " ORDER BY " + JOB_ID_FLD;

    private static final String qLastActiveJobTime = qXQJobDataBase + " WHERE "+ STATUS_FLD + "=" + Constants.XQ_PROCESSING + " ORDER BY TIME_STAMP desc limit 1";
    
    private static final String qJobsByInstanceAndStatus = "SELECT INSTANCE, N_STATUS, COUNT(*) as JOBS_SUM FROM T_XQJOBS GROUP BY INSTANCE, N_STATUS";

    private static final String qJobsObject = "SELECT *" + " FROM " + WQ_TABLE + " WHERE " + STATUS_FLD + "= ?" + " AND " + INTERNAL_STATUS_ID_FLD + " = ?";

    private static final String qJobsLongRunning = "SELECT " + JOB_ID_FLD + " FROM " + WQ_TABLE + " WHERE " + STATUS_FLD + " = ?" + " AND " + INTERNAL_STATUS_ID_FLD + " = ?" + " AND " + DURATION_FLD + ">=?  ";

    private static final String qApiUsername = "SELECT " + API_USERNAME + " FROM " + API_TABLE + " LIMIT 1 ";

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
    public Map<String, Timestamp> getJobsWithTimestamps(int status, Integer internalStatusId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, Timestamp> jobIdTimeStampMap = new HashMap();

        if (isDebugMode) {
            LOGGER.debug("Query is " + qJobsObject);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qJobsObject);
            pstmt.setInt(1, status);
            pstmt.setInt(2, internalStatusId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                jobIdTimeStampMap.put(rs.getString("JOB_ID"), rs.getTimestamp("TIME_STAMP"));
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return jobIdTimeStampMap;
    }


    @Override
    public String[] getLongRunningJobs(Long duration, Integer status, Integer internalStatus) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] jobIds;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qJobsLongRunning);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qJobsLongRunning);
            pstmt.setInt(1, status);
            pstmt.setInt(2, internalStatus);
            pstmt.setLong(3, duration);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            if (r.length == 0) {
                jobIds = null;
            } else {
                jobIds = new String[r.length];

                for (int i = 0; i < r.length; i++) {
                    jobIds[i] = r[i][0];
                }
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return jobIds;
    }

    @Override
    public String getAPIUsername() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[][] r = null;
        String username = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qApiUsername);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qApiUsername);
            rs = pstmt.executeQuery();
            r = getResults(rs);
            if (r.length > 0) {
                username = r[0][0];
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return username;
    }

    @Override
    public String[][] getRunningJobs() throws SQLException {
        return executeSimpleQuery(qXQRunningJobs);
    }

}
