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

package eionet.gdem.services;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

public interface DbModuleIF {


  /**
  * Table for storing file info in repository
  */
  public static final String FILE_TABLE="T_FILE";
  /**
  * Table for XForms browsers
  */
  public static final String XFBROWSER_TABLE="T_XFBROWSER";

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
  * Table for root element mappings for schemas in the DB
  */
  public static final String ROOTELEM_TABLE="T_ROOT_ELEM";

  /**
  * Table for hosts with usernames and passwords in the DB
  */
  public static final String HOST_TABLE="T_HOST";

  /**
  * Table for conversion types
  */
  public static final String CONVTYPE_TABLE="T_CONVTYPE";
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
  public static final String DTD_PUBLIC_ID_FLD="DTD_PUBLIC_ID";

  /**
  * Field names in ROOT ELEMENTS table
  */
  public static final String ROOTELEM_ID_FLD="ROOTELEM_ID";
  public static final String ELEM_SCHEMA_ID_FLD="SCHEMA_ID";
  public static final String NAMESPACE_FLD="NAMESPACE";
  public static final String ELEM_NAME_FLD="ELEM_NAME";

  /*
  * T_XQJOBS
  */
  public static final String JOB_ID_FLD="JOB_ID";
  public static final String URL_FLD="URL";
  public static final String RESULT_FILE_FLD="RESULT_FILE";
  public static final String XQ_FILE_FLD="XQ_FILE";
  public static final String STATUS_FLD="N_STATUS";
  public static final String TIMESTAMP_FLD="TIME_STAMP";
  
  /**
  * Field names in FILE table
  */
  public static final String FILE_ID_FLD="FILE_ID";
  public static final String FILE_NAME_FLD="FILE_NAME";
  public static final String FILE_TITLE_FLD="TITLE";
  public static final String FILE_TYPE_FLD="TYPE";
  public static final String FILE_PARENTTYPE_FLD="PARENT_TYPE";
  public static final String FILE_PARENTID_FLD="PARENT_ID";
  public static final String FILE_DESCRIPTION_FLD="DESCRIPTION";
  public static final String FILE_DEFAULT_FLD="F_DEFAULT";

/**
 * TYPE values in FILE table
 */
  public static final String XFORM_FILE_TYPE="xform";
  public static final String CSS_FILE_TYPE="css";
  public static final String IMAGE_FILE_TYPE="image";
/**
 * PARENT_TYPE values in FILE table
 */
  public static final String SCHEMA_FILE_PARENT="xml_schema";


  /**
  * Field names in XFBROWSER table
  */
  public static final String BROWSER_ID_FLD="BROWSER_ID";
  public static final String BROWSER_TYPE_FLD="BROWSER_TYPE";
  public static final String BROWSER_TITLE_FLD="BROWSER_TITLE";
  public static final String BROWSER_STYLESHEET_FLD="STYLESHEET";
  public static final String BROWSER_PRIORITY_FLD="PRIORITY";
  
  
  /**
  * Field names in HOSTS table
  */
  public static final String HOST_ID_FLD="HOST_ID";
  public static final String HOST_NAME_FLD="HOST_NAME";
  public static final String USER_FLD="USER";
  public static final String PWD_FLD="PWD";

  /**
  * Field names in CONVTYPE table
  */
  public static final String CONV_TYPE_FLD="CONV_TYPE";
  public static final String CONTENT_TYPE_FLD="CONTENT_TYPE";
  public static final String FILE_EXT_FLD="FILE_EXT";
  public static final String CONVTYPE_DESCRIPTION_FLD="DESCRIPTION";

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
  * Removes the xform from the xformss table
  * @param - xform ID
  */
  public void removeXForm(String xformId) throws SQLException;

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
  * Gets the data of one or several schemas from the repository
  * Vector contains HashMaps with schema and it's stylesheets information if needed
  */
  public Vector getSchemas(String schemaId, boolean stylesheets) throws SQLException;

  /**
  * Gets the data of one schema from the repository
  * HashMap contains only one row from schema table
  */
  public HashMap getSchema(String schema_id) throws SQLException;


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
  * Returns job IDs in the Workqueue with the given status
  * @return String[]
  */
  public String[] getJobs(int status) throws SQLException;

  /**
  * Removes the XQJob 
  * No checking performed by this method
  */
  public void endXQJob(String jobId) throws SQLException;

  /**
  * returns all root element mappings for schema ID
  * @param - schema ID
  * @return Vector containing HashMaps with root element info
  */

  public Vector getSchemaRootElems(String schemaId) throws SQLException;

  /**
  * find possible schema matching for given root element and namespace
  * @param - rootElem root element name
  * @param - namespace
  * @return Vector containing HashMaps with schema info (same as getSchemas)
  */
  public Vector getRootElemMatching(String rootElem, String namespace) throws SQLException;

  /**
  * Removes the root element mapping from the root element table
  * @param - root element ID
  */
  public void removeRootElem(String rootElemId) throws SQLException;

  /**
  * Adds a new root element mapping to the database
  * @param xmlSchemaID - xml schema ID
  * @param elemName - root element name
  * @param namespcae - namespace of the root element
  * @return The ID of the added rootElement
  */
  public String addRootElem(String xmlSchemaID, String elemName, String namespace) throws SQLException;

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
  * returns XForms capable browser types 
  * @return Vector contining all fields from BROWSER table
  */

  public Vector getBrowsers() throws SQLException;

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

  /**
  * Adds a new Host to the database
  * @param hostName - host name  (http://eionet.eea.eu.int"
  * @param userName - username
  * @param pwd - password
  * @return The ID of the added host
  */
  public String addHost(String hostName,  String userName, String pwd) throws SQLException;

  /**
  * Updates a Host properties in the database
  * @param host_id - id from database, used as a constraint 
  * @param hostName - host name  (http://eionet.eea.eu.int"
  * @param userName - username
  * @param pwd - password
  */
  public void updateHost(String hostId, String hostName,  String userName, String pwd) throws SQLException;

  /**
  * Deletes the Host from the database
  * @param host_id - id from database, used as a constraint 
  */
  public void removeHost(String hostId) throws SQLException;

/**
  * returns hosts from database
  * @param host - if empty, then all fields are return
  *             - numeric id from database 
  *             - host name as string - wildcard search is performed
  * @return Vector contining all fields from T_HOST table
  */

  public Vector getHosts(String host) throws SQLException;

/**
  * returns conversion types from database
  * @param host - if empty, then all fields will be returned
  *             - conv_ty as string - wildcard search is performed
  * @return Vector contining all fields as HashMaps from T_CONVTYPET table
  */

  public Vector getConvTypes() throws SQLException;

  /**
  * returns one row of conversion type from database
  * @param conv_type as string
  * @return HashMap containing all fields as HashMap from T_CONVTYPE table
  */

  public Hashtable getConvType(String conv_type) throws SQLException;
  /**
   * returns all the job data in the WQ table
   * @return String[][] containing all fields as HashMap from T_CONVTYPE table
   */

   public String[][] getJobData() throws SQLException;

}
