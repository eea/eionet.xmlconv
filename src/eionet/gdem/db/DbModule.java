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
 * The Original Code is "EINRC-6 / AIT project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.gdem.db;

import eionet.gdem.Utils;

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

public class DbModule implements DbModuleIF {
  DBPool dbPool=null;
  //ResourceBundle props;
  String dbUrl, dbDriver, dbUser, dbPwd;
  
  DbModule() throws GDEMException {
    if (dbUrl==null) {
      dbUrl=Utils.dbUrl;
      dbDriver=Utils.dbDriver;
      dbUser=Utils.dbUser;
      dbPwd=Utils.dbPwd;
    }

    if (dbUrl==null || dbDriver==null || dbUser==null || dbPwd==null )
      throw new GDEMException("Database connection settings are not specified in gdem.properties.");
      
    dbPool = new DBPool( dbUrl, dbDriver, dbUser, dbPwd ) ;        

  }

  public HashMap getStylesheetInfo(String convertId) throws SQLException {

    int id = 0;

    try { 
      id=Integer.parseInt(convertId);
    } catch(NumberFormatException n) {
      throw new SQLException("not numeric ID " + convertId);
    }
    
    String sql="SELECT " + XSL_TABLE + "." + XSL_SCHEMA_ID_FLD + "," + XSL_FILE_FLD + ", " + XSL_TABLE + "." + DESCR_FLD + "," +
      RESULT_TYPE_FLD + ", " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD + " FROM " + XSL_TABLE + " LEFT OUTER JOIN " + SCHEMA_TABLE +
          " ON " + XSL_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD +
          " WHERE " + CNV_ID_FLD + "=" + id;
        

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

  public Vector listConversions(String xmlSchema) throws SQLException {

    String sql="SELECT " + XSL_TABLE + "." + CNV_ID_FLD + "," + XSL_TABLE + "." + XSL_FILE_FLD + ", " + XSL_TABLE + "." + DESCR_FLD + "," +
      RESULT_TYPE_FLD + ", " + SCHEMA_TABLE + "." + XML_SCHEMA_FLD +  " FROM " + XSL_TABLE + " LEFT JOIN " + SCHEMA_TABLE +
          " ON " + XSL_TABLE + "." + XSL_SCHEMA_ID_FLD + "=" + SCHEMA_TABLE + "." + SCHEMA_ID_FLD;

    if (xmlSchema != null)
      sql +=  " WHERE " + XML_SCHEMA_FLD +  "='" + xmlSchema + "'";

    sql += " ORDER BY " + XML_SCHEMA_FLD + ", " + RESULT_TYPE_FLD;

    String [][] r = _executeStringQuery(sql);

    Vector v = new Vector();

    for (int i =0; i< r.length; i++) {
      Hashtable h = new Hashtable();
      h.put("convert_id", r[i][0]);
      h.put("xsl", r[i][1]);
      h.put("description", r[i][2]);
      h.put("content_type_out", r[i][3]);      
      h.put("xml_schema", r[i][4]);
      v.add(h);      
    }

    return v;
    
  }

  public String addStylesheet(String xmlSchemaID, String resultType, String xslFileName, String description) throws SQLException {

    description = (description == null ? "" : description );
    
    String sql = "INSERT INTO " + XSL_TABLE + " ( " + XSL_SCHEMA_ID_FLD + ", " + RESULT_TYPE_FLD +
      ", " + XSL_FILE_FLD + ", " + DESCR_FLD + ") VALUES ('" + xmlSchemaID + "', '" +
      resultType + "', '" + xslFileName + "', '" + description + "')";

    _executeUpdate(sql);

    sql = "SELECT " + CNV_ID_FLD + " FROM " + XSL_TABLE + " WHERE " + XSL_FILE_FLD + "= '" +
      xslFileName + "'";

    String[][] r = _executeStringQuery(sql);

    if (r.length==0)
      throw new SQLException("Error when returning id  for " + xslFileName + " ");
      
    return r[0][0];
  }
  public String addSchema(String xmlSchema,  String description) throws SQLException{
    
    description = (description == null ? "" : description );
    
    String sql = "INSERT INTO " + SCHEMA_TABLE + " ( " + XML_SCHEMA_FLD + ", " + SCHEMA_DESCR_FLD + 
          ") VALUES ('" + xmlSchema + "', '" +  description + "')";

    _executeUpdate(sql);

    return getSchemaID(xmlSchema);
/*    sql = "SELECT " + SCHEMA_ID_FLD + " FROM " + SCHEMA_TABLE + " WHERE " + XML_SCHEMA_FLD + "= '" +
      xmlSchema + "'";

    String[][] r = _executeStringQuery(sql);

    if (r.length==0)
      throw new SQLException("Error when returning id  for " + xmlSchema + " ");
      
    return r[0][0];*/
  }

  public void removeStylesheet(String convertId) throws SQLException {

    String sql = "DELETE FROM " + XSL_TABLE + " WHERE " + CNV_ID_FLD + "=" + convertId;
    _executeUpdate(sql);    
    
  }
  public void removeSchema(String schemaId) throws SQLException {


    //delete all stylesheets at first
    String sql_xsl = "DELETE FROM " + XSL_TABLE + " WHERE " + XSL_SCHEMA_ID_FLD + "=" + schemaId;
    _executeUpdate(sql_xsl);

    String sql = "DELETE FROM " + SCHEMA_TABLE + " WHERE " + SCHEMA_ID_FLD + "=" + schemaId;
    _executeUpdate(sql);
    
  }
  
  private String[][] _executeStringQuery(String sql) throws SQLException {
      Vector rvec = new Vector(); // Return value as Vector
  		String rval[][] = {};       // Return value
      Connection con = null;
      Statement stmt = null;
      ResultSet rset = null;

//      _log(sql);
      
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

     _ll(sql);

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


  private static void _ll(String s ){
    System.out.println("==== " + s);
  }

 
  public Vector getSchemas(String schemaId) throws SQLException {

    int id = 0;

    if (schemaId!=null){
      try { 
        id=Integer.parseInt(schemaId);
      } catch(NumberFormatException n) {
        throw new SQLException("not numeric ID " + schemaId);
      }
    }  
    
    String sql="SELECT " + SCHEMA_ID_FLD + "," + XML_SCHEMA_FLD + ", " + SCHEMA_DESCR_FLD +
      " FROM " + SCHEMA_TABLE;
    if (schemaId!=null)
      sql +=  " WHERE " + SCHEMA_ID_FLD + "=" + id;
         
    sql +=  " ORDER BY " + XML_SCHEMA_FLD;

    String [][] r = _executeStringQuery(sql);

    Vector v = new Vector();

    for (int i =0; i< r.length; i++) {

      HashMap h = new HashMap();    
      h.put("schema_id", r[i][0]);
      h.put("xml_schema", r[i][1]);
      h.put("description", r[i][2]);

      Vector v_xls=getSchemaStylesheets(r[i][0]);
      h.put("stylesheets", v_xls);
      v.add(h);
    }

    return v;
  }
  public String getSchemaID(String schema) throws SQLException {

    
    String sql="SELECT " + SCHEMA_ID_FLD + " FROM " + SCHEMA_TABLE +
        " WHERE " + XML_SCHEMA_FLD + "='" + schema + "'";
         
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
  public static void main(String[] args ) throws Exception {
    //AccessController.addAcl("/datasets/7", "jaanus", "Lakes");
    //AccessController.addAcl("/xxx", "kaido", "Testme");
    //AccessController.removeAcl("/datasets/5");
   }

  public String[] getXQJobData(String jobId) throws SQLException {
    String sql = "SELECT " + URL_FLD + "," + XQ_FILE_FLD + "," + RESULT_FILE_FLD +
      ", " + STATUS_FLD +
      " FROM " +  WQ_TABLE + " WHERE " + JOB_ID_FLD + "=" + jobId;


    String[][] r = _executeStringQuery(sql);
    String s[];

    if (r.length==0)
      s=null;
    else
      s=r[0];

    return s;
  }

   public void startXQJob(String url, String xqFile, String resultFile) throws SQLException {
      String sql = "INSERT INTO " + WQ_TABLE + " (" + URL_FLD + "," + XQ_FILE_FLD +
        ", " + RESULT_FILE_FLD +
        "," + STATUS_FLD + "," + TIMESTAMP_FLD +
        ") VALUES ('" + url + "', '" + xqFile + "','" + resultFile + "', " +
        Utils.XQ_RECEIVED + ", NOW())";
        
      _executeUpdate(sql);
   }

   public void changeJobStatus(String jobId, int status) throws SQLException {
      String sql="UPDATE " + WQ_TABLE + " SET " + STATUS_FLD + "=" + status +
        ", " + TIMESTAMP_FLD + "= NOW()" +
        " WHERE " + JOB_ID_FLD + "=" + jobId;

      _executeUpdate(sql);
   }

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
}