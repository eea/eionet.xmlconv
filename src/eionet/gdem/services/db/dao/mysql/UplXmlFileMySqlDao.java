/*
 * Created on 16.11.2007
 */
package eionet.gdem.services.db.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import eionet.gdem.services.db.dao.IUPLXmlFileDao;

/**
 * MySql implementationd for uplodaed XML file database object.
 *
 * @author Enriko Käsper (TietoEnator)
 *
 */
public class UplXmlFileMySqlDao extends MySqlBaseDao implements IUPLXmlFileDao{

    //query for getting all XML files
    private static final String qUplXmlFile = 	"SELECT "
        + FILE_ID_FLD + ", "
        + FILE_NAME_FLD + ", "
        + FILE_TITLE_FLD
        + " FROM " + FILE_TABLE
        + " WHERE " + FILE_TYPE_FLD + "='" + XML_FILE_TYPE + "'"
        + " ORDER BY " + FILE_NAME_FLD;

    //query for getting 1 row by file ID
    private static final String  qUplXmlFileByID = 	"SELECT "
        + FILE_ID_FLD + ", "
        + FILE_NAME_FLD + ","
        + FILE_TITLE_FLD
        + " FROM " + FILE_TABLE
        + " WHERE " + FILE_ID_FLD + "= ?";
    //query for inserting new XML file
    private static final String qInsertUplXmlFile = "INSERT INTO "
        + FILE_TABLE
        + " ( "
        + FILE_NAME_FLD + " ,"
        + FILE_TITLE_FLD + ", "
        + FILE_TYPE_FLD
        + ") "
        + "VALUES (?,?,?)";
    //query for updating XML file row
    private static final String qUpdateUplXmlFile = 	"UPDATE  " + FILE_TABLE +
        " SET "
        + FILE_TITLE_FLD + "= ? "  + ", "
        + FILE_TYPE_FLD + "= ? "
        + " WHERE " + FILE_ID_FLD + "= ? ";

    //query for deleting XML file row
    private static final String qRemoveUplXmlFile = "DELETE FROM " + FILE_TABLE + " WHERE " + FILE_ID_FLD + "= ?";

    //query for checking duplicate xml files by file name
    private static final String  checkUplXmlFile = "SELECT COUNT(*) FROM " + FILE_TABLE + " WHERE " + FILE_TYPE_FLD + "='"
        + XML_FILE_TYPE + "' AND " + FILE_NAME_FLD + "= ?";

    //query for getting xml file name by ID
    private static final String qUplXmlFileNameById = "SELECT " + FILE_NAME_FLD + " FROM " + FILE_TABLE + " WHERE " + FILE_ID_FLD + "= ?";


    private SchemaMySqlDao schemaDao;

    public UplXmlFileMySqlDao() {
    }


    public Vector getUplXmlFile() throws SQLException{
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs =null;
        Vector v = null;

        if (isDebugMode){ logger.debug("Query is " + qUplXmlFile);}

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
        }
        finally {
            closeAllResources(rs,pstmt,conn);
        }
        return v;
    }

    public void removeUplXmlFile(String uplXmlFileId) throws SQLException{
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode){ logger.debug("Query is " + qRemoveUplXmlFile);}

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(qRemoveUplXmlFile);
            pstmt.setInt(1, Integer.parseInt(uplXmlFileId));
            pstmt.executeUpdate();
        }finally{
            closeAllResources(null,pstmt,conn);
        }
    }


    public void updateUplXmlFile(String uplXmlFileId, String title) throws SQLException{
        Connection conn = null;
        PreparedStatement pstmt = null;

        title = (title == null ? "" : title);

        try{
            conn = getConnection();
            conn.setAutoCommit(false);

            if (isDebugMode){ logger.debug("Query is " + qUpdateUplXmlFile);}
            pstmt = conn.prepareStatement(qUpdateUplXmlFile);
            pstmt.setString(1,title);
            pstmt.setString(2,XML_FILE_TYPE);
            pstmt.setInt(3,Integer.parseInt(uplXmlFileId));
            pstmt.executeUpdate();
            pstmt.close();

            conn.commit();
        }
        catch(SQLException sqle){
            if (conn != null) conn.rollback();
            throw new SQLException(sqle.getMessage(),sqle.getSQLState());
        }finally{
            closeAllResources(null,pstmt,conn);
        }

    }

    public String getUplXmlFileName(String uplXmlFileId) throws SQLException{
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs =null;
         String result = null;

        if (isDebugMode){ logger.debug("Query is " + qUplXmlFileNameById);}

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qUplXmlFileNameById);

            pstmt.setInt(1,Integer.parseInt(uplXmlFileId));
            rs = pstmt.executeQuery();

            String[][] r = getResults(rs);
            if (r.length == 0) return null;

            result = r[0][0];
        }
        finally {
            closeAllResources(rs,pstmt,conn);
        }
        return result;
    }

    public Hashtable getUplXmlFileById(String xmlFileId) throws SQLException{
        int id = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs =null;
        Hashtable h = null;

        if (xmlFileId == null) throw new SQLException("XML file ID not defined");
        try {
            id = Integer.parseInt(xmlFileId);
        } catch (NumberFormatException n) {
            throw new SQLException("not numeric ID " + xmlFileId);
        }

        if (isDebugMode){ logger.debug("Query is " + qUplXmlFileByID);}

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qUplXmlFileByID);
            pstmt.setInt(1,id);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);

            h = new Hashtable();
            h.put("file_id", r[0][0]);
            h.put("file_name", r[0][1]);
            h.put("title", r[0][2]);
        }
        finally {
            closeAllResources(rs,pstmt,conn);
        }
        return h;
    }


    public String addUplXmlFile(String xmlFileName, String title) throws SQLException{
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode){ logger.debug("Query is " + qInsertUplXmlFile);}
        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(qInsertUplXmlFile);
            pstmt.setString(1, xmlFileName);
            pstmt.setString(2, title);
            pstmt.setString(3, XML_FILE_TYPE);
            pstmt.executeUpdate();
        }finally{
            closeAllResources(null,pstmt,conn);
        }
        return getLastInsertID();
    }

    public boolean checkUplXmlFile(String xmlFileName) throws SQLException{
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs =null;
        boolean result = false;

        if (isDebugMode){ logger.debug("Query is " + checkUplXmlFile);}

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(checkUplXmlFile);
            pstmt.setString(1,xmlFileName);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            String count = r[0][0];
            if (!count.equals("0"))
                result = true;
        }
        finally {
            closeAllResources(rs,pstmt,conn);
        }
        return result;
    }

}
