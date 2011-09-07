package eionet.gdem.services.db.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import eionet.gdem.Constants;
import eionet.gdem.services.db.dao.IXQJobDao;
import eionet.gdem.utils.Utils;

public class XQJobMySqlDao extends MySqlBaseDao implements IXQJobDao, Constants {

    private static final String qXQJobData = "SELECT " + URL_FLD + "," + XQ_FILE_FLD + "," + RESULT_FILE_FLD + ", " + STATUS_FLD
            + ", " + SRC_FILE_FLD + ", " + XQ_ID_FLD + " FROM " + WQ_TABLE + " WHERE " + JOB_ID_FLD + "= ?";

    private static final String qStartXQJob = "INSERT INTO " + WQ_TABLE + " (" + URL_FLD + "," + XQ_FILE_FLD + ", "
            + RESULT_FILE_FLD + "," + STATUS_FLD + "," + XQ_ID_FLD + "," + TIMESTAMP_FLD + ") " + "VALUES (?,?,?,?,?,{fn now()})";

    private static final String qCheckJobID = "SELECT " + JOB_ID_FLD + " FROM " + WQ_TABLE + " WHERE " + XQ_FILE_FLD + " = ?"
            + " AND " + RESULT_FILE_FLD + " =  ?";

    private static final String qChangeJobStatus = "UPDATE " + WQ_TABLE + " SET " + STATUS_FLD + "= ?" + ", " + TIMESTAMP_FLD
            + "= NOW() " + " WHERE " + JOB_ID_FLD + "= ?";

    private static final String qChangeFileJobsStatus = "UPDATE " + WQ_TABLE + " SET " + STATUS_FLD + "= ?" + ", " + SRC_FILE_FLD
            + "= ? " + ", " + TIMESTAMP_FLD + "= NOW() " + " WHERE " + URL_FLD + "= ? " + " AND " + STATUS_FLD + "< ? ";

    private static final String qJobs = "SELECT " + JOB_ID_FLD + " FROM " + WQ_TABLE + " WHERE " + STATUS_FLD + "= ? ORDER BY "
            + JOB_ID_FLD;

    private static final String qJobsLimit = "SELECT " + JOB_ID_FLD + " FROM " + WQ_TABLE + " WHERE " + STATUS_FLD
            + "= ? ORDER BY " + JOB_ID_FLD + " LIMIT 0,?";

    private static final String qEndXQJob = "DELETE FROM " + WQ_TABLE + " WHERE " + JOB_ID_FLD + "= ?";

    private static final String qEndXQJobs = "DELETE FROM " + WQ_TABLE + " WHERE " + JOB_ID_FLD + " IN ";

    private static final String qJobData = "SELECT " + JOB_ID_FLD + ", " + URL_FLD + "," + XQ_FILE_FLD + ", " + RESULT_FILE_FLD
            + ", " + STATUS_FLD + ", " + TIMESTAMP_FLD + ", " + XQ_ID_FLD + " FROM " + WQ_TABLE + " ORDER BY " + JOB_ID_FLD;

    private static final String qChangeJobsStatuses = "UPDATE " + WQ_TABLE + " SET " + STATUS_FLD + "= ?" + ", " + TIMESTAMP_FLD
            + "= NOW() " + " WHERE " + JOB_ID_FLD + " IN  ";

    private static final String qRestartActiveXQJobs = "UPDATE " + WQ_TABLE + " SET " + STATUS_FLD + "= ?" + ", " + TIMESTAMP_FLD
            + "= NOW() " + " WHERE " + STATUS_FLD + "= ?";

    private static final String qCountActiveJobs = "SELECT COUNT(*) " + " FROM " + WQ_TABLE + " WHERE " + STATUS_FLD + "="
            + Constants.XQ_DOWNLOADING_SRC + " OR " + STATUS_FLD + "=" + Constants.XQ_PROCESSING;

    /*
     * public String[] getXQJobData(String jobId) throws SQLException { String sql = "SELECT " + URL_FLD + "," + XQ_FILE_FLD + "," +
     * RESULT_FILE_FLD + ", " + STATUS_FLD + ", " + SRC_FILE_FLD + ", " + XQ_ID_FLD + " FROM " + WQ_TABLE + " WHERE " + JOB_ID_FLD +
     * "=" + jobId;
     * 
     * String[][] r = _executeStringQuery(sql); String s[];
     * 
     * if (r.length == 0) s = null; else s = r[0];
     * 
     * return s; }
     */
    public String[] getXQJobData(String jobId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String s[];

        if (isDebugMode) {
            logger.debug("Query is " + qXQJobData);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qXQJobData);
            pstmt.setInt(1, Integer.parseInt(jobId));
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            if (r.length == 0)
                s = null;
            else
                s = r[0];
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return s;
    }

    /*
     * public String startXQJob(String url, String xqFile, String resultFile) throws SQLException { return startXQJob(url, xqFile,
     * resultFile, JOB_FROMSTRING); }
     */
    public String startXQJob(String url, String xqFile, String resultFile) throws SQLException {
        return startXQJob(url, xqFile, resultFile, JOB_FROMSTRING);
    }

