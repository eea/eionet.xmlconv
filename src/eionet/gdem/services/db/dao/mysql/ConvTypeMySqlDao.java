package eionet.gdem.services.db.dao.mysql;

import eionet.gdem.services.db.dao.IConvTypeDao;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;


public class ConvTypeMySqlDao extends MySqlBaseDao implements IConvTypeDao {
	
	
	private static final String qListAllConversions = "SELECT " + XSL_TABLE + "." + CNV_ID_FLD + "," + XSL_TABLE + "." + XSL_FILE_FLD + ", " + XSL_TABLE + "." + DESCR_FLD + "," +
													RESULT_TYPE_FLD + ", " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + ", " + 
													CONVTYPE_TABLE + "." + CONTENT_TYPE_FLD + 
													" FROM " + XSL_TABLE + " LEFT JOIN " + SCHEMA_TABLE + 
													" ON " + XSL_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD + 
													" LEFT JOIN " + CONVTYPE_TABLE + " ON " + XSL_TABLE + "." + RESULT_TYPE_FLD + "=" +  CONVTYPE_TABLE + "." + CONV_TYPE_FLD +
													" ORDER BY " + XML_SCHEMA_FLD + ", " + RESULT_TYPE_FLD;													
													

	private static final String qListConversionsForSchema = "SELECT " + XSL_TABLE + "." + CNV_ID_FLD + "," + XSL_TABLE + "." + XSL_FILE_FLD + ", " + XSL_TABLE + "." + DESCR_FLD + "," +
													RESULT_TYPE_FLD + ", " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + ", " + 
													CONVTYPE_TABLE + "." + CONTENT_TYPE_FLD + 
													" FROM " + XSL_TABLE + " LEFT JOIN " + SCHEMA_TABLE + 
													" ON " + XSL_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD + 
													" LEFT JOIN " + CONVTYPE_TABLE + " ON " + XSL_TABLE + "." + RESULT_TYPE_FLD + "=" +  CONVTYPE_TABLE + "." + CONV_TYPE_FLD +
													" WHERE " + XML_SCHEMA_FLD +  " =? " + 
													" ORDER BY " + XML_SCHEMA_FLD + ", " + RESULT_TYPE_FLD;													
	

	private static final String qConvTypes = "SELECT " + CONV_TYPE_FLD + ", " + CONTENT_TYPE_FLD + ", " + FILE_EXT_FLD + ", " + CONVTYPE_DESCRIPTION_FLD + " FROM " + CONVTYPE_TABLE + " ORDER BY " + CONV_TYPE_FLD;	
	
	private static final String qConvType = "SELECT " + CONV_TYPE_FLD + ", " + CONTENT_TYPE_FLD + ", " + FILE_EXT_FLD + ", " + CONVTYPE_DESCRIPTION_FLD + " FROM " + CONVTYPE_TABLE + " WHERE " + CONV_TYPE_FLD + "=?";

	
	public ConvTypeMySqlDao(){}

	
/*	public Vector listConversions(String xmlSchema) throws SQLException {

		String sql = "SELECT " + XSL_TABLE + "." + CNV_ID_FLD + "," + XSL_TABLE + "." + XSL_FILE_FLD + ", " + XSL_TABLE + "." + DESCR_FLD + "," + 
		RESULT_TYPE_FLD + ", " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + ", " + 
		CONVTYPE_TABLE + "." + CONTENT_TYPE_FLD + 
		" FROM " + XSL_TABLE + " LEFT JOIN " + SCHEMA_TABLE + 
		" ON " + XSL_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD + 
		" LEFT JOIN " + CONVTYPE_TABLE + " ON " + XSL_TABLE + "." + RESULT_TYPE_FLD + "=" + 
		CONVTYPE_TABLE + "." + CONV_TYPE_FLD;
		
		if (xmlSchema != null) sql +=  " WHERE " + XML_SCHEMA_FLD +  "=" + Utils.strLiteral(xmlSchema);

		sql += " ORDER BY " + XML_SCHEMA_FLD + ", " + RESULT_TYPE_FLD;
		 
		// System.out.println(sql);
		String[][] r = _executeStringQuery(sql);

		Vector v = new Vector();

		for (int i = 0; i < r.length; i++) {
			Hashtable h = new Hashtable();
			h.put("convert_id", r[i][0]);
			h.put("xsl", r[i][1]);
			h.put("description", r[i][2]);
			h.put("content_type_out", r[i][5]);
			h.put("xml_schema", r[i][4]);
			h.put("result_type", r[i][3]);
			v.add(h);
		}

		return v;
		
	}	
*/	public Vector listConversions(String xmlSchema) throws SQLException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		boolean forSchema = xmlSchema != null ;
		Vector v = null;
		String query = (forSchema) ? qListConversionsForSchema : qListAllConversions;
		
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
				h.put("convert_id", r[i][0]==null? "": r[i][0]);
				h.put("xsl", r[i][1]==null? "": r[i][1]);
				h.put("description", r[i][2]==null? "": r[i][2]);
				h.put("content_type_out", r[i][5]==null? "": r[i][5]);
				h.put("xml_schema", r[i][4]==null? "": r[i][4]);				
				h.put("result_type", r[i][3]==null? "": r[i][3]);
				v.add(h);
			}			
		} 
		finally {
			closeAllResources(rs,pstmt,conn);			
		}
		
		return v;	
	}


