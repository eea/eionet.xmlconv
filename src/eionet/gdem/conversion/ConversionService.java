

package eionet.gdem.conversion;
import java.io.*;

//import org.apache.log4j.Category;
//import org.apache.log4j.Priority;

//import java.net.URL;
import java.net.MalformedURLException;

import java.util.Hashtable;
import java.util.Vector;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import eionet.gdem.services.*;
import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.InputFile;

import eionet.gdem.services.LoggerIF;

import java.util.HashMap;

//KL 040427 not used?
//import org.apache.avalon.framework.logger.Logger; 
//import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.fop.apps.Driver;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;

import org.apache.xalan.xslt.*;
import org.apache.xalan.*;
 import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.apache.xerces.parsers.DOMParser;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.httpclient.*;
//import org.apache.commons.httpclient.methods.*;


/**
* Container for different conversions
* for being used through XML/RPC
* @author Kaido Laine
*/

public class ConversionService {

  private static final String DEFAULT_CONTENT_TYPE= "text/plain";
  private static final String DEFAULT_FILE_EXT= "txt";

  private String xslFolder;
  private String tmpFolder;

  private DbModuleIF db;
  
  private HashMap convTypes;
  
  private String cnvContentType = null;
  private String cnvFileExt = null;
  private String cnvTypeOut = null;

  private OutputStream result = null;

  private static LoggerIF _logger;

  /***
  * Constant values for HttpResponse content types
  * deprecated
  */
  private void initCnvTypes() {
    convTypes=new HashMap();

    convTypes.put("EXCEL", "application/vnd.ms-excel");
    convTypes.put("PDF", "application/pdf");
    convTypes.put("HTML", "text/html");
    convTypes.put("XML", "text/xml");    
    convTypes.put("SQL", "text/plain");
  }

  public ConversionService()  {
      _logger=GDEMServices.getLogger();
  
      xslFolder=Properties.xslFolder; //props.getString("xsl.folder");
      tmpFolder=Properties.tmpFolder;  //props.getString("tmp.folder");

      initCnvTypes();

  }


  /**
  * List all possible conversions 
  */

  public Vector listConversions() throws GDEMException {
      return listConversions(null);
  }


  /**
  * List all possible conversions for this namespace
  */
  public Vector listConversions(String schema) throws GDEMException {

    if (db==null)
      db = GDEMServices.getDbModule();

    Vector v = null;
    try {
      v=db.listConversions(schema);
    } catch (Exception e ) {
      throw new GDEMException("Error getting data from the DB " + e.toString());
    }

    return v;
    

  }

