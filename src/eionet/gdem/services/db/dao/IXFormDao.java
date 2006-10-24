package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

public interface IXFormDao extends IDbSchema{
	  /**
	  * Removes the xform from the xformss table
	  * @param - xform ID
	  */
	  public void removeXForm(String xformId) throws SQLException;

	  /**
	   * returns XForm file name for specified schema
	   * @param - XML schema url
	   * @return Hashtable contining XForm  url
	   */

	   public Hashtable getXForm(String XMLSchema) throws SQLException;
	   
	   /**
	   * returns XForm information
	   * @param - XForm id
	   * @return Hashtable contining XForm  info
	   */

	   public Hashtable getXFormByID(String xform_id) throws SQLException;

	 /**
	   * returns XForm file info  for specified schemas
	   * @param - array of XML schema urls
	   * @return Hashtable contining schema url as key and  XForm  url as value
	   */

	   public Hashtable getXForms(Vector XMLSchemas) throws SQLException;
	 /**
	   * returns XForm file names  for specified schemas
	   * @param - array of XML schema urls
	   * @return Hashtable contining schema url as key and  XForm  url as value
	   */

	   public Hashtable getXFormNames(Vector XMLSchemas) throws SQLException;

	 /**
	   * returns all XForm file names 
	   * @return Hashtable contining schema url as key and  XForm  url as value
	   */

	   public Hashtable getXForms() throws SQLException;

	   /**
	    * Adds a new XForm to the database
	    * @param xmlSchemaID - xml schema ID
	    * @param xform - xform file name in the folder
	    * @param title - title describing the xform
	    * @param description - describes the xform
	    * @return The ID of the added xform
	    */
	    public String addXForm(String xmlSchemaID, String xform, String title, String description) throws SQLException;

	    /**
	    * Updates a XForm properties in the database
	    * @param xform_id - id from database, used as a constraint 
	    * @param schema_id - xml schema id
	    * @param title - title describing the xform shortly
	    * @param description - text describes the xform
	    * @param xform_name - xform file name
	    */
	    public void updateXForm(String xform_id, String schema_id,  String title, String xform_name, String description) throws SQLException;
	   
	  
}
