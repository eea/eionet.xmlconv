package eionet.gdem.services.db.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.sql.DataSource;

import eionet.gdem.SpringApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * MySQL base dao class.
 * @author Unknown
 * @author George Sofianos
 */
public abstract class MySqlBaseDao {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlBaseDao.class);

    protected static boolean isDebugMode = LOGGER.isDebugEnabled();

    /**
     * Init JNDI datasource
     *
     * @throws NamingException If an error occurs.
     */
    private static class DataSourceHolder {
        private static final DataSource DATASOURCE;
        static {
            try {
                DATASOURCE = (DataSource) SpringApplicationContext.getBean("dataSource");
                //CONNECTION = new ObjectFactory();
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    /**
     * Returns new database connection.
     *
     * @return Connection from the LazyHolder Class (Initialization-on-demand holder idiom)
     * @throws SQLException If an error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return DataSourceHolder.DATASOURCE.getConnection();
    }

    /**
     * Closes all resources.
     * TODO: improve this and add logging
     * @param rs Resultset
     * @param pstmt Prepared statement
     * @param conn Connection
     */
    public static void closeAllResources(ResultSet rs, Statement pstmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (pstmt != null) {
                pstmt.close();
                pstmt = null;
            }
            if ((conn != null) && (!conn.isClosed())) {
                conn.close();
                conn = null;
            }
        } catch (SQLException sqle) {
        }
    }

    /**
     * Closes connection
     * @param conn Connection
     */
    public static void closeConnection(Connection conn) {
        try {
            if ((conn != null) && (!conn.isClosed())) {
                conn.close();
                conn = null;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Commits connection
     * @param conn connection
     */
    public static void commit(Connection conn) {
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rollbacks connection
     * @param conn connection
     */
    public static void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns result
     * @param rset Resultset
     * @return Results
     * @throws SQLException If an error occurs.
     */
    public static String[][] getResults(ResultSet rset) throws SQLException {
        Vector rvec = new Vector(); // Return value as Vector
        String[][] rval = {}; // Return value

        // if (logger.enable(logger.DEBUG)) logger.debug(sql);

        // Process the result set

        try {

            ResultSetMetaData md = rset.getMetaData();

            // number of columns in the result set
            int colCnt = md.getColumnCount();

            while (rset.next()) {
                String[] row = new String[colCnt]; // Row of the result set

                // Retrieve the columns of the result set
                for (int i = 0; i < colCnt; ++i) {
                    row[i] = rset.getString(i + 1);
                }

                rvec.addElement(row); // Store the row into the vector
            }
        } catch (SQLException e) {
            // logger.error("Error occurred when processing result set: " +
            // sql,e);
            LOGGER.error(e.getMessage());
            throw new SQLException("Error occurred when processing result set: " + "");
        }

        // Build return value
        if (rvec.size() > 0) {
            rval = new String[rvec.size()][];

            for (int i = 0; i < rvec.size(); ++i) {
                rval[i] = (String[]) rvec.elementAt(i);
            }
        }

        // Success
        return rval;
    }

    /**
     * Returns last insert id
     * @return Insert id
     * @throws SQLException If an error occurs.
     */
    protected String getLastInsertID() throws SQLException {
        Connection con = null;
        String lastInsertId = null;

        con = getConnection();

        String qry = "SELECT LAST_INSERT_ID()";

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(qry);
        rs.clearWarnings();
        if (rs.next()) {
            lastInsertId = rs.getString(1);
        }
        closeAllResources(rs, stmt, con);
        return lastInsertId;
    }

    /**
     * Executes simple query.
     * @param query SQL query
     * @return Result
     * @throws SQLException If an error occurs.
     */
    protected String[][] executeSimpleQuery(String query) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[][] r;

        if (isDebugMode) {
            LOGGER.debug("Query is " + query);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            r = getResults(rs);
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return r;

    }
}
