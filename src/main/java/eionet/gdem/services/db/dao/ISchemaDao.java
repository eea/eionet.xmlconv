package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

public interface ISchemaDao extends IDbSchema {
    /**
     * Adds a new Schema to the database.
     *
     * @param xmlSchema
     *            - xml schema (http://eionet.eea.eu.int/RASchema"
     * @param xsdDescription
     *            - text describing the schema
     * @param schemaLang
     *            - SD, DTD, ..
     * @param doValidate
     *            - use schema for validatin
     * @param public_id
     *            - dtd id
     * @return The ID of the added schema
     */
    String addSchema(String xmlSchema, String description, String schemaLang, boolean doValidate, String public_id)
            throws SQLException;

    /**
     * Updates a Schema properties in the database.
     *
     * @param schema_id
     *            - id from database, used as a constraint
     * @param xmlSchema
     *            - xml schema (http://eionet.eea.eu.int/RASchema"
     * @param schemaLang
     *            - SD, DTD, ..
     * @param doValidate
     *            - use schema for validatin
     * @param xsdDescription
     *            - text describing the schema
     * @param public_id
     *            - dtd id
     */
    void updateSchema(String schema_id, String xmlSchema, String description, String schemaLang, boolean doValidation,
            String public_id, Date expireDate) throws SQLException;

    /**
     * Updates a Schema validate properties in the database.
     *
     * @param String
     *            schema_id - id from database, used as a constraint
     * @param String
     *            validate - validate property
     */
    void updateSchemaValidate(String schema_id, boolean validate) throws SQLException;

    String addSchema(String xmlSchema, String description) throws SQLException;

    /**
     * Removes the schema and all it's related stuff if needed.
     *
     * @param schemaId
     * @param del_stylesheets
     * @param del_queries
     * @param del_upl_schemas
     * @param del_self
     * @throws SQLException
     */
    void removeSchema(String schemaId, boolean del_stylesheets, boolean del_queries, boolean del_upl_schemas, boolean del_self)
            throws SQLException;

    /**
     * Gets the data of one or several schemas from the repository. Vector contains HashMaps with schema and its stylesheets
     * information
     */
    Vector getSchemas(String schemaId) throws SQLException;

    /**
     * Gets the data of one or several schemas from the repository. Vector contains HashMaps with schema and its stylesheets
     * information if needed
     */
    Vector getSchemas(String schemaId, boolean stylesheets) throws SQLException;

    /**
     * Gets the data of one schema from the repository. HashMap contains only one row from schema table
     */
    HashMap getSchema(String schema_id) throws SQLException;

    HashMap getSchema(String schema_id, boolean stylesheets) throws SQLException;

    /**
     * returns the schema ID from the repository.
     *
     * @param - schema URL
     * @return schema ID
     */
    String getSchemaID(String schema) throws SQLException;

    /**
     * returns all stylesheets for schema ID.
     *
     * @param - schema ID
     * @return Vector containing HashMaps with styleheet info
     */

    Vector getSchemaStylesheets(String schemaId) throws SQLException;

    Vector getSchemaQueries(String schemaId) throws SQLException;

    /**
     * returns all schemas which have stylesheets.
     *
     * @return
     * @throws SQLException
     */
    Vector getSchemasWithStl() throws SQLException;

}
