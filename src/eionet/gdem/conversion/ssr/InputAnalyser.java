package eionet.gdem.conversion.ssr;

import java.io.*;
import java.util.*;
import org.xml.sax.*;
//import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import eionet.gdem.GDEMException;
import eionet.gdem.utils.Utils;

import java.net.URL;
import java.net.MalformedURLException;

public class InputAnalyser
{
  private String schemaOrDTD = null;
  private String rootElement = null;
  private String namespace = null;
  private String dtdPublicId = null;

  public InputAnalyser()
  {
  
  }
  public String parseXML(String srcUrl) throws GDEMException
  {
    String dtd=null;
    try{
      URL url = new URL(srcUrl);
      InputSource is = new InputSource( url.openStream());
      //Defaulthandler handler = new DefaultHandler();
      //InputAnalyserDTD dtd_handler = new InputAnalyserDTD();
      SchemaFinder handler=new SchemaFinder();
      SAXParserFactory spfact = SAXParserFactory.newInstance();
      SAXParser parser = spfact.newSAXParser();
      XMLReader reader = parser.getXMLReader();
        
      spfact.setValidating(false);

      //make parser to not validate
      reader.setFeature("http://xml.org/sax/features/validation", false); 
      reader.setFeature("http://apache.org/xml/features/validation/schema", false);
      reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      //reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      reader.setFeature("http://xml.org/sax/features/namespaces", true);
      
      SAXDoctypeReader doctype_reader = new SAXDoctypeReader();
      // turn on dtd handling
      try {
        parser.setProperty("http://xml.org/sax/properties/lexical-handler", doctype_reader);
      }
      catch (SAXNotRecognizedException e) {
        System.err.println("Installed XML parser does not provide lexical events...");
        //return e.toString();
      }
      catch (SAXNotSupportedException e) {
        System.err.println("Cannot turn on comment processing here");
          //return e.toString();
      }       

      reader.setContentHandler(handler);
      try{
        reader.parse(is);
      }
      catch (SAXException e){
        if (!e.getMessage().equals("OK"))
          throw new SAXException(e);
      }
      // we want schema url only
      schemaOrDTD=Utils.isURL(handler.getSchemaLocation())? handler.getSchemaLocation():null;
      rootElement = handler.getStartTag();
      namespace = handler.getStartTagNamespace();
      
       //Find DTD
      if (schemaOrDTD==null){
        schemaOrDTD=Utils.isURL(doctype_reader.getDTD())? doctype_reader.getDTD():null;
        dtdPublicId=doctype_reader.getDTDPublicId();
      }

    } 
    catch (MalformedURLException mfe ) {
      throw new GDEMException("Bad URL : " + mfe.toString());
    } 
    catch (IOException ioe ) {
      throw new GDEMException("Error opening URL " + ioe.toString());
    } 
    catch ( SAXParseException se ) {
      //ignore
    } 
    catch (Exception e ) {
      Exception se = e;
      if (e instanceof SAXException) {
          se = ((SAXException)e).getException();
      }
      if (se != null)
        se.printStackTrace(System.err);
      else
        e.printStackTrace(System.err);    
      throw new GDEMException("Error parsing: " + e.toString());
    }
  
  return "OK";
  }
  public String getSchemaOrDTD(){
    return this.schemaOrDTD;
  }
  public String getRootElement(){
    return this.rootElement;
  }
  public String getNamespace(){
    return this.namespace;
  }
    public static void main(String[] argv) {
        InputAnalyser sch = new InputAnalyser();//
        try{
          //sch.parseXML("http://localhost:8080/gdem/xml/meta.xml");
          sch.parseXML("http://localhost:8080/gdem/water1.xml");
          //sch.parseXML("http://195.250.186.59:8080/gdem/countrynames.tmx");
        }
        catch(GDEMException e){
          System.out.println(e.toString());
        }
         System.out.println("start tag: " + sch.getRootElement());
         System.out.println("schema or dtd: " + sch.getSchemaOrDTD());
     }
}