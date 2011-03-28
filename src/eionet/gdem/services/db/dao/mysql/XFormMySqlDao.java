package eionet.gdem.services.db.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import eionet.gdem.services.db.dao.IXFormDao;
import eionet.gdem.utils.Utils;


public class XFormMySqlDao extends MySqlBaseDao implements IXFormDao{


    private static final String qRemoveFile = "DELETE FROM " + FILE_TABLE + " WHERE " + FILE_ID_FLD + "= ?";

    private static final String qXFormBySchemaID = "SELECT "
                                                    + FILE_ID_FLD + ", "
                                                    + XML_SCHEMA_FLD + ", "
                                                    + FILE_NAME_FLD + ", "
                                                    + FILE_TITLE_FLD + ", "
                                                    + FILE_TABLE + "." + FILE_DESCRIPTION_FLD
                                                    + " FROM " + FILE_TABLE
                                                    + " LEFT OUTER JOIN " + SCHEMA_TABLE
                                                    + " ON " + FILE_TABLE + "." + FILE_PARENTID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD
                                                    + " WHERE " + SCHEMA_TABLE + "." + SCHEMA_ID_FLD + " = ?"
                                                    + " AND " + FILE_PARENTTYPE_FLD + "= ? "
                                                    + " AND " + FILE_TYPE_FLD + "= ?";


    private static final String qXFormBySchemaName = 	"SELECT "
                                                        + FILE_ID_FLD + ", "
                                                        + XML_SCHEMA_FLD + ", "
                                                        + FILE_NAME_FLD + ", "
                                                        + FILE_TITLE_FLD + ", "
                                                        + FILE_TABLE + "." + FILE_DESCRIPTION_FLD
                                                        + " FROM " + FILE_TABLE
                                                        + " LEFT OUTER JOIN " + SCHEMA_TABLE
                                                        + " ON " + FILE_TABLE + "." + FILE_PARENTID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD
                                                        + " WHERE " + XML_SCHEMA_FLD + " = ?"
                                                        + " AND " + FILE_PARENTTYPE_FLD + "= ?"
                                                        + " AND " + FILE_TYPE_FLD + "= ?";


    private static final String qXFormByID = 	"SELECT "
                                                + FILE_ID_FLD + ", "
                                                + XML_SCHEMA_FLD + ", "
                                                + FILE_NAME_FLD + ", "
                                                + FILE_TITLE_FLD + ", "
                                                + FILE_TABLE + "." + FILE_DESCRIPTION_FLD
                                                + " FROM " + FILE_TABLE
                                                + " LEFT OUTER JOIN " + SCHEMA_TABLE
                                                + " ON " + FILE_TABLE + "." + FILE_PARENTID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD
                                                + " WHERE " + FILE_ID_FLD + " = ? "
                                                + " AND " + FILE_PARENTTYPE_FLD + "= ?";


    private static final String qInsertFile = 	"INSERT INTO " + FILE_TABLE
                                                + " ( "
                                                + FILE_NAME_FLD + ", "
                                                + FILE_TITLE_FLD + ", "
                                                + FILE_TYPE_FLD + ", "
                                                + FILE_PARENTTYPE_FLD + ", "
                                                + FILE_PARENTID_FLD + ", "
                                                + FILE_DESCRIPTION_FLD + ", "
                                                + FILE_DEFAULT_FLD
                                                + ") " +
                                                " VALUES (?,?,?,?,?,?,?)";


    private static final String qFileByNameAndType =  "SELECT " + FILE_ID_FLD + " FROM " + FILE_TABLE + " WHERE " + FILE_NAME_FLD + "= ?" + " AND " + FILE_TYPE_FLD + "= ? ";



    private static final String qUpdateFile = 	"UPDATE  " + FILE_TABLE +
                                                " SET " + FILE_NAME_FLD + "= ? " + ", "
                                                + FILE_TITLE_FLD + "= ? "  + ", "
                                                + FILE_PARENTTYPE_FLD + "= ? " + ", "
                                                + FILE_PARENTID_FLD + "= ? " + ", "
                                                + FILE_DESCRIPTION_FLD + "= ? " + ""
                                                + " WHERE " + FILE_ID_FLD + "= ? ";



    private SchemaMySqlDao schemaDao;