  /**
  * Converts the XML file to a specific format
  * TODO MAppings between conversion ids and XSL's
  */
  public Hashtable convert (String sourceURL, String convertId) throws GDEMException {
    return convert(sourceURL, convertId, null);
  }
  public Hashtable convert (String sourceURL, String convertId, HttpServletResponse res) throws GDEMException {
    Hashtable h = new Hashtable();
    String sourceFile=null;
    String xslFile=null;
    String outputFileName=null;
    //String cnvTypeOut=null;
    InputFile src= null;

    try {
    
    src = new InputFile(sourceURL);
    if (db==null)
      db = GDEMServices.getDbModule();

    try {
      HashMap styleSheetData=db.getStylesheetInfo(convertId);

      if (styleSheetData==null)
        throw new GDEMException("No stylesheet info for convertID= " + convertId);
      xslFile= xslFolder + (String)styleSheetData.get("xsl");
      cnvTypeOut= (String)styleSheetData.get("content_type_out");
      
      Hashtable convType=db.getConvType(cnvTypeOut);

      if (convType!=null){
        try{
          cnvContentType = (String)convType.get("content_type");
          cnvFileExt = (String)convType.get("file_ext");
        }
        catch (Exception e){
          //Take no action, use default params
        }
      }
      if (cnvContentType == null)
        cnvContentType = "text/plain";
      if (cnvFileExt == null)
        cnvFileExt = "txt";
      
      
              
    } catch (Exception e ) {
      throw new GDEMException("Error getting stylesheet info from repository for " + convertId);
    }
      if (res!=null){  
        try {
          this.result = res.getOutputStream();
          res.setContentType(cnvContentType);
        } catch (IOException e ) {
          throw new GDEMException("Error getting response outputstream " + e.toString());
        }
      }
    if (cnvTypeOut.equals("HTML")){
      outputFileName=convertHTML(src.getSrcInputStream(), xslFile);
    }
    else if (cnvTypeOut.equals("PDF")){
      outputFileName=convertPDF(src.getSrcInputStream(), xslFile);
    }
    else if (cnvTypeOut.equals("EXCEL")){
      outputFileName=convertExcel(src.getSrcInputStream(), xslFile);
    }
    else  if (cnvTypeOut.equals("XML")){
      outputFileName=convertXML(src.getSrcInputStream(), xslFile);
    }
    else{
      outputFileName=convertTextOutput(src.getSrcInputStream(), xslFile);
    }
    //else
    //  throw new GDEMException("Unknown conversion type or converter not  implemented: " + cnvTypeOut);

    } 
    catch (MalformedURLException mfe ) {
      throw new GDEMException("Bad URL : " + mfe.toString());
    } 
    catch (IOException ioe ) {
      throw new GDEMException("Error opening URL " + ioe.toString());
    } 
    catch (Exception e ) {
      throw new GDEMException("Error converting: " + e.toString());
    }
    finally{
      src.close();
    }


    h.put("content-type", cnvContentType);
    if (res!=null){
      try{
        result.close();
      } catch (IOException e ) {
        throw new GDEMException("Error closing result ResponseOutputStream " + convertId);
      }
      return h;
    }
    //log("========= going to bytes " + htmlFileName);


    byte[] file = fileToBytes(outputFileName);
    //log("========= bytes ok");

    h.put("content", file);
    try{
      //Utils.deleteFile(sourceFile);
      //deleteFile(htmlFileName);
      Utils.deleteFile(outputFileName);
    }
    catch(Exception e){
      _logger.error("Couldn't delete the result file");
    }
    
    
    return h;
    
  }

 
  /** 
  * reads temporary file from dis and returs as a bytearray
  */
  private byte[] fileToBytes(String fileName) throws GDEMException {

    ByteArrayOutputStream baos = null;
    try {

      //log("========= open fis " + fileName);
      FileInputStream fis = new     FileInputStream(fileName);
      //log("========= fis opened");
      
      baos = new ByteArrayOutputStream();
    
      int bufLen = 0;
      byte[] buf = new byte[1024];

  
     while ( (bufLen=fis.read( buf ))!= -1 )
          baos.write(buf, 0, bufLen );

      fis.close();
      
    } catch (FileNotFoundException fne) {
      throw new GDEMException("File not found " + fileName);
    } catch (Exception e) {
      throw new GDEMException("Exception " + e.toString());
    }    
      return baos.toByteArray();    
  }
  private String convertPDF(InputStream source, String xslt) throws GDEMException {

      String pdfFile=tmpFolder + "gdem_" + System.currentTimeMillis() + ".pdf";
      //String args[]={"-xml", source, "-xsl", xslt, "-pdf", pdfFile  };
      //org.apache.fop.apps.Fop.main(args);
      if (result!=null)
        runFOPTransformation(source, xslt, result);
      else{
        try{
          runFOPTransformation(source, xslt,  new FileOutputStream(pdfFile));
        } catch (IOException e ) {
          _logger.error("Error " + e.toString());
          throw new GDEMException("Error creating PDF output file " + e.toString());
        }
      }
        
      
      return pdfFile;
  }



  private String convertHTML(InputStream source, String xslt) throws GDEMException {

      String htmlFile=tmpFolder + "gdem_" + System.currentTimeMillis() + ".html";
      //String args[]={"-in", source, "-xsl", xslt, "-out", htmlFile  };
      //[-xsl stylesheet] [-o dest] file1.xml file2.xml ...       
      //String args[]={"-xsl", xslt, "-o", htmlFile, source  };

      if (result!=null)
        runXalanTransformation(source, xslt, result);
      else{
        try{
          runXalanTransformation(source, xslt,  new FileOutputStream(htmlFile));
        } catch (IOException e ) {
          _logger.error("Error " + e.toString());
          throw new GDEMException("Error creating HTML output file " + e.toString());
        }
      }
      //org.apache.xalan.xslt.Process.main(args);
      //log("conversion done");

    
      //System.out.println("======= html OK");
      return htmlFile;
  }

  private String convertExcel(InputStream source, String xslt) throws GDEMException {

      String xmlFile=tmpFolder + "gdem_out" + System.currentTimeMillis() + ".xml";
      String excelFile=tmpFolder + "gdem_" + System.currentTimeMillis() + ".xls";
      //String args[]={"-in", source, "-xsl", xslt, "-out", xmlFile  };
      //String excel_args[]={"-in", xmlFile, "-out", excelFile  };
      //[-xsl stylesheet] [-o dest] file1.xml file2.xml ...       
      //String args[]={"-xsl", xslt, "-o", htmlFile, source  };
    try {
      runXalanTransformation(source, xslt,  new FileOutputStream(xmlFile));
      //org.apache.xalan.xslt.Process.main(args);
      ExcelProcessor ep = new ExcelProcessor();
      if (result!=null)
        ep.makeExcel(xmlFile, result);
      else
        ep.makeExcel(xmlFile, excelFile);

      try{
        Utils.deleteFile(xmlFile);
      }
      catch(Exception e){
        _logger.error("Couldn't delete the result file");
      }

    } catch (Exception e ) {
      _logger.error("Error " + e.toString());
      e.printStackTrace(System.out);    
      throw new GDEMException("Error transforming Excel " + e.toString());
    }


    
      //System.out.println("======= html OK");
      return excelFile;
  }
  
