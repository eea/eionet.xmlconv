package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

public interface IUPLSchemaDao extends IDbSchema{
	  /**
	   * returns all uploaded schemas
	   * @return Vector containing all fields as HashMap from UPL_SCHEMA table
	   */

	   public Vector getUplSchema() throws SQLException;  
	   
	   /**
	    * Adds a new uploaded Schema to the database
	    * @param schema - xml schema name
	    * @return The ID of the added schema
	    */    
	    public String addUplSchema(String schema, String description, String schema_url) throws SQLException;

		 /**
		  * Removes the uploaded xml schema from the uploaded schema table
		  * @param uplSchemaId - schema Id
		  */
		 public void removeUplSchema(String uplSchemaId) throws SQLException;
		 
		 /**
		  * returns schema for requested schema id
		  * @param uplSchemaId
		  * @return
		  * @throws SQLException
		  */
		 public String getUplSchema(String uplSchemaId) throws SQLException; 

		 public Hashtable getUplSchemaById(String schemaId) throws SQLException;
		 
		 public void updateUplSchema(String schema_id, String description, String schema_file, String schema_url) throws SQLException;
		 
		 public boolean checkUplSchemaFile(String schemaFileName) throws SQLException;
}
