package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.util.HashMap;

public interface IStyleSheetDao extends IDbSchema{

	  /**
	  * Adds a new Stylesheet to the database
	  * @param xmlSchemaID - xml schema ID
	  * @param resultType - conversion type out: EXCEL, HTML, PDF, XML
	  * @param xslFileName - xslFileName in the folder
	  * @param xslDescription - text describing the stylesheet
	  * @param content_type - result file content_type
	  * @return The ID of the added stylesheet
	  */
	  public String addStylesheet(String xmlSchemaID, String resultType, String xslFileName, String description) throws SQLException;

	  /**
	  * Updates stylesheet properties in the database
	  * @param String xsl_id - id from database, used as a constraint 
	  * @param String schema_id - schema id
	  * @param String description - text describing the query
	  * @param String fileName - query file name
	  * @param String content_type - result content type
	  */
	  public void updateStylesheet(String xsl_id, String schema_id, String description, String fileName, String content_type) throws SQLException;

	  /**
	   * Removes the stylesheet from the stylesheets table
	   * @param - convert ID
	   */
	  public void removeStylesheet(String convertId) throws SQLException;

	  /**
	   * Gets the data of the stylesheet from the repository
	   */
	   public HashMap getStylesheetInfo(String convertId) throws SQLException;
	  
		 
	   public boolean checkStylesheetFile(String xslFileName) throws SQLException;
		 
	   public boolean checkStylesheetFile(String xsl_id, String xslFileName) throws SQLException;
	  
	
}