  private String convertXML(InputStream source, String xslt) throws GDEMException {

      String xmlFile=tmpFolder + "gdem_out" + System.currentTimeMillis() + "." + cnvFileExt;
      //String args[]={"-in", source, "-xsl", xslt, "-out", xmlFile  };
      if (result!=null)
        runXalanTransformation(source, xslt, result);
      else
        try{
          runXalanTransformation(source, xslt,  new FileOutputStream(xmlFile));
        } catch (IOException e ) {
          _logger.error("Error " + e.toString());
          throw new GDEMException("Error creating XML output file " + e.toString());
        }
        //org.apache.xalan.xslt.Process.main(args);
        //log("conversion done");
  
      //System.out.println("======= html OK");
      return xmlFile;
  }
  private String convertTextOutput(InputStream source, String xslt) throws GDEMException {

      String outFile=tmpFolder + "gdem_out" + System.currentTimeMillis() + "." + cnvFileExt;
      if (result!=null)
        runXalanTransformation(source, xslt, result);
      else
        try{
          runXalanTransformation(source, xslt,  new FileOutputStream(outFile));
        } catch (IOException e ) {
          _logger.error("Error " + e.toString());
          throw new GDEMException("Error creating " + cnvTypeOut + " output file with Xalan:" + e.toString());
        }
        //org.apache.xalan.xslt.Process.main(args);
        //log("conversion done");
  
      //System.out.println("======= html OK");
      return outFile;
  }
  
  
        
  private void runXalanTransformation(InputStream in, String xsl, OutputStream  out) throws GDEMException {
    try{
      // 1. Instantiate a TransformerFactory.
      TransformerFactory tFactory = TransformerFactory.newInstance();
      //tFactory.setAttribute("http://xml.apache.org/xalan/features/incremental", Boolean.TRUE);
      // 2. Use the TransformerFactory to process the stylesheet Source and
      //    generate a Transformer.
      Transformer transformer = tFactory.newTransformer(new StreamSource(xsl));
      // 3. Use the Transformer to transform an XML Source and send the
      //    output to a Result object.

      //For testing
      //System.out.println("Transform Start: " + Long.toString(System.currentTimeMillis()));
      
      transformer.transform(new StreamSource(in),
                   new StreamResult(out));

      //For testing
      //System.out.println("Transform End: " + Long.toString(System.currentTimeMillis()));
      
    } catch (Throwable e ) {
        _logger.error("Error " + e.toString());
        e.printStackTrace(System.out);    
        throw new GDEMException("Error transforming XML " + e.toString());
    }
  }
  private void runFOPTransformation(InputStream in, String xsl, OutputStream out) throws GDEMException {

    try{
      Driver driver = new Driver();
      //Setup logging here: driver.setLogger(...
      driver.setRenderer(Driver.RENDER_PDF);

      //Setup the OutputStream for FOP
      driver.setOutputStream(out);

      //Make sure the XSL transformation's result is piped through to FOP
      Result res = new SAXResult(driver.getContentHandler());

      //Setup XML input
      Source src = new StreamSource(in);

      //Setup Transformer
      Source xsltSrc = new StreamSource(xsl);
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer(xsltSrc);

      //Start the transformation and rendering process
      transformer.transform(src, res); 
    } catch (Throwable e ) {
        _logger.error("Error " + e.toString());
        e.printStackTrace(System.out);    
        throw new GDEMException("Error transforming XML to PDF " + e.toString());
    }
  }
  /**
  * Saves the source file temporarily
  */
  /*
  private String saveSourceFile(String sourceURL) throws java.io.IOException {
      java.net.URL url = new java.net.URL( sourceURL);      
      InputStream is = url.openStream();

      String fileName=null;
      String tmpFileName=tmpFolder + "gdem_" + System.currentTimeMillis() + ".xml";
      //log("========= tempFile=" + tmpFileName);  
      File file =new File(tmpFileName);
      FileOutputStream fos=new FileOutputStream(file);
      
      int bufLen = 0;
      byte[] buf = new byte[1024];
      
      while ( (bufLen=is.read( buf ))!= -1 )
        fos.write(buf, 0, bufLen );
     
      fileName=tmpFileName;
      is.close();
      fos.flush(); fos.close();

      return fileName;
  } */

  private void log(String msg) {
    System.out.println("================================");
    System.out.println(msg);
    System.out.println("================================");    
  } 
  public static void main(String args[]) {
    try{
      ConversionService cs = new ConversionService();
      Hashtable h = cs.convert("http://cdr-ewn.eionet.eu.int/ee/eea/ewn3/envqrnu8a/bodies_IE.xml","gw_gc2htmltable_1637.xsl");
    }
    catch(Exception e ){
      System.out.println(e.toString());
    }
    
}

}