package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

/**
 * Query DAO interface.
 * @author Unknown
 * @author George Sofianos
 */
public interface IQueryDao extends IDbSchema {

    /** Text for exception explanation. */
    String FILEREAD_EXCEPTION = "Unable to read the file: ";

    /**
     * Adds Query
     * @param xmlSchemaID Schema Id
     * @param shortName Short name
     * @param queryFileName Query file name
     * @param description Description
     * @param content_type Content type
     * @param script_type Script type
     * @param upperLimit Upper Limit
     * @param url URL
     * @return Result
     * @throws SQLException If an error occurs.
     */
    String addQuery(String xmlSchemaID, String shortName, String queryFileName, String description, String content_type,
            String script_type, String upperLimit, String url) throws SQLException;

    /**
     * Updates a Query properties in the database.
     *
     * @param query_id - id from database, used as a constraint
     * @param schema_id - schema id
     * @param short_name - db field for title
     * @param description - text describing the query
     * @param fileName - query file name
     * @param content_type - result content type
     * @param script_type - xquery, xsl, xgawk
     * @param upperLimit - result upper limit in MB
     * @param url - original url of the XQ file
     * @throws SQLException If an error occurs.
     */
    void updateQuery(String query_id, String schema_id, String short_name, String description, String fileName,
            String content_type, String script_type, String upperLimit, String url) throws SQLException;

    /**
     * Remove query
     * @param queryId query id
     * @throws SQLException If an error occurs.
     */
    void removeQuery(String queryId) throws SQLException;

    /**
     * Gets query info
     * @param queryId query id
     * @return Query info
     * @throws SQLException If an error occurs.
     */
    HashMap getQueryInfo(String queryId) throws SQLException;

    /**
     * Gets query text
     * @param queryId Query id
     * @return Query text
     * @throws SQLException If an error occurs.
     */
    String getQueryText(String queryId) throws SQLException;

    /**
     * returns all records from T_QUERY WHERE XML_SCHEMA=xmlSchema.
     *
     * @param xmlSchema - xmlSchema as an URL
     * @return Vector contining all fields as Hashtable from T_QUERY table
     * @throws SQLException If an error occurs.
     */
    Vector listQueries(String xmlSchema) throws SQLException;
    
    Vector listQueries(String xmlSchema, boolean active) throws SQLException;

    /**
     * Checks query file
     * @param queryFileName Query filename
     * @return True if query file is valid
     * @throws SQLException If an error occurs.
     */
    boolean checkQueryFile(String queryFileName) throws SQLException;

    /**
     * Checks query file
     * @param query_id Query id
     * @param queryFileName Query filename
     * @return True if query file is valid
     * @throws SQLException If an error occurs.
     */
    boolean checkQueryFile(String query_id, String queryFileName) throws SQLException;

    /**
     * Activates query
     * @param queryId Query id
     * @throws SQLException If an error occurs.
     */
    void activateQuery(String queryId) throws SQLException;

    /**
     * Deactivates query
     * @param queryId Query id
     * @throws SQLException If an error occurs.
     */
    void deactivateQuery(String queryId) throws SQLException;

}
