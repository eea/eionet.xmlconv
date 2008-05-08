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

package eionet.gdem.qa;
import eionet.gdem.GDEMException;
import eionet.gdem.utils.Utils;

import java.io.OutputStream;
/**
* Interface for XQuery Engine implementation
*/

public interface XQEngineIF {

  public static final String DEFAULT_ENCODING ="UTF-8";
  public static final String DEFAULT_OUTPUTTYPE ="html";
  public static final String HTML_CONTENT_TYPE ="html";
  public static final String XML_CONTENT_TYPE="xml";
	/**
   * processes the XQuery
   * @param xqScript the XQuery script
   * @param params XQuery parameter name value pairs
   * in format {name1=value1, name2=value2, ... , nameN=valueN}
   * @return the result of XQuery
   * @throws eionet.gdem.GDEMException
   */
  public String getResult(String xqScript, String params[]) throws GDEMException;
  /**
   * processes the XQuery
   * @param xqScript the XQuery script
   * @return the result of XQuery
   * @throws eionet.gdem.GDEMException
   */
  public String getResult(String xqScript) throws GDEMException;
  
  public void getResult(String xqScript, String params[], OutputStream out) throws GDEMException;
  
  /**
   * get encoding for XQuery engine to use. If not set use default encoding UTF-8.
   * @return
   */
  public String getEncoding();
  /**
   * set encoding parameter for XQuery engine. If not set use default encoding UTF-8.
   * @param encoding
   */
  public void setEncoding(String encoding);
  /**
   * get output type of the XQuery script result. Default is text/html.
   * @return
   */
  public String getOutputType();
  /**
   * set output type for XQuery engine. If output type is text/xml, then the XML declaration is omitted to the result.
   * @param outputType
   */
  public void setOutputType(String outputType);
}