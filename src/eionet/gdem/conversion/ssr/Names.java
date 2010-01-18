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
* Constants, used by GDEM (deprecated, but some constants may be still in use)
*/
public interface Names {

//Request + session Attribute names
  public static final String SESS_ATT         = "GDEM_SESS";
  public static final String USER_ATT         = "GDEM_ACL_USR_ATT";
  public static final String ERROR_ATT        = "GDEM_ERROR_ATT";
  public static final String STYLESHEETS_ATT  = "GDEM_ACL_SS_ATT";
  public static final String TICKET_ATT  = "GDEM_TICKET";
  public static final String SUCCESS_ATT        = "GDEM_SUCCESS_ATT";


  //JSP names
  public static final String INDEX_JSP = "index.jsp";
  public static final String ERROR_JSP = "error.jsp";
  public static final String STYLESHEET_JSP = "stylesheetEditForm.do";
  public static final String STYLESHEETS_JSP = "schemaStylesheets.do";
  public static final String ADD_XSL_JSP = "addStylesheetForm.do";
  public static final String TEST_CONVERSION_JSP = "testConversionForm.do";
  public static final String LIST_CONVERSION_JSP = "listConvForm.do";
  public static final String TEST_CONVERSION_SERVLET = "convert";
  public static final String LOGIN_JSP = "start.do?login=true";
  public static final String SCHEMA_JSP = "schemaElemForm.do";
  public static final String LIST_WORKQUEUE_JSP = "workqueue.jsp";
  public static final String QUERIESINDEX_JSP = "qaScripts.do";

  //actions
  public static final String LOGIN_ACTION = "F";
  public static final String LOGOUT_ACTION = "I";
  public static final String SHOW_TESTCONVERSION_ACTION = "J";
  public static final String SHOW_LISTCONVERSION_ACTION = "L";
  public static final String EXECUTE_TESTCONVERSION_ACTION = "K";
  public static final String WQ_DEL_ACTION = "X";
  public static final String WQ_RESTART_ACTION = "WQR";

  //Parameters
  public static final String XSL_FOLDER = "xsl/";
  public static final String QUERY_FOLDER = "queries/";

  public static final String ACL_STYLESHEETS_PATH = "stylesheets";
  public static final String ACL_TESTCONVERSION_PATH = "testconversion";
  public static final String ACL_SCHEMA_PATH = "schema";
  public static final String ACL_HOST_PATH = "host";
  public static final String ACL_WQ_PATH = "workqueue";
  public static final String ACL_QUERIES_PATH = "queries";
  public static final String ACL_CONFIG_PATH = "config";
  public static final String ACL_XMLFILE_PATH = "xmlfile";


  public static final String RPC_SERVICE_NAME="XService";
  public static final String EXCEL_CONVERSION_JSP = "excel2xml_conversion.jsp";
  public static final String EXCEL2XML_CONV_PARAM = "excel2dd_xml";

}
