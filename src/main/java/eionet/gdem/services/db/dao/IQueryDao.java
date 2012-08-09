package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

public interface IQueryDao extends IDbSchema {
    public static String FILEREAD_EXCEPTION = "Unable to read the file: ";

    public String addQuery(String xmlSchemaID, String shortName, String queryFileName, String description, String content_type,
            String script_type, String upperLimit, String url) throws SQLException;

    /**
     * Updates a Query properties in the database.
     *
     * @param String
     *            query_id - id from database, used as a constraint
     * @param String
     *            schema_id - schema id
     * @param String
     *            short_name - db field for title
     * @param String
     *            description - text describing the query
     * @param String
     *            fileName - query file name
     * @param String
     *            content_type - result content type
     * @param String
     *            script_type - xquery, xsl, xgawk
     * @param String
     *            upperLimit - result upper limit in MB
     *  @param String url - original url of the XQ file
     */
    public void updateQuery(String query_id, String schema_id, String short_name, String description, String fileName,
            String content_type, String script_type, String upperLimit, String url) throws SQLException;

    public void removeQuery(String queryId) throws SQLException;

    public HashMap getQueryInfo(String queryId) throws SQLException;

    public String getQueryText(String queryId) throws SQLException;

    /**
     * returns all records from T_QUERY WHERE XML_SCHEMA=xmlSchema.
     *
     * @param String
     *            xmlSchema - xmlSchema as an URL
     * @return Vector contining all fields as Hashtable from T_QUERY table
     */
    public Vector listQueries(String xmlSchema) throws SQLException;

    public boolean checkQueryFile(String queryFileName) throws SQLException;

    public boolean checkQueryFile(String query_id, String queryFileName) throws SQLException;

}
