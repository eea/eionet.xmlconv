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
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 */

package eionet.gdem.conversion.ssr;

/**
* Constants, used by GDEM
*/
public interface Names {

//Request + session Attribute names
  public static final String SESS_ATT         = "GDEM_SESS";
  public static final String USER_ATT         = "GDEM_ACL_USR_ATT"; 
  public static final String ERROR_ATT        = "GDEM_ERROR_ATT";  
  public static final String STYLESHEETS_ATT  = "GDEM_ACL_SS_ATT"; 

  //JSP names
  public static final String INDEX_JSP = "index.jsp";
  public static final String ERROR_JSP = "error.jsp";  
  public static final String STYLESHEET_JSP = "stylesheet.jsp";
  public static final String STYLESHEETS_JSP = "stylesheets.jsp";
  public static final String ADD_XSL_JSP = "add_stylesheet.jsp";
  public static final String TEST_CONVERSION_JSP = "test_conversion.jsp";
  public static final String LIST_CONVERSION_JSP = "list_conversions.jsp";
  public static final String TEST_CONVERSION_SERVLET = "convert";
  public static final String LOGIN_JSP = "login.jsp";
  public static final String SCHEMA_JSP = "schema.jsp";
  public static final String HOSTS_JSP = "hosts.jsp";
  public static final String HOST_JSP = "host.jsp";
  public static final String LIST_WORKQUEUE_JSP = "workqueue.jsp";
  public static final String SANDBOX_JSP = "sandbox.jsp";
  public static final String QUERIESINDEX_JSP = "queriesindex.jsp";
  public static final String QUERIES_JSP = "queries.jsp";
  public static final String ADD_QUERY_JSP = "add_query.jsp";
  public static final String QUERY_JSP = "query.jsp";

  //actions
  public static final String XSD_UPD_ACTION = "B";
  public static final String XSDQ_DEL_ACTION = "QD";
  public static final String XSD_DEL_ACTION = "D";
  public static final String XSD_UPDVAL_ACTION = "QV";
  public static final String XSL_ADD_ACTION = "O";
  public static final String XSL_DEL_ACTION = "Q";
  public static final String XSL_UPD_ACTION = "XU";
  public static final String ELEM_DEL_ACTION = "P";
  public static final String ELEM_ADD_ACTION = "U";
  public static final String LOGIN_ACTION = "F";  
  public static final String LOGOUT_ACTION = "I";    
  public static final String SHOW_SCHEMAS_ACTION = "S";
  public static final String SHOW_STYLESHEETS_ACTION = "C";  
  public static final String SHOW_QUERIES_ACTION = "QC";  
  public static final String SHOW_ADDXSL_ACTION = "H";  
  public static final String SHOW_TESTCONVERSION_ACTION = "J";
  public static final String SHOW_LISTCONVERSION_ACTION = "L";
  public static final String SHOW_SCHEMA_ACTION = "T";
  public static final String EXECUTE_TESTCONVERSION_ACTION = "K";
  public static final String HOST_DEL_ACTION = "M";
  public static final String HOST_ADD_ACTION = "E";
  public static final String HOST_UPD_ACTION = "G";
  public static final String WQ_DEL_ACTION = "X";
  public static final String QUERY_ADD_ACTION = "QA";
  public static final String QUERY_DEL_ACTION = "QX";
  public static final String QUERY_UPD_ACTION = "QU";

  //public static final String ERROR_ACTION = "XXX";
  //Parameters
  public static final String XSL_FOLDER = "xsl/";  
  public static final String QUERY_FOLDER = "queries/";  
  public static final String SCHEMA_ID = "ID";  
  public static final String XSL_DEL_ID = "XSL_DEL_ID";  
  public static final String XSD_DEL_ID = "XSD_DEL_ID";  
  public static final String QUERY_DEL_ID = "QUERY_DEL_ID";  

  public static final String ACL_STYLESHEETS_PATH = "stylesheets";
  public static final String ACL_TESTCONVERSION_PATH = "testconversion";
  public static final String ACL_SCHEMA_PATH = "schema";
  public static final String ACL_HOST_PATH = "host";
  public static final String ACL_WQ_PATH = "workqueue";
  public static final String ACL_QUERIES_PATH = "queries";

  public static final String RPC_SERVICE_NAME="XService";
  public static final String EXCEL_CONVERSION_JSP = "excel2xml_conversion.jsp";
  public static final String EXCEL2XML_CONV_PARAM = "excel2dd_xml";  

}