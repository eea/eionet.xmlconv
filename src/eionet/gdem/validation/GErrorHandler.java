package eionet.gdem.validation;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GErrorHandler extends DefaultHandler {
  private StringBuffer errContainer;
  private StringBuffer htmlErrContainer;
  public GErrorHandler(StringBuffer errContainer, StringBuffer htmlErrContainer) {
    this.errContainer=errContainer;
    this.htmlErrContainer= htmlErrContainer;
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
      
      writeRowStart();
      writeCell(type);
      writeCell("Line: "+ String.valueOf(ex.getLineNumber())+ ", Col: " + ex.getColumnNumber());
      writeCell(ex.getMessage());
      writeRowEnd();
      
  }
  private void writeRowStart(){
    if (htmlErrContainer.length()==0){
      htmlErrContainer.append("<html>"); 
      htmlErrContainer.append("<table border='1'><tr>"); 
      htmlErrContainer.append("<th>Type</th>");
      htmlErrContainer.append("<th>Position</th>"); 
      htmlErrContainer.append("<th>Error message</th>");
      htmlErrContainer.append("</tr>"); 
    }
    htmlErrContainer.append("<tr>");
  }
  private void writeRowEnd(){
    htmlErrContainer.append("</tr>");
  }
  private void writeCell(String val){
    htmlErrContainer.append("<td>");
    htmlErrContainer.append(val);
    htmlErrContainer.append("</td>");
  }
  public String getHTMLError(){
    htmlErrContainer.append("</table>");
    
    return htmlErrContainer.toString();
  }

  
}