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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

public interface DbModuleIF {


  /**
  * Table for XQuery Workqueue
  */
  public static final String WQ_TABLE="T_XQJOBS";

  /**
  * Table for stylesheets in the DB
  */
  public static final String XSL_TABLE="T_STYLESHEET";
  /**
  * Table for xml schemas in the DB
  */
  public static final String SCHEMA_TABLE="T_SCHEMA";

  /**
  * Field names in XSL table
  */
  public static final String CNV_ID_FLD="CONVERT_ID";
  public static final String XSL_SCHEMA_ID_FLD="SCHEMA_ID";
  public static final String DESCR_FLD="DESCRIPTION";
  public static final String RESULT_TYPE_FLD="RESULT_TYPE";
  public static final String XSL_FILE_FLD="XSL_FILENAME";  

  /**
  * Field names in SCHEMA table
  */
  public static final String SCHEMA_ID_FLD="SCHEMA_ID";
  public static final String XML_SCHEMA_FLD="XML_SCHEMA";
  public static final String SCHEMA_DESCR_FLD="DESCRIPTION";

  /*
  * T_XQJOBS
  */
  public static final String JOB_ID_FLD="JOB_ID";
  public static final String URL_FLD="URL";
  public static final String RESULT_FILE_FLD="RESULT_FILE";
  public static final String XQ_FILE_FLD="XQ_FILE";
  public static final String STATUS_FLD="STATUS";
  public static final String TIMESTAMP_FLD="TIME_STAMP";
  
  /**
  * Adds a new Stylesheet to the database
  * @param xmlSchemaID - xml schema ID
  * @param resultType - conversion type out: EXCEL, HTML, PDF, XML
  * @param xslFileName - xslFileName in the folder
  * @param xslDescription - text describing the stylesheet
  * @return The ID of the added stylesheet
  */
  public String addStylesheet(String xmlSchemaID, String resultType, String xslFileName, String description) throws SQLException;

  /**
  * Adds a new Schema to the database
  * @param xmlSchema - xml schema  (http://eionet.eea.eu.int/RASchema"
  * @param xsdDescription - text describing the schema
  * @return The ID of the added schema
  */
  public String addSchema(String xmlSchema,  String description) throws SQLException;

  /**
  * returns all records from t_STYLESHEET WHERE XML_SCHEMA=xmlSchema
  */
  public Vector listConversions(String xmlSchema) throws SQLException;

  /**
  * Removes the stylesheet from the stylesheets table
  * @param - convert ID
  */
  public void removeStylesheet(String convertId) throws SQLException;

  /**
  * Removes the schema and all it's stylesheets
  * @param - schema ID
  */
  public void removeSchema(String schemaId) throws SQLException;

  /**
  * Gets the data of the stylesheet from the repository
  */
  public HashMap getStylesheetInfo(String convertId) throws SQLException;
  
  /**
  * Gets the data of one or several schemas from the repository
  * Vector contains HashMaps with schema and it's stylesheets information
  */
  public Vector getSchemas(String schemaId) throws SQLException;

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
  
  /**
  * Gets information about the received job in Workqueue
  * @param String jobId
  * @ return String[]
  */
  public String[] getXQJobData(String jobId) throws SQLException;

  /**
  * Creates a new job in the queue
  * XQ Script is saved earlier in the 
  * @param String url, String xqFile, String resultFile
  */
  public String startXQJob(String url, String xqFile, String resultFile) throws SQLException;

  /**
  * Changes the status of the job in the table
  * also changes the time_stamp showing when the new task was started
  */
  public void changeJobStatus(String jobId, int status) throws SQLException;

  /**
  * Returns jobs in the Workqueue with the given status
  */
  public String[] getJobs(int status) throws SQLException;

  /**
  * Removes the XQJob when done or "dead"
  */
  public void endXQJob(String jobId) throws SQLException;
}