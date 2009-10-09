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

package eionet.gdem.qa.engines;
import javax.xml.transform.TransformerException;

import net.sf.saxon.StandardErrorListener;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

/**
 * Extension of the Saxon error listener
 * to catch all the errors and feedback them to user
 */
public class SaxonListener extends StandardErrorListener {

  private StringBuffer _errBuf; //in this buffer we collect all the error messages
  private boolean _hasErrors=false;
  private LoggerIF _logger;
  

  public SaxonListener()  {
  	_logger= GDEMServices.getLogger();
    _errBuf=new StringBuffer();
  }


  boolean hasErrors() {
    return _hasErrors;
  }
  /**
   * Returns all the error messages gathered when processing the XQuery script
   * @return  String errors - all the errors
   */
  public String getErrors() {
    return _errBuf.toString();
  }
  public void error(TransformerException exception) throws TransformerException {
    _hasErrors=true;
    String message = "Error " +
                         getLocationMessage(exception) +
                         "\n  " +
                         getExpandedMessage(exception);

     _errBuf.append(message).append("\n");                         
      super.error(exception);
  }
  
  public void warning(TransformerException exception)  throws TransformerException {
  	_hasErrors=true;
     String message = "";
       if (exception.getLocator()!=null) {
            message = getLocationMessage(exception) + "\n  ";
        }
        message += getExpandedMessage(exception);  

     _errBuf.append(message).append("\n");

      super.warning(exception);     
  }
}