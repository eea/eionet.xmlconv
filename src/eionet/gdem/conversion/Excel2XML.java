package eionet.gdem.conversion;


import eionet.gdem.GDEMException;
import eionet.gdem.conversion.excel.*;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

import eionet.gdem.utils.Utils;
import java.io.PrintWriter;
import org.xml.sax.*;
import javax.xml.parsers.*;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.net.MalformedURLException;

/**
* This class is creating handlers for creating XML file from MS Excel
* called from ConversionService
* @author Enriko Käsper
*/public class Excel2XML 
{
  private static LoggerIF _logger;
  private static final String INSTANCE_SERVLET = "GetXmlInstance";
  private static final String SCHEMA_SERVLET = "GetSchema";
  public Excel2XML()
  {
    _logger = GDEMServices.getLogger();
  }
    public String convertDD_XML(String sIn, String sOut) throws GDEMException {
    try
    {     
        FileOutputStream outStream = new FileOutputStream(sOut);
        FileInputStream inStream = new FileInputStream(sIn);
        return convertDD_XML(inStream, outStream);

//        InputSource is = new InputSource(

    }
    catch(Exception e)
    {
       return "ErrorConversionHandler - couldn't save the Excel file: " + e.toString();
    }
  }
public String convertDD_XML(InputStream inStream, OutputStream outStream){
    
      if (inStream == null) return "Could not find InputStream";
      if (outStream == null) return "Could not find OutputStream";
      PrintWriter writer = null;
      try{
        ExcelReaderIF excel = ExcelUtils.getExcelReader();
        excel.initReader(inStream);
        String xml_schema = excel.getXMLSchema();
        
        if (xml_schema==null){
          throw new Exception("Excel file is not generated from Data Dictionary " +
                "or it has been modified later. Could not find XML Schema!");
        }
        String instance_url = getInstanceUrl(xml_schema);
        
        writer = new PrintWriter(outStream);
        DD_XMLInstance instance = new DD_XMLInstance(writer);
        DD_XMLInstanceHandler handler=new DD_XMLInstanceHandler(instance);
        
        SAXParserFactory spfact = SAXParserFactory.newInstance();
        SAXParser parser = spfact.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        spfact.setValidating(false);
        reader.setFeature("http://xml.org/sax/features/validation", false); 
        reader.setFeature("http://apache.org/xml/features/validation/schema", false);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);

        reader.setContentHandler(handler);
        reader.parse(instance_url);
        
        excel.readDocumentToInstance(instance);
        instance.flush();
        
        writer.flush();
      }
      catch (Exception e){
        return "Error generating XML file from Excel file: " + e.toString();
      }
	    finally{
	        try{
      				if (writer != null) writer.close();
	            if (inStream != null) inStream.close();
	            if (outStream != null) outStream.close();
	        }
	        catch(Exception e){}
	    }
      return "OK";
  }
  private String getInstanceUrl(String schema_url) throws GDEMException{
  
    try{
      URL SchemaURL = new URL(schema_url);
      
      int path_idx = schema_url.toLowerCase().indexOf(SCHEMA_SERVLET.toLowerCase());
      String path = schema_url.substring(0,path_idx);
      
      int id_idx = schema_url.indexOf("id=");
      String id = schema_url.substring(id_idx+3);
      if (id.indexOf("&")>-1)
        id = id.substring(0,id.indexOf("&"));
      
      String type = id.substring(0,3);
      id = id.substring(3);
      
      String instance_url = path + INSTANCE_SERVLET + "?id=" + id + "&type=" + type.toLowerCase();
      URL InstanceURL = new URL(instance_url);
      return instance_url;
    }
    catch(MalformedURLException e){
        throw new GDEMException("Error getting Instance file URL: " + 
          e.toString() + " - " + schema_url);      
    }
    catch(Exception e){
        throw new GDEMException("Error getting Instance file URL: " + 
          e.toString() + " - " + schema_url);      
    }
  }
  public static void main(String[] args){
    String excelFile = "E:\\Projects\\gdem\\exelToXML\\CDDA_Siteboundaries.xls";
    //String excelFile = "E:\\Projects\\gdem\\exelToXML\\Groundwater_GG_CCxxx.xls";
    String outFile = "E:\\Projects\\gdem\\exelToXml\\Instance2508_.xml";
    try{
      Excel2XML processor = new Excel2XML();
      processor.convertDD_XML(excelFile,outFile);  
    }
    catch(Exception e){
      System.out.println(e.toString());
    }
  }}