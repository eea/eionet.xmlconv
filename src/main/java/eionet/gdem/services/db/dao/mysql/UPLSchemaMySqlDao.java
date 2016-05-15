package eionet.gdem.services.db.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.dto.UplSchema;
import eionet.gdem.services.db.dao.IUPLSchemaDao;

/**
 * Upload Schema Dao class.
 * @author Unknown
 */
@Repository("uplSchemaDao")
public class UPLSchemaMySqlDao extends MySqlBaseDao implements IUPLSchemaDao {

    /**
     * Jdbc template for accessing data storage.
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** */
    private static final Log LOGGER = LogFactory.getLog(UPLSchemaMySqlDao.class);

    // T_SCHEMA LEFT JOIN T_UPL_SCHEMA
    private static final String qSchemas = "SELECT " + "S." + SCHEMA_ID_FLD + ", " + "S." + XML_SCHEMA_FLD + ", " + "S."
            + SCHEMA_DESCR_FLD + ", " + "U." + UPL_SCHEMA_ID_FLD + ", " + "U." + UPL_SCHEMA_FLD + " FROM " + SCHEMA_TABLE
            + " S LEFT JOIN " + UPL_SCHEMA_TABLE + " U ON S." + SCHEMA_ID_FLD + "=U." + UPL_FK_SCHEMA_ID + " ORDER BY S."
            + XML_SCHEMA_FLD;

    private static final String qUplSchema = "SELECT " + UPL_SCHEMA_ID_FLD + ", " + UPL_SCHEMA_FLD + ", " + UPL_SCHEMA_DESC + ", "
            + UPL_FK_SCHEMA_ID + " FROM " + UPL_SCHEMA_TABLE + " ORDER BY " + UPL_SCHEMA_FLD;

    private static final String qInsertUplSchema = "INSERT INTO " + UPL_SCHEMA_TABLE + " ( " + UPL_SCHEMA_FLD + " ,"
            + UPL_SCHEMA_DESC + " ," + UPL_FK_SCHEMA_ID + ") " + "VALUES (?,?,?)";

    public static final String qRemoveUplSchema = "DELETE FROM " + UPL_SCHEMA_TABLE + " WHERE " + UPL_SCHEMA_ID_FLD + "= ?";

    private static final String qUplSchemaByID = "SELECT " + "S." + SCHEMA_ID_FLD + ", " + "S." + XML_SCHEMA_FLD + ", " + "S."
            + SCHEMA_DESCR_FLD + ", " + "U." + UPL_SCHEMA_ID_FLD + ", " + "U." + UPL_SCHEMA_FLD + " FROM " + SCHEMA_TABLE
            + " S RIGHT JOIN " + UPL_SCHEMA_TABLE + " U ON S." + SCHEMA_ID_FLD + "=U." + UPL_FK_SCHEMA_ID + " WHERE U."
            + SCHEMA_ID_FLD + "= ?";

    private static final String qUplSchemaByFkId = "SELECT " + "S." + SCHEMA_ID_FLD + ", " + "S." + XML_SCHEMA_FLD + ", " + "S."
            + SCHEMA_DESCR_FLD + ", " + "U." + UPL_SCHEMA_ID_FLD + ", " + "U." + UPL_SCHEMA_FLD + " FROM " + SCHEMA_TABLE
            + " S LEFT JOIN " + UPL_SCHEMA_TABLE + " U ON S." + SCHEMA_ID_FLD + "=U." + UPL_FK_SCHEMA_ID + " WHERE S."
            + SCHEMA_ID_FLD + "= ?";

    private static final String qUplSchemaByURL = "SELECT U." + SCHEMA_ID_FLD + ", " + "U." + UPL_SCHEMA_FLD + "," + "S."
            + SCHEMA_DESCR_FLD + "," + "S." + XML_SCHEMA_FLD + " FROM " + SCHEMA_TABLE + " S RIGHT JOIN " + UPL_SCHEMA_TABLE
            + " U ON S." + SCHEMA_ID_FLD + "=U." + UPL_FK_SCHEMA_ID + " WHERE S." + XML_SCHEMA_FLD + "= ?";

    private static final String qUpdateUplSchema = "UPDATE  " + UPL_SCHEMA_TABLE + " SET " + UPL_SCHEMA_FLD + "= ?, "
            + UPL_SCHEMA_DESC + "= ?, " + UPL_FK_SCHEMA_ID + "= ? " + " WHERE " + UPL_SCHEMA_ID_FLD + "= ?";

    private static final String qUpdateSchema = "UPDATE  " + SCHEMA_TABLE + " SET " + SCHEMA_DESCR_FLD + "= ? " + " WHERE "
            + XML_SCHEMA_FLD + "= ? ";

