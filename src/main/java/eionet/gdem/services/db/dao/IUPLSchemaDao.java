package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import eionet.gdem.dto.UplSchema;

/**
 * Uploading schema Dao interface.
 * @author Unknown
 * @author George Sofianos
 */
public interface IUPLSchemaDao extends IDbSchema {

    /**
     * returns all uploaded schemas.
     *
     * @return Vector containing all fields as HashMap from UPL_SCHEMA table
     * @throws SQLException If an error occurs.
     */
    Vector getUplSchema() throws SQLException;

    /**
     * Adds a new uploaded Schema to the database.
     *
     * @param schema
     *            - xml schema name
     * @param description Description
     * @param fk_schema_id Schema Id
     * @return The ID of the added schema
     * @throws SQLException If an error occurs.
     */
    String addUplSchema(String schema, String description, String fk_schema_id) throws SQLException;

    /**
     * Removes the uploaded xml schema from the uploaded schema table.
     *
     * @param uplSchemaId
     *            - schema Id
     * @throws SQLException If an error occurs.
     */
    void removeUplSchema(String uplSchemaId) throws SQLException;

    /**
     * returns schema for requested schema id.
     *
     * @param uplSchemaId Uploaded Schema id
     * @return Schema
     * @throws SQLException If an error occurs.
     */
    String getUplSchema(String uplSchemaId) throws SQLException;

    /**
     * Returns the uploaded schema identified with the given ID.
     *
     * @param schemaId Schema Id
     * @return Schema
     * @throws SQLException If an error occurs.
     */
    Hashtable getUplSchemaById(String schemaId) throws SQLException;

    /**
     * Updates Uploaded Schema
     * @param schema_id Schema Id
     * @param description Description
     * @param schema_file Schema file
     * @param fk_schema_id Schema id
     * @throws SQLException If an error occurs.
     */
    void updateUplSchema(String schema_id, String description, String schema_file, String fk_schema_id) throws SQLException;

    /**
     * Checks if schema with the given file name already ecxists in T_UPL_SCHEMA table.
     *
     * @param schemaFileName Schema filename
     * @return true, if file exists in the database
     * @throws SQLException If an error occurs.
     */
    boolean checkUplSchemaFile(String schemaFileName) throws SQLException;

    /**
     * Checks if specified schema foreign ID is already registered in T_UPL_SCHEMA table.
     *
     * @param schemaFK Schema foreign Id
     * @return true, if FK ID exists in the database
     * @throws SQLException If an error occurs.
     */
    boolean checkUplSchemaFK(String schemaFK) throws SQLException;

    /**
     * Returns the uploaded schema identified with the given schema URL.
     *
     * @param schemaURL Schema URL
     * @return Uploaded Schema
     * @throws SQLException If an error occurs.
     */
    HashMap<String, String> getUplSchemaByUrl(String schemaURL) throws SQLException;

    /**
     * Gets all schemas from T_SCHEMA table with LEFT JOIN T_UPL_SCHEMA.
     *
     * @return List of UplSchema objects
     * @throws SQLException If an error occurs.
     */
    List<UplSchema> getUploadedSchemas() throws SQLException;

    /**
     * Returns the uploaded schema identified with the given ID.
     *
     * @param schemaId Schema Id
     * @return Uploaded Schema
     * @throws SQLException If an error occurs.
     */
    HashMap<String, String> getUplSchemaByFkSchemaId(String schemaId) throws SQLException;
}
