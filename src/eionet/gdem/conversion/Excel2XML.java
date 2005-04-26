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

package eionet.gdem.conversion;


import eionet.gdem.GDEMException;
import eionet.gdem.conversion.excel.*;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.MalformedURLException;

import org.xml.sax.*;
import javax.xml.parsers.*;

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
public String convertDD_XML(InputStream inStream, OutputStream outStream) throws GDEMException{
    
      if (inStream == null) return "Could not find InputStream";
      if (outStream == null) return "Could not find OutputStream";
      try{
        ExcelReaderIF excel = ExcelUtils.getExcelReader();
        excel.initReader(inStream);
        String xml_schema = excel.getXMLSchema();
        if (xml_schema==null){
          throw new Exception("Excel file is not generated from Data Dictionary " +
                "or it has been modified later. Could not find XML Schema!");
        }
        String instance_url = getInstanceUrl(xml_schema);
        
        DD_XMLInstance instance = new DD_XMLInstance();
        DD_XMLInstanceHandler handler=new DD_XMLInstanceHandler(instance);
        
        SAXParserFactory spfact = SAXParserFactory.newInstance();
        SAXParser parser = spfact.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        spfact.setValidating(false);
        spfact.setNamespaceAware(true);
        reader.setFeature("http://xml.org/sax/features/validation", false); 
        reader.setFeature("http://apache.org/xml/features/validation/schema", false);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);

        reader.setContentHandler(handler);
        reader.parse(instance_url);

        if (Utils.isNullStr(instance.getEncoding())){
          String enc_url = getEncodingFromStream(instance_url);
          if (!Utils.isNullStr(enc_url)) instance.setEncoding(enc_url);
        }
        excel.readDocumentToInstance(instance);
        instance.flush(outStream);
        
      }
      catch (Exception e){
        throw new GDEMException("Error generating XML file from Excel file: " + e.toString(), e);
      }
	    finally{
	        try{
	            if (inStream != null) inStream.close();
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
  //Reads the XML declaration from instance file
  // It is called only, when SAX coudn't read it
  protected String getEncodingFromStream(String str_url){
    BufferedReader br = null;
    try{
      URL url = new URL(str_url);
      //ins = new DataInputStream(url.openStream());
      br = new BufferedReader(new InputStreamReader(url.openStream()));
      String xml_decl = br.readLine();
      
      if (xml_decl==null) return null;
      if (!xml_decl.startsWith("<?xml version=") && !xml_decl.endsWith("?>")) return null;  
      int idx = xml_decl.indexOf("encoding=");
      if (idx==-1) return null;
      String start = xml_decl.substring(idx+10);
      int end_idx = start.indexOf("\"");
      if (end_idx==-1) return null;
      String enc = start.substring(0,end_idx);
      
      return enc;
    }
    catch(MalformedURLException e){
      _logger.debug("It is not url: " + str_url + "; " + e.toString());
    return null;
    }
    catch(IOException e){
      _logger.debug("could not read encoding from url: " + str_url + "; " + e.toString());
      return null;
  }
    catch(Exception e){
      return null;
      //couldn't read encoding
    }
    finally{
      try{
        if (br != null) br.close();
	    }
	    catch(IOException e){}
    }
  }
  public static void main(String[] args){
    String excelFile = "D:/Projects/gdemxf/doc/Monthly_ozone_Station_DD.xls";
    //String excelFile = "E:\\Projects\\gdem\\exelToXML\\Groundwater_GG_CCxxx.xls";
    String outFile = "D:\\Projects\\gdemxf\\doc\\Instance1925_.xml";
    try{
      Excel2XML processor = new Excel2XML();
      processor.convertDD_XML(excelFile,outFile);  
    }
    catch(Exception e){
      System.out.println(e.toString());
    }
  }
}