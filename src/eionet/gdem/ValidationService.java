package eionet.gdem;

import org.apache.xerces.parsers.SAXParser;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import java.net.URL;
import java.net.MalformedURLException;

import java.io.IOException;


public class ValidationService {
  private StringBuffer errors;
  public ValidationService()  {
    errors=new StringBuffer()  ;
    errHandler = new GErrorHandler(errors);
  }

  private ErrorHandler errHandler;
  
  public String validateSchema (String srcUrl, String schema) throws GDEMException {
    try {
      URL url = new URL(srcUrl);

      SAXParser parser = new SAXParser();
      parser.setErrorHandler(errHandler);

      //make parser to validate
      parser.setFeature("http://xml.org/sax/features/validation", true); 
      
      parser.setFeature("http://apache.org/xml/features/validation/schema", true);

//      parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
     if (schema != null)
      parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", schema);      
      
      InputSource is = new InputSource( url.openStream());

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

    //we have errors!
    if (errors.length()>0)
      return errors.toString();
    else
      return "OK";
  }


  public String validate (String srcUrl) throws GDEMException {
    return validateSchema(srcUrl, null);
  }

  private void log(String s ) {
    Utils.log(s);
  }
}