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
 * Constants, used by GDEM (deprecated, but some constants may be still in use).
 * @author Unknown
 * @author George Sofianos
 */
public interface Names {

    // Request + session Attribute names
    String SESS_ATT = "GDEM_SESS";
    String USER_ATT = "GDEM_ACL_USR_ATT";
    String ERROR_ATT = "GDEM_ERROR_ATT";
    String STYLESHEETS_ATT = "GDEM_ACL_SS_ATT";
    String TICKET_ATT = "GDEM_TICKET";
    String SUCCESS_ATT = "GDEM_SUCCESS_ATT";

    // JSP names
    String INDEX_JSP = "index.jsp";
    String ERROR_JSP = "error.jsp";
    String STYLESHEET_JSP = "do/stylesheetEditForm";
    String STYLESHEETS_JSP = "do/schemaStylesheets";
    String ADD_XSL_JSP = "do/addStylesheetForm";
    String TEST_CONVERSION_JSP = "testConversionService.html";
    String LIST_CONVERSION_JSP = "do/listConvForm";
    String TEST_CONVERSION_SERVLET = "convert";
    String LOGIN_JSP = "login.jsp";
    String SCHEMA_JSP = "do/schemaElemForm";
    String LIST_WORKQUEUE_JSP = "workqueue.jsp";
    String QUERIESINDEX_JSP = "do/qaScripts";

    // actions
    String LOGIN_ACTION = "F";
    String LOGOUT_ACTION = "I";
    String SHOW_TESTCONVERSION_ACTION = "J";
    String SHOW_LISTCONVERSION_ACTION = "L";
    String EXECUTE_TESTCONVERSION_ACTION = "K";
    String WQ_DEL_ACTION = "X";
    String WQ_RESTART_ACTION = "WQR";

    // Parameters
    String XSL_FOLDER = "xsl/";
    String QUERY_FOLDER = "queries/";

    String ACL_STYLESHEETS_PATH = "stylesheets";
    String ACL_TESTCONVERSION_PATH = "testconversion";
    String ACL_SCHEMA_PATH = "schema";
    String ACL_HOST_PATH = "host";
    String ACL_WQ_PATH = "workqueue";
    String ACL_QUERIES_PATH = "queries";
    String ACL_CONFIG_PATH = "config";
    String ACL_XMLFILE_PATH = "xmlfile";
    String ACL_QASANDBOX_PATH = "qasandbox";
    String ACL_LOGFILE_PATH = "logfile";
    String ACL_SERVERSTATUS_PATH = "serverstatus";

    String RPC_SERVICE_NAME = "XService";
    String EXCEL_CONVERSION_JSP = "excel2xml_conversion.jsp";
    String EXCEL2XML_CONV_PARAM = "excel2dd_xml";

}
