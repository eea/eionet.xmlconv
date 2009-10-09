package eionet.gdem.services.db.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

import eionet.gdem.services.db.dao.IStyleSheetDao;
import eionet.gdem.utils.Utils;


public class StyleSheetMySqlDao extends MySqlBaseDao implements IStyleSheetDao {


	
	private static final String qInsertStylesheet = "INSERT INTO " + XSL_TABLE 
													+ " ( " 
													+ XSL_SCHEMA_ID_FLD + ", " 
													+ RESULT_TYPE_FLD + ", " 
													+ XSL_FILE_FLD + ", " 
													+ DESCR_FLD + ", "
													+ DEPENDS_ON
													+ ") " 
													+ " VALUES (?,?,?,?,?)";

	private static final String qStylesheetByFileName = "SELECT " + CNV_ID_FLD + " FROM " + XSL_TABLE + " WHERE " + XSL_FILE_FLD + "= ? ";

	
	private static final String qUpdateStyleSheet = "UPDATE  " + XSL_TABLE 
													+ " SET " + DESCR_FLD + "= ? "  + ", " 
													+ XSL_SCHEMA_ID_FLD + "= ? "  + ", " 
													+ RESULT_TYPE_FLD + "= ? " + ", " + DEPENDS_ON + "= ?"  
													+ " WHERE " + CNV_ID_FLD + "= ?";

	private static final String qUpdateStyleSheetFN = "UPDATE  " + XSL_TABLE 
														+ " SET " + XSL_FILE_FLD + "= ? "  + ", " 
														+ DESCR_FLD + "= ? "  + ", " 
														+ XSL_SCHEMA_ID_FLD + "= ? " + ", " 
														+ RESULT_TYPE_FLD + "= ? " + ", " + DEPENDS_ON + "= ?"  
														+ " WHERE " + CNV_ID_FLD + "= ? ";

	
	private static final String qRemoveStylesheet = "DELETE FROM " + XSL_TABLE + " WHERE " + CNV_ID_FLD + "= ? ";
	
	private static final String qStylesheetInfoBase = 	"SELECT " 
														+ XSL_TABLE + "." + XSL_SCHEMA_ID_FLD + "," 
														+ XSL_FILE_FLD + ", " 
														+ XSL_TABLE + "." + DESCR_FLD + "," 
														+ RESULT_TYPE_FLD + ", " 
														+ SCHEMA_TABLE + "." + XML_SCHEMA_FLD + ", "
														+ XSL_TABLE + "." + DEPENDS_ON
														+ " FROM " + XSL_TABLE 
														+ " LEFT OUTER JOIN " + SCHEMA_TABLE 
														+ " ON " + XSL_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD;


	private static final String qStylesheetInfoByFileName = qStylesheetInfoBase + " WHERE " + XSL_FILE_FLD + "= ?";  	
	private static final String qStylesheetInfoByID = qStylesheetInfoBase + " WHERE " + CNV_ID_FLD + "= ?";  	
	
	private static final String qCheckStylesheetFile = "SELECT COUNT(*) FROM " + XSL_TABLE + " WHERE " + XSL_FILE_FLD + "= ?";	
	private static final String qCheckStylesheetFileID = "SELECT COUNT(*) FROM " + XSL_TABLE + " WHERE " + XSL_FILE_FLD + "= ? " + "and " +CNV_ID_FLD+"= ?";
	
	
	public StyleSheetMySqlDao(){}
	
/*	public String addStylesheet(String xmlSchemaID, String resultType, String xslFileName, String description) throws SQLException {

		description = (description == null ? "" : description);

		String sql = "INSERT INTO " + XSL_TABLE + " ( " + XSL_SCHEMA_ID_FLD + ", " + RESULT_TYPE_FLD + ", " + XSL_FILE_FLD + ", " + DESCR_FLD + ") VALUES ('" + xmlSchemaID + "', '" + resultType + "', " + Utils.strLiteral(xslFileName) + ", " + Utils.strLiteral(description) + ")";

		_executeUpdate(sql);

		sql = "SELECT " + CNV_ID_FLD + " FROM " + XSL_TABLE + " WHERE " + XSL_FILE_FLD + "=" + Utils.strLiteral(xslFileName);

		String[][] r = _executeStringQuery(sql);

		if (r.length == 0) throw new SQLException("Error when returning id  for " + xslFileName + " ");

		return r[0][0];
	}

*/	
	public String addStylesheet(String xmlSchemaID, String resultType, String xslFileName, String description, String dependsOn) throws SQLException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String result = null;
		
		description = (description == null ? "" : description);
		
		if (isDebugMode){ logger.debug("Query is " + qInsertStylesheet);}

