package eionet.gdem.services.db.dao.mysql;

import eionet.gdem.Properties;
import eionet.gdem.services.db.dao.IQueryDao;
import eionet.gdem.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.HashMap;


public class QueryMySqlDao extends MySqlBaseDao implements IQueryDao {

	private static final String  qListQueries = "SELECT "
													+ QUERY_TABLE + "." + QUERY_ID_FLD + ", "
													+ SHORT_NAME_FLD + ", " + QUERY_FILE_FLD + ", "
													+ QUERY_TABLE + "." + DESCR_FLD + ","
													+ SCHEMA_TABLE + "." + SCHEMA_ID_FLD + ","
													+ SCHEMA_TABLE + "." + XML_SCHEMA_FLD + ", "
													+  QUERY_TABLE + "." + RESULT_TYPE_FLD + ", "
													+  CONVTYPE_TABLE + "." + CONTENT_TYPE_FLD
													+ " FROM "
													+  QUERY_TABLE + " LEFT OUTER JOIN " + SCHEMA_TABLE
													+ " ON " + QUERY_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD
													+ " LEFT OUTER JOIN " + CONVTYPE_TABLE 
													+ " ON " +QUERY_TABLE + "." + RESULT_TYPE_FLD + "=" + CONVTYPE_TABLE + "." + CONV_TYPE_FLD;

	private static final String  qListQueriesBySchema = qListQueries  + " WHERE " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + "= ?";
    private static final String  qQueryTextByFileName = "SELECT " + QUERY_FILE_FLD  + " FROM " + QUERY_TABLE + " WHERE " + QUERY_FILE_FLD + "= ?";
	private static final String  qQueryTextByID = "SELECT " + QUERY_FILE_FLD  + " FROM " + QUERY_TABLE + " WHERE " + QUERY_ID_FLD + "= ?";

	private static final String  qQueryInfo = 	"SELECT "
												+ QUERY_TABLE + "." + XSL_SCHEMA_ID_FLD + ","
												+ QUERY_FILE_FLD + ", " + QUERY_TABLE + "." + DESCR_FLD + ","
												+ SHORT_NAME_FLD + ", " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + ","
												+ QUERY_TABLE + "." + RESULT_TYPE_FLD + ", "
												+ CONVTYPE_TABLE + "." + CONTENT_TYPE_FLD
												+ " FROM "
												+ QUERY_TABLE + " LEFT OUTER JOIN " + SCHEMA_TABLE
												+ " ON " + QUERY_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD
												+ " LEFT OUTER JOIN " + CONVTYPE_TABLE
												+ " ON " + QUERY_TABLE + "." + RESULT_TYPE_FLD + "=" + CONVTYPE_TABLE + "." + CONV_TYPE_FLD;


	private static final String  qQueryInfoByID =  qQueryInfo + " WHERE " + QUERY_ID_FLD + "=?" ;
	private static final String  qQueryInfoByFileName = qQueryInfo + " WHERE " + QUERY_FILE_FLD + "=?";

	private static final String  qRemoveQuery = "DELETE FROM " + QUERY_TABLE + " WHERE " + QUERY_ID_FLD + "=?";
	private static final String  qUpdateQuery = "UPDATE  " + QUERY_TABLE
												+ " SET "
												+ QUERY_FILE_FLD + "=?" + ", "
												+ SHORT_NAME_FLD + "=?" + ", "
												+ DESCR_FLD + "=?" + ", "
												+ XSL_SCHEMA_ID_FLD + "=?" + ", "
												+ RESULT_TYPE_FLD + "=?"
												+ " WHERE " + QUERY_ID_FLD + "=?";



	private static final String  qInsertQuery = "INSERT INTO " + QUERY_TABLE
												+ " ( "
												+ XSL_SCHEMA_ID_FLD + ", "
												+ SHORT_NAME_FLD + ", "
												+ QUERY_FILE_FLD + ", "
												+ DESCR_FLD + ", "
												+ RESULT_TYPE_FLD
												+ ") " +
												" VALUES (?,?,?,?,?)";