    private static final String qUplSchemaByUplSchemaId = "SELECT " + UPL_SCHEMA_FLD + " FROM " + UPL_SCHEMA_TABLE + " WHERE "
            + UPL_SCHEMA_ID_FLD + "= ?";

    private static final String checkUplSchemaFile = "SELECT COUNT(*) FROM " + UPL_SCHEMA_TABLE + " WHERE " + UPL_SCHEMA_FLD
            + "= ?";

    private static final String checkUplSchemaFK = "SELECT COUNT(*) FROM " + UPL_SCHEMA_TABLE + " WHERE " + UPL_FK_SCHEMA_ID
            + "!='' AND " + UPL_FK_SCHEMA_ID + "= ?";

    /**
     * Default constructor.
     */
    public UPLSchemaMySqlDao() {
    }

    @Override
    public Vector getUplSchema() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector v = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qUplSchema);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qUplSchema);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            v = new Vector(r.length);
            for (int i = 0; i < r.length; i++) {
                Hashtable h = new Hashtable();
                h.put("id", r[i][0]);
                h.put("schema", r[i][1]);
                h.put("description", r[i][2]);
                h.put("fk_schema_id", r[i][3]);
                v.add(h);
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return v;
    }

    @Override
    public List<UplSchema> getUploadedSchemas() throws SQLException {

        if (isDebugMode) {
            LOGGER.debug("Query is " + qSchemas);
        }

        final List<UplSchema> schemas = new ArrayList<UplSchema>();
        jdbcTemplate.query(qSchemas, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                UplSchema uplSchema = new UplSchema();
                uplSchema.setSchemaId(rs.getString(1));
                uplSchema.setSchemaUrl(rs.getString(2));
                uplSchema.setDescription(rs.getString(3));
                uplSchema.setUplSchemaId(rs.getString(4));
                uplSchema.setUplSchemaFile(rs.getString(5));
                uplSchema.setUplSchemaFileUrl(Properties.gdemURL + "/" + Constants.SCHEMA_FOLDER + rs.getString(5));
                schemas.add(uplSchema);
            }
        });
        return schemas;
    }

    @Override
    public String addUplSchema(String schema, String description, String fk_schema_id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        description = (description == null ? "" : description);

        if (isDebugMode) {
            LOGGER.debug("Query is " + qInsertUplSchema);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qInsertUplSchema);
            pstmt.setString(1, schema);
            pstmt.setString(2, description);
            pstmt.setString(3, fk_schema_id);
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
        return getLastInsertID();
    }

    /*
     * public void removeUplSchema(String uplSchemaId) throws SQLException {
     * String sql = "DELETE FROM " + UPL_SCHEMA_TABLE + " WHERE " + UPL_SCHEMA_ID_FLD + "=" + uplSchemaId; _executeUpdate(sql); //
     * System.out.println(sql);
     * }
     */
    @Override
    public void removeUplSchema(String uplSchemaId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qRemoveUplSchema);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qRemoveUplSchema);
            pstmt.setInt(1, Integer.parseInt(uplSchemaId));
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    /*
     * public void updateUplSchema(String schema_id, String description) throws SQLException {
     * description = (description == null ? "" : description);
     * String sql = "UPDATE  " + UPL_SCHEMA_TABLE + " SET " + SCHEMA_DESCR_FLD + "=" + Utils.strLiteral(description) + " WHERE " +
     * UPL_SCHEMA_ID_FLD + "=" + schema_id;
     * _executeUpdate(sql);
     * Hashtable sch = getUplSchemaById(schema_id); String schema = (String) sch.get("schema");
     * sql = "UPDATE  " + SCHEMA_TABLE + " SET " + SCHEMA_DESCR_FLD + "=" + Utils.strLiteral(description) + " WHERE " +
     * XML_SCHEMA_FLD + "=" + Utils.strLiteral(Properties.gdemURL + "/schema/" + schema);
     * _executeUpdate(sql);
     * }
     */

    @Override
    public void updateUplSchema(String schema_id, String schema_file, String description, String fk_schema_id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        description = (description == null ? "" : description);

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            if (isDebugMode) {
                LOGGER.debug("Query is " + qUpdateUplSchema);
            }
            pstmt = conn.prepareStatement(qUpdateUplSchema);
            pstmt.setString(1, schema_file);
            pstmt.setString(2, description);
            pstmt.setString(3, fk_schema_id);
            pstmt.setInt(4, Integer.parseInt(schema_id));
            pstmt.executeUpdate();
            pstmt.close();

            Hashtable sch = getUplSchemaById(schema_id);
            String schema = (String) sch.get("schema");

            if (isDebugMode) {
                LOGGER.debug("Query is " + qUpdateSchema);
            }
            pstmt = conn.prepareStatement(qUpdateSchema);
            pstmt.setString(1, description);
            pstmt.setString(2, Properties.gdemURL + "/schema/" + schema);
            pstmt.executeUpdate();

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

    /*
     * public String getUplSchema(String uplSchemaId) throws SQLException {
     * String sql = "SELECT " + UPL_SCHEMA_FLD + " FROM " + UPL_SCHEMA_TABLE + " WHERE " + UPL_SCHEMA_ID_FLD + "=" + uplSchemaId;
     * String[][] r = _executeStringQuery(sql);
     * if (r.length == 0) return null;
     * return r[0][0]; }
     */

    @Override
    public String getUplSchema(String uplSchemaId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String result = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qUplSchemaByUplSchemaId);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qUplSchemaByUplSchemaId);

            pstmt.setInt(1, Integer.parseInt(uplSchemaId));
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

    /*
     * public Hashtable getUplSchemaById(String schemaId) throws SQLException {
     * int id = 0;
     * if (schemaId == null) throw new SQLException("Schema ID not defined"); try { id = Integer.parseInt(schemaId); } catch
     * (NumberFormatException n) { throw new SQLException("not numeric ID " + schemaId); }
     * String sql = "SELECT " + SCHEMA_ID_FLD + ", " + UPL_SCHEMA_FLD + "," + UPL_SCHEMA_DESC + " FROM " + UPL_SCHEMA_TABLE +
     * " WHERE " + SCHEMA_ID_FLD + "=" + id;
     * String[][] r = _executeStringQuery(sql);
     * Hashtable h = new Hashtable(); h.put("schema_id", r[0][0]); h.put("schema", r[0][1]); h.put("description", r[0][2]);
     * return h; }
     */

    @Override
    public Hashtable getUplSchemaById(String schemaId) throws SQLException {
        int id = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Hashtable h = null;

        if (schemaId == null) {
            throw new SQLException("Schema ID not defined");
        }
        try {
            id = Integer.parseInt(schemaId);
        } catch (NumberFormatException n) {
            throw new SQLException("not numeric ID " + schemaId);
        }

        if (isDebugMode) {
            LOGGER.debug("Query is " + qUplSchemaByID);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qUplSchemaByID);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);

            h = new Hashtable();
            h.put("schema_id", r[0][0]);
            h.put("xml_schema", r[0][1]);
            h.put("description", r[0][2]);
            h.put("upl_schema_id", r[0][3]);
            h.put("upl_schema_file", r[0][4]);
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return h;
    }

    @Override
    public HashMap<String, String> getUplSchemaByFkSchemaId(String schemaId) throws SQLException {
        int id = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, String> h = null;

        if (schemaId == null) {
            throw new SQLException("Schema ID not defined");
        }
        try {
            id = Integer.parseInt(schemaId);
        } catch (NumberFormatException n) {
            throw new SQLException("not numeric ID " + schemaId);
        }

        if (isDebugMode) {
            LOGGER.debug("Query is " + qUplSchemaByFkId);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qUplSchemaByFkId);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);

            if (r.length == 0) {
                return null;
            }

            h = new HashMap<String, String>();

            h.put("schema_id", r[0][0]);
            h.put("xml_schema", r[0][1]);
            h.put("description", r[0][2]);
            h.put("upl_schema_id", r[0][3]);
            h.put("upl_schema_file", r[0][4]);
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return h;
    }

    @Override
    public boolean checkUplSchemaFile(String schemaFileName) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean result = false;

        if (isDebugMode) {
            LOGGER.debug("Query is " + checkUplSchemaFile);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(checkUplSchemaFile);
            pstmt.setString(1, schemaFileName);
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

    @Override
    public boolean checkUplSchemaFK(String schemaFK) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean result = false;

        if (isDebugMode) {
            LOGGER.debug("Query is " + checkUplSchemaFK);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(checkUplSchemaFK);
            pstmt.setString(1, schemaFK);
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

    @Override
    public HashMap<String, String> getUplSchemaByUrl(String schemaUrl) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, String> h = null;

        if (schemaUrl == null) {
            throw new SQLException("Schema URL key not defined");
        }

        if (isDebugMode) {
            LOGGER.debug("Query is " + qUplSchemaByURL);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qUplSchemaByURL);
            pstmt.setString(1, schemaUrl);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);

            if (r.length == 0) {
                return null;
            }

            h = new HashMap<String, String>();
            h.put("schema_id", r[0][0]);
            h.put("schema", r[0][1]);
            h.put("description", r[0][2]);
            h.put("fk_schema_id", r[0][3]);
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return h;
    }
}