    public XFormMySqlDao() {
        schemaDao = new SchemaMySqlDao();
    }

/*	public void removeXForm(String xformId) throws SQLException {

        removeFile(xformId);

    }

*/	public void removeXForm(String xformId) throws SQLException{
        removeFile(xformId);
    }


/*public Hashtable getXForm(String XMLSchema) throws SQLException {

    String sql = null;

    if (Utils.isNum(XMLSchema)) {
        sql = "SELECT " + FILE_ID_FLD + ", " + XML_SCHEMA_FLD + ", " + FILE_NAME_FLD + ", " + FILE_TITLE_FLD + ", " + FILE_TABLE + "." + FILE_DESCRIPTION_FLD + " FROM " + FILE_TABLE + " LEFT OUTER JOIN " + SCHEMA_TABLE + " ON " + FILE_TABLE + "." + FILE_PARENTID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD + " WHERE " + SCHEMA_TABLE + "."
                + SCHEMA_ID_FLD + " = " + XMLSchema + " AND " + FILE_PARENTTYPE_FLD + "='" + SCHEMA_FILE_PARENT + "'" + " AND " + FILE_TYPE_FLD + "='" + XFORM_FILE_TYPE + "'";
    } else {
        sql = "SELECT " + FILE_ID_FLD + ", " + XML_SCHEMA_FLD + ", " + FILE_NAME_FLD + ", " + FILE_TITLE_FLD + ", " + FILE_TABLE + "." + FILE_DESCRIPTION_FLD + " FROM " + FILE_TABLE + " LEFT OUTER JOIN " + SCHEMA_TABLE + " ON " + FILE_TABLE + "." + FILE_PARENTID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD + " WHERE " + XML_SCHEMA_FLD + " ="
                + Utils.strLiteral(XMLSchema) + " AND " + FILE_PARENTTYPE_FLD + "='" + SCHEMA_FILE_PARENT + "'" + " AND " + FILE_TYPE_FLD + "='" + XFORM_FILE_TYPE + "'";
    }

    String r[][] = _executeStringQuery(sql);

    if (r.length == 0) return null;

    Hashtable h = new Hashtable();
    h.put("xform_id", r[0][0]);
    h.put("xml_schema", r[0][1]);
    h.put("xform_name", r[0][2]);
    h.put("xform_title", r[0][3]);
    h.put("xform_description", r[0][4]);

    return h;
}
*/
    public Hashtable getXForm(String XMLSchema) throws SQLException{
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs =null;
        Hashtable h = null;
        boolean byID = Utils.isNum(XMLSchema);

        if (isDebugMode){ logger.debug("Query is " + (byID?qXFormBySchemaID:qXFormBySchemaName));}

        try {
            conn = getConnection();
            if(byID){
                pstmt = conn.prepareStatement(qXFormBySchemaID);
                pstmt.setInt(1,Integer.parseInt(XMLSchema));
            }
            else {
                pstmt = conn.prepareStatement(qXFormBySchemaName);
                pstmt.setString(1,XMLSchema);
            }

            pstmt.setString(2,SCHEMA_FILE_PARENT);
            pstmt.setString(3,XFORM_FILE_TYPE);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            if (r.length == 0) return null;

            h = new Hashtable();
            h.put("xform_id", r[0][0]);
            h.put("xml_schema", r[0][1]);
            h.put("xform_name", r[0][2]);
            h.put("xform_title", r[0][3]);
            h.put("xform_description", r[0][4]);
        }
        finally {
            closeAllResources(rs,pstmt,conn);
        }

        return h;
    }





/*	public Hashtable getXFormByID(String xform_id) throws SQLException {

        if (xform_id == null) return null;
        if (!Utils.isNum(xform_id)) throw new SQLException("XForm id is not numeric");

        String sql = "SELECT " + FILE_ID_FLD + ", " + XML_SCHEMA_FLD + ", " + FILE_NAME_FLD + ", " + FILE_TITLE_FLD + ", " + FILE_TABLE + "." + FILE_DESCRIPTION_FLD + " FROM " + FILE_TABLE + " LEFT OUTER JOIN " + SCHEMA_TABLE + " ON " + FILE_TABLE + "." + FILE_PARENTID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD + " WHERE " + FILE_ID_FLD + " = "
                + xform_id + " AND " + FILE_PARENTTYPE_FLD + "='" + SCHEMA_FILE_PARENT + "'";

        String r[][] = _executeStringQuery(sql);

        if (r.length == 0) return null;

        Hashtable h = new Hashtable();
        h.put("xform_id", r[0][0]);
        h.put("xml_schema", r[0][1]);
        h.put("xform_name", r[0][2]);
        h.put("xform_title", r[0][3]);
        h.put("xform_description", r[0][4]);

        return h;
    }
*/

    public Hashtable getXFormByID(String xform_id) throws SQLException{
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs =null;
        Hashtable h = null;

        if (xform_id == null) return null;
        if (!Utils.isNum(xform_id)) throw new SQLException("XForm id is not numeric");

        if (isDebugMode){ logger.debug("Query is " + qXFormByID);}

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qXFormByID);
            pstmt.setInt(1,Integer.parseInt(xform_id));
            pstmt.setString(2,SCHEMA_FILE_PARENT);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);

            if (r.length == 0) return null;

            h = new Hashtable();
            h.put("xform_id", r[0][0]);
            h.put("xml_schema", r[0][1]);
            h.put("xform_name", r[0][2]);
            h.put("xform_title", r[0][3]);
            h.put("xform_description", r[0][4]);
        }
        finally {
            closeAllResources(rs,pstmt,conn);
        }
                return h;

    }



