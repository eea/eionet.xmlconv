

package eionet.gdem;
import java.io.*;

//import org.apache.log4j.Category;
//import org.apache.log4j.Priority;

import java.net.URL;

import java.util.Hashtable;
import java.util.Vector;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import eionet.gdem.db.*;
import java.util.HashMap;

import org.apache.avalon.framework.logger.Logger; 
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.fop.apps.Driver;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;



/**
* Container for different conversions
* for being used through XML/RPC
* @author Kaido Laine
*/

public class ConversionService {

  private String xslFolder;
  private String tmpFolder;

  private DbModuleIF db;
  
  private HashMap convTypes;
  
  //private static final String xslFolder="C:/einrc/webs/gdem/xsl/";
  //private static final String tmpFolder="C:/einrc/webs/gdem/tmp/";
  
  //Category logger;


  /***
  * Constant values for HttpResponse content types
  */
  private void initCnvTypes() {
    convTypes=new HashMap();

    convTypes.put("EXCEL", "application/vnd.ms-excel");
    convTypes.put("PDF", "application/pdf");
    convTypes.put("HTML", "text/html");
    convTypes.put("XML", "text/xml");    
  }

  public ConversionService()  {
      xslFolder=Utils.xslFolder; //props.getString("xsl.folder");
      tmpFolder=Utils.tmpFolder;  //props.getString("tmp.folder");

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
      db = DbUtils.getDbModule();

    Vector v = null;
    try {
      v=db.listConversions(schema);
    } catch (Exception e ) {
      throw new GDEMException("Error getting data from the DB " + e.toString());
    }

    return v;
    
    /*
    Vector v = new Vector();

    if ( schema.equals("http://roddev.eionet.eu.int/waterdemo/water_measurements.xsd")) {
      Hashtable h = new Hashtable();
      h.put("xsl", "simpletablehtml");
      h.put("description", "Simple table");
      v.add(h);

      h= new Hashtable();      
      h.put("xsl", "averagephhtml");
      h.put("description", "Average Ph");

      v.add(h);      
    }
    else if ( schema.equals("http://roddev.eionet.eu.int/eper/eper_examples.xsd")) {

      Hashtable h = new Hashtable();
      h.put("xsl", "eper2html");
      h.put("description", "EPER html example");
      v.add(h);

      h= new Hashtable();
      h.put("xsl", "eper2pdf");
      h.put("description", "EPER pdf example");
      v.add(h);      
   
    }
    
    return v;
  */
  }

