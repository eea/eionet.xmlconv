/*
 * Created on 16.11.2007
 */
package eionet.gdem.web.spring.xmlfile;

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

/**
 * MySql implementationd for uplodaed XML file database object.
 *
 * @author Enriko Käsper (TietoEnator)
 *
 */
@Repository("uplXmlFileDao")
public class UplXmlFileMySqlDao extends MySqlBaseDao implements IUPLXmlFileDao {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(UplXmlFileMySqlDao.class);

    /**
     * Table for storing file info in repository.
     */
    public static final String FILE_TABLE = "T_FILE";

    /**
     * Field names in FILE table.
     */
    public static final String FILE_ID_FLD = "FILE_ID";
    public static final String FILE_NAME_FLD = "FILE_NAME";
    public static final String FILE_TITLE_FLD = "TITLE";
    public static final String FILE_TYPE_FLD = "TYPE";
    public static final String FILE_PARENTTYPE_FLD = "PARENT_TYPE";
    public static final String FILE_PARENTID_FLD = "PARENT_ID";
    public static final String FILE_DESCRIPTION_FLD = "DESCRIPTION";
    public static final String FILE_DEFAULT_FLD = "F_DEFAULT";
    public static final String XML_FILE_TYPE = "xml";

    // query for getting all XML files
    private static final String qUplXmlFile = "SELECT " + FILE_ID_FLD + ", " + FILE_NAME_FLD + ", " + FILE_TITLE_FLD + " FROM "
    + FILE_TABLE + " WHERE " + FILE_TYPE_FLD + "='" + XML_FILE_TYPE + "'" + " ORDER BY " + FILE_NAME_FLD;

    // query for getting 1 row by file ID
    private static final String qUplXmlFileByID = "SELECT " + FILE_ID_FLD + ", " + FILE_NAME_FLD + "," + FILE_TITLE_FLD + " FROM "
    + FILE_TABLE + " WHERE " + FILE_ID_FLD + "= ?";
    // query for inserting new XML file
    private static final String qInsertUplXmlFile = "INSERT INTO " + FILE_TABLE + " ( " + FILE_NAME_FLD + " ," + FILE_TITLE_FLD
    + ", " + FILE_TYPE_FLD + ") " + "VALUES (?,?,?)";
    // query for updating XML file row
    private static final String qUpdateUplXmlFile = "UPDATE  " + FILE_TABLE + " SET " + FILE_TITLE_FLD + "= ? " + ", "
    + FILE_TYPE_FLD + "= ?, " + FILE_NAME_FLD + " = ? "+ " WHERE " + FILE_ID_FLD + "= ? ";

    // query for deleting XML file row
    private static final String qRemoveUplXmlFile = "DELETE FROM " + FILE_TABLE + " WHERE " + FILE_ID_FLD + "= ?";

    // query for checking duplicate xml files by file name
    private static final String checkUplXmlFile = "SELECT COUNT(*) FROM " + FILE_TABLE + " WHERE " + FILE_TYPE_FLD + "='"
    + XML_FILE_TYPE + "' AND " + FILE_NAME_FLD + "= ?";

    // query for getting xml file name by ID
    private static final String qUplXmlFileNameById = "SELECT " + FILE_NAME_FLD + " FROM " + FILE_TABLE + " WHERE " + FILE_ID_FLD
    + "= ?";

    @Override
    public Vector getUplXmlFile() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector v = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qUplXmlFile);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qUplXmlFile);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            v = new Vector(r.length);
            for (int i = 0; i < r.length; i++) {
                Hashtable h = new Hashtable();
                h.put("id", r[i][0]);
                h.put("file_name", r[i][1]);
                h.put("title", r[i][2]);
                v.add(h);
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return v;
    }

    @Override
    public void removeUplXmlFile(String uplXmlFileId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qRemoveUplXmlFile);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qRemoveUplXmlFile);
            pstmt.setInt(1, Integer.parseInt(uplXmlFileId));
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    @Override
    public void updateUplXmlFile(String uplXmlFileId, String title, String fileName) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        title = (title == null ? "" : title);

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            if (isDebugMode) {
                LOGGER.debug("Query is " + qUpdateUplXmlFile);
            }
            pstmt = conn.prepareStatement(qUpdateUplXmlFile);
            pstmt.setString(1, title);
            pstmt.setString(2, XML_FILE_TYPE);
            pstmt.setString(3, fileName);
            pstmt.setInt(4, Integer.parseInt(uplXmlFileId));
            pstmt.executeUpdate();
            pstmt.close();

            conn.commit();
        } catch (SQLException sqle) {
            if (conn != null) {
                conn.rollback();
            }
            throw new SQLException(sqle.getMessage(), sqle.getSQLState());
        } finally {
            closeAllResources(null, pstmt, conn);
        }

    }

    @Override
    public String getUplXmlFileName(String uplXmlFileId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String result = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qUplXmlFileNameById);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qUplXmlFileNameById);

            pstmt.setInt(1, Integer.parseInt(uplXmlFileId));
            rs = pstmt.executeQuery();

            String[][] r = getResults(rs);
            if (r.length == 0) {
                return null;
            }

            result = r[0][0];
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return result;
    }

    @Override
    public Hashtable getUplXmlFileById(String xmlFileId) throws SQLException {
        int id = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Hashtable h = null;

        if (xmlFileId == null) {
            throw new SQLException("XML file ID not defined");
        }
        try {
            id = Integer.parseInt(xmlFileId);
        } catch (NumberFormatException n) {
            throw new SQLException("not numeric ID " + xmlFileId);
        }

        if (isDebugMode) {
            LOGGER.debug("Query is " + qUplXmlFileByID);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qUplXmlFileByID);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);

            h = new Hashtable();
            h.put("file_id", r[0][0]);
            h.put("file_name", r[0][1]);
            h.put("title", r[0][2]);
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return h;
    }

    @Override
    public String addUplXmlFile(String xmlFileName, String title) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qInsertUplXmlFile);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qInsertUplXmlFile);
            pstmt.setString(1, xmlFileName);
            pstmt.setString(2, title);
            pstmt.setString(3, XML_FILE_TYPE);
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
        return getLastInsertID();
    }

    @Override
    public boolean checkUplXmlFile(String xmlFileName) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean result = false;

        if (isDebugMode) {
            LOGGER.debug("Query is " + checkUplXmlFile);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(checkUplXmlFile);
            pstmt.setString(1, xmlFileName);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            String count = r[0][0];
            if (!count.equals("0")) {
                result = true;
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return result;
    }

}
