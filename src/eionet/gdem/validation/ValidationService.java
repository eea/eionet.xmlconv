package eionet.gdem.validation;

import org.apache.xerces.parsers.SAXParser;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import java.net.URL;
import java.net.MalformedURLException;

import eionet.gdem.utils.InputFile;

import java.io.IOException;

//import eionet.gdem.utils.Utils;
import eionet.gdem.GDEMException;


public class ValidationService {
  private StringBuffer errors;
  public ValidationService()  {
    errors=new StringBuffer()  ;
    errHandler = new GErrorHandler(errors);
  }

  private ErrorHandler errHandler;
  
  public String validateSchema (String srcUrl, String schema) throws GDEMException {
    
    InputFile src=null;
    try {
      src = new InputFile(srcUrl);
      
      //URL url = new URL(srcUrl);

      SAXParser parser = new SAXParser();
      parser.setErrorHandler(errHandler);

      //make parser to validate
      parser.setFeature("http://xml.org/sax/features/validation", true); 
      
      parser.setFeature("http://apache.org/xml/features/validation/schema", true);

//      parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
     if (schema != null)
      parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", schema);      
      
      InputSource is = new InputSource( src.getSrcInputStream());
      //InputSource is = new InputSource( url.openStream());
      parser.parse(is);

      //log("OK");      
  
    } catch (MalformedURLException mfe ) {
      throw new GDEMException("Bad URL : " + mfe.toString());
    } catch (IOException ioe ) {
      throw new GDEMException("Error opening URL " + ioe.toString());
    } catch ( SAXParseException se ) {
      //ignore
    } catch (Exception e ) {
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
    finally{
      src.close();
    }

    //we have errors!
    if (errors.length()>0)
      return errors.toString();
    else
      return "OK";
  }


  public String validate (String srcUrl) throws GDEMException {
    return validateSchema(srcUrl, null);
  }

  /*private void log(String s ) {
    Utils.log(s);
  } */

  public static void main(String[] s) {

try {
    //String xml = "http://reportek2.eionet.eu.int/colqaj8nw/envqe8zva/countrynames.tmx";
    String xml = "http://localhost:8080/gdemxf/forms/data/data30.xml";
    //String sch = "http://dd.eionet.eu.int/GetSchema?comp_id=1752&comp_type=TBL";
    // String sch = "http://www.lisa.org/tmx/tmx14.dtd";
    //String sch = "http://roddev.eionet.eu.int/waterdemo/water_measurements.xsd";
    
    ValidationService v = new ValidationService();
  //  v.log(v.validateSchema(xml,sch));
  //System.out.println(v.validateSchema(xml,sch));
  System.out.println(v.validate(xml));
    //v.log(v.validate(xml));
    
} catch (Exception e) {
System.out.println("===== " + e.toString());
}

  }
}