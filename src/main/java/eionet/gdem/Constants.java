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
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.gdem;

/**
 * Constants interface.
 * TODO: interfaces should have methods
 * @author Unknown
 */
public interface Constants {
    // constants:
    // XQuery job statuses in the DB: (internal)
    int XQ_RECEIVED = 0; // waiting for the engine to begin processing
    int XQ_DOWNLOADING_SRC = 1; // downloading from the server to be stored locally
    int XQ_PROCESSING = 2; // XQEngine is processing
    int XQ_READY = 3; // waiting for pulling by the client
    int XQ_FATAL_ERR = 4; // fatal error
    int XQ_LIGHT_ERR = 5; // error, can be tried again
    int XQ_JOBNOTFOUND_ERR = 6; // job not found or result has been downloadad

    int XQ_CANCELLED = 8; // cancelled by cdr/bdr

    int XQ_INTERRUPTED = 7; // job interrupted

    int CANCELLED_BY_USER = 9;
    int JOB_NOT_FOUND = 10;

    int JOB_VALIDATION = -1;
    int JOB_FROMSTRING = 0;

    // status values for reportek getResult() method (external)
    int JOB_READY = 0;
    int JOB_NOT_READY = 1;
    int JOB_FATAL_ERROR = 2;
    int JOB_LIGHT_ERROR = 3;

    String QA_TYPE_XQUERY = "xquery";
    String QA_TYPE_XSLT = "xslt";

    // key names for te getResult() STRUCT
    String RESULT_CODE_PRM = "CODE";
    String RESULT_VALUE_PRM = "VALUE";
    String RESULT_METATYPE_PRM = "METATYPE";
    String RESULT_SCRIPTTITLE_PRM = "SCRIPT_TITLE";

    //Script feedback variables for CDR
    String RESULT_FEEDBACKSTATUS_PRM = "FEEDBACK_STATUS";
    String RESULT_FEEDBACKMESSAGE_PRM = "FEEDBACK_MESSAGE";

    //Feedback Status values
    String XQ_FEEDBACKSTATUS_UNKNOWN = "UNKNOWN";

    /**
     * Default parameter name of the source URL to be given to the XQuery script by the QA service
     */
    String XQ_SOURCE_PARAM_NAME = "source_url";
    String XQ_SCRIPT_ID_PARAM = "script_id";

    // Folder for temporary files - to be placed under public
    String TMP_FOLDER = "tmp/";
    String QUERIES_FOLDER = "queries/";
    String SCHEMA_FOLDER = "schemas/";

    // Public constants for SourceFileAdapter
    String GETSOURCE_URL = "/s/getsource";
    String AUTH_PARAM = "auth";
    String TICKET_PARAM = "ticket";
    String SOURCE_URL_PARAM = "source_url";

    int URL_TEXT_LEN = 100;

    String TMP_FILE_PREFIX = "xmlconv_tmp_";
    String BACKUP_FILE_PREFIX = "bup_";
    String BACKUP_FOLDER_NAME = "backup";

    String FILEREAD_EXCEPTION = "Unable to read the file: ";

    // Copied from deprecated Names interface
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
    String ACL_ADMIN_PATH = "admin";
    String ACL_XMLFILE_PATH = "xmlfile";
    String ACL_QASANDBOX_PATH = "qasandbox";
    String ACL_LOGFILE_PATH = "logfile";
    String ACL_SERVERSTATUS_PATH = "serverstatus";

    String RPC_SERVICE_NAME = "XService";
    String EXCEL_CONVERSION_JSP = "excel2xml_conversion.jsp";
    String EXCEL2XML_CONV_PARAM = "excel2dd_xml";

    String HTML_FILE = ".html";

}