	private static final String  qQueryByFileName = "SELECT " + QUERY_ID_FLD + " FROM " + QUERY_TABLE + " WHERE " + QUERY_FILE_FLD + "=?";


	private static final String  qCheckQueryFileByName = "SELECT COUNT(*) FROM " + QUERY_TABLE + " WHERE " + QUERY_FILE_FLD + "=?";

	private static final String  qCheckQueryFileByIdAndName =  "SELECT COUNT(*) FROM " + QUERY_TABLE + " WHERE " + QUERY_FILE_FLD + "=?" + " and " +QUERY_ID_FLD+"=?";
;





	public QueryMySqlDao(){}

/*	public String addQuery(String xmlSchemaID, String shortName, String queryFileName, String description, String content_type) throws SQLException {

		description = (description == null ? "" : description);

		String sql = "INSERT INTO " + QUERY_TABLE + " ( " + XSL_SCHEMA_ID_FLD + ", " + SHORT_NAME_FLD + ", " + QUERY_FILE_FLD + ", " + DESCR_FLD + ", " + RESULT_TYPE_FLD + ")
		VALUES ('" + xmlSchemaID + "', '" + shortName + "', " + Utils.strLiteral(queryFileName) + ", " + Utils.strLiteral(description) + ", " + Utils.strLiteral(content_type) + ")";

		_executeUpdate(sql);

		sql = "SELECT " + QUERY_ID_FLD + " FROM " + QUERY_TABLE + " WHERE " + QUERY_FILE_FLD + "=" + Utils.strLiteral(queryFileName);

		String[][] r = _executeStringQuery(sql);

		if (r.length == 0) throw new SQLException("Error when returning id  for " + queryFileName + " ");

		return r[0][0];
	}
*/
	public String addQuery(String xmlSchemaID, String shortName, String queryFileName, String description, String content_type) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String result = null;

		description = (description == null ? "" : description);

		if (isDebugMode){ logger.debug("Query is " + qInsertQuery);}

		try{
			conn = getConnection();
			pstmt = conn.prepareStatement(qInsertQuery);
			pstmt.setInt(1, Integer.parseInt(xmlSchemaID));
			pstmt.setString(2, shortName);
			pstmt.setString(3, queryFileName);
			pstmt.setString(4, description);
			pstmt.setString(5, content_type);
			pstmt.executeUpdate();

			if(pstmt != null) pstmt.close();

			pstmt = conn.prepareStatement(qQueryByFileName);
			pstmt.setString(1,queryFileName);
			rs = pstmt.executeQuery();
			String[][] r = getResults(rs);
			if (r.length == 0) throw new SQLException("Error when returning id  for " + queryFileName + " ");
			result = r[0][0];
		}finally{
			closeAllResources(rs,pstmt,conn);
		}

		return result;
	}


/*	public void updateQuery(String query_id, String schema_id, String short_name, String description, String fileName, String content_type) throws SQLException {

		short_name = (short_name == null ? "" : short_name);
		description = (description == null ? "" : description);

		String sql = "UPDATE  " + QUERY_TABLE + " SET " + QUERY_FILE_FLD + "=" + Utils.strLiteral(fileName) + ", " + SHORT_NAME_FLD + "=" + Utils.strLiteral(short_name) + ", " + DESCR_FLD + "=" + Utils.strLiteral(description) + ", " + XSL_SCHEMA_ID_FLD + "=" + schema_id + ", " + RESULT_TYPE_FLD + "=" + Utils.strLiteral(content_type) + " WHERE "
				+ QUERY_ID_FLD + "=" + query_id;

		_executeUpdate(sql);

	}
*/
	public void updateQuery(String query_id, String schema_id, String short_name, String description, String fileName, String content_type) throws SQLException{
		Connection conn = null;
		PreparedStatement pstmt = null;

		short_name = (short_name == null ? "" : short_name);
		description = (description == null ? "" : description);

		if (isDebugMode){ logger.debug("Query is " + qUpdateQuery);}

		try{
			conn = getConnection();
			pstmt = conn.prepareStatement(qUpdateQuery);
			pstmt.setString(1, fileName);
			pstmt.setString(2, short_name);
			pstmt.setString(3, description);
			pstmt.setString(4, schema_id);
			pstmt.setString(5, content_type);
			pstmt.setInt(6, Integer.parseInt(query_id));
			pstmt.executeUpdate();
		}finally{
			closeAllResources(null,pstmt,conn);
		}

	}



