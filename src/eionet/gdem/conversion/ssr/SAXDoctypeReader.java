package eionet.gdem.conversion.ssr;

import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SAXDoctypeReader implements LexicalHandler {

  private String dtdSystemId=null;
  private String dtdPublicId=null;
  
  public void startDTD(String name, String publicId, String systemId)
   throws SAXException {

      dtdSystemId = systemId;
      dtdPublicId = publicId;
      System.out.println("dtd: " + name + "-" + publicId + "-" +systemId);
     // throw new SAXException("OK");
  
  }


  public void endDTD() throws SAXException {}
  public void startEntity(String name) throws SAXException {}
  public void endEntity(String name) throws SAXException {}
  public void startCDATA() throws SAXException {}
  public void endCDATA() throws SAXException {}

  public void comment (char[] text, int start, int length)
   throws SAXException {

   // String comment = new String(text, start, length);
   // System.out.println(comment);
    System.out.println("1");
   

  }
  public String getDTD()
  {
      return this.dtdSystemId; 
  }
  public String getDTDPublicId()
  {
      return this.dtdPublicId; 
  }
}