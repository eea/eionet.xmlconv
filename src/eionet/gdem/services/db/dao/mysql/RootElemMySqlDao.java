package eionet.gdem.services.db.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import eionet.gdem.services.db.dao.IRootElemDao;
import eionet.gdem.utils.Utils;

public class RootElemMySqlDao extends MySqlBaseDao implements IRootElemDao {

    private static final String qSchemaRootElems = "SELECT " + ROOTELEM_ID_FLD + ", " + ELEM_NAME_FLD + ", " + NAMESPACE_FLD + ","
            + ELEM_SCHEMA_ID_FLD + " FROM " + ROOTELEM_TABLE + " WHERE " + ELEM_SCHEMA_ID_FLD + "= ?" + " ORDER BY "
            + ELEM_NAME_FLD;

    private static final String qRootElemMatching = "SELECT " + ELEM_SCHEMA_ID_FLD + " FROM " + ROOTELEM_TABLE + " WHERE "
            + ELEM_NAME_FLD + "= ? ";

    private static final String qRootElemMatchingNamespace = qRootElemMatching + " AND " + NAMESPACE_FLD + "= ?";

    private static final String gRemoveRootElem = "DELETE FROM " + ROOTELEM_TABLE + " WHERE " + ROOTELEM_ID_FLD + "= ? ";

    private static final String qInsertRootElem = "INSERT INTO " + ROOTELEM_TABLE + " ( " + ELEM_SCHEMA_ID_FLD + ", "
            + ELEM_NAME_FLD + ", " + NAMESPACE_FLD + " ) " + " VALUES (?,?,?) ";

    public RootElemMySqlDao() {
    }

    /*
     * public Vector getSchemaRootElems(String schemaId) throws SQLException {
     * 
     * int id = 0;
     * 
     * if (schemaId == null) throw new SQLException("Schema ID not defined"); try { id = Integer.parseInt(schemaId); } catch
     * (NumberFormatException n) { throw new SQLException("not numeric ID " + schemaId); }
     * 
     * String sql = "SELECT " + ROOTELEM_ID_FLD + ", " + ELEM_NAME_FLD + ", " + NAMESPACE_FLD + "," + ELEM_SCHEMA_ID_FLD + " FROM "
     * + ROOTELEM_TABLE + " WHERE " + ELEM_SCHEMA_ID_FLD + "=" + id;
     * 
     * sql += " ORDER BY " + ELEM_NAME_FLD;
     * 
     * String[][] r = _executeStringQuery(sql);
     * 
     * Vector v = new Vector();
     * 
     * for (int i = 0; i < r.length; i++) { HashMap h = new HashMap(); h.put("rootelem_id", r[i][0]); h.put("elem_name", r[i][1]);
     * h.put("namespace", r[i][2]); h.put("schema_id", r[i][3]); v.add(h); }
     * 
     * return v; }
     */
    public Vector getSchemaRootElems(String schemaId) throws SQLException {
        int id = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector v = null;

        if (schemaId == null)
            throw new SQLException("Schema ID not defined");
        try {
            id = Integer.parseInt(schemaId);
        } catch (NumberFormatException n) {
            throw new SQLException("not numeric ID " + schemaId);
        }

        if (isDebugMode) {
            logger.debug("Query is " + qSchemaRootElems);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qSchemaRootElems);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            v = new Vector(r.length);
            for (int i = 0; i < r.length; i++) {
                HashMap h = new HashMap();
                h.put("rootelem_id", r[i][0]);
                h.put("elem_name", r[i][1]);
                h.put("namespace", r[i][2]);
                h.put("schema_id", r[i][3]);
                v.add(h);
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }

        return v;
    }

    /*
     * public Vector getRootElemMatching(String rootElem, String namespace) throws SQLException {
     * 
     * StringBuffer sql = new StringBuffer("SELECT "); sql.append(ELEM_SCHEMA_ID_FLD); sql.append(" FROM " + ROOTELEM_TABLE +
     * " WHERE " + ELEM_NAME_FLD + "=" + Utils.strLiteral(rootElem));
     * 
     * if (!Utils.isNullStr(namespace)) sql.append(" AND " + NAMESPACE_FLD + "=" + Utils.strLiteral(namespace)); //
     * System.out.println(sql.toString()); String[][] r = _executeStringQuery(sql.toString());
     * 
     * Vector v = new Vector(); // System.out.println(r.length);
     * 
     * for (int i = 0; i < r.length; i++) { HashMap h = getSchema(r[i][0], true); v.add(h); }
     * 
     * return v;
     * 
     * }
     */

    public Vector getRootElemMatching(String rootElem, String namespace) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector v = null;

        try {
            conn = getConnection();
            boolean hasNamespace = !Utils.isNullStr(namespace);
            if (hasNamespace) {
                pstmt = conn.prepareStatement(qRootElemMatchingNamespace);
                pstmt.setString(2, namespace);
            } else {
                pstmt = conn.prepareStatement(qRootElemMatching);
            }
            pstmt.setString(1, rootElem);

            if (isDebugMode) {
                logger.debug("Query is " + (hasNamespace ? qRootElemMatchingNamespace : qRootElemMatching));
            }
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);

            v = new Vector();
            SchemaMySqlDao shemaDao = new SchemaMySqlDao();

            for (int i = 0; i < r.length; i++) {
                HashMap h = shemaDao.getSchema(r[i][0], true);
                v.add(h);
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }

        return v;

    }

    /*
     * public void removeRootElem(String rootElemId) throws SQLException {
     * 
     * String sql = "DELETE FROM " + ROOTELEM_TABLE + " WHERE " + ROOTELEM_ID_FLD + "=" + rootElemId; _executeUpdate(sql);
     * 
     * }
     */

    public void removeRootElem(String rootElemId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        if (isDebugMode) {
            logger.debug("Query is " + gRemoveRootElem);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(gRemoveRootElem);
            pstmt.setInt(1, Integer.parseInt(rootElemId));
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }

    }

    /*
     * public String addRootElem(String xmlSchemaID, String elemName, String namespace) throws SQLException {
     * 
     * namespace = (namespace == null ? "" : namespace);
     * 
     * String sql = "INSERT INTO " + ROOTELEM_TABLE + " ( " + ELEM_SCHEMA_ID_FLD + ", " + ELEM_NAME_FLD + ", " + NAMESPACE_FLD +
     * ") VALUES (" + Utils.strLiteral(xmlSchemaID) + ", " + Utils.strLiteral(elemName) + ", " + Utils.strLiteral(namespace) + ")";
     * 
     * _executeUpdate(sql);
     * 
     * return _getLastInsertID(); }
     */

    public String addRootElem(String xmlSchemaID, String elemName, String namespace) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        namespace = (namespace == null ? "" : namespace);

        if (isDebugMode) {
            logger.debug("Query is " + qInsertRootElem);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qInsertRootElem);
            pstmt.setString(1, xmlSchemaID);
            pstmt.setString(2, elemName);
            pstmt.setString(3, namespace);
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
        return getLastInsertID();

    }

}
