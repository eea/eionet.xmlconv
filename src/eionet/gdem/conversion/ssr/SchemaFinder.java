package eionet.gdem.conversion.ssr;


// Copyright (c) 2000 TietoEnator

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import eionet.gdem.utils.Utils;

/**
* Handler for parsing xml document
* extening SAX DefaultHandler
* This class is calling different ExcelConversionhandler methods, which is actuially creating Excel file
* @author Enriko Käsper
*/

public class SchemaFinder extends DefaultHandler{

  private static String SCHEMA_REFERENCE="schemaLocation";
  private static String NO_NS_SCHEMA_REFERENCE="noNamespaceSchemaLocation";

  private String startTag=null;
  private String startTagNamespace=null;
  private String schemaLocation=null;

  
  public void startElement(String uri, String localName, String name, Attributes attrs) throws SAXException {
//System.out.println("element:" + uri + "||" + localName + "||" + name);

        startTag = (localName==null) ? name : localName; //we want the tag name without ns prefix, if ns processing is turned off, them we use name
        startTagNamespace = uri;        

        String schema_location_attr = (Utils.isNullStr(startTagNamespace))? NO_NS_SCHEMA_REFERENCE:SCHEMA_REFERENCE;
        
        //System.out.println("("+name);
        int length = attrs != null ? attrs.getLength() : 0;
        for (int i = 0; i < length; i++) {
            String attrName =  attrs.getLocalName(i);
            if (attrName.equalsIgnoreCase(schema_location_attr))
                schemaLocation=attrs.getValue(i);
        }
        throw new SAXException("OK");
        

  }
  public void error(SAXParseException e){
    System.out.println("jjj");
  }
  public void fatalError(SAXParseException e){
    System.out.println("fataljjj");
  }
  public String getStartTag(){
    return this.startTag;
  }
  public String getStartTagNamespace(){
    return this.startTagNamespace;
  }
  public String getSchemaLocation(){
    return this.schemaLocation;
  }
  /*public void startPrefixMapping(java.lang.String prefix,
                               java.lang.String uri)
                        throws SAXException   {
                          System.out.println("ns: " + prefix + "||" + uri);
                        }
                        */
}