/*	public Hashtable getXForms(Vector XMLSchemas) throws SQLException {

        if (XMLSchemas == null) return null;

        Hashtable h = new Hashtable();

        for (int i = 0; i < XMLSchemas.size(); i++) {
            String schema = (String) XMLSchemas.get(i);
            Hashtable xform = getXForm(schema);
            if (xform != null) h.put(schema, xform);

        }

        return h;
    }
*/
    public Hashtable getXForms(Vector XMLSchemas) throws SQLException{
        if (XMLSchemas == null) return null;

        Hashtable h = new Hashtable();

        for (int i = 0; i < XMLSchemas.size(); i++) {
            String schema = (String) XMLSchemas.get(i);
            Hashtable xform = getXForm(schema);
            if (xform != null) h.put(schema, xform);
        }
        return h;
    }


/*	public Hashtable getXFormNames(Vector XMLSchemas) throws SQLException {

        boolean all_schemas = false;
        if (Utils.isNullVector(XMLSchemas)) {
            XMLSchemas = getSchemas(null, false);
            all_schemas = true;
        }

        Hashtable h = new Hashtable();

        for (int i = 0; i < XMLSchemas.size(); i++) {
            String schema = "";
            if (all_schemas) {
                HashMap schema_table = (HashMap) XMLSchemas.get(i);
                schema = (String) schema_table.get("xml_schema");
            } else
                schema = (String) XMLSchemas.get(i);
            if (schema == null) continue;

            Hashtable h_xform = getXForm(schema);
            if (h_xform == null) continue;

            String xform = (String) h_xform.get("xform_name");

            if (xform != null && !h.containsKey(schema)) h.put(schema, xform);

        }

        return h;
    }
*/

    public Hashtable getXFormNames(Vector XMLSchemas) throws SQLException{
        boolean all_schemas = false;
        if (Utils.isNullVector(XMLSchemas)) {
            XMLSchemas = schemaDao.getSchemas(null, false);
            all_schemas = true;
        }

        Hashtable h = new Hashtable();

        for (int i = 0; i < XMLSchemas.size(); i++) {
            String schema = "";
            if (all_schemas) {
                HashMap schema_table = (HashMap) XMLSchemas.get(i);
                schema = (String) schema_table.get("xml_schema");
            } else
                schema = (String) XMLSchemas.get(i);
            if (schema == null) continue;

            Hashtable h_xform = getXForm(schema);
            if (h_xform == null) continue;

            String xform = (String) h_xform.get("xform_name");

            if (xform != null && !h.containsKey(schema)) h.put(schema, xform);

        }
        return h;
    }



/*	public Hashtable getXForms() throws SQLException {

        Vector XMLSchemas = getSchemas(null, false);

        if (XMLSchemas == null) return null;

        Hashtable h = new Hashtable();

        for (int i = 0; i < XMLSchemas.size(); i++) {
            HashMap schema_table = (HashMap) XMLSchemas.get(i);
            String schema_id = (String) schema_table.get("schema_id");
            String schema = (String) schema_table.get("xml_schema");
            if (schema == null) continue;
            Hashtable xform = getXForm(schema);

            if (xform != null) h.put(schema, xform);

        }

        return h;
    }
*/
    public Hashtable getXForms() throws SQLException{
        Vector XMLSchemas = schemaDao.getSchemas(null, false);

        if (XMLSchemas == null) return null;

        Hashtable h = new Hashtable();

        for (int i = 0; i < XMLSchemas.size(); i++) {
            HashMap schema_table = (HashMap) XMLSchemas.get(i);
            //String schema_id = (String) schema_table.get("schema_id");
            String schema = (String) schema_table.get("xml_schema");
            if (schema == null) continue;
            Hashtable xform = getXForm(schema);
            if (xform != null) h.put(schema, xform);
        }
        return h;
    }



/*	public String addXForm(String schema_id, String xform, String title, String description) throws SQLException {

        return addFile(xform, title, XFORM_FILE_TYPE, SCHEMA_FILE_PARENT, schema_id, description);

    }

*/	public String addXForm(String schema_id, String xform, String title, String description) throws SQLException{

        return addFile(xform, title, XFORM_FILE_TYPE, SCHEMA_FILE_PARENT, schema_id, description);
    }