/*	public void removeQuery(String queryId) throws SQLException {
		String sql = "DELETE FROM " + QUERY_TABLE + " WHERE " + QUERY_ID_FLD + "=" + queryId;
		_executeUpdate(sql);
	}

*/
	public void removeQuery(String queryId) throws SQLException{
		Connection conn = null;
		PreparedStatement pstmt = null;

		if (isDebugMode){ logger.debug("Query is " + qRemoveQuery);}
		try{
			conn = getConnection();
			pstmt = conn.prepareStatement(qRemoveQuery);
			pstmt.setInt(1, Integer.parseInt(queryId));
			pstmt.executeUpdate();
		}finally{
			closeAllResources(null,pstmt,conn);
		}

	}








/*	public HashMap getQueryInfo(String queryId) throws SQLException {

		int id = 0;
		String queryName = null;
		try {
			id = Integer.parseInt(queryId);
		} catch (NumberFormatException n) {
			if (queryId.endsWith("xql"))
				queryName = queryId;
			else
				throw new SQLException("not numeric ID or xql file name: " + queryId);
		}

		String sql = "SELECT " + QUERY_TABLE + "." + XSL_SCHEMA_ID_FLD + "," + QUERY_FILE_FLD + ", " + QUERY_TABLE + "." + DESCR_FLD + "," + SHORT_NAME_FLD + ", " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + "," + QUERY_TABLE + "." + RESULT_TYPE_FLD + ", " + CONVTYPE_TABLE + "." + CONTENT_TYPE_FLD + " FROM " + QUERY_TABLE + " LEFT OUTER JOIN " + SCHEMA_TABLE
				+ " ON " + QUERY_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD + " LEFT OUTER JOIN " + CONVTYPE_TABLE + " ON " + QUERY_TABLE + "." + RESULT_TYPE_FLD + "=" + CONVTYPE_TABLE + "." + CONV_TYPE_FLD;
		if (queryName != null) {
			sql += " WHERE " + QUERY_FILE_FLD + "=" + Utils.strLiteral(queryName);

		} else {
			sql += " WHERE " + QUERY_ID_FLD + "=" + id;
		}

		String r[][] = _executeStringQuery(sql);

		HashMap h = null;

		if (r.length > 0) {
			h = new HashMap();
			h.put("query_id", queryId);
			h.put("schema_id", r[0][0]);
			h.put("query", r[0][1]);
			h.put("description", r[0][2]);
			h.put("short_name", r[0][3]);
			h.put("xml_schema", r[0][4]);
			h.put("content_type", r[0][5]);
			h.put("meta_type", r[0][6]);
		}

		return h;
	}
*/
	public HashMap getQueryInfo(String queryId) throws SQLException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		int id = 0;
		String queryName = null;
		HashMap h = null;
		try {
			id = Integer.parseInt(queryId);
		} catch (NumberFormatException n) {
			if (queryId.endsWith("xql"))
				queryName = queryId;
			else
				throw new SQLException("not numeric ID or xql file name: " + queryId);
		}

		try {
			conn = getConnection();
			if (queryName != null) {
				pstmt = conn.prepareStatement(qQueryInfoByFileName);
				pstmt.setString(1,queryName);
			} else {
				pstmt = conn.prepareStatement(qQueryInfoByID);
				pstmt.setInt(1,id);
			}
			if (isDebugMode){logger.debug("Query is " + ((queryName != null)?qQueryInfoByFileName:qQueryInfoByID));}

			rs = pstmt.executeQuery();
			String[][] r = getResults(rs);

			if (r.length > 0) {
				h = new HashMap();
				h.put("query_id", queryId);
				h.put("schema_id", r[0][0]);
				h.put("query", r[0][1]);
				h.put("description", r[0][2]);
				h.put("short_name", r[0][3]);
				h.put("xml_schema", r[0][4]);
				h.put("content_type", r[0][5]);
				h.put("meta_type", r[0][6]);
			}

		} finally{
			closeAllResources(rs,pstmt,conn);
		}

		return h;

	}









	/*public String getQueryText(String queryId) throws SQLException {
	int id = 0;
	String queryName = null;
	try {
		id = Integer.parseInt(queryId);
	} catch (NumberFormatException n) {
		if (queryId.endsWith("xql"))
			queryName = queryId;
		else
			throw new SQLException("not numeric ID or xql file name: " + queryId);
	}

	String sql = "SELECT " + QUERY_FILE_FLD + " FROM " + QUERY_TABLE;
	if (queryName != null) {
		sql += " WHERE " + QUERY_FILE_FLD + "=" + Utils.strLiteral(queryName);
	} else {
		sql += " WHERE " + QUERY_ID_FLD + "=" + id;
	}

	String r[][] = _executeStringQuery(sql);

	String qText = "";
	if (r.length > 0) {
		String queriesFolder = Properties.queriesFolder;
		if (!queriesFolder.endsWith(File.separator)) queriesFolder = queriesFolder + File.separator;
		try {
			qText = Utils.readStrFromFile(queriesFolder + r[0][0]);
		} catch (IOException e) {
			qText = "Unable to read file: " + queriesFolder + r[0][0] + "\n " + e.toString();
		}
	}

	return qText;
	}
	*/


	public String getQueryText(String queryId) throws SQLException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		int id = 0;
		String qText = "";
		String queryName = null;
		try {
			id = Integer.parseInt(queryId);
		} catch (NumberFormatException n) {
			if (queryId.endsWith("xql"))
				queryName = queryId;
			else
				throw new SQLException("not numeric ID or xql file name: " + queryId);
		}

		try {
			conn = getConnection();
			if (queryName != null) {
				pstmt = conn.prepareStatement(qQueryTextByFileName);
				pstmt.setString(1,queryName);
			} else {
				pstmt = conn.prepareStatement(qQueryTextByID);
				pstmt.setInt(1,id);
			}
			if (isDebugMode){logger.debug("Query is " + ((queryName != null)?qQueryTextByFileName:qQueryTextByID));}

			rs = pstmt.executeQuery();
			String[][] r = getResults(rs);

			if (r.length > 0) {
				String queriesFolder = Properties.queriesFolder;
				if (!queriesFolder.endsWith(File.separator)) queriesFolder = queriesFolder + File.separator;
				try {
					qText = Utils.readStrFromFile(queriesFolder + r[0][0]);
				} catch (IOException e) {
					qText = FILEREAD_EXCEPTION + queriesFolder + r[0][0] + "\n " + e.toString();
				}
			}

		} finally{
			closeAllResources(rs,pstmt,conn);
		}

		return qText;
	}




	/*public Vector listQueries(String xmlSchema) throws SQLException {

	String sql = "SELECT " + QUERY_TABLE + "." + QUERY_ID_FLD + ", " + SHORT_NAME_FLD + ", " + QUERY_FILE_FLD + ", " + QUERY_TABLE + "." + DESCR_FLD + "," + SCHEMA_TABLE + "." + SCHEMA_ID_FLD + "," + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + ", " + QUERY_TABLE + "." + RESULT_TYPE_FLD + " FROM " + QUERY_TABLE + " LEFT OUTER JOIN " + SCHEMA_TABLE + " ON "
			+ QUERY_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD;

	if (xmlSchema != null) sql += " WHERE " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + "=" + Utils.strLiteral(xmlSchema);

	String[][] r = _executeStringQuery(sql);

	Vector v = new Vector();

	for (int i = 0; i < r.length; i++) {
		Hashtable h = new Hashtable();
		h.put("query_id", r[i][0]);
		h.put("short_name", r[i][1]);
		h.put("query", r[i][2]);
		h.put("description", r[i][3]);
		h.put("schema_id", r[i][4]);
		h.put("xml_schema", r[i][5]);
		h.put("content_type_out", r[i][6]);
		v.add(h);
	}

	return v;

}
*/
	public Vector listQueries(String xmlSchema) throws SQLException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		boolean forSchema = xmlSchema != null ;
		Vector v = null;
		String query = (forSchema) ? qListQueriesBySchema : qListQueries;

		if (isDebugMode){
			logger.debug("XMLSchema is " + xmlSchema);
			logger.debug("Query is " + query);
		}
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(query);
			if(forSchema){pstmt.setString(1, xmlSchema);}
			rs = pstmt.executeQuery();
			String[][] r = getResults(rs);
			v = new Vector(r.length);
			for (int i = 0; i < r.length; i++) {
				Hashtable h = new Hashtable();
				h.put("query_id", r[i][0]);
				h.put("short_name", r[i][1]);
				h.put("query", r[i][2]);
				h.put("description", r[i][3]);
				h.put("schema_id", r[i][4]);
				h.put("xml_schema", r[i][5]);
				h.put("content_type_id", r[i][6]);
				h.put("content_type_out", r[i][7]);
				v.add(h);
			}
		}
		finally {
			closeAllResources(rs,pstmt,conn);
		}

		return v;
	}


