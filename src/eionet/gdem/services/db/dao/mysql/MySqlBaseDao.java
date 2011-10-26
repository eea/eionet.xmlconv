package eionet.gdem.services.db.dao.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.services.GDEMServices;

public abstract class MySqlBaseDao {

    /** */
    private static final Log LOGGER = LogFactory.getLog(MySqlBaseDao.class);

    private static DataSource ds = null;

    protected static boolean isDebugMode = LOGGER.isDebugEnabled();

    public MySqlBaseDao() {
    }

    /**
     * Init JNDI datasource
     *
     * @throws NamingException
     */
    private static void initDataSource() throws NamingException {
        try {
            InitialContext ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/GDEM_DB");
        } catch (NamingException e) {
            LOGGER.fatal("Initialization of datasource  (jdbc/GDEM_DB) failed: ", e);
        }
    }

    /**
     * Returns new database connection.
     *
     * @throw ServiceException if no connections were available.
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (GDEMServices.isTestConnection()) {
            return getSimpleConnection();
        } else {
            return getJNDIConnection();
        }
    }

    /**
     * Returns new database connection. Read properties from Context
     *
     * @throw ServiceException if no connections were available.
     */
    public static synchronized Connection getJNDIConnection() throws SQLException {
        try {
            if (ds == null) {
                initDataSource();
            }
            return ds.getConnection();
        } catch (Exception e) {
            throw new SQLException("Failed to get connection through JNDI: " + e.toString());
        }
    }

    /**
     * Returns new database connection. Read properties from gdem.properties
     *
     * @return
     * @throws SQLException
     * @throws GDEMException
     */
    private static synchronized Connection getSimpleConnection() throws SQLException {

        String drv = Properties.dbDriver;
        if (drv == null || drv.trim().length() == 0) {
            throw new SQLException("Failed to get connection, missing property: " + Properties.dbDriver);
        }

        String url = Properties.dbUrl;
        if (url == null || url.trim().length() == 0) {
            throw new SQLException("Failed to get connection, missing property: " + Properties.dbUrl);
        }

        String usr = Properties.dbUser;
        if (usr == null || usr.trim().length() == 0) {
            throw new SQLException("Failed to get connection, missing property: " + Properties.dbUser);
        }

        String pwd = Properties.dbPwd;
        if (pwd == null || pwd.trim().length() == 0) {
            throw new SQLException("Failed to get connection, missing property: " + Properties.dbPwd);
        }

        try {
            Class.forName(drv);
            return DriverManager.getConnection(url, usr, pwd);
        } catch (ClassNotFoundException e) {
            throw new SQLException((new StringBuilder()).append("Failed to get connection, driver class not found: ").append(drv)
                    .append(".").append(e.toString()).toString());
        }
    }

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

    public static void commit(Connection conn) {
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String[][] getResults(ResultSet rset) throws SQLException {
        Vector rvec = new Vector(); // Return value as Vector
        String rval[][] = {}; // Return value

        // if (logger.enable(logger.DEBUG)) logger.debug(sql);

        // Process the result set

        try {

            ResultSetMetaData md = rset.getMetaData();

            // number of columns in the result set
            int colCnt = md.getColumnCount();

            while (rset.next()) {
                String row[] = new String[colCnt]; // Row of the result set

                // Retrieve the columns of the result set
                for (int i = 0; i < colCnt; ++i) {
                    row[i] = rset.getString(i + 1);
                }

                rvec.addElement(row); // Store the row into the vector
            }
        } catch (SQLException e) {
            // logger.error("Error occurred when processing result set: " +
            // sql,e);
            LOGGER.error(e);
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

    protected String[][] executeSimpleQuery(String query) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String r[][];

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
