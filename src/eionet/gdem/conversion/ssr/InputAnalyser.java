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

package eionet.gdem.conversion.ssr;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import eionet.gdem.GDEMException;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Utils;

public class InputAnalyser
{
  private String schemaOrDTD = null;
  private String rootElement = null;
  private String namespace = null;
  private String dtdPublicId = null;
  private boolean hasNamespace = false;

  public InputAnalyser()
  {
  
  }
  public String parseXML(String srcUrl) throws DCMException{
    InputFile src=null;
    InputStream input = null;
    try{
      src = new InputFile(srcUrl);
      src.setTrustedMode(true);
      input = src.getSrcInputStream();
      return parseXML(input);
    } catch (MalformedURLException mfe ) {
      //throw new GDEMException("Bad URL : " + mfe.toString());
		throw new DCMException(BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED);
    } catch (IOException ioe ) {
      //throw new GDEMException("Error opening URL " + ioe.toString());
		throw new DCMException(BusinessConstants.EXCEPTION_CONVERT_URL_ERROR);	
    } catch (GDEMException e ) {
		e.printStackTrace();
		throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
    }
	
    finally{
      try{
        if (input!=null) input.close();
	    }
	    catch(Exception e){}
    }
    
  }
  public String parseXML(InputStream input) throws GDEMException
  {
    String dtd=null;
    try{
      //URL url = new URL(srcUrl);
      //InputSource is = new InputSource( url.openStream());
      
      InputSource is = new InputSource( input);
      
      
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
      hasNamespace = handler.hasNamespace();
      
       //Find DTD
      if (schemaOrDTD==null){
        schemaOrDTD=Utils.isURL(doctype_reader.getDTD())? doctype_reader.getDTD():null;
        dtdPublicId=doctype_reader.getDTDPublicId();
      }

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
      throw new GDEMException("Error parsing: " + e.toString(), e);
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
  public boolean hasNamespace(){
    return this.hasNamespace;
  }
    public static void main(String[] argv) {
        InputAnalyser sch = new InputAnalyser();//
        /*try{
          //sch.parseXML("http://localhost:8080/gdem/xml/meta.xml");
          //sch.parseXML("http://reporter.ceetel.net:18180/ro/eea/ewn3/envqhw5eg/test.xml");
          //sch.parseXML("http://195.250.186.59:8080/gdem/countrynames.tmx");
        }
        catch(GDEMException e){
          System.out.println(e.toString());
        }
        */
         System.out.println("start tag: " + sch.getRootElement());
         System.out.println("schema or dtd: " + sch.getSchemaOrDTD());
         System.out.println("ns: " + sch.getNamespace());
     }
}