//	public boolean checkQueryFile(String queryFileName) throws SQLException {
//
//		int id = 0;
//
//		String sql = "SELECT COUNT(*) FROM " + QUERY_TABLE + " WHERE " + QUERY_FILE_FLD + "=" + Utils.strLiteral(queryFileName);
//
//		String r[][] = _executeStringQuery(sql);
//
//		String count = r[0][0];
//		if (count.equals("0")) {
//			return false;
//		} else {
//			return true;
//		}
//
//	}

	public boolean checkQueryFile(String queryFileName) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		if (isDebugMode){ logger.debug("Query is " + qCheckQueryFileByName);}

		try{
			conn = getConnection();
			pstmt = conn.prepareStatement(qCheckQueryFileByName);
			pstmt.setString(1, queryFileName);
			rs = pstmt.executeQuery();
			String[][] r = getResults(rs);
			String count = r[0][0];
			if (count.equals("0")) {
				return false;
			} else {
				return true;
			}
		}finally{
			closeAllResources(rs,pstmt,conn);
		}

	}


//	public boolean checkQueryFile(String query_id, String queryFileName) throws SQLException {
//		int id = 0;
//
//		String sql = "SELECT COUNT(*) FROM " + QUERY_TABLE + " WHERE " + QUERY_FILE_FLD + "=" + Utils.strLiteral(queryFileName)
//		+ "and " +QUERY_ID_FLD+"="+query_id;
//
//		String r[][] = _executeStringQuery(sql);
//
//		String count = r[0][0];
//		if (count.equals("0")) {
//			return false;
//		} else {
//			return true;
//		}
//	}



	public boolean checkQueryFile(String query_id, String queryFileName) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		if (isDebugMode){ logger.debug("Query is " + qCheckQueryFileByIdAndName);}

		try{
			conn = getConnection();
			pstmt = conn.prepareStatement(qCheckQueryFileByIdAndName);
			pstmt.setString(1, queryFileName);
			pstmt.setString(2, query_id);
			rs = pstmt.executeQuery();
			String[][] r = getResults(rs);
			String count = r[0][0];
			if (count.equals("0")) {
				return false;
			} else {
				return true;
			}
		}finally{
			closeAllResources(rs,pstmt,conn);
		}

	}




}






