

package eionet.gdem;
import java.io.*;

//import org.apache.log4j.Category;
//import org.apache.log4j.Priority;

import java.net.URL;

import java.util.Hashtable;
import java.util.Vector;
import java.util.ResourceBundle;
import java.util.MissingResourceException;


/**
* Container for different conversions
* Can be used through XML/RPC
* @author Kaido Laine
*/

public class ConversionService {

  /*
  * FIXME!! Properties to be held in props'file or XML
  */

  private String xslFolder;
  private String tmpFolder;
  
  //private static final String xslFolder="C:/einrc/webs/gdem/xsl/";
  //private static final String tmpFolder="C:/einrc/webs/gdem/tmp/";
  
  //Category logger;

  public ConversionService()  {
      xslFolder=Utils.xslFolder; //props.getString("xsl.folder");
      tmpFolder=Utils.tmpFolder;  //props.getString("tmp.folder");
  }

  /**
  * List all possible conversions for this namespace
  * This is a dummy implementation for Sofia demo
  */

  public Vector listConversions(String schema) {
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
  }

  /**
  * Converts the XML file to a specific format
  * TODO MAppings between conversion ids and XSL's
  */
  public Hashtable convert (String sourceURL, String convertId) throws GDEMException {
    Hashtable h = new Hashtable();
    
    String sourceFile=null;
    String xslFile=null;
    String htmlFileName=null;
    
    try {
      //sourceFile=saveSourceFile(sourceURL);
      sourceFile=Utils.saveSrcFile(sourceURL);
    } catch (IOException  ioe ) {
      throw new GDEMException("Error reading from URL and saving tmp file: " + sourceURL + "\n"
        + ioe.toString());
    }

    xslFile=xslFolder + convertId + ".xsl";

    //!!! QUICK FIX!! CORRECT ME IF MAPPINGS ARE OK !!
    if (convertId.indexOf("html") != -1) {
      htmlFileName=convertHTML(sourceFile, xslFile);
      //htmlFileName=convertHTML(sourceURL, xslFile);      
      h.put("content-type", "text/html");
    }
    else if (convertId.indexOf("pdf") != -1) {
      htmlFileName=convertPDF(sourceFile, xslFile);
      h.put("content-type", "application/pdf");
    }
    else if (convertId.indexOf("excel") != -1) {
      htmlFileName=convertExcel(sourceFile, xslFile);
      h.put("content-type", "application/vnd.ms-excel");
    }
    else if (convertId.indexOf("xml") != -1) {
      htmlFileName=convertXML(sourceFile, xslFile);
      h.put("content-type", "text/xml");
    }

    //log("========= going to bytes " + htmlFileName);
    byte[] file = fileToBytes(htmlFileName);
    //log("========= bytes ok");

    h.put("content", file);

    Utils.deleteFile(sourceFile);
    //deleteFile(htmlFileName);
    
    
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
      String args[]={"-xml", source, "-xsl", xslt, "-pdf", pdfFile  };
      org.apache.fop.apps.Fop.main(args);
        
      
      return pdfFile;
  }



  private String convertHTML(String source, String xslt) throws GDEMException {

      String htmlFile=tmpFolder + "gdem_" + System.currentTimeMillis() + ".html";
      String args[]={"-in", source, "-xsl", xslt, "-out", htmlFile  };
      //[-xsl stylesheet] [-o dest] file1.xml file2.xml ...       
      //String args[]={"-xsl", xslt, "-o", htmlFile, source  };
    try {

    org.apache.xalan.xslt.Process.main(args);
    //log("conversion done");

    
    } catch (Throwable e ) {
      log("Error " + e.toString());
      e.printStackTrace(System.out);    
      throw new GDEMException("Error transforming HTML " + e.toString());
    }
      //System.out.println("======= html OK");
      return htmlFile;
  }

  private String convertExcel(String source, String xslt) throws GDEMException {

      String xmlFile=tmpFolder + "gdem_out" + System.currentTimeMillis() + ".xml";
      String excelFile=tmpFolder + "gdem_" + System.currentTimeMillis() + ".xls";
      String args[]={"-in", source, "-xsl", xslt, "-out", xmlFile  };
      //String excel_args[]={"-in", xmlFile, "-out", excelFile  };
      //[-xsl stylesheet] [-o dest] file1.xml file2.xml ...       
      //String args[]={"-xsl", xslt, "-o", htmlFile, source  };
    try {

      org.apache.xalan.xslt.Process.main(args);

      ExcelProcessor ep = new ExcelProcessor();
      ep.makeExcel(xmlFile, excelFile);

    
    } catch (Throwable e ) {
      log("Error " + e.toString());
      e.printStackTrace(System.out);    
      throw new GDEMException("Error transforming Excel " + e.toString());
    }


    
      //System.out.println("======= html OK");
      return excelFile;
  }
  
  private String convertXML(String source, String xslt) throws GDEMException {

      String xmlFile=tmpFolder + "gdem_out" + System.currentTimeMillis() + ".xml";
      String args[]={"-in", source, "-xsl", xslt, "-out", xmlFile  };
    try {

    org.apache.xalan.xslt.Process.main(args);
   //log("conversion done");

    
    } catch (Throwable e ) {
      log("Error " + e.toString());
      e.printStackTrace(System.out);    
      throw new GDEMException("Error transforming XML " + e.toString());
    }
      //System.out.println("======= html OK");
      return xmlFile;
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
 
}