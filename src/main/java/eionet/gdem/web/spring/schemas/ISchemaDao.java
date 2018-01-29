package eionet.gdem.web.spring.schemas;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import eionet.gdem.dto.Schema;

/**
 *
 * DAO interface class for XML Schema database operations.
 *
 * @author Enriko Käsper
 */
public interface ISchemaDao {
    /**
     * Adds a new Schema to the database.
     *
     * @param xmlSchema
     *            - xml schema (http://eionet.eea.eu.int/RASchema"
     * @param description
     *            - text describing the schema
     * @param schemaLang
     *            - SD, DTD, ..
     * @param doValidate
     *            - use schema for validatin
     * @param publicId
     *            - dtd id
     * @param blocker If true, then failed XML Schema validation blocks releasing of envelope in CDR.
     * @return The ID of the added schema
     * @throws SQLException in case of database operation.
     */
            String
            addSchema(String xmlSchema, String description, String schemaLang, boolean doValidate, String publicId, boolean blocker)
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
     * @param doValidation
     *            - use schema for validating
     * @param publicId
     *            - dtd id
     * @param blocker If true, then failed XML Schema validation blocks releasing of envelope in CDR.
     * @throws SQLException in case of database error.
     */
    void updateSchema(String schema_id, String xmlSchema, String description, String schemaLang, boolean doValidation,
            String publicId, Date expireDate, boolean blocker) throws SQLException;

    /**
     * Updates a Schema validate properties in the database.
     *
     * @param schemaId - id from database, used as a constraint
     * @param validate - validate property
     * @param blocker If true, then failed XML Schema validation blocks releasing of envelope in CDR.
     * @throws SQLException in case of database error.
     */
    void updateSchemaValidate(String schemaId, boolean validate, boolean blocker) throws SQLException;

    /**
     * Add XML Schema into database.
     * @param xmlSchema XML Schema url.
     * @param description Schema textual description.
     * @return Primary key of newely added Schema.
     * @throws SQLException in case of database error.
     */
    String addSchema(String xmlSchema, String description) throws SQLException;

    /**
     * Removes the schema and all it's related stuff if needed.
     *
     * @param schemaId XML Schema database numeric id or URL.
     * @param delQueries
     * @param delUplSchemas
     * @param delSelf
     * @throws SQLException in case of database error.
     */
    void removeSchema(String schemaId, boolean delQueries, boolean delUplSchemas, boolean delSelf) throws SQLException;

    /**
     * Gets the data of one or several schemas from the repository. Vector contains HashMaps with schema and its stylesheets
     * information.
     *
     * @param schemaId XML Schema database numeric id or URL.
     * @return List of Schemas.
     * @throws SQLException in case of database error.
     */
    Vector getSchemas(String schemaId) throws SQLException;

    /**
     * Gets the data of one or several schemas from the repository. Vector contains HashMaps with schema and its stylesheets
     * information if needed.
     *
     * @param schemaId XML Schema database numeric id or URL.
     * @param stylesheets true if the result contains also Schema related stylesheets information.
     * @return List of Schemas.
     * @throws SQLException in case of database error.
     */
    Vector getSchemas(String schemaId, boolean stylesheets) throws SQLException;

    /**
     * Gets the data of one schema from the repository. HashMap contains only one row from schema table
     *
     * @param schemaId XML Schema database numeric id or URL.
     * @return HashMap with XML Schema info.
     * @throws SQLException in case of database error.
     */
    HashMap getSchema(String schemaId) throws SQLException;

    /**
     *
     * @param schemaId XML Schema database numeric id or URL.
     * @param stylesheets stylesheets
     * @return Schema map
     * @throws SQLException in case of database error.
     */
    HashMap getSchema(String schemaId, boolean stylesheets) throws SQLException;

    /**
     * returns the schema ID from the repository.
     *
     * @param schema XML Schema database numeric id or URL.
     * @return schema ID
     * @throws SQLException in case of database error.
     */
    String getSchemaID(String schema) throws SQLException;

    /**
     * returns all stylesheets for schema ID.
     *
     * @param schemaId XML Schema database numeric id or URL.
     * @return Vector containing HashMaps with styleheet info
     * @throws SQLException in case of database error.
     */
    Vector getSchemaStylesheets(String schemaId) throws SQLException;

    /**
     * Get XML Schemas with related QA scripts information.
     * @param schemaId XML Schema database numeric id or URL.
     * @return List of XML Schemas.
     * @throws SQLException in case of database error.
     */
    Vector getSchemaQueries(String schemaId) throws SQLException;

    /**
     * Returns all schemas which have stylesheets.
     *
     * @return List of XML Schemas.
     * @throws SQLException in case of database error.
     */
    Vector getSchemasWithStl() throws SQLException;

    /**
     * Get all XML Schemas with the reference to uploaded file if exists and
     * count related stylesheets and QA scripts.
     * @return List of Schema objects.
     */
    List<Schema> getSchemasWithRelations();

}