/*public Vector getConvTypes() throws SQLException {
	String sql = "SELECT " + CONV_TYPE_FLD + ", " + CONTENT_TYPE_FLD + ", " + FILE_EXT_FLD + ", " + CONVTYPE_DESCRIPTION_FLD + " FROM " + CONVTYPE_TABLE + " ORDER BY " + CONV_TYPE_FLD;

	String r[][] = _executeStringQuery(sql);

	Vector v = new Vector();

	for (int i = 0; i < r.length; i++) {
		Hashtable h = new Hashtable();
		h.put("conv_type", r[i][0]);
		h.put("content_type", r[i][1]);
		h.put("file_ext", r[i][2]);
		h.put("description", r[i][3]);
		v.add(h);
	}
	return v;
}

*/
	public Vector getConvTypes() throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		Vector v = null;
 		
		if (isDebugMode){ logger.debug("Query is " + qConvTypes);}
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(qConvTypes);
			rs = pstmt.executeQuery();			
			String[][] r = getResults(rs);
			v = new Vector(r.length);
			for (int i = 0; i < r.length; i++) {
				Hashtable h = new Hashtable();
				h.put("conv_type", r[i][0]);
				h.put("content_type", r[i][1]);
				h.put("file_ext", r[i][2]);
				h.put("description", r[i][3]);
				v.add(h);
			}			
		} 
		finally {
			closeAllResources(rs,pstmt,conn);
		}
		
		return v;	
	}
	

	
/*	public Hashtable getConvType(String conv_type) throws SQLException {

		String sql = "SELECT " + CONV_TYPE_FLD + ", " + CONTENT_TYPE_FLD + ", " + FILE_EXT_FLD + ", " + CONVTYPE_DESCRIPTION_FLD + " FROM " + CONVTYPE_TABLE + " WHERE " + CONV_TYPE_FLD + "=" + Utils.strLiteral(conv_type);

		String r[][] = _executeStringQuery(sql);

		if (r.length == 0) return null;

		Hashtable h = new Hashtable();
		h.put("conv_type", r[0][0]);
		h.put("content_type", r[0][1]);
		h.put("file_ext", r[0][2]);
		h.put("description", r[0][3]);

		return h;
	}
*/	
	  public Hashtable getConvType(String conv_type) throws SQLException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		Hashtable h = null;
		
		if (isDebugMode){
			logger.debug("Conv type is " + conv_type);
			logger.debug("Query is " + qConvType);
		}
			
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(qConvType);
			pstmt.setString(1, conv_type);
			rs = pstmt.executeQuery();			
			String[][] r = getResults(rs);

			if (r.length == 0) return null;
			h = new Hashtable();
			h.put("conv_type", r[0][0]);
			h.put("content_type", r[0][1]);
			h.put("file_ext", r[0][2]);
			h.put("description", r[0][3]);			
		} 
		finally {
			closeAllResources(rs,pstmt,conn);			
		}		
		return h;	
	}

	  
}
