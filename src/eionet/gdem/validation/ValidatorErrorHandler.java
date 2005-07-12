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
 * Original Code: Enriko Käsper (TietoEnator)
 */

package eionet.gdem.validation;

import java.util.ArrayList;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eionet.gdem.dto.ValidateDto;

public class ValidatorErrorHandler extends DefaultHandler {
  //private StringBuffer errContainer;
  //private StringBuffer htmlErrContainer;
  
  private ArrayList errContainer;
  
  public ValidatorErrorHandler(ArrayList errContainer) {
    this.errContainer=errContainer;
  }

  public void warning(SAXParseException ex) throws SAXException {
    //System.out.println("WARNING: " + ex.getMessage());
    addError("WARNING", ex);
  }

  public void error(SAXParseException ex) throws SAXException {
    addError("ERROR", ex);    
  }
  
  public void fatalError(SAXParseException ex) throws SAXException {
    //System.out.println("FATAL ERROR: " + ex.getMessage());
    addError("FATAL ERROR", ex);    
  }

  private void addError(String type, SAXParseException ex) {
	  ValidateDto val = new ValidateDto();
	  val.setType(type);
	  val.setDescription(ex.getMessage());
	  val.setColumn(ex.getColumnNumber());
	  val.setLine(ex.getLineNumber());
	  
	  errContainer.add(val);      
  }
  public ArrayList getErrors(){
    return errContainer;
  }

  
}