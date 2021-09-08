package eionet.gdem.qa;

import eionet.gdem.Properties;
import eionet.gdem.database.MySqlBaseDao;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import static eionet.gdem.web.spring.conversions.ConvTypeMySqlDao.CONTENT_TYPE_FLD;
import static eionet.gdem.web.spring.conversions.ConvTypeMySqlDao.CONVTYPE_TABLE;
import static eionet.gdem.web.spring.conversions.ConvTypeMySqlDao.CONV_TYPE_FLD;
import static eionet.gdem.web.spring.conversions.StyleSheetMySqlDao.DESCR_FLD;
import static eionet.gdem.web.spring.conversions.StyleSheetMySqlDao.RESULT_TYPE_FLD;
import static eionet.gdem.web.spring.conversions.StyleSheetMySqlDao.XSL_SCHEMA_ID_FLD;
import static eionet.gdem.web.spring.schemas.SchemaMySqlDao.SCHEMA_ID_FLD;
import static eionet.gdem.web.spring.schemas.SchemaMySqlDao.SCHEMA_TABLE;
import static eionet.gdem.web.spring.schemas.SchemaMySqlDao.XML_SCHEMA_FLD;

/**
 * Query MySQL Dao class.
 * @author Unknown
 * @author George Sofianos
 */
