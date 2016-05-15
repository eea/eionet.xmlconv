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
    String GETSOURCE_URL = "/do/getsource";
    String AUTH_PARAM = "auth";
    String TICKET_PARAM = "ticket";
    String SOURCE_URL_PARAM = "source_url";

    int URL_TEXT_LEN = 100;

    String TMP_FILE_PREFIX = "xmlconv_tmp_";
    String BACKUP_FILE_PREFIX = "bup_";
    String BACKUP_FOLDER_NAME = "backup";

    String FILEREAD_EXCEPTION = "Unable to read the file: ";
}
