package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import eionet.gdem.dto.UplSchema;

public interface IUPLSchemaDao extends IDbSchema {
    /**
     * returns all uploaded schemas.
     *
     * @return Vector containing all fields as HashMap from UPL_SCHEMA table
     */

    Vector getUplSchema() throws SQLException;

    /**
     * Adds a new uploaded Schema to the database.
     *
     * @param schema
     *            - xml schema name
     * @return The ID of the added schema
     */
    String addUplSchema(String schema, String description, String fk_schema_id) throws SQLException;

    /**
     * Removes the uploaded xml schema from the uploaded schema table.
     *
     * @param uplSchemaId
     *            - schema Id
     */
    void removeUplSchema(String uplSchemaId) throws SQLException;

    /**
     * returns schema for requested schema id.
     *
     * @param uplSchemaId
     * @return
     * @throws SQLException
     */
    String getUplSchema(String uplSchemaId) throws SQLException;

    /**
     * Returns the uploaded schema identified with the given ID.
     *
     * @param schemaId
     * @return
     * @throws SQLException
     */
    Hashtable getUplSchemaById(String schemaId) throws SQLException;

    void updateUplSchema(String schema_id, String description, String schema_file, String fk_schema_id) throws SQLException;

    /**
     * Checks if schema with the given file name already ecxists in T_UPL_SCHEMA table.
     *
     * @param schemaFileName
     * @return true, if file exists in the database
     * @throws SQLException
     */
    boolean checkUplSchemaFile(String schemaFileName) throws SQLException;

    /**
     * Checks if specified schema foregin ID is already registered in T_UPL_SCHEMA table.
     *
     * @param schemaFK
     * @return true, if FK ID exists in the database
     * @throws SQLException
     */
    boolean checkUplSchemaFK(String schemaFK) throws SQLException;

    /**
     * Returns the uploaded schema identified with the given schema URL.
     *
     * @param schemaURL
     * @return
     * @throws SQLException
     */
    HashMap<String, String> getUplSchemaByUrl(String schemaURL) throws SQLException;

    /**
     * Gets all schemas from T_SCHEMA table with LEFT JOIN T_UPL_SCHEMA.
     *
     * @return List of UplSchema objects
     * @throws SQLException
     */
    List<UplSchema> getUploadedSchemas() throws SQLException;

    /**
     * Returns the uploaded schema identified with the given ID.
     *
     * @param schemaId
     * @return
     * @throws SQLException
     */
    HashMap<String, String> getUplSchemaByFkSchemaId(String schemaId) throws SQLException;
}
