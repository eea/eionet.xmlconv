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

public interface Constants {
 //constants:
  //XQuery job statuses in the DB: (internal)
  public static final int XQ_RECEIVED=0; //waiting for the engine to begin processing
  public static final int XQ_DOWNLOADING_SRC=1; //downloading from the server to be stored locally
  public static final int XQ_PROCESSING=2; //XQEngine is processing
  public static final int XQ_READY=3; //waiting for pulling by the client
	public static final int XQ_FATAL_ERR=4; //fatal error
	public static final int XQ_LIGHT_ERR=5; //error, can be tried again


	//status values for reportek getResult() method (external)
  public static final int JOB_READY=0;
  public static final int JOB_NOT_READY=1;
  public static final int JOB_FATAL_ERROR=2;
  public static final int JOB_LIGHT_ERROR=3;

	//key names for te getResult() STRUCT
	public static final String RESULT_CODE_PRM = "CODE";
	public static final String RESULT_VALUE_PRM = "VALUE";

  /**
   * Default parameter name of the source URL
   * to be given to the XQuery script by the QA service
   */
  public static final String XQ_SOURCE_PARAM_NAME="source_url";

  public static final String XQ_SCRIPT_PARAM="XQSCRIPT"; //field name for XQscript in the sandbox
}