    /*
     * public String startXQJob(String url, String xqFile, String resultFile, int xqID) throws SQLException { String sql =
     * "INSERT INTO " + WQ_TABLE + " (" + URL_FLD + "," + XQ_FILE_FLD + ", " + RESULT_FILE_FLD + "," + STATUS_FLD + "," +
     * TIMESTAMP_FLD + "," + XQ_ID_FLD + ") VALUES ('" + url + "', '" + xqFile + "','" + resultFile + "', " + XQ_RECEIVED +
     * ", NOW()," + xqID + ")";
     * 
     * _executeUpdate(sql);
     * 
     * sql = "SELECT " + JOB_ID_FLD + " FROM " + WQ_TABLE + " WHERE " + XQ_FILE_FLD + " = '" + xqFile + "' AND " + RESULT_FILE_FLD +
     * " = '" + resultFile + "'";
     * 
     * String r[][] = _executeStringQuery(sql);
     * 
     * return r[0][0]; }
     */

    public String startXQJob(String url, String xqFile, String resultFile, int xqID) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[][] r = null;

        if (isDebugMode) {
            logger.debug("Query is " + qStartXQJob);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qStartXQJob);
            pstmt.setString(1, url);
            pstmt.setString(2, xqFile);
            pstmt.setString(3, resultFile);
            pstmt.setInt(4, XQ_RECEIVED);
            pstmt.setInt(5, xqID);
            pstmt.executeUpdate();
            pstmt.close();

            pstmt = conn.prepareStatement(qCheckJobID);
            pstmt.setString(1, xqFile);
            pstmt.setString(2, resultFile);
            rs = pstmt.executeQuery();
            r = getResults(rs);
        } finally {
            closeAllResources(null, pstmt, conn);
        }

        return r[0][0];

    }

    /*
     * public void changeJobStatus(String jobId, int status) throws SQLException { String sql = "UPDATE " + WQ_TABLE + " SET " +
     * STATUS_FLD + "=" + status + // String sql="UPDATE " + WQ_TABLE + " SET STATUS=" + status + ", " + TIMESTAMP_FLD + "= NOW()" +
     * " WHERE " + JOB_ID_FLD + "=" + jobId; _executeUpdate(sql); }
     */

    public void changeJobStatus(String jobId, int status) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            logger.debug("Query is " + qChangeJobStatus);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qChangeJobStatus);
            pstmt.setInt(1, status);
            pstmt.setInt(2, Integer.parseInt(jobId));
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    /*
     * public void changeFileJobsStatus(String url, String savedFile, int status) throws SQLException { String sql = "UPDATE " +
     * WQ_TABLE + " SET " + STATUS_FLD + "=" + status + ", " + SRC_FILE_FLD + "=" + Utils.strLiteral(savedFile) + // String
     * sql="UPDATE " + WQ_TABLE + " SET STATUS=" + status + ", " + TIMESTAMP_FLD + "= NOW()" + " WHERE " + URL_FLD + "=" +
     * Utils.strLiteral(url) + " AND " + STATUS_FLD + "<" + status; _executeUpdate(sql); }
     */

    public void changeFileJobsStatus(String url, String savedFile, int status) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            logger.debug("Query is " + qChangeFileJobsStatus);
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

    /*
     * public String[] getJobs(int status) throws SQLException { String sql = "SELECT " + JOB_ID_FLD + " FROM " + WQ_TABLE +
     * " WHERE " + STATUS_FLD + "=" + status;
     * 
     * String[][] r = _executeStringQuery(sql); String[] s = null;
     * 
     * if (r.length > 0) { s = new String[r.length];
     * 
     * for (int i = 0; i < r.length; i++) s[i] = r[i][0]; } return s; }
     */

    public String[] getJobs(int status) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] s = null;

        if (isDebugMode) {
            logger.debug("Query is " + qJobs);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qJobs);
            pstmt.setInt(1, status);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            if (r.length > 0) {
                s = new String[r.length];

                for (int i = 0; i < r.length; i++)
                    s[i] = r[i][0];
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return s;
    }

    public String[] getJobsLimit(int status, int max_rows) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] s = null;

        if (isDebugMode) {
            logger.debug("Query is " + qJobsLimit);
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

                for (int i = 0; i < r.length; i++)
                    s[i] = r[i][0];
            }
            if (isDebugMode) {
                logger.debug("number of jobs in result: " + r.length);
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return s;
    }

    /*
     * public void endXQJob(String jobId) throws SQLException { String sql = "DELETE FROM " + WQ_TABLE + " WHERE " + JOB_ID_FLD +
     * "=" + jobId;
     * 
     * _executeUpdate(sql); }
     */

    public void endXQJob(String jobId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            logger.debug("Query is " + qEndXQJob);
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

    /*
     * public String[][] getJobData() throws SQLException { String sql = "SELECT " + JOB_ID_FLD + ", " + URL_FLD + "," + XQ_FILE_FLD
     * + ", " + RESULT_FILE_FLD + ", " + STATUS_FLD + ", " + TIMESTAMP_FLD + ", " + XQ_ID_FLD + " FROM " + WQ_TABLE + " ORDER BY " +
     * JOB_ID_FLD;
     * 
     * return _executeStringQuery(sql);
     * 
     * }
     */

    public String[][] getJobData() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[][] r = null;

        if (isDebugMode) {
            logger.debug("Query is " + qJobData);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qJobData);
            rs = pstmt.executeQuery();
            r = getResults(rs);
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return r;
    }

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
            logger.debug("Query is " + queryBuf.toString());
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(queryBuf.toString());
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

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
            logger.debug("Query is " + queryBuf.toString());
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

    public void changeJobStatusByStatus(int currentStatus, int newStatus) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            logger.debug("Query is " + qRestartActiveXQJobs);
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

    public int countActiveJobs() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[][] r = null;
        int ret = 0;

        if (isDebugMode) {
            logger.debug("Query is " + qCountActiveJobs);
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
                logger.debug("number of active jobs: " + ret);
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return ret;
    }

}