@Repository("queryDao")
public class QueryMySqlDao extends MySqlBaseDao implements IQueryDao {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryMySqlDao.class);

    /**
     * Table for queries in the DB.
     */
    public static final String QUERY_TABLE = "T_QUERY";

    /**
     * Field names in QUERY table.
     */
    public static final String QUERY_ID_FLD = "QUERY_ID";
    public static final String SHORT_NAME_FLD = "SHORT_NAME";
    public static final String QUERY_FILE_FLD = "QUERY_FILENAME";
    public static final String QUERY_RESULT_TYPE = "RESULT_TYPE";
    public static final String QUERY_SCRIPT_TYPE = "SCRIPT_TYPE";
    public static final String UPPER_LIMIT_FLD = "UPPER_LIMIT";
    public static final String QUERY_URL_FLD    = "URL";
    public static final String ACTIVE_FLD = "ACTIVE";
    public static final String JOB_RETRY_COUNTER = "RETRY_COUNTER";
    public static final String ASYNCHRONOUS_EXECUTION_FLD = "ASYNCHRONOUS_EXECUTION";

    private static final String qListQueries = "SELECT " + QUERY_TABLE + "." + QUERY_ID_FLD + ", " + SHORT_NAME_FLD + ", "
            + QUERY_FILE_FLD + ", " + QUERY_TABLE + "." + DESCR_FLD + "," + SCHEMA_TABLE + "." + SCHEMA_ID_FLD + ","
            + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + ", " + QUERY_TABLE + "." + RESULT_TYPE_FLD + ", " + CONVTYPE_TABLE + "."
            + CONTENT_TYPE_FLD + ", " + QUERY_TABLE + "." + QUERY_SCRIPT_TYPE + "," + QUERY_TABLE + "." + UPPER_LIMIT_FLD
            + ", " + QUERY_TABLE + "." + QUERY_URL_FLD + ", " + QUERY_TABLE +"."+ ACTIVE_FLD
            + " FROM " + QUERY_TABLE + " LEFT OUTER JOIN " + SCHEMA_TABLE + " ON " + QUERY_TABLE + "." + XSL_SCHEMA_ID_FLD + "="
            + SCHEMA_TABLE + "." + SCHEMA_ID_FLD + " LEFT OUTER JOIN " + CONVTYPE_TABLE + " ON " + QUERY_TABLE + "."
            + RESULT_TYPE_FLD + "=" + CONVTYPE_TABLE + "." + CONV_TYPE_FLD;

    private static final String qListQueriesBySchema = qListQueries + " WHERE " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + "= ?";

    private static final String qListQueriesByActive = qListQueries + " WHERE " + QUERY_TABLE + "." + ACTIVE_FLD + "= ?";

    private static final String qListQueriesBySchemaAndActive = qListQueries + " WHERE " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + "= ?" + " AND " + QUERY_TABLE + "." + ACTIVE_FLD + "= ?";

    private static final String qQueryTextByFileName = "SELECT " + QUERY_FILE_FLD + " FROM " + QUERY_TABLE + " WHERE "
            + QUERY_FILE_FLD + "= ?";
    private static final String qQueryTextByID = "SELECT " + QUERY_FILE_FLD + " FROM " + QUERY_TABLE + " WHERE " + QUERY_ID_FLD
            + "= ?";

    private static final String qQueryInfo = "SELECT " + QUERY_TABLE + "." + XSL_SCHEMA_ID_FLD + "," + QUERY_FILE_FLD + ", "
            + QUERY_TABLE + "." + DESCR_FLD + "," + SHORT_NAME_FLD + ", " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + ","
            + QUERY_TABLE + "." + RESULT_TYPE_FLD + ", " + CONVTYPE_TABLE + "." + CONTENT_TYPE_FLD + "," + QUERY_TABLE + "."
            + QUERY_SCRIPT_TYPE + "," + QUERY_TABLE + "." + UPPER_LIMIT_FLD + "," + QUERY_TABLE + "." + QUERY_URL_FLD + "," + ACTIVE_FLD + ","+ QUERY_TABLE + "." + ASYNCHRONOUS_EXECUTION_FLD
            + " FROM " + QUERY_TABLE + " LEFT OUTER JOIN "
            + SCHEMA_TABLE + " ON " + QUERY_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD
            + " LEFT OUTER JOIN " + CONVTYPE_TABLE + " ON " + QUERY_TABLE + "." + RESULT_TYPE_FLD + "=" + CONVTYPE_TABLE + "."
            + CONV_TYPE_FLD;

    private static final String qQueryInfoByID = qQueryInfo + " WHERE " + QUERY_ID_FLD + "=?";
    private static final String qQueryInfoByFileName = qQueryInfo + " WHERE " + QUERY_FILE_FLD + "=?";
    
    private static final String qQueryUpdateActive = "UPDATE " + QUERY_TABLE + " SET " + ACTIVE_FLD + "=? WHERE " + QUERY_ID_FLD + "=?" ;
    
    private static final String qRemoveQuery = "DELETE FROM " + QUERY_TABLE + " WHERE " + QUERY_ID_FLD + "=?";
    private static final String qUpdateQuery = "UPDATE  " + QUERY_TABLE + " SET " + QUERY_FILE_FLD + "=?" + ", " + SHORT_NAME_FLD
            + "=?" + ", " + DESCR_FLD + "=?" + ", " + XSL_SCHEMA_ID_FLD + "=?" + ", " + RESULT_TYPE_FLD + "=?" + ", "
            + QUERY_SCRIPT_TYPE + "=?" + ", " + UPPER_LIMIT_FLD + "=?" + ", " + QUERY_URL_FLD + "=?" + ", " + ASYNCHRONOUS_EXECUTION_FLD + "=?" + " WHERE "
            + QUERY_ID_FLD + "=?";

    private static final String qInsertQuery = "INSERT INTO " + QUERY_TABLE + " ( " + XSL_SCHEMA_ID_FLD + ", " + SHORT_NAME_FLD
            + ", " + QUERY_FILE_FLD + ", " + DESCR_FLD + ", " + RESULT_TYPE_FLD + ", " + QUERY_SCRIPT_TYPE + ", "
            + UPPER_LIMIT_FLD + "," + QUERY_URL_FLD + "," + ASYNCHRONOUS_EXECUTION_FLD + ") " + " VALUES (?,?,?,?,?,?,?,?,?)";

    private static final String qQueryByFileName = "SELECT " + QUERY_ID_FLD + " FROM " + QUERY_TABLE + " WHERE " + QUERY_FILE_FLD
            + "=?";

    private static final String qCheckQueryFileByName = "SELECT COUNT(*) FROM " + QUERY_TABLE + " WHERE " + QUERY_FILE_FLD + "=?";

    private static final String qCheckQueryFileByIdAndName = "SELECT COUNT(*) FROM " + QUERY_TABLE + " WHERE " + QUERY_FILE_FLD
            + "=?" + " and " + QUERY_ID_FLD + "=?";

    private static final String qGetAsynchronousExecution = "SELECT " + ASYNCHRONOUS_EXECUTION_FLD + " FROM " + QUERY_TABLE + " WHERE " + QUERY_ID_FLD + "=?";


    /**
     * Adds Query
     * @param xmlSchemaID Xml Schema Id
     * @param shortName Short name
     * @param queryFileName Query file name
     * @param description Description
     * @param content_type Content type
     * @param script_type Script type
     * @param upperLimit Upper limit
     * @param url URL
     * @param asynchronousExecution
     * @return Result
     * @throws SQLException
     */
    @Override
    public String addQuery(String xmlSchemaID, String shortName, String queryFileName, String description, String content_type,
            String script_type, String upperLimit, String url, Boolean asynchronousExecution) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String result = null;

        description = (description == null ? "" : description);

        if (isDebugMode) {
            LOGGER.debug("Query is " + qInsertQuery);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qInsertQuery);
            pstmt.setInt(1, Integer.parseInt(xmlSchemaID));
            pstmt.setString(2, shortName);
            pstmt.setString(3, queryFileName);
            pstmt.setString(4, description);
            pstmt.setString(5, content_type);
            pstmt.setString(6, script_type);
            pstmt.setInt(7, Integer.parseInt(upperLimit));
            pstmt.setString(8, url);
            pstmt.setBoolean(9, asynchronousExecution);

            pstmt.executeUpdate();

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt = conn.prepareStatement(qQueryByFileName);
            pstmt.setString(1, queryFileName);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            if (r.length == 0) {
                throw new SQLException("Error when returning id  for " + queryFileName + " ");
            }
            result = r[0][0];
        } finally {
            closeAllResources(rs, pstmt, conn);
        }

        return result;
    }

    /**
     * Update query
     * @param query_id - id from database, used as a constraint
     * @param schema_id - schema id
     * @param short_name - db field for title
     * @param description - text describing the query
     * @param fileName - query file name
     * @param content_type - result content type
     * @param script_type - xquery, xsl, xgawk
     * @param upperLimit - result upper limit in MB
     * @param url - original url of the XQ file
     * @param asynchronousExecution
     * @throws SQLException If an error occurs.
     */
    @Override
    public void updateQuery(String query_id, String schema_id, String short_name, String description, String fileName,
            String content_type, String script_type, String upperLimit, String url, Boolean asynchronousExecution) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        short_name = (short_name == null ? "" : short_name);
        description = (description == null ? "" : description);

        if (isDebugMode) {
            LOGGER.debug("Query is " + qUpdateQuery);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qUpdateQuery);
            pstmt.setString(1, fileName);
            pstmt.setString(2, short_name);
            pstmt.setString(3, description);
            pstmt.setString(4, schema_id);
            pstmt.setString(5, content_type);
            pstmt.setString(6, script_type);
            pstmt.setInt(7, Integer.parseInt(upperLimit));
            pstmt.setString(8, url);
            pstmt.setBoolean(9, asynchronousExecution);

            pstmt.setInt(10, Integer.parseInt(query_id));


            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }

    }

    @Override
    public void removeQuery(String queryId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qRemoveQuery);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qRemoveQuery);
            pstmt.setInt(1, Integer.parseInt(queryId));
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }

    }

    @Override
    public HashMap getQueryInfo(String queryId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int id = 0;
        String queryName = null;
        HashMap h = null;
        try {
            id = Integer.parseInt(queryId);
        } catch (NumberFormatException n) {
            if (queryId.contains(".")) {
                queryName = queryId;
            } else {
                throw new SQLException("not numeric ID or xql file name: " + queryId);
            }
        }

        try {
            conn = getConnection();
            if (queryName != null) {
                pstmt = conn.prepareStatement(qQueryInfoByFileName);
                pstmt.setString(1, queryName);
            } else {
                pstmt = conn.prepareStatement(qQueryInfoByID);
                pstmt.setInt(1, id);
            }
            if (isDebugMode) {
                LOGGER.debug("Query is " + ((queryName != null) ? qQueryInfoByFileName : qQueryInfoByID));
            }

            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);

            if (r.length > 0) {
                h = new HashMap();
                h.put(QaScriptView.QUERY_ID, queryId);
                h.put(QaScriptView.SCHEMA_ID, r[0][0]);
                h.put(QaScriptView.QUERY, r[0][1]);
                h.put(QaScriptView.DESCRIPTION, r[0][2]);
                h.put(QaScriptView.SHORT_NAME, r[0][3]);
                h.put(QaScriptView.XML_SCHEMA, r[0][4]);
                h.put(QaScriptView.CONTENT_TYPE, r[0][5]);
                h.put(QaScriptView.META_TYPE, r[0][6]);
                h.put(QaScriptView.SCRIPT_TYPE, r[0][7]);
                h.put(QaScriptView.UPPER_LIMIT, r[0][8]);
                h.put(QaScriptView.URL, r[0][9]);
                h.put(QaScriptView.IS_ACTIVE, r[0][10]);
                h.put(QaScriptView.ASYNCHRONOUS_EXECUTION, r[0][11]);
            }

        } finally {
            closeAllResources(rs, pstmt, conn);
        }

        return h;

    }

    @Override
    public String getQueryText(String queryId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int id = 0;
        String qText = "";
        String queryName = null;
        try {
            id = Integer.parseInt(queryId);
        } catch (NumberFormatException n) {
            if (queryId.endsWith("xql")) {
                queryName = queryId;
            } else {
                throw new SQLException("not numeric ID or xql file name: " + queryId);
            }
        }

        try {
            conn = getConnection();
            if (queryName != null) {
                pstmt = conn.prepareStatement(qQueryTextByFileName);
                pstmt.setString(1, queryName);
            } else {
                pstmt = conn.prepareStatement(qQueryTextByID);
                pstmt.setInt(1, id);
            }
            if (isDebugMode) {
                LOGGER.debug("Query is " + ((queryName != null) ? qQueryTextByFileName : qQueryTextByID));
            }

            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);

            if (r.length > 0) {
                String queriesFolder = Properties.queriesFolder;
                if (!queriesFolder.endsWith(File.separator)) {
                    queriesFolder = queriesFolder + File.separator;
                }
                try {
                    qText = Utils.readStrFromFile(queriesFolder + r[0][0]);
                } catch (IOException e) {
                    LOGGER.error(FILEREAD_EXCEPTION, e);
                    qText = FILEREAD_EXCEPTION + queriesFolder + r[0][0] + "\n " + e.toString();
                }
            }

        } finally {
            closeAllResources(rs, pstmt, conn);
        }

        return qText;
    }

    /**
     * Temporary fix for #93299 - this DAO works with the old XML-RPC api.
     * TODO: refactor database layer and remove vectors from daos
     * @param xmlSchema
     * @return
     * @throws SQLException
     */
    @Override
    public Vector listQueriesOld(String xmlSchema) throws SQLException {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean forSchema = xmlSchema != null;
        Vector v = null;
        String query = (forSchema) ? qListQueriesBySchema : qListQueries;

        if (isDebugMode) {
            LOGGER.debug("XMLSchema is " + xmlSchema);
            LOGGER.debug("Query is " + query);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(query);
            if (forSchema) {
                pstmt.setString(1, xmlSchema);
            }
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            v = new Vector(r.length);
            for (int i = 0; i < r.length; i++) {
                Hashtable h = new Hashtable();
                h.put(ListQueriesMethod.KEY_QUERY_ID, r[i][0]);
                h.put(ListQueriesMethod.KEY_SHORT_NAME, r[i][1]);
                h.put(ListQueriesMethod.KEY_QUERY, r[i][2]);
                h.put(ListQueriesMethod.KEY_DESCRIPTION, r[i][3]);
                h.put(ListQueriesMethod.KEY_SCHEMA_ID, r[i][4]);
                h.put(ListQueriesMethod.KEY_XML_SCHEMA, r[i][5]);
                h.put(ListQueriesMethod.KEY_CONTENT_TYPE_ID, r[i][6]);
                h.put(ListQueriesMethod.KEY_CONTENT_TYPE_OUT, r[i][7]);
                h.put(ListQueriesMethod.KEY_TYPE, r[i][8]);
                h.put(ListQueriesMethod.KEY_UPPER_LIMIT, r[i][9]);
                h.put(QaScriptView.IS_ACTIVE, r[i][11]);
                v.add(h);
            }
        }  finally {
            closeAllResources(rs, pstmt, conn);
        }

        return v;
    }

    @Override
    public Vector listQueries(String xmlSchema) throws SQLException {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean forSchema = xmlSchema != null;
        Vector v = null;
        String query = (forSchema) ? qListQueriesBySchema : qListQueries;

        if (isDebugMode) {
            LOGGER.debug("XMLSchema is " + xmlSchema);
            LOGGER.debug("Query is " + query);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(query);
            if (forSchema) {
                pstmt.setString(1, xmlSchema);
            }
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            v = new Vector(r.length);
            for (int i = 0; i < r.length; i++) {
                Hashtable h = new Hashtable();
                h.put(QaScriptView.QUERY_ID, r[i][0]);
                h.put(QaScriptView.SHORT_NAME, r[i][1]);
                h.put(QaScriptView.QUERY, r[i][2]);
                h.put(QaScriptView.DESCRIPTION, r[i][3]);
                h.put(QaScriptView.SCHEMA_ID, r[i][4]);
                h.put(QaScriptView.XML_SCHEMA, r[i][5]);
                h.put(QaScriptView.CONTENT_TYPE_ID, r[i][6]);
                h.put(QaScriptView.CONTENT_TYPE_OUT, r[i][7]);
                h.put(QaScriptView.SCRIPT_TYPE, r[i][8]);
                h.put(QaScriptView.UPPER_LIMIT, r[i][9]);
                h.put(QaScriptView.IS_ACTIVE, r[i][11]);
                v.add(h);
            }
        }  finally {
            closeAllResources(rs, pstmt, conn);
        }

        return v;
    }

    @Override
    public Vector listQueries(String xmlSchema, boolean active) throws SQLException {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean forSchema = xmlSchema != null;
        Vector v = null;
        String query = (forSchema) ? qListQueriesBySchemaAndActive : qListQueriesByActive;

        if (isDebugMode) {
            LOGGER.debug("XMLSchema is " + xmlSchema);
            LOGGER.debug("Query is " + query);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(query);
            if (forSchema) {
                pstmt.setString(1, xmlSchema);
                pstmt.setString(2, (active) ? "1" : "0");
            } else {
                pstmt.setString(1, (active) ? "1" : "0");
            }

            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            v = new Vector(r.length);
            for (int i = 0; i < r.length; i++) {

                Hashtable h = new Hashtable();
                h.put(QaScriptView.QUERY_ID, r[i][0]);
                h.put(QaScriptView.SHORT_NAME, r[i][1]);
                h.put(QaScriptView.QUERY, r[i][2]);
                h.put(QaScriptView.DESCRIPTION, r[i][3]);
                h.put(QaScriptView.SCHEMA_ID, r[i][4]);
                h.put(QaScriptView.XML_SCHEMA, r[i][5]);
                h.put(QaScriptView.CONTENT_TYPE_ID, r[i][6]);
                h.put(QaScriptView.CONTENT_TYPE, r[i][7]);
                h.put(QaScriptView.SCRIPT_TYPE, r[i][8]);
                h.put(QaScriptView.UPPER_LIMIT, r[i][9]);
                h.put(QaScriptView.IS_ACTIVE, r[i][11]);
                v.add(h);

            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }

        return v;
    }

    @Override
    public boolean checkQueryFile(String queryFileName) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qCheckQueryFileByName);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qCheckQueryFileByName);
            pstmt.setString(1, queryFileName);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            String count = r[0][0];
            if (count.equals("0")) {
                return false;
            } else {
                return true;
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }

    }

    @Override
    public boolean checkQueryFile(String query_id, String queryFileName) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qCheckQueryFileByIdAndName);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qCheckQueryFileByIdAndName);
            pstmt.setString(1, queryFileName);
            pstmt.setString(2, query_id);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            String count = r[0][0];
            if (count.equals("0")) {
                return false;
            } else {
                return true;
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }

    }
    
    @Override
    public void activateQuery(String query_id) throws SQLException {
         setQueryActivation(query_id, true);
    }
    
    @Override
    public void deactivateQuery(String query_id) throws SQLException {
         setQueryActivation(query_id, false);
    }

    /**
     * Sets query activation
     * @param query_id Query Id
     * @param set_active Active
     * @throws SQLException If an error occurs.
     */
    public void setQueryActivation(String query_id, boolean set_active) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qQueryUpdateActive);
        }
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qQueryUpdateActive);
            pstmt.setBoolean(1, set_active);
            pstmt.setString(2, query_id);
            pstmt.executeUpdate();
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
    }

    @Override
    public Boolean getAsynchronousExecution(String queryId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Boolean result = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qGetAsynchronousExecution);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qGetAsynchronousExecution);

            pstmt.setString(1, queryId);
            rs = pstmt.executeQuery();

            String[][] r = getResults(rs);
            if (r.length == 0) {
                return null;
            }
            if(r[0][0] != null && r[0][0].equals("1")){
                return true;
            }
            return false;
        } catch (Exception e ){
            LOGGER.error ("Error while retrieving asynchronous execution field for script " + queryId + " : " + e.getMessage() );
            return null;
        }

        finally {
            closeAllResources(rs, pstmt, conn);
        }
    }

}