/*private String addFile(String fileName, String title, String type, String parent_type, String parent_id, String description) throws SQLException {

    title = (title == null ? "" : title);
    description = (description == null ? "" : description);

    String sql = "INSERT INTO " + FILE_TABLE + " ( " + FILE_NAME_FLD + ", " + FILE_TITLE_FLD + ", " + FILE_TYPE_FLD + ", " + FILE_PARENTTYPE_FLD + ", " + FILE_PARENTID_FLD + ", " + FILE_DESCRIPTION_FLD + ", " + FILE_DEFAULT_FLD + ") VALUES (" + Utils.strLiteral(fileName) + ", " + Utils.strLiteral(title) + ", '" + type + "', '" + parent_type + "', "
            + parent_id + ", " + Utils.strLiteral(description) + ", 'Y')";

    _executeUpdate(sql);

    sql = "SELECT " + FILE_ID_FLD + " FROM " + FILE_TABLE + " WHERE " + FILE_NAME_FLD + "= '" + fileName + "' AND " + FILE_TYPE_FLD + "='" + type + "'";

    String[][] r = _executeStringQuery(sql);

    if (r.length == 0) throw new SQLException("Error when returning id  for " + fileName + " ");

    return r[0][0];
}
*/

    private String addFile(String fileName, String title, String type, String parent_type, String parent_id, String description) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        title = (title == null ? "" : title);
        description = (description == null ? "" : description);
        String[][] r = null;

        if (isDebugMode){ logger.debug("Query is " + qInsertFile);}
        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(qInsertFile);
            pstmt.setString(1, fileName);
            pstmt.setString(2, title);
            pstmt.setString(3, type);
            pstmt.setString(4, parent_type);
            pstmt.setInt(5, Integer.parseInt(parent_id));
            pstmt.setString(6, description);
            pstmt.setString(7, "Y");
            pstmt.executeUpdate();
            pstmt.close();

            pstmt = conn.prepareStatement(qFileByNameAndType);
            pstmt.setString(1,fileName);
            pstmt.setString(2,type);
            rs = pstmt.executeQuery();
            r = getResults(rs);

            if (r.length == 0) throw new SQLException("Error when returning id  for " + fileName + " ");

        }finally{
            closeAllResources(null,pstmt,conn);
        }

        return r[0][0];
    }




/*	public void updateXForm(String xform_id, String schema_id, String title, String xform_name, String description) throws SQLException {

        updateFile(xform_id, xform_name, title, XFORM_FILE_TYPE, SCHEMA_FILE_PARENT, schema_id, description);

    }
*/
    public void updateXForm(String xform_id, String schema_id,  String title, String xform_name, String description) throws SQLException{
        updateFile(xform_id, xform_name, title, XFORM_FILE_TYPE, SCHEMA_FILE_PARENT, schema_id, description);
    }

    /*
    private void updateFile(String file_id, String fileName, String title, String type, String parent_type, String parent_id, String description) throws SQLException {

        title = (title == null ? "" : title);

        String sql = "UPDATE  " + FILE_TABLE + " SET " + FILE_NAME_FLD + "=" + Utils.strLiteral(fileName) + ", " + FILE_TITLE_FLD + "=" + Utils.strLiteral(title) + ", " + FILE_PARENTTYPE_FLD + "='" + parent_type + "', " + FILE_PARENTID_FLD + "=" + parent_id + ", " + FILE_DESCRIPTION_FLD + "=" + Utils.strLiteral(description) + "" + " WHERE " + FILE_ID_FLD
                + "=" + file_id;

        _executeUpdate(sql);

    }
    */




    private void updateFile(String file_id, String fileName, String title, String type, String parent_type, String parent_id, String description) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        description = (description == null ? "" : description);
        title = (title == null ? "" : title);
        if (isDebugMode){ logger.debug("Query is " + qUpdateFile);}
        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(qUpdateFile);
            pstmt.setString(1, fileName);
            pstmt.setString(2, title);
            pstmt.setString(3, parent_type);
            pstmt.setInt(4, Integer.parseInt(parent_id));
            pstmt.setString(5, description);
            pstmt.setInt(6, Integer.parseInt(file_id));
            pstmt.executeUpdate();
        }finally{
            closeAllResources(null,pstmt,conn);
        }
    }


/*	public void removeFile(String file_id) throws SQLException {

        String sql = "DELETE FROM " + FILE_TABLE + " WHERE " + FILE_ID_FLD + "=" + file_id;
        _executeUpdate(sql);

    }
*/
    private void removeFile(String file_id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode){ logger.debug("Query is " + qRemoveFile);}

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(qRemoveFile);
            pstmt.setInt(1, Integer.parseInt(file_id));
            pstmt.executeUpdate();
        }finally{
            closeAllResources(null,pstmt,conn);
        }
    }


}
