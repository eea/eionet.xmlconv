package eionet.gdem.validation;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GErrorHandler extends DefaultHandler {
  private StringBuffer errContainer;
  public GErrorHandler(StringBuffer errContainer) {
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
    errContainer.append(type + ": " 
      + "at line: " + ex.getLineNumber() + ", "
      + "col: " + ex.getColumnNumber() + " "
      + ex.getMessage() + "\n");
  }

  
}