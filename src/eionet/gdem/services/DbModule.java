/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine, Enriko Käsper (TietoEnator)
 */

package eionet.gdem.services;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.Hashtable;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


//import java.util.ResourceBundle;
//import java.security.acl.AclEntry;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import eionet.gdem.GDEMException;

public class DbModule implements DbModuleIF, Constants {

  private static LoggerIF _l;

  DBPool dbPool=null;
  //ResourceBundle props;
  String dbUrl, dbDriver, dbUser, dbPwd;
  
  DbModule() throws GDEMException  {
    _l=GDEMServices.getLogger();
    
    if (dbUrl==null) {
      dbUrl=Properties.dbUrl;
      dbDriver=Properties.dbDriver;
      dbUser=Properties.dbUser;
      dbPwd=Properties.dbPwd;
    }

    if (dbUrl==null || dbDriver==null || dbUser==null || dbPwd==null )
      throw new GDEMException( "Database connection settings are not specified in gdem.properties.");
      
    dbPool = new DBPool( dbUrl, dbDriver, dbUser, dbPwd ) ;        

  }

  public HashMap getStylesheetInfo(String convertId) throws SQLException {

    int id = 0;
    String xslName=null;
    try { 
      id=Integer.parseInt(convertId);
    } catch(NumberFormatException n) {
      if (convertId.endsWith("xsl"))
        xslName = convertId;
      else
        throw new SQLException("not numeric ID or xsl file name: " + convertId);
    }
    
    String sql="SELECT " + XSL_TABLE + "." + XSL_SCHEMA_ID_FLD + "," + XSL_FILE_FLD + ", " + XSL_TABLE + "." + DESCR_FLD + "," +
      RESULT_TYPE_FLD + ", " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + " FROM " + XSL_TABLE + " LEFT OUTER JOIN " + SCHEMA_TABLE +
          " ON " + XSL_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD;
      if (xslName!=null){
        sql += " WHERE " + XSL_FILE_FLD + "=" + Utils.strLiteral(xslName);

      }
      else{
          sql += " WHERE " + CNV_ID_FLD + "=" + id;
      }
        

    String r[][] = _executeStringQuery(sql);

    HashMap h = null;

    if (r.length>0){
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

  public String getQueryText(String queryId) throws SQLException {
    int id = 0;
    String queryName=null;
    try { 
      id=Integer.parseInt(queryId);
    } catch(NumberFormatException n) {
      if (queryId.endsWith("xql"))
        queryName = queryId;
      else
        throw new SQLException("not numeric ID or xql file name: " + queryId);
    }
    
    String sql="SELECT " + QUERY_FILE_FLD + " FROM " + QUERY_TABLE;
    if (queryName!=null){
      sql += " WHERE " + QUERY_FILE_FLD + "=" + Utils.strLiteral(queryName);
    }
    else {
      sql += " WHERE " + QUERY_ID_FLD + "=" + id;
    }
        
    String r[][] = _executeStringQuery(sql);
    
    String qText = "";
    if(r.length > 0) {
       String queriesFolder=Properties.queriesFolder;
       if(!queriesFolder.endsWith(File.separator))
         queriesFolder = queriesFolder + File.separator;
       try {
          qText = Utils.readStrFromFile(queriesFolder + r[0][0]);
       }
       catch (IOException e) {
          qText = "Unable to read file: " + queriesFolder + r[0][0] + "\n " + e.toString();
       }
    }
    
    return qText;
  }

  public HashMap getQueryInfo(String queryId) throws SQLException {

    int id = 0;
    String queryName=null;
    try { 
      id=Integer.parseInt(queryId);
    } catch(NumberFormatException n) {
      if (queryId.endsWith("xql"))
        queryName = queryId;
      else
        throw new SQLException("not numeric ID or xql file name: " + queryId);
    }
    
    String sql="SELECT " + QUERY_TABLE + "." + XSL_SCHEMA_ID_FLD + "," + QUERY_FILE_FLD + ", " + QUERY_TABLE + "." + DESCR_FLD + "," +
      SHORT_NAME_FLD + ", " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + "," + QUERY_TABLE + "." + RESULT_TYPE_FLD +
          ", " + CONVTYPE_TABLE + "." + CONTENT_TYPE_FLD + 
          " FROM " + QUERY_TABLE + " LEFT OUTER JOIN " + SCHEMA_TABLE +
          " ON " + QUERY_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD +
          " LEFT OUTER JOIN " + CONVTYPE_TABLE + " ON " + QUERY_TABLE + "." + RESULT_TYPE_FLD + "="+
          CONVTYPE_TABLE + "." + CONV_TYPE_FLD;
      if (queryName!=null){
        sql += " WHERE " + QUERY_FILE_FLD + "=" + Utils.strLiteral(queryName);

      }
      else{
          sql += " WHERE " + QUERY_ID_FLD + "=" + id;
      }
        

    String r[][] = _executeStringQuery(sql);

    HashMap h = null;

    if (r.length>0){
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

  public Vector listQueries(String xmlSchema) throws SQLException {

    String sql="SELECT " + QUERY_TABLE + "." + QUERY_ID_FLD + ", " + SHORT_NAME_FLD +  ", " +
      QUERY_FILE_FLD + ", " + QUERY_TABLE + "." + DESCR_FLD + "," + SCHEMA_TABLE + "." + SCHEMA_ID_FLD +
      "," + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + ", " + QUERY_TABLE + "." + RESULT_TYPE_FLD + 
        " FROM " + QUERY_TABLE + " LEFT OUTER JOIN " + SCHEMA_TABLE +
        " ON " + QUERY_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD;

    if (xmlSchema != null)
      sql +=  " WHERE " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD +  "=" + Utils.strLiteral(xmlSchema);


    String [][] r = _executeStringQuery(sql);

    Vector v = new Vector();

    for (int i =0; i< r.length; i++) {
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

  public Vector listConversions(String xmlSchema) throws SQLException {


	  	String sql="SELECT " + XSL_TABLE + "." + CNV_ID_FLD + "," + XSL_TABLE + "." + XSL_FILE_FLD + ", " + XSL_TABLE + "." + DESCR_FLD + "," +
	      RESULT_TYPE_FLD + ", " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD +  ", " +
	      CONVTYPE_TABLE + "." + CONTENT_TYPE_FLD +
				" FROM " + XSL_TABLE + " LEFT JOIN " + SCHEMA_TABLE +
	      " ON " + XSL_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD +
	      " LEFT JOIN " + CONVTYPE_TABLE + " ON " + XSL_TABLE + "." + RESULT_TYPE_FLD + "="+
		    CONVTYPE_TABLE + "." + CONV_TYPE_FLD;

	    
	    if (xmlSchema != null)
	      sql +=  " WHERE " + XML_SCHEMA_FLD +  "=" + Utils.strLiteral(xmlSchema);

	    sql += " ORDER BY " + XML_SCHEMA_FLD + ", " + RESULT_TYPE_FLD;

	    //System.out.println(sql);
	    String [][] r = _executeStringQuery(sql);

	    Vector v = new Vector();

	    for (int i =0; i< r.length; i++) {
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
  
  public String addStylesheet(String xmlSchemaID, String resultType, String xslFileName, String description) throws SQLException {

    description = (description == null ? "" : description );
    
    String sql = "INSERT INTO " + XSL_TABLE + " ( " + XSL_SCHEMA_ID_FLD + ", " + RESULT_TYPE_FLD +
      ", " + XSL_FILE_FLD + ", " + DESCR_FLD + ") VALUES ('" + xmlSchemaID + "', '" +
      resultType + "', " + Utils.strLiteral(xslFileName) + ", " + Utils.strLiteral(description) + ")";

    _executeUpdate(sql);

    sql = "SELECT " + CNV_ID_FLD + " FROM " + XSL_TABLE + " WHERE " + XSL_FILE_FLD + "=" +
      Utils.strLiteral(xslFileName);

    String[][] r = _executeStringQuery(sql);

    if (r.length==0)
      throw new SQLException("Error when returning id  for " + xslFileName + " ");
      
    return r[0][0];
  }

  public String addQuery(String xmlSchemaID, String shortName, String queryFileName, String description, String content_type) throws SQLException {

    description = (description == null ? "" : description );
    
    String sql = "INSERT INTO " + QUERY_TABLE + " ( " + XSL_SCHEMA_ID_FLD + ", " + SHORT_NAME_FLD +
      ", " + QUERY_FILE_FLD + ", " + DESCR_FLD + ", " + RESULT_TYPE_FLD + ") VALUES ('" + xmlSchemaID + "', '" +
      shortName + "', " + Utils.strLiteral(queryFileName) + ", " + Utils.strLiteral(description) + ", " + 
      Utils.strLiteral(content_type) + ")";

    _executeUpdate(sql);

    sql = "SELECT " + QUERY_ID_FLD + " FROM " + QUERY_TABLE + " WHERE " + QUERY_FILE_FLD + "=" +
      Utils.strLiteral(queryFileName);

    String[][] r = _executeStringQuery(sql);

    if (r.length==0)
      throw new SQLException("Error when returning id  for " + queryFileName + " ");
      
    return r[0][0];
  }

  public String addSchema(String xmlSchema,  String description) throws SQLException{
      return addSchema(xmlSchema, description, null);
  }
  public String addSchema(String xmlSchema,  String description, String public_id) throws SQLException{
    
    description = (description == null ? "" : description );
    
    String sql = "INSERT INTO " + SCHEMA_TABLE + " ( " + XML_SCHEMA_FLD + ", " + SCHEMA_DESCR_FLD + ", " + DTD_PUBLIC_ID_FLD +
          ") VALUES (" + Utils.strLiteral(xmlSchema) + ", " +  Utils.strLiteral(description) + ", " + Utils.strLiteral(public_id) + ")";

    _executeUpdate(sql);

    return getSchemaID(xmlSchema);
/*    sql = "SELECT " + SCHEMA_ID_FLD + " FROM " + SCHEMA_TABLE + " WHERE " + XML_SCHEMA_FLD + "= '" +
      xmlSchema + "'";

    String[][] r = _executeStringQuery(sql);

    if (r.length==0)
      throw new SQLException("Error when returning id  for " + xmlSchema + " ");
      
    return r[0][0];*/
  }
  public void updateSchema(String schema_id, String xmlSchema,  String description, String public_id) throws SQLException{
    
    description = (description == null ? "" : description );
    public_id = (public_id == null ? "" : public_id );
    
    String sql = "UPDATE  " + SCHEMA_TABLE + " SET " + XML_SCHEMA_FLD + "=" + Utils.strLiteral(xmlSchema) + ", " +
          SCHEMA_DESCR_FLD + "=" + Utils.strLiteral(description) + ", " + DTD_PUBLIC_ID_FLD + "=" + Utils.strLiteral(public_id) + "" +
          " WHERE " + SCHEMA_ID_FLD + "=" + schema_id;

    _executeUpdate(sql);

  }
  public void updateSchemaValidate(String schema_id, String validate) throws SQLException{
    
    validate = (validate == null ? "0" : validate );
    if (!validate.equals("1")) validate="0";
    
    String sql = "UPDATE  " + SCHEMA_TABLE + " SET " + SCHEMA_VALIDATE_FLD + "=" + Utils.strLiteral(validate) + 
          " WHERE " + SCHEMA_ID_FLD + "=" + schema_id;

    _executeUpdate(sql);

  }  public void updateXForm(String xform_id, String schema_id,  String title, String xform_name, String description) throws SQLException{
    
    
    updateFile(xform_id, xform_name, title, XFORM_FILE_TYPE, SCHEMA_FILE_PARENT, schema_id, description);

  }
  public void updateQuery(String query_id, String schema_id, String short_name, String description, String fileName, String content_type) throws SQLException{
    
    short_name = (short_name == null ? "" : short_name );
    description = (description == null ? "" : description );
    
    String sql = "UPDATE  " + QUERY_TABLE + " SET " + QUERY_FILE_FLD + "=" + Utils.strLiteral(fileName) + ", " +
          SHORT_NAME_FLD + "=" + Utils.strLiteral(short_name) + ", " + DESCR_FLD + "=" + Utils.strLiteral(description) + ", " +
          XSL_SCHEMA_ID_FLD+ "=" + schema_id + ", " + RESULT_TYPE_FLD + "=" + Utils.strLiteral(content_type) + 
          " WHERE " + QUERY_ID_FLD + "=" + query_id;

    _executeUpdate(sql);

  }
  public void updateStylesheet(String xsl_id, String schema_id, String description, String fileName, String content_type) throws SQLException{
    
    description = (description == null ? "" : description );
	String sql;
	if(fileName== null || fileName.equals("")){
		 sql = "UPDATE  " + XSL_TABLE + " SET " +
         DESCR_FLD + "=" + Utils.strLiteral(description) + ", " + XSL_SCHEMA_ID_FLD+ "=" + schema_id + ", " + 
         RESULT_TYPE_FLD + "=" + Utils.strLiteral(content_type) + 
         " WHERE " + CNV_ID_FLD + "=" + xsl_id;		
	}else{			
		 sql = "UPDATE  " + XSL_TABLE + " SET " + XSL_FILE_FLD + "=" + Utils.strLiteral(fileName) + ", " +
          DESCR_FLD + "=" + Utils.strLiteral(description) + ", " + XSL_SCHEMA_ID_FLD+ "=" + schema_id + ", " + 
          RESULT_TYPE_FLD + "=" + Utils.strLiteral(content_type) + 
          " WHERE " + CNV_ID_FLD + "=" + xsl_id;
		 
	}

    _executeUpdate(sql);

  }
  
  public String addRootElem(String xmlSchemaID, String elemName, String namespace) throws SQLException {

    namespace = (namespace == null ? "" : namespace );
    
    String sql = "INSERT INTO " + ROOTELEM_TABLE + " ( " + ELEM_SCHEMA_ID_FLD + ", " + ELEM_NAME_FLD +
      ", " + NAMESPACE_FLD + ") VALUES (" + Utils.strLiteral(xmlSchemaID) + ", " + Utils.strLiteral(elemName) + ", " + Utils.strLiteral(namespace) + ")";

    _executeUpdate(sql);

      
    return _getLastInsertID();
  }

  public void removeStylesheet(String convertId) throws SQLException {

    String sql = "DELETE FROM " + XSL_TABLE + " WHERE " + CNV_ID_FLD + "=" + convertId;
    _executeUpdate(sql);    
    
  }

  public void removeQuery(String queryId) throws SQLException {
    String sql = "DELETE FROM " + QUERY_TABLE + " WHERE " + QUERY_ID_FLD + "=" + queryId;
    _executeUpdate(sql);    
  }

  public void removeRootElem(String rootElemId) throws SQLException {

    String sql = "DELETE FROM " + ROOTELEM_TABLE + " WHERE " + ROOTELEM_ID_FLD + "=" + rootElemId;
    _executeUpdate(sql);    
    
  }
  public void removeSchema(String schemaId, boolean del_stylesheets, boolean del_queries, boolean del_self) throws SQLException {


    //delete all stylesheets at first
    if(del_stylesheets) {
       String sql_xsl = "DELETE FROM " + XSL_TABLE + " WHERE " + XSL_SCHEMA_ID_FLD + "=" + schemaId;
       _executeUpdate(sql_xsl);
    }

    if(del_queries) {
       String sql_xsl = "DELETE FROM " + QUERY_TABLE + " WHERE " + XSL_SCHEMA_ID_FLD + "=" + schemaId;
       _executeUpdate(sql_xsl);
    }

    if(del_self) {
       //delete all root element mappings at first
       String sql_elem = "DELETE FROM " + ROOTELEM_TABLE + " WHERE " + ELEM_SCHEMA_ID_FLD + "=" + schemaId;
       _executeUpdate(sql_elem);
   
       String sql = "DELETE FROM " + SCHEMA_TABLE + " WHERE " + SCHEMA_ID_FLD + "=" + schemaId;
       _executeUpdate(sql);
    }
    
  }
  public void removeXForm(String xformId) throws SQLException {

    removeFile(xformId);
    
  }
  
  private String[][] _executeStringQuery(String sql) throws SQLException {
    Vector rvec = new Vector(); // Return value as Vector
  	String rval[][] = {};       // Return value
    Connection con = null;
    Statement stmt = null;
    ResultSet rset = null;

    if (_l.enable(_l.DEBUG))
      _l.debug(sql);
      
      // Process the result set
    con = getConnection();
      
    try {
      stmt = con.createStatement();
      rset = stmt.executeQuery(sql);
      ResultSetMetaData md = rset.getMetaData();

        //number of columns in the result set
      int colCnt = md.getColumnCount();

      while (rset.next()) {
        String row[] = new String[colCnt]; // Row of the result set

      // Retrieve the columns of the result set
      for (int i = 0; i < colCnt; ++i)
         row[i] = rset.getString(i + 1);

        rvec.addElement(row); // Store the row into the vector
      }
    } catch (SQLException e) {
       //logger.error("Error occurred when processing result set: " + sql,e);
       throw new SQLException("Error occurred when processing result set: " + sql);
    } finally {
				 // Close connection

     _close(con, stmt, null);
    }

			// Build return value
			if (rvec.size() > 0) {
				 rval = new String[rvec.size()][];

				 for (int i = 0; i < rvec.size(); ++i)
						rval[i] = (String[])rvec.elementAt(i);
			}

			// Success
			return rval;
	 }


   private void _close(Connection con, Statement stmt, ResultSet rset) throws SQLException  {
      try {
         if (rset != null)
            rset.close();
         if (stmt != null) {
            stmt.close();
            if (!con.getAutoCommit())
               con.commit();
         }
      } catch (Exception e) {
         throw new SQLException("Error"  + e.getMessage()) ;
      } finally {  
         try { con.close(); } catch (SQLException e) {
	         throw new SQLException("Error"  + e.getMessage()) ;
         }
      }
   }


/**
 * Returns new database connection. 
 *
 * @throw ServiceException if no connections were available.
 */   
  public Connection getConnection() throws SQLException {
    Connection con = dbPool.getConnection( dbUser, dbPwd );
    if (con == null)
      throw new SQLException("Failed to get database connection");

    return con;
    }

   private int _executeUpdate(String sql) throws SQLException {

    if (_l.enable(_l.DEBUG))
      _l.debug(sql);


    Connection con = null; // Connection object
    Statement stmt = null; // Statement object
    int rval = 0;          // Return value
    // Get connection object
    con = getConnection();

    // Create statement object
    try {
       stmt = con.createStatement();
    } catch (SQLException e) {
       // Error handling
       //   logger.error( "UpdateStatement failed: " + e.toString());
       // Free resources
       try {
          _close(con, stmt, null);
       } catch (Throwable exc) {
          throw new SQLException("_close() failed: " + sql);
       }
      //logger.error("Connection.createStatement() failed: " + sql_stmt,e);
      throw new SQLException("Update failed: " + sql);        
    }


   // Execute update
   try {
      rval = stmt.executeUpdate(sql);
   } catch (Exception e) {
      // Error handling
      throw new SQLException( "Statement.executeUpdate(" + sql + ") failed" + e.getMessage() );
   } finally {
      // Free resources
      _close(con, stmt, null);
   }
    // Success
    return rval;
  }


/*  private static void _ll(String s ){
    System.out.println("==== " + s);
  } */
  public HashMap getSchema(String schema_id) throws SQLException {
    return getSchema(schema_id, false); 
  }
  public HashMap getSchema(String schema_id, boolean stylesheets) throws SQLException {

		Vector schemas = getSchemas(schema_id, stylesheets);

		if (schemas==null) return null;
		if (schemas.size()==0) return null;

		return (HashMap)schemas.get(0);
    
  }
  public Vector getSchemas(String schemaId) throws SQLException {
    return getSchemas(schemaId, true);
  }
  public Vector getSchemas(String schemaId, boolean stylesheets) throws SQLException {

    int id = 0;

    if (schemaId!=null){
      try { 
        id=Integer.parseInt(schemaId);
      } catch(NumberFormatException n) {
        throw new SQLException("not numeric ID " + schemaId);
      }
    }  
    
    String sql="SELECT " + SCHEMA_ID_FLD + "," + XML_SCHEMA_FLD + ", " + SCHEMA_DESCR_FLD + ", " + 
    DTD_PUBLIC_ID_FLD + ", " + SCHEMA_VALIDATE_FLD + " FROM " + SCHEMA_TABLE;
    if (schemaId!=null)
      sql +=  " WHERE " + SCHEMA_ID_FLD + "=" + id;
         
    sql +=  " ORDER BY " + XML_SCHEMA_FLD;

    String [][] r = _executeStringQuery(sql);

    Vector v = new Vector();

    for (int i =0; i<   r.length; i++) {

      HashMap h = new HashMap();    
      h.put("schema_id", r[i][0]);
      h.put("xml_schema", r[i][1]);
      h.put("description", r[i][2]);
      h.put("dtd_public_id", r[i][3]);
      h.put("validate", r[i][4]);

      if (stylesheets){
        Vector v_xls=getSchemaStylesheets(r[i][0]);
        h.put("stylesheets", v_xls);
        Vector v_queries=getSchemaQueries(r[i][0]);
        h.put("queries", v_queries);
      }
      v.add(h);
    }

    return v;
  }
  public String getSchemaID(String schema) throws SQLException {

    
    String sql="SELECT " + SCHEMA_ID_FLD + " FROM " + SCHEMA_TABLE +
        " WHERE " + XML_SCHEMA_FLD + "=" + Utils.strLiteral(schema);
         
    String [][] r = _executeStringQuery(sql);

    if (r.length==0)
      return null;

    return r[0][0];
  }
  
  public Vector getSchemaStylesheets(String schemaId) throws SQLException {

    int id = 0;

    if (schemaId==null)
        throw new SQLException("Schema ID not defined");
    try { 
       id=Integer.parseInt(schemaId);
     } catch(NumberFormatException n) {
       throw new SQLException("not numeric ID " + schemaId);
   }
    
     String sql="SELECT " + CNV_ID_FLD + ", " + XSL_FILE_FLD + ", " + DESCR_FLD + "," + RESULT_TYPE_FLD + 
          " FROM " + XSL_TABLE + " WHERE " + XSL_SCHEMA_ID_FLD + 
        "=" + id;
        
      sql += " ORDER BY " + RESULT_TYPE_FLD;

    String [][] r = _executeStringQuery(sql);

    Vector v = new Vector();

    for (int i =0; i< r.length; i++) {
      HashMap h = new HashMap();
      h.put("convert_id", r[i][0]);
      h.put("xsl", r[i][1]);
      h.put("description", r[i][2]);
      h.put("content_type_out", r[i][3]);      
      v.add(h);      
    }

    return v;
  }

  public Vector getSchemaQueries(String schemaId) throws SQLException {

    int id = 0;

    if (schemaId==null)
        throw new SQLException("Schema ID not defined");
    try { 
       id=Integer.parseInt(schemaId);
     } catch(NumberFormatException n) {
       throw new SQLException("not numeric ID " + schemaId);
   }
    
     String sql="SELECT " + QUERY_ID_FLD + ", " + QUERY_FILE_FLD + ", " + DESCR_FLD + "," + SHORT_NAME_FLD + 
          " FROM " + QUERY_TABLE + " WHERE " + XSL_SCHEMA_ID_FLD + 
        "=" + id;
        
      sql += " ORDER BY " + SHORT_NAME_FLD;

    String [][] r = _executeStringQuery(sql);

    Vector v = new Vector();

    for (int i =0; i< r.length; i++) {
      HashMap h = new HashMap();
      h.put("query_id", r[i][0]);
      h.put("query", r[i][1]);
      h.put("description", r[i][2]);
      h.put("short_name", r[i][3]);      
      v.add(h);      
    }

    return v;
  }

  public Vector getSchemaRootElems(String schemaId) throws SQLException {

    int id = 0;

    if (schemaId==null)
        throw new SQLException("Schema ID not defined");
    try { 
       id=Integer.parseInt(schemaId);
     } catch(NumberFormatException n) {
       throw new SQLException("not numeric ID " + schemaId);
   }
    
     String sql="SELECT " + ROOTELEM_ID_FLD + ", " + ELEM_NAME_FLD + ", " + NAMESPACE_FLD + "," + ELEM_SCHEMA_ID_FLD + 
          " FROM " + ROOTELEM_TABLE + " WHERE " + ELEM_SCHEMA_ID_FLD + 
        "=" + id;
        
      sql += " ORDER BY " + ELEM_NAME_FLD;

    String [][] r = _executeStringQuery(sql);

    Vector v = new Vector();

    for (int i =0; i< r.length; i++) {
      HashMap h = new HashMap();
      h.put("rootelem_id", r[i][0]);
      h.put("elem_name", r[i][1]);
      h.put("namespace", r[i][2]);
      h.put("schema_id", r[i][3]);      
      v.add(h);      
    }

    return v;
  }
  public Vector getRootElemMatching(String rootElem, String namespace) throws SQLException {

    StringBuffer sql= new StringBuffer("SELECT ");
    sql.append(ELEM_SCHEMA_ID_FLD);  
    sql.append(" FROM " + ROOTELEM_TABLE + " WHERE " + ELEM_NAME_FLD + "=" + Utils.strLiteral(rootElem));

    if (!Utils.isNullStr(namespace))    
        sql.append(" AND " + NAMESPACE_FLD + "=" + Utils.strLiteral(namespace));
//System.out.println(sql.toString());
    String [][] r = _executeStringQuery(sql.toString());

    Vector v = new Vector();
//System.out.println(r.length);

    for (int i =0; i< r.length; i++) {
      HashMap h = getSchema(r[i][0],true);
      v.add(h);      
    }

    return v;
 
  }
  
  public static void main(String[] args ) throws Exception {
    //AccessController.addAcl("/datasets/7", "jaanus", "Lakes");
    //AccessController.addAcl("/xxx", "kaido", "Testme");
    //AccessController.removeAcl("/datasets/5");
   }

  public String[] getXQJobData(String jobId) throws SQLException {
    String sql = "SELECT " + URL_FLD + "," + XQ_FILE_FLD + "," + RESULT_FILE_FLD +
      ", " + STATUS_FLD + ", " + SRC_FILE_FLD + ", " + XQ_ID_FLD +
      " FROM " +  WQ_TABLE + " WHERE " + JOB_ID_FLD + "=" + jobId;


    String[][] r = _executeStringQuery(sql);
    String s[];

    if (r.length==0)
      s=null;
    else
      s=r[0];

    return s;
  }

   public String startXQJob(String url, String xqFile, String resultFile) throws SQLException {
      return startXQJob(url, xqFile, resultFile, JOB_FROMSTRING);
   }
   public String startXQJob(String url, String xqFile, String resultFile, int xqID) throws SQLException {
    String sql = "INSERT INTO " + WQ_TABLE + " (" + URL_FLD + "," + XQ_FILE_FLD +
        ", " + RESULT_FILE_FLD +
        "," + STATUS_FLD + "," + TIMESTAMP_FLD + "," + XQ_ID_FLD +
        ") VALUES ('" + url + "', '" + xqFile + "','" + resultFile + "', " +
          XQ_RECEIVED + ", NOW()," + xqID + ")";
        
      _executeUpdate(sql);

			sql = "SELECT " + JOB_ID_FLD  + " FROM " + WQ_TABLE + " WHERE " + XQ_FILE_FLD + " = '" +
				xqFile + "' AND " + RESULT_FILE_FLD + " = '" + resultFile + "'";

			String r[][] = _executeStringQuery(sql);

			return r[0][0];
   }

   public void changeJobStatus(String jobId, int status) throws SQLException {
    String sql="UPDATE " + WQ_TABLE + " SET " + STATUS_FLD + "=" + status +
    //String sql="UPDATE " + WQ_TABLE + " SET STATUS=" + status +
        ", " + TIMESTAMP_FLD + "= NOW()" +
        " WHERE " + JOB_ID_FLD + "=" + jobId;
      _executeUpdate(sql);
   }

   public void changeFileJobsStatus(String url, String savedFile, int status) throws SQLException {
    String sql="UPDATE " + WQ_TABLE + " SET " + STATUS_FLD + "=" + status + ", " +
        SRC_FILE_FLD + "=" + Utils.strLiteral(savedFile) + 
    //String sql="UPDATE " + WQ_TABLE + " SET STATUS=" + status +
        ", " + TIMESTAMP_FLD + "= NOW()" +
        " WHERE " + URL_FLD + "=" + Utils.strLiteral(url) + 
        " AND " + STATUS_FLD + "<" + status;
      _executeUpdate(sql);
   }

  //jobs in the queue with the given status
  //jobs in the queue with the given status
  public String[] getJobs(int status) throws SQLException {
    String sql = "SELECT " + JOB_ID_FLD + " FROM " + WQ_TABLE + " WHERE " +
      STATUS_FLD + "=" + status;


    String [][] r = _executeStringQuery(sql);
    String[] s = null;
    
    if (r.length>0) {
      s = new String[r.length];

      for(int i=0; i<r.length; i++)
        s[i]=r[i][0];
    }  
    return s;
  }

  public void endXQJob(String jobId) throws SQLException {
    String sql = "DELETE FROM " + WQ_TABLE + " WHERE " + JOB_ID_FLD + "=" + jobId;

    _executeUpdate(sql);
  }
  private String _getLastInsertID() throws SQLException {
        
    Connection con = null;
    String lastInsertId=null;

    con = getConnection();

    String qry = "SELECT LAST_INSERT_ID()";
       
       
    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery(qry);        
    rs.clearWarnings();
    if (rs.next())
         lastInsertId = rs.getString(1);
    _close(con,stmt,rs);
    
    return lastInsertId;
  }
  public Hashtable getXForm(String XMLSchema) throws SQLException{
  
      String sql=null;
      
      if (Utils.isNum(XMLSchema)){
        sql = "SELECT " + FILE_ID_FLD  + ", " + XML_SCHEMA_FLD  + ", " + FILE_NAME_FLD  + ", " + FILE_TITLE_FLD  +  ", " + FILE_TABLE + "." + FILE_DESCRIPTION_FLD  + 
        " FROM " + FILE_TABLE  + " LEFT OUTER JOIN " + SCHEMA_TABLE +
          " ON " + FILE_TABLE + "." + FILE_PARENTID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD +
          " WHERE " + SCHEMA_TABLE + "." + SCHEMA_ID_FLD + " = " +	XMLSchema + 
          " AND " + FILE_PARENTTYPE_FLD + "='" + SCHEMA_FILE_PARENT + "'" +
          " AND " + FILE_TYPE_FLD + "='" + XFORM_FILE_TYPE + "'";
      }
      else{
        sql = "SELECT " + FILE_ID_FLD  + ", " + XML_SCHEMA_FLD  + ", " + FILE_NAME_FLD  + ", " + FILE_TITLE_FLD  +   ", " + FILE_TABLE + "." + FILE_DESCRIPTION_FLD  + 
        " FROM " + FILE_TABLE  + " LEFT OUTER JOIN " + SCHEMA_TABLE +
          " ON " + FILE_TABLE + "." + FILE_PARENTID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD +
          " WHERE " + XML_SCHEMA_FLD + " =" +	Utils.strLiteral(XMLSchema) + 
          " AND " + FILE_PARENTTYPE_FLD + "='" + SCHEMA_FILE_PARENT + "'" +
          " AND " + FILE_TYPE_FLD + "='" + XFORM_FILE_TYPE + "'";
      }

   
			String r[][] = _executeStringQuery(sql);
      
      if (r.length==0)return null;
      
      Hashtable h = new Hashtable();
      h.put("xform_id", r[0][0]);
      h.put("xml_schema", r[0][1]);
      h.put("xform_name", r[0][2]);
      h.put("xform_title", r[0][3]);
      h.put("xform_description", r[0][4]);

			return h;
  }
 public Hashtable getXForms(Vector XMLSchemas) throws SQLException{
      
      if (XMLSchemas==null) return null;

      Hashtable h = new Hashtable();
      
      for (int i=0;i<XMLSchemas.size();i++){
        String schema = (String)XMLSchemas.get(i);
        Hashtable xform = getXForm(schema);
        if (xform!=null)
          h.put(schema, xform);      
        
      }        

			return h;
  }
 public Hashtable getXFormNames(Vector XMLSchemas) throws SQLException{
      
      boolean all_schemas=false;
      if (Utils.isNullVector(XMLSchemas)){
        XMLSchemas = getSchemas(null,false);
        all_schemas=true;
      }

      Hashtable h = new Hashtable();
      
      for (int i=0;i<XMLSchemas.size();i++){
        String schema="";
        if (all_schemas){
          HashMap schema_table = (HashMap)XMLSchemas.get(i);
          schema = (String)schema_table.get("xml_schema");
        }
        else
          schema = (String)XMLSchemas.get(i);
        if (schema==null) continue;
        
        Hashtable h_xform = getXForm(schema);
        if (h_xform==null) continue;
        
        String xform = (String)h_xform.get("xform_name");
        
        if (xform!=null && !h.containsKey(schema))
          h.put(schema, xform);      
        
      }        

			return h;
  }
 public Hashtable getXForms() throws SQLException{
      
      Vector XMLSchemas = getSchemas(null,false);
      
      if (XMLSchemas==null) return null;

      Hashtable h = new Hashtable();
      
      for (int i=0;i<XMLSchemas.size();i++){
        HashMap schema_table = (HashMap)XMLSchemas.get(i);
        String schema_id = (String)schema_table.get("schema_id");
        String schema = (String)schema_table.get("xml_schema");
        if (schema==null) continue;
        Hashtable xform = getXForm(schema);
        
        if (xform!=null)
          h.put(schema, xform);      
        
      }        

			return h;
  }
  private String getXFormID(String XMLSchema) throws SQLException{
      
    int schema_id = 0;
    String str_schema_id="";
    
    try { 
      schema_id=Integer.parseInt(XMLSchema);
    } catch(NumberFormatException n) {
      schema_id=0;
    }
    str_schema_id = (schema_id==0) ? getSchemaID(XMLSchema) : String.valueOf(schema_id);
    
		String sql = "SELECT " + FILE_ID_FLD  + " FROM " + FILE_TABLE + " WHERE " + FILE_PARENTID_FLD + " = " +
			schema_id + " AND " + FILE_TYPE_FLD + "='" + XFORM_FILE_TYPE + 
      "' AND" + FILE_PARENTTYPE_FLD + "='" + SCHEMA_FILE_PARENT + "'";

		String r[][] = _executeStringQuery(sql);

		return r[0][0];
  
  }
  public String addXForm(String schema_id, String xform, String title, String description) throws SQLException {

    return addFile(xform, title, XFORM_FILE_TYPE, SCHEMA_FILE_PARENT, schema_id, description);

  }
  public Hashtable getXFormByID(String xform_id) throws SQLException{
  
      if (xform_id==null) return null;
      if (!Utils.isNum(xform_id))
          throw new SQLException("XForm id is not numeric");

      String sql = "SELECT " + FILE_ID_FLD  + ", " + XML_SCHEMA_FLD  + ", " + FILE_NAME_FLD  + ", " + FILE_TITLE_FLD  + ", " + FILE_TABLE + "." + FILE_DESCRIPTION_FLD  + 
        " FROM " + FILE_TABLE  + " LEFT OUTER JOIN " + SCHEMA_TABLE +
          " ON " + FILE_TABLE + "." + FILE_PARENTID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD +
          " WHERE " + FILE_ID_FLD + " = " +	xform_id + " AND " + FILE_PARENTTYPE_FLD + "='" + SCHEMA_FILE_PARENT + "'";
   
			String r[][] = _executeStringQuery(sql);
      
      if (r.length==0)return null;
      
      Hashtable h = new Hashtable();
      h.put("xform_id", r[0][0]);
      h.put("xml_schema", r[0][1]);
      h.put("xform_name", r[0][2]);
      h.put("xform_title", r[0][3]);
      h.put("xform_description", r[0][4]);

			return h;
  }  public Vector getBrowsers() throws SQLException{
     String sql="SELECT " + BROWSER_ID_FLD + "," + BROWSER_TYPE_FLD + ", " + BROWSER_TITLE_FLD + ", " + BROWSER_STYLESHEET_FLD +
      ", " + BROWSER_PRIORITY_FLD + " FROM " + XFBROWSER_TABLE + " ORDER BY " + BROWSER_PRIORITY_FLD;

    String [][] r = _executeStringQuery(sql);

    Vector v = new Vector();

    for (int i =0; i<   r.length; i++) {

      HashMap h = new HashMap();
      h.put("browser_id", r[i][0]);
      h.put("browser_type", r[i][1]);
      h.put("browser_title", r[i][2]);
      h.put("stylesheet", r[i][3]);
      h.put("priority", r[i][4]);

      v.add(h);
    }
    return v; 
  }
  private String addFile(String fileName, String title, String type, String parent_type, String parent_id, String description) throws SQLException {

    title = (title == null ? "" : title );
    description = (description == null ? "" : description );
    
    String sql = "INSERT INTO " + FILE_TABLE + " ( " + FILE_NAME_FLD + ", " + FILE_TITLE_FLD +
      ", " + FILE_TYPE_FLD + ", " + FILE_PARENTTYPE_FLD + ", " + FILE_PARENTID_FLD + ", " + FILE_DESCRIPTION_FLD + ", " + FILE_DEFAULT_FLD + 
      ") VALUES (" + Utils.strLiteral(fileName) + ", " +  Utils.strLiteral(title) + ", '" + type +  "', '" + parent_type +  "', " + parent_id +  ", " + Utils.strLiteral(description) + ", 'Y')";

    _executeUpdate(sql);

    sql = "SELECT " + FILE_ID_FLD + " FROM " + FILE_TABLE + " WHERE " + FILE_NAME_FLD + "= '" +  fileName + "' AND " +
          FILE_TYPE_FLD + "='" + type + "'";

    String[][] r = _executeStringQuery(sql);

    if (r.length==0)
      throw new SQLException("Error when returning id  for " + fileName + " ");
      
    return r[0][0];
  }
  public void updateFile(String file_id, String fileName, String title, String type, String parent_type, String parent_id, String description) throws SQLException{
    
    title = (title == null ? "" : title );
    
    String sql = "UPDATE  " + FILE_TABLE + " SET " + FILE_NAME_FLD + "=" + Utils.strLiteral(fileName) + ", " +
          FILE_TITLE_FLD + "=" + Utils.strLiteral(title) + ", " + FILE_PARENTTYPE_FLD + "='" + parent_type + "', " +
          FILE_PARENTID_FLD + "=" + parent_id + ", " + FILE_DESCRIPTION_FLD + "=" + Utils.strLiteral(description) + "" +
          " WHERE " + FILE_ID_FLD + "=" + file_id;

    _executeUpdate(sql);

  }
  public void removeFile(String file_id) throws SQLException {

    String sql = "DELETE FROM " + FILE_TABLE + " WHERE " + FILE_ID_FLD + "=" + file_id;
    _executeUpdate(sql);    
    
  }
  
  public String addHost(String hostName, String userName, String pwd) throws SQLException {

    hostName = (hostName == null ? "" : hostName );
    
    String sql = "INSERT INTO " + HOST_TABLE + " ( " + HOST_NAME_FLD + ", " + USER_FLD +
      ", " + PWD_FLD + ") VALUES (" + Utils.strLiteral(hostName) + ", " + Utils.strLiteral(userName) + ", " + Utils.strLiteral(pwd) + ")";

    _executeUpdate(sql);

      
    return _getLastInsertID();
  }
 public void removeHost(String hostId) throws SQLException {

    String sql = "DELETE FROM " + HOST_TABLE + " WHERE " + HOST_ID_FLD + "=" + hostId;
    _executeUpdate(sql);    
    
  }
  public void updateHost(String hostId, String hostName, String userName, String pwd) throws SQLException {

    hostName = (hostName == null ? "" : hostName );
    
    String sql = "UPDATE " + HOST_TABLE + " SET " + HOST_NAME_FLD + "=" + Utils.strLiteral(hostName) + ", " + USER_FLD +
      "=" + Utils.strLiteral(userName) + ", " + PWD_FLD + "=" + Utils.strLiteral(pwd) + 
      " WHERE " + HOST_ID_FLD + "=" + hostId;

    _executeUpdate(sql);

  }
  public Vector getHosts(String host) throws SQLException{
  
      
    StringBuffer sql_buf = new StringBuffer("SELECT " + HOST_ID_FLD  + ", " + HOST_NAME_FLD  + ", " + USER_FLD  + ", " + PWD_FLD  +  
      " FROM " + HOST_TABLE);
    if (!Utils.isNullStr(host)){
      if (Utils.isNum(host)){
        sql_buf.append(" WHERE " + HOST_ID_FLD + "=" + host);
      }
      else{
        sql_buf.append(" WHERE " + HOST_NAME_FLD + " like '%" + host + "%'");
      }
    }
    sql_buf.append(" ORDER BY " + HOST_NAME_FLD);
    
		String r[][] = _executeStringQuery(sql_buf.toString());
      
    Vector v = new Vector();

    for (int i =0; i< r.length; i++) {
      Hashtable h = new Hashtable();
      h.put("host_id", r[i][0]);
      h.put("host_name", r[i][1]);
      h.put("user_name", r[i][2]);
      h.put("pwd", r[i][3]);      
      v.add(h);      
    }

    return v;
  }
  public Vector getConvTypes() throws SQLException{
    String sql = "SELECT " + CONV_TYPE_FLD  + ", " + CONTENT_TYPE_FLD  + ", " + FILE_EXT_FLD  + ", " + CONVTYPE_DESCRIPTION_FLD  +  
      " FROM " + CONVTYPE_TABLE + " ORDER BY " + CONV_TYPE_FLD;
    
	String r[][] = _executeStringQuery(sql);
      
    Vector v = new Vector();

    for (int i =0; i< r.length; i++) {
      Hashtable h = new Hashtable();
      h.put("conv_type", r[i][0]);
      h.put("content_type", r[i][1]);
      h.put("file_ext", r[i][2]);
      h.put("description", r[i][3]);      
      v.add(h);      
    }
    return v;
  } 
  public Hashtable getConvType(String conv_type) throws SQLException{

    String sql = "SELECT " + CONV_TYPE_FLD  + ", " + CONTENT_TYPE_FLD  + ", " + FILE_EXT_FLD  + ", " + CONVTYPE_DESCRIPTION_FLD  +  
      " FROM " + CONVTYPE_TABLE + " WHERE " + CONV_TYPE_FLD + "=" + Utils.strLiteral(conv_type);
 			
      String r[][] = _executeStringQuery(sql);
      
      if (r.length==0)return null;
      
      Hashtable h = new Hashtable();
      h.put("conv_type", r[0][0]);
      h.put("content_type", r[0][1]);
      h.put("file_ext", r[0][2]);
      h.put("description", r[0][3]);      

			return h;
 } 
  
  public String[][] getJobData() throws SQLException{
  	String sql = "SELECT " + JOB_ID_FLD + ", " + URL_FLD + ","  
		+ XQ_FILE_FLD + 
  	  ", " + RESULT_FILE_FLD + ", " + STATUS_FLD + ", " + TIMESTAMP_FLD + ", " + XQ_ID_FLD + 
		 	" FROM " + WQ_TABLE + " ORDER BY " + JOB_ID_FLD;
  	
  	return _executeStringQuery(sql);
  
  }

  public Vector getUplSchema() throws SQLException {

	    
	String sql = "SELECT " + UPL_SCHEMA_ID_FLD + ", " + UPL_SCHEMA_FLD +  
		 	" FROM " + UPL_SCHEMA_TABLE + " ORDER BY " + UPL_SCHEMA_FLD;
  	
	String r[][] = _executeStringQuery(sql);
    
	Vector v = new Vector();

    for (int i =0; i< r.length; i++) {
      Hashtable h = new Hashtable();
      h.put("id", r[i][0]);
      h.put("schema", r[i][1]);
      v.add(h);      
    }
    return v;
  }  

  public String addUplSchema(String schema) throws SQLException {
	    
	    String sql = "INSERT INTO " + UPL_SCHEMA_TABLE + " ( " + UPL_SCHEMA_FLD + ") VALUES (" + Utils.strLiteral(schema) + ")";

		_executeUpdate(sql);
	    
		return _getLastInsertID();
	  }

  public void removeUplSchema(String uplSchemaId) throws SQLException {

	    String sql = "DELETE FROM " + UPL_SCHEMA_TABLE + " WHERE " + UPL_SCHEMA_ID_FLD + "=" + uplSchemaId;
	    _executeUpdate(sql);  
		//System.out.println(sql);
	    
	  }

  public String getUplSchema(String uplSchemaId) throws SQLException {

	    
	    String sql="SELECT " + UPL_SCHEMA_FLD + " FROM " + UPL_SCHEMA_TABLE +
	        " WHERE " + UPL_SCHEMA_ID_FLD + "=" + uplSchemaId;
	         
	    String [][] r = _executeStringQuery(sql);

	    if (r.length==0)
	      return null;

	    return r[0][0];
	  }
  public Vector getSchemasWithStl() throws SQLException {

	    
	    String sql= "SELECT DISTINCT S." + SCHEMA_ID_FLD + ", S." + XML_SCHEMA_FLD + ", S." + SCHEMA_DESCR_FLD  + " FROM " + SCHEMA_TABLE + " S"
		 + " JOIN " + XSL_TABLE + " ST ON S." + SCHEMA_ID_FLD + " = ST." + XSL_SCHEMA_ID_FLD + 	         
		  " ORDER BY S." + XML_SCHEMA_FLD;
			
	    String [][] r = _executeStringQuery(sql);

	    Vector v = new Vector();

	    for (int i =0; i<   r.length; i++) {

	      HashMap h = new HashMap();    
	      h.put("schema_id", r[i][0]);
	      h.put("xml_schema", r[i][1]);
	      h.put("description", r[i][2]);
	      v.add(h);
	    }

	    return v;
	  }

  
}