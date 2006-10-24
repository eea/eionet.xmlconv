package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

public interface ISchemaDao extends IDbSchema{
	  /**
	  * Adds a new Schema to the database
	  * @param xmlSchema - xml schema  (http://eionet.eea.eu.int/RASchema"
	  * @param xsdDescription - text describing the schema
	  * @param public_id - dtd public id
	  * @return The ID of the added schema
	  */
	  public String addSchema(String xmlSchema,  String description, String public_id) throws SQLException;

	  /**
	  * Updates a Schema properties in the database
	  * @param schema_id - id from database, used as a constraint 
	  * @param xmlSchema - xml schema  (http://eionet.eea.eu.int/RASchema"
	  * @param xsdDescription - text describing the schema
	  * @param public_id - dtd public id
	  */
	  public void updateSchema(String schema_id, String xmlSchema,  String description, String public_id) throws SQLException;


	  /**
	  * Updates a Schema validate properties in the database
	  * @param String schema_id - id from database, used as a constraint 
	  * @param String validate - validate property
	  */
	  public void updateSchemaValidate(String schema_id, String validate) throws SQLException;




	  public String addSchema(String xmlSchema,  String description) throws SQLException;


	  /**
	  * Removes the schema and all it's stylesheets
	  * @param - schema ID
	  */
	  public void removeSchema(String schemaId, boolean del_stylesheets, boolean del_queries, boolean del_self) throws SQLException;



	  /**
	  * Gets the data of one or several schemas from the repository
	  * Vector contains HashMaps with schema and it's stylesheets information
	  */
	  public Vector getSchemas(String schemaId) throws SQLException;
	  /**
	  * Gets the data of one or several schemas from the repository
	  * Vector contains HashMaps with schema and it's stylesheets information if needed
	  */
	  public Vector getSchemas(String schemaId, boolean stylesheets) throws SQLException;

	  /**
	  * Gets the data of one schema from the repository
	  * HashMap contains only one row from schema table
	  */
	  public HashMap getSchema(String schema_id) throws SQLException;

	  public HashMap getSchema(String schema_id, boolean stylesheets) throws SQLException ;

	  /**
	  * returns the schema ID from the repository
	  * @param - schema URL
	  * @return schema ID
	  */
	  public String getSchemaID(String schema) throws SQLException;

	  /**
	  * returns all stylesheets for schema ID
	  * @param - schema ID
	  * @return Vector containing HashMaps with styleheet info
	  */

	  public Vector getSchemaStylesheets(String schemaId) throws SQLException;
	  public Vector getSchemaQueries(String schemaId) throws SQLException;

	  

	 /**
	  * retturns all schemas which have stylesheets
	  * @return
	  * @throws SQLException
	  */
	 public Vector getSchemasWithStl() throws SQLException;
		 
	  	  
}