		try{
			conn = getConnection();	
			pstmt = conn.prepareStatement(qInsertStylesheet);
			pstmt.setInt(1, Integer.parseInt(xmlSchemaID));
			pstmt.setString(2, resultType);
			pstmt.setString(3, xslFileName);
			pstmt.setString(4, description);
			if (Utils.isNullStr(dependsOn)) {
				pstmt.setNull(5, Types.INTEGER);
			} else {
				pstmt.setInt(5, Integer.parseInt(dependsOn));
			}
			
			pstmt.executeUpdate();
			
			if(pstmt != null) pstmt.close();
			
			pstmt = conn.prepareStatement(qStylesheetByFileName);
			pstmt.setString(1,xslFileName);
			rs = pstmt.executeQuery();			
			String[][] r = getResults(rs);
			if (r.length == 0) throw new SQLException("Error when returning id  for " + xslFileName + " ");
			result = r[0][0];
		}finally{
			closeAllResources(rs,pstmt,conn);			
		}		
				
		return result;
	}


/*	public void updateStylesheet(String xsl_id, String schema_id, String description, String fileName, String content_type) throws SQLException {

		description = (description == null ? "" : description);
		String sql;
		if (fileName == null || fileName.equals("")) {
			sql = "UPDATE  " + XSL_TABLE + " SET " + DESCR_FLD + "=" + Utils.strLiteral(description) + ", " + XSL_SCHEMA_ID_FLD + "=" + schema_id + ", " + RESULT_TYPE_FLD + "=" + Utils.strLiteral(content_type) + " WHERE " + CNV_ID_FLD + "=" + xsl_id;
		} else {
			sql = "UPDATE  " + XSL_TABLE + " SET " + XSL_FILE_FLD + "=" + Utils.strLiteral(fileName) + ", " + DESCR_FLD + "=" + Utils.strLiteral(description) + ", " + XSL_SCHEMA_ID_FLD + "=" + schema_id + ", " + RESULT_TYPE_FLD + "=" + Utils.strLiteral(content_type) + " WHERE " + CNV_ID_FLD + "=" + xsl_id;
		}

		_executeUpdate(sql);

	}
*/
	
	
	/**
	 * {@inheritDoc}
	 */
	public void updateStylesheet(String xsl_id, String schema_id, String description, String fileName, String content_type, String dependsOn) throws SQLException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		description = (description == null ? "" : description);

		boolean isEmptyFileName = (fileName == null || fileName.equals(""));
	
		try{
			conn = getConnection();
			if (isEmptyFileName) {
				pstmt = conn.prepareStatement(qUpdateStyleSheet);
				pstmt.setString(1, description);
				pstmt.setInt(2, Integer.parseInt(schema_id));				
				pstmt.setString(3, content_type);
				if (Utils.isNullStr(dependsOn)) {
					pstmt.setNull(4, Types.INTEGER);
				} else {
					pstmt.setInt(4, Integer.parseInt(dependsOn));
				}
				pstmt.setInt(5, Integer.parseInt(xsl_id));
			} else {
				pstmt = conn.prepareStatement(qUpdateStyleSheetFN);
				pstmt.setString(1, fileName);
				pstmt.setString(2, description);
				pstmt.setInt(3, Integer.parseInt(schema_id));				
				pstmt.setString(4, content_type);
				if (Utils.isNullStr(dependsOn)) {
					pstmt.setNull(5, Types.INTEGER);
				} else {
					pstmt.setInt(5, Integer.parseInt(dependsOn));
				}
				pstmt.setInt(6, Integer.parseInt(xsl_id));
			}			
			if (isDebugMode){ logger.debug("Query is " + (isEmptyFileName?qUpdateStyleSheet:qUpdateStyleSheetFN));}			
			pstmt.executeUpdate();
		}finally{
			closeAllResources(null,pstmt,conn);			
		}		
		
	}
	
	
/*	public void removeStylesheet(String convertId) throws SQLException {

		String sql = "DELETE FROM " + XSL_TABLE + " WHERE " + CNV_ID_FLD + "=" + convertId;
		_executeUpdate(sql);

	}
*/	
	public void removeStylesheet(String convertId) throws SQLException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		if (isDebugMode){ logger.debug("Query is " + qRemoveStylesheet);}
		
		try{
			conn = getConnection();
			pstmt = conn.prepareStatement(qRemoveStylesheet);
			pstmt.setInt(1, Integer.parseInt(convertId));				
			pstmt.executeUpdate();
		}finally{
			closeAllResources(null,pstmt,conn);			
		}				
	}
	
	
	
