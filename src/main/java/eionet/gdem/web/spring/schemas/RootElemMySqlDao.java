package eionet.gdem.web.spring.schemas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;


import eionet.gdem.database.MySqlBaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import eionet.gdem.utils.Utils;

/**
 * Root element MySQL Dao class.
 * @author Unknown
 */
@Repository("rootElemDao")
public class RootElemMySqlDao extends MySqlBaseDao implements IRootElemDao {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(RootElemMySqlDao.class);

    @Autowired
    private ISchemaDao schemaDao;

    /**
     * Field names in ROOT ELEMENTS table.
     */
    public static final String ROOTELEM_ID_FLD = "ROOTELEM_ID";
    public static final String ELEM_SCHEMA_ID_FLD = "SCHEMA_ID";
    public static final String NAMESPACE_FLD = "NAMESPACE";
    public static final String ELEM_NAME_FLD = "ELEM_NAME";

    /**
     * Table for root element mappings for schemas in the DB.
     */
    public static final String ROOTELEM_TABLE = "T_ROOT_ELEM";

    private static final String qSchemaRootElems = "SELECT " + ROOTELEM_ID_FLD + ", " + ELEM_NAME_FLD + ", " + NAMESPACE_FLD + ","
            + ELEM_SCHEMA_ID_FLD + " FROM " + ROOTELEM_TABLE + " WHERE " + ELEM_SCHEMA_ID_FLD + "= ?" + " ORDER BY "
            + ELEM_NAME_FLD;

    private static final String qRootElemMatching = "SELECT " + ELEM_SCHEMA_ID_FLD + " FROM " + ROOTELEM_TABLE + " WHERE "
            + ELEM_NAME_FLD + "= ? ";

    private static final String qRootElemMatchingNamespace = qRootElemMatching + " AND " + NAMESPACE_FLD + "= ?";

    private static final String gRemoveRootElem = "DELETE FROM " + ROOTELEM_TABLE + " WHERE " + ROOTELEM_ID_FLD + "= ? ";

    private static final String qInsertRootElem = "INSERT INTO " + ROOTELEM_TABLE + " ( " + ELEM_SCHEMA_ID_FLD + ", "
            + ELEM_NAME_FLD + ", " + NAMESPACE_FLD + " ) " + " VALUES (?,?,?) ";

    @Override
    public Vector getSchemaRootElems(String schemaId) throws SQLException {
        int id = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector v = null;

        if (schemaId == null) {
            throw new SQLException("Schema ID not defined");
        }
        try {
            id = Integer.parseInt(schemaId);
        } catch (NumberFormatException n) {
            throw new SQLException("not numeric ID " + schemaId);
        }

        if (isDebugMode) {
            LOGGER.debug("Query is " + qSchemaRootElems);
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

    @Override
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
                LOGGER.debug("Query is " + (hasNamespace ? qRootElemMatchingNamespace : qRootElemMatching));
            }
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);

            v = new Vector();

            for (int i = 0; i < r.length; i++) {
                HashMap h = schemaDao.getSchema(r[i][0], true);
                v.add(h);
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }

        return v;

    }

    @Override
    public void removeRootElem(String rootElemId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        if (isDebugMode) {
            LOGGER.debug("Query is " + gRemoveRootElem);
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

    @Override
    public String addRootElem(String xmlSchemaID, String elemName, String namespace) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        namespace = (namespace == null ? "" : namespace);

        if (isDebugMode) {
            LOGGER.debug("Query is " + qInsertRootElem);
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
