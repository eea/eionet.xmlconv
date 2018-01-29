package eionet.gdem.web.spring.hosts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;


import eionet.gdem.database.MySqlBaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eionet.gdem.utils.Utils;

/**
 * Host MySQL Dao class.
 * @author Unknown
 */
@Repository("hostDao")
public class HostMySqlDao extends MySqlBaseDao implements IHostDao {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(HostMySqlDao.class);

    /**
     * Table for hosts with usernames and passwords in the DB.
     */
    public static final String HOST_TABLE = "T_HOST";

    /**
     * Field names in HOSTS table.
     */
    public static final String HOST_ID_FLD = "HOST_ID";
    public static final String HOST_NAME_FLD = "HOST_NAME";
    public static final String USER_FLD = "USER";
    public static final String PWD_FLD = "PWD";

    private static final String qAddHost = "INSERT INTO " + HOST_TABLE + " ( " + HOST_NAME_FLD + ", " + USER_FLD + ", " + PWD_FLD
    + ") VALUES (?,?,?)";
    private static final String qUpdateHost = "UPDATE " + HOST_TABLE + " SET " + HOST_NAME_FLD + "=?" + ", " + USER_FLD + "=?"
    + ", " + PWD_FLD + "=?" + " WHERE " + HOST_ID_FLD + "=?";
    private static final String qRemoveHost = "DELETE FROM " + HOST_TABLE + " WHERE " + HOST_ID_FLD + "=?";
    private static final String qAllHosts = "SELECT " + HOST_ID_FLD + ", " + HOST_NAME_FLD + ", " + USER_FLD + ", " + PWD_FLD
    + " FROM " + HOST_TABLE + " ORDER BY " + HOST_NAME_FLD;
    private static final String qHostByID = "SELECT " + HOST_ID_FLD + ", " + HOST_NAME_FLD + ", " + USER_FLD + ", " + PWD_FLD
    + " FROM " + HOST_TABLE + " WHERE " + HOST_ID_FLD + "=?" + " ORDER BY " + HOST_NAME_FLD;
    private static final String qHostByName = "SELECT " + HOST_ID_FLD + ", " + HOST_NAME_FLD + ", " + USER_FLD + ", " + PWD_FLD
    + " FROM " + HOST_TABLE + " WHERE " + HOST_NAME_FLD + " like ? " + " ORDER BY " + HOST_NAME_FLD;

    @Override
    public String addHost(String hostName, String userName, String pwd) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        hostName = (hostName == null ? "" : hostName);

        if (isDebugMode) {
            LOGGER.debug("Query is " + qAddHost);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qAddHost);
            pstmt.setString(1, hostName);
            pstmt.setString(2, userName);
            pstmt.setString(3, pwd);
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
        return getLastInsertID();
    }

    /*
     * public void updateHost(String hostId, String hostName, String userName, String pwd) throws SQLException {
     *
     * hostName = (hostName == null ? "" : hostName);
     *
     * String sql = "UPDATE " + HOST_TABLE + " SET " + HOST_NAME_FLD + "=" + Utils.strLiteral(hostName) + ", " + USER_FLD + "=" +
     * Utils.strLiteral(userName) + ", " + PWD_FLD + "=" + Utils.strLiteral(pwd) + " WHERE " + HOST_ID_FLD + "=" + hostId;
     *
     * _executeUpdate(sql);
     *
     * }
     */
    @Override
    public void updateHost(String hostId, String hostName, String userName, String pwd) throws SQLException {
        hostName = (hostName == null ? "" : hostName);
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qUpdateHost);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qUpdateHost);
            pstmt.setString(1, hostName);
            pstmt.setString(2, userName);
            pstmt.setString(3, pwd);
            pstmt.setInt(4, Integer.parseInt(hostId));
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    /*
     * public void removeHost(String hostId) throws SQLException {
     *
     * String sql = "DELETE FROM " + HOST_TABLE + " WHERE " + HOST_ID_FLD + "=" + hostId; _executeUpdate(sql);
     *
     * }
     */
    @Override
    public void removeHost(String hostId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qRemoveHost);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qRemoveHost);
            pstmt.setInt(1, Integer.parseInt(hostId));
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    /*
     * public Vector getHosts(String host) throws SQLException {
     *
     * StringBuffer sql_buf = new StringBuffer("SELECT " + HOST_ID_FLD + ", " + HOST_NAME_FLD + ", " + USER_FLD + ", " + PWD_FLD +
     * " FROM " + HOST_TABLE); if (!Utils.isNullStr(host)) { if (Utils.isNum(host)) { sql_buf.append(" WHERE " + HOST_ID_FLD + "=" +
     * host); } else { sql_buf.append(" WHERE " + HOST_NAME_FLD + " like '%" + host + "%'"); } } sql_buf.append(" ORDER BY " +
     * HOST_NAME_FLD);
     *
     * String r[][] = _executeStringQuery(sql_buf.toString());
     *
     * Vector v = new Vector();
     *
     * for (int i = 0; i < r.length; i++) { Hashtable h = new Hashtable(); h.put("host_id", r[i][0]); h.put("host_name", r[i][1]);
     * h.put("user_name", r[i][2]); h.put("pwd", r[i][3]); v.add(h); }
     *
     * return v; }
     */
    private static final int ALL_HOSTS = 1;
    private static final int HOST_BY_ID = 2;
    private static final int HOST_BY_NAME = 3;

    @Override
    public Vector getHosts(String host) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector v = null;

        int queryType = ALL_HOSTS;
        if (!Utils.isNullStr(host)) {
            if (Utils.isNum(host)) {
                queryType = HOST_BY_ID;
            } else {
                queryType = HOST_BY_NAME;
            }
        }

        try {
            conn = getConnection();

            switch (queryType) {
                case ALL_HOSTS:
                    pstmt = conn.prepareStatement(qAllHosts);
                    break;
                case HOST_BY_ID:
                    pstmt = conn.prepareStatement(qHostByID);
                    pstmt.setInt(1, Integer.parseInt(host));
                    break;
                case HOST_BY_NAME:
                    pstmt = conn.prepareStatement(qHostByName);
                    pstmt.setString(1, "%" + host + "%");
                    break;
                default:
                    break;
            }

            if (isDebugMode) {
                LOGGER.debug("Query is "
                        + ((queryType == ALL_HOSTS) ? qAllHosts : (queryType == HOST_BY_ID) ? qHostByID : qHostByName));
            }
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            v = new Vector(r.length);

            for (int i = 0; i < r.length; i++) {
                Hashtable h = new Hashtable();
                h.put("host_id", r[i][0]);
                h.put("host_name", r[i][1]);
                h.put("user_name", r[i][2]);
                h.put("pwd", r[i][3]);
                v.add(h);
            }
        } finally {
            closeAllResources(null, pstmt, conn);
        }
        return v;
    }
}