/*	public HashMap getStylesheetInfo(String convertId) throws SQLException {

		int id = 0;
		String xslName = null;
		try {
			id = Integer.parseInt(convertId);
		} catch (NumberFormatException n) {
			if (convertId.endsWith("xsl"))
				xslName = convertId;
			else
				throw new SQLException("not numeric ID or xsl file name: " + convertId);
		}

		String sql = "SELECT " + XSL_TABLE + "." + XSL_SCHEMA_ID_FLD + "," + XSL_FILE_FLD + ", " + XSL_TABLE + "." + DESCR_FLD + "," + RESULT_TYPE_FLD + ", " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + " FROM " + XSL_TABLE + " LEFT OUTER JOIN " + SCHEMA_TABLE + " ON " + XSL_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD;
		if (xslName != null) {
			sql += " WHERE " + XSL_FILE_FLD + "=" + Utils.strLiteral(xslName);

		} else {
			sql += " WHERE " + CNV_ID_FLD + "=" + id;
		}

		String r[][] = _executeStringQuery(sql);

		HashMap h = null;

		if (r.length > 0) {
			h = new HashMap();
			h.put("convert_id", convertId);
			h.put("schema_id", r[0][0]);
			h.put("xsl", r[0][1]);
			h.put("description", r[0][2]);
			h.put("content_type_out", r[0][3]);
			h.put("xml_schema", r[0][4]);
		}

		return h;
	}
*/

	public HashMap getStylesheetInfo(String convertId) throws SQLException{
		int id = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		HashMap h = null;
		String xslName = null;	
		
		boolean byFile = convertId.endsWith("xsl"); 
		
		try {
			id = Integer.parseInt(convertId);
		} catch (NumberFormatException n) {
			if (byFile)
				xslName = convertId;
			else
				throw new SQLException("not numeric ID or xsl file name: " + convertId);
		}
			
		if (isDebugMode){ logger.debug("Query is " + (byFile?qStylesheetInfoByFileName:qStylesheetInfoByID));}
		
		try {
			conn = getConnection();
			if (byFile){
				pstmt = conn.prepareStatement(qStylesheetInfoByFileName);
				pstmt.setString(1,xslName);
			}else {
				pstmt = conn.prepareStatement(qStylesheetInfoByID);
				pstmt.setInt(1,id);				
			}
			rs = pstmt.executeQuery();			

			String[][] r = getResults(rs);
			if (r.length > 0) {
				h = new HashMap();
				h.put("convert_id", convertId);
				h.put("schema_id", r[0][0]);
				h.put("xsl", r[0][1]);
				h.put("description", r[0][2]);
				h.put("content_type_out", r[0][3]);
				h.put("xml_schema", r[0][4]);
				h.put("depends_on", r[0][5]);
			}
		} 
		finally {
			closeAllResources(rs,pstmt,conn);
		}		
		return h;			
	}
	
	
	
/*	public boolean checkStylesheetFile(String xslFileName) throws SQLException {

		int id = 0;

		String sql = "SELECT COUNT(*) FROM " + XSL_TABLE + " WHERE " + XSL_FILE_FLD + "=" + Utils.strLiteral(xslFileName);

		String r[][] = _executeStringQuery(sql);

		String count = r[0][0];
		if (count.equals("0")) {
			return false;
		} else {
			return true;
		}

	}
*/

	public boolean checkStylesheetFile(String xslFileName) throws SQLException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		boolean result = false;
 		
		if (isDebugMode){ logger.debug("Query is " + qCheckStylesheetFile);}
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(qCheckStylesheetFile);
			pstmt.setString(1,xslFileName);
			rs = pstmt.executeQuery();			
			String[][] r = getResults(rs);
			String count = r[0][0];
			if (!count.equals("0")) 
				result = true;			
		} 
		finally {
			closeAllResources(rs,pstmt,conn);
		}
		return result;	
	}
	

	
	
	
/*	public boolean checkStylesheetFile(String xsl_id, String xslFileName) throws SQLException {
		int id = 0;

		String sql = "SELECT COUNT(*) FROM " + XSL_TABLE + " WHERE " + XSL_FILE_FLD + "=" + Utils.strLiteral(xslFileName) 
		+ "and " +CNV_ID_FLD+"="+xsl_id;

		String r[][] = _executeStringQuery(sql);

		String count = r[0][0];
		if (count.equals("0")) {
			return false;
		} else {
			return true;
		}
	}
*/		
	public boolean checkStylesheetFile(String xsl_id, String xslFileName) throws SQLException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		boolean result = false;
 		
		if (isDebugMode){ logger.debug("Query is " + qCheckStylesheetFileID);}
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(qCheckStylesheetFileID);
			pstmt.setString(1,xslFileName);
			pstmt.setInt(2, Integer.parseInt(xsl_id));			
			rs = pstmt.executeQuery();			
			String[][] r = getResults(rs);
			String count = r[0][0];
			if (!count.equals("0")) 
				result = true;			
		} 
		finally {
			closeAllResources(rs,pstmt,conn);
		}
		return result;			
	}
	  
	
}