  /**
  * Converts the XML file to a specific format
  * TODO MAppings between conversion ids and XSL's
  */
  public Hashtable convert (String sourceURL, String convertId) throws GDEMException {
    Hashtable h = new Hashtable();
    
    String sourceFile=null;
    String xslFile=null;
    String outputFileName=null;
    String cnvTypeOut=null;
    
    try {
      //sourceFile=saveSourceFile(sourceURL);
      sourceFile=Utils.saveSrcFile(sourceURL);
    } catch (IOException  ioe ) {
      throw new GDEMException("Error reading from URL and saving tmp file: " + sourceURL + "\n"
        + ioe.toString());
    }
    
    //xslFile=xslFolder + convertId + ".xsl";
    if (db==null)
      db = DbUtils.getDbModule();

    try {
      HashMap styleSheetData=db.getStylesheetInfo(convertId);

      if (styleSheetData==null)
        throw new GDEMException("No stylesheet info for convertID= " + convertId);

      xslFile= xslFolder + (String)styleSheetData.get("xsl");
      cnvTypeOut= (String)styleSheetData.get("content_type_out");
      
      
              
    } catch (Exception e ) {
      throw new GDEMException("Error getting stylesheet info from repository for " + convertId);
    }
        
    
    //!!! QUICK FIX!! CORRECT ME IF MAPPINGS ARE OK !!
    //if (convertId.indexOf("html") != -1) {
    if (cnvTypeOut.equals("HTML")){
      outputFileName=convertHTML(sourceFile, xslFile);
      //htmlFileName=convertHTML(sourceURL, xslFile);      
      //h.put("content-type", "text/html");
    }
    else if (cnvTypeOut.equals("PDF")){
      outputFileName=convertPDF(sourceFile, xslFile);
      //h.put("content-type", "application/pdf");
    }
    else if (cnvTypeOut.equals("EXCEL")){
      outputFileName=convertExcel(sourceFile, xslFile);
      //h.put("content-type", "application/vnd.ms-excel");
    }
    else  if (cnvTypeOut.equals("XML")){
      outputFileName=convertXML(sourceFile, xslFile);
      //h.put("content-type", "text/xml");
    }
    else
      throw new GDEMException("Unknown conversion type or converter not  implemented: " + cnvTypeOut);


    h.put("content-type", (String)convTypes.get(cnvTypeOut));

    //log("========= going to bytes " + htmlFileName);
    byte[] file = fileToBytes(outputFileName);
    //log("========= bytes ok");

    h.put("content", file);

    Utils.deleteFile(sourceFile);
    //deleteFile(htmlFileName);
    Utils.deleteFile(outputFileName);
    
    
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
  private String convertPDF(String source, String xslt) throws GDEMException {

      String pdfFile=tmpFolder + "gdem_" + System.currentTimeMillis() + ".pdf";
      //String args[]={"-xml", source, "-xsl", xslt, "-pdf", pdfFile  };
      //org.apache.fop.apps.Fop.main(args);
      runFOPTransformation(source, xslt, pdfFile);
        
      
      return pdfFile;
  }



  private String convertHTML(String source, String xslt) throws GDEMException {

      String htmlFile=tmpFolder + "gdem_" + System.currentTimeMillis() + ".html";
      //String args[]={"-in", source, "-xsl", xslt, "-out", htmlFile  };
      //[-xsl stylesheet] [-o dest] file1.xml file2.xml ...       
      //String args[]={"-xsl", xslt, "-o", htmlFile, source  };

      runXalanTransformation(source, xslt, htmlFile);
      //org.apache.xalan.xslt.Process.main(args);
      //log("conversion done");

    
      //System.out.println("======= html OK");
      return htmlFile;
  }

  private String convertExcel(String source, String xslt) throws GDEMException {

      String xmlFile=tmpFolder + "gdem_out" + System.currentTimeMillis() + ".xml";
      String excelFile=tmpFolder + "gdem_" + System.currentTimeMillis() + ".xls";
      //String args[]={"-in", source, "-xsl", xslt, "-out", xmlFile  };
      //String excel_args[]={"-in", xmlFile, "-out", excelFile  };
      //[-xsl stylesheet] [-o dest] file1.xml file2.xml ...       
      //String args[]={"-xsl", xslt, "-o", htmlFile, source  };
    try {

      runXalanTransformation(source, xslt, xmlFile);
      //org.apache.xalan.xslt.Process.main(args);

      ExcelProcessor ep = new ExcelProcessor();
      ep.makeExcel(xmlFile, excelFile);

      Utils.deleteFile(xmlFile);

    } catch (Exception e ) {
      log("Error " + e.toString());
      e.printStackTrace(System.out);    
      throw new GDEMException("Error transforming Excel " + e.toString());
    }


    
      //System.out.println("======= html OK");
      return excelFile;
  }
  
  private String convertXML(String source, String xslt) throws GDEMException {

      String xmlFile=tmpFolder + "gdem_out" + System.currentTimeMillis() + ".xml";
      //String args[]={"-in", source, "-xsl", xslt, "-out", xmlFile  };
        runXalanTransformation(source, xslt, xmlFile);
        //org.apache.xalan.xslt.Process.main(args);
        //log("conversion done");
  
      //System.out.println("======= html OK");
      return xmlFile;
  }
  private void runXalanTransformation(String in, String xsl, String out) throws GDEMException {
    try{
      // 1. Instantiate a TransformerFactory.
      TransformerFactory tFactory = TransformerFactory.newInstance();

      // 2. Use the TransformerFactory to process the stylesheet Source and
      //    generate a Transformer.
      Transformer transformer = tFactory.newTransformer(new StreamSource(xsl));

      // 3. Use the Transformer to transform an XML Source and send the
      //    output to a Result object.
      transformer.transform(new StreamSource(in), 
                   new StreamResult( new FileOutputStream(out)));
                   
    } catch (Throwable e ) {
        log("Error " + e.toString());
        e.printStackTrace(System.out);    
        throw new GDEMException("Error transforming XML " + e.toString());
    }
  }
  private void runFOPTransformation(String in, String xsl, String out) throws GDEMException {

    try{
      Driver driver = new Driver();
      //Setup logging here: driver.setLogger(...
      driver.setRenderer(Driver.RENDER_PDF);

      //Setup the OutputStream for FOP
      driver.setOutputStream(new java.io.FileOutputStream(out));

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
        log("Error " + e.toString());
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
    Utils.log(msg);
  }


/*  public static void main(String ar[]) {
    try {
      ConversionService c = new ConversionService();
      Utils.log("x=" + c.listConversions());    
      //Utils.log("x=" + db.
      DbModuleIF d = DbUtils.getDbModule();

      //Utils.log("x=" + d.addStylesheet("xxx", "XML", "/tmp/y-file", null));

      //d.removeStyleSheet("3");
    } catch (Exception e ) {
      Utils.log("error " + e.toString());
    }
  } */

}