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
import java.io.*;

//import org.apache.log4j.Category;
//import org.apache.log4j.Priority;

//import java.net.URL;
import java.net.MalformedURLException;

import java.util.Hashtable;
import java.util.Vector;

import eionet.gdem.services.*;
import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.InputFile;

import eionet.gdem.services.LoggerIF;

import java.util.HashMap;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.fop.apps.Driver;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;

import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.dcm.business.DDServiceClient;
import eionet.gdem.dcm.xsl.Conversion;
import eionet.gdem.dcm.xsl.ConversionDto;
import eionet.gdem.services.DbModuleIF;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Utils;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;






import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;


import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Driver;






import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.dcm.business.DDServiceClient;
import eionet.gdem.dcm.xsl.Conversion;
import eionet.gdem.dcm.xsl.ConversionDto;
import eionet.gdem.services.DbModuleIF;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Utils;



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
	v = new Vector();
	
	
	if (schema!= null && schema.startsWith(Properties.ddURL)) {

		// schema is from DD
		// parse tbl id
		// check tbl id
		//
		
		String tblId = schema.substring(schema.indexOf("id=TBL") + 6,
				schema.length());
		System.out.println(tblId);
		List convs =  Conversion.getConversions();
		
		for (int i = 0; i < convs.size(); i++){
			Hashtable h = new Hashtable();
			h.put("convert_id", "DD_TBL" + tblId + "_CONV" + ((ConversionDto) convs.get(i)).getConvId());
			h.put("xsl", Properties.gdemURL + "GetStylesheet?id=" + tblId + "&conv=" + ((ConversionDto) convs.get(i)).getConvId());
			h.put("description", ((ConversionDto) convs.get(i)).getDescription());
			h.put("content_type_out", ((ConversionDto) convs.get(i)).getResultType());
			h.put("xml_schema", schema);
			v.add(h);
		}			
		
	}
	//
	if (schema == null){ 
		List ddTables = DDServiceClient.getDDTables();
		List convs =  Conversion.getConversions();
		
		for (int i=0; i<ddTables.size(); i++){
			Hashtable schemaDD = (Hashtable)ddTables.get(i);
			String tblId = (String)schemaDD.get("tblId");
			String schemaUrl = Properties.ddURL + "GetSchema?id=TBL" + tblId;

			for (int j = 0; j < convs.size(); j++){
				Hashtable h = new Hashtable();
				h.put("convert_id", "DD_TBL" + tblId + "_CONV" + ((ConversionDto) convs.get(j)).getConvId());
				h.put("xsl", Properties.gdemURL + "GetStylesheet?id=" + tblId + "&conv=" + ((ConversionDto) convs.get(j)).getConvId());
				h.put("description", ((ConversionDto) convs.get(j)).getDescription());
				h.put("content_type_out", ((ConversionDto) convs.get(j)).getResultType());
				h.put("xml_schema", schemaUrl);
				v.add(h);
			}			
			
		}
	}
	
	
	//retriving handocoded transformations
	try {
		Vector vDb = db.listConversions(schema);
		
		for (int i = 0; i < vDb.size(); i++){
			v.add(vDb.get(i));
		}			
		
	} catch (Exception e) {
		throw new GDEMException("Error getting data from the DB "
				+ e.toString(), e);
	}

	

    return v;
    

  }

  /**
  * Converts the XML file to a specific format
  */
  public Hashtable convert (String sourceURL, String convertId) throws GDEMException {
    return convert(sourceURL, convertId, null);
  }
	public Hashtable convert(String sourceURL, String convertId,HttpServletResponse res) throws GDEMException {
		_logger.error("sourceURL=" + sourceURL+"convertId=" + convertId+"res=" + res);
		if (convertId.startsWith("DD")) {
			return convertDDTable(sourceURL, convertId,res);
		}else
		{
		
		
		Hashtable h = new Hashtable();
		String sourceFile = null;
		String xslFile = null;
		String outputFileName = null;
		// String cnvTypeOut=null;
		InputFile src = null;

		try {
			src = new InputFile(sourceURL);
			if (db == null)
				db = GDEMServices.getDbModule();

			try {
				HashMap styleSheetData = db.getStylesheetInfo(convertId);

				if (styleSheetData == null)
					throw new GDEMException(
							"No stylesheet info for convertID= " + convertId);
				xslFile = xslFolder + (String) styleSheetData.get("xsl");
				cnvTypeOut = (String) styleSheetData.get("content_type_out");

				Hashtable convType = db.getConvType(cnvTypeOut);

				if (convType != null) {
					try {
						cnvContentType = (String) convType.get("content_type");
						cnvFileExt = (String) convType.get("file_ext");
					} catch (Exception e) {
						// Take no action, use default params
					}
				}
				if (cnvContentType == null)
					cnvContentType = "text/plain";
				if (cnvFileExt == null)
					cnvFileExt = "txt";

			} catch (Exception e) {
				throw new GDEMException(
						"Error getting stylesheet info from repository for "
								+ convertId, e);
			}
			if (res != null) {
				try {
					this.result = res.getOutputStream();
					res.setContentType(cnvContentType);
				} catch (IOException e) {
					throw new GDEMException(
							"Error getting response outputstream "
									+ e.toString(), e);
				}
			}
			if (cnvTypeOut.equals("HTML")) {
				outputFileName = convertHTML(src.getSrcInputStream(), xslFile);
			} else if (cnvTypeOut.equals("PDF")) {
				outputFileName = convertPDF(src.getSrcInputStream(), xslFile);
			} else if (cnvTypeOut.equals("EXCEL")) {
				outputFileName = convertExcel(src.getSrcInputStream(), xslFile);
			} else if (cnvTypeOut.equals("XML")) {
				outputFileName = convertXML(src.getSrcInputStream(), xslFile);
			} else {
				outputFileName = convertTextOutput(src.getSrcInputStream(),
						xslFile);
			}
			// else
			// throw new GDEMException("Unknown conversion type or converter not
			// implemented: " + cnvTypeOut);

		} catch (MalformedURLException mfe) {
			throw new GDEMException("Bad URL : " + mfe.toString(), mfe);
		} catch (IOException ioe) {
			throw new GDEMException("Error opening URL " + ioe.toString(), ioe);
		} catch (Exception e) {



			throw new GDEMException("Error converting: " + e.toString(), e);
		} finally {
			try {
				if (src != null)
					src.close();
			} catch (Exception e) {
			}
		}


		h.put("content-type", cnvContentType);
		if (res != null) {
			try {
				result.close();
			} catch (IOException e) {
				// throw new GDEMException("Error closing result
				// ResponseOutputStream " + convertId);
			}
			return h;
		}
		// log("========= going to bytes " + htmlFileName);

		byte[] file = fileToBytes(outputFileName);
		// log("========= bytes ok");

		h.put("content", file);
		try {
			// Utils.deleteFile(sourceFile);
			// deleteFile(htmlFileName);
			Utils.deleteFile(outputFileName);
		} catch (Exception e) {

			_logger.error("Couldn't delete the result file: " + outputFileName);
		}

		return h;
		}
	}

	public Hashtable convertDDTable(String sourceURL, String convertId,HttpServletResponse res) throws GDEMException {
		Hashtable h = new Hashtable();
		String sourceFile = null;
		String xslFile = null;
		String outputFileName = null;
		// String cnvTypeOut=null;
		InputFile src = null;
		InputFile xsl = null;
		String tblId = "";
		String convId = "";
		String xslURL;

		System.out.println("sourceURL="+sourceURL);
		System.out.println("convertId="+convertId);
		
		// prase idtable and id conversion
		if (convertId.startsWith("DD")) {
			tblId = convertId.substring(6, convertId.indexOf("_CONV"));
			convId = convertId.substring(convertId.indexOf("_CONV") + 5,convertId.length());
		}

		//xslFile = "http://rubi:8080/gdem/GetStylesheet?id=" + tblId + "&conv=" + convId;
		xslFile = Properties.gdemURL+"GetStylesheet?id=" + tblId + "&conv=" + convId;

		
		
		// pozvati konverziju za sourceURL i xslURL

		try {
			//src = new InputFile(sourceURL);
			src = new InputFile(sourceURL);
			//xsl = new InputFile(xslURL);
			if (db == null)
				db = GDEMServices.getDbModule();

			try {
				//HashMap styleSheetData = db.getStylesheetInfo(convertId);

				//if (styleSheetData == null)
				//	throw new GDEMException(
				//			"No stylesheet info for convertID= " + convertId);
				//xslFile = xslFolder + (String) styleSheetData.get("xsl");
				//cnvTypeOut = (String) styleSheetData.get("content_type_out");

				ConversionDto conv =  Conversion.getConversionById(convId);
				cnvTypeOut = conv.getResultType();
				
				Hashtable convType = db.getConvType(cnvTypeOut);

				if (convType != null) {
					try {
						cnvContentType = (String) convType.get("content_type");
						cnvFileExt = (String) convType.get("file_ext");
					} catch (Exception e) {
						// Take no action, use default params
					}
				}
				
				if (cnvContentType == null)
					cnvContentType = "text/plain";
				if (cnvFileExt == null)
					cnvFileExt = "txt";
					
				
			} catch (Exception e) {
				throw new GDEMException(
						"Error getting stylesheet info from repository for "
								+ convertId, e);
			}
			if (res != null) {
				try {
					this.result = res.getOutputStream();
					res.setContentType(cnvContentType);
				} catch (IOException e) {
					throw new GDEMException(
							"Error getting response outputstream "
									+ e.toString(), e);
				}
			}
			if (cnvTypeOut.equals("HTML")) {
				outputFileName = convertHTML(src.getSrcInputStream(), xslFile);
			} else if (cnvTypeOut.equals("PDF")) {
				outputFileName = convertPDF(src.getSrcInputStream(), xslFile);
			} else if (cnvTypeOut.equals("EXCEL")) {
				outputFileName = convertExcel(src.getSrcInputStream(), xslFile);
			} else if (cnvTypeOut.equals("XML")) {
				outputFileName = convertXML(src.getSrcInputStream(), xslFile);
			} else {
				outputFileName = convertTextOutput(src.getSrcInputStream(),
						xslFile);
			}
			// else
			// throw new GDEMException("Unknown conversion type or converter not
			// implemented: " + cnvTypeOut);

		} catch (MalformedURLException mfe) {
			throw new GDEMException("Bad URL : " + mfe.toString(), mfe);
		} catch (IOException ioe) {
			throw new GDEMException("Error opening URL " + ioe.toString(), ioe);
		} catch (Exception e) {
			throw new GDEMException("Error converting: " + e.toString(), e);
		} finally {
			try {
				if (src != null)
					src.close();
			} catch (Exception e) {
			}
		}

		h.put("content-type", cnvContentType);
		if (res != null) {
			try {
				result.close();
			} catch (IOException e) {
				// throw new GDEMException("Error closing result
				// ResponseOutputStream " + convertId);
			}
			return h;
		}
		// log("========= going to bytes " + htmlFileName);

		byte[] file = fileToBytes(outputFileName);
		// log("========= bytes ok");

		h.put("content", file);
		try {
			// Utils.deleteFile(sourceFile);
			// deleteFile(htmlFileName);
			Utils.deleteFile(outputFileName);
		} catch (Exception e) {
			_logger.error("Couldn't delete the result file: " + outputFileName);
		}

		return h;

	}

    
  public Hashtable makeDynamicXSL(String sourceURL, String xslFile,HttpServletResponse res) throws GDEMException {
		Hashtable h = new Hashtable();
		String sourceFile = null;
		//String xslFile = null;
		String outputFileName = null;
		// String cnvTypeOut=null;
		InputFile src = null;
		InputFile xsl = null;
		String tblId = "";
		String convId = "";
		String xslURL;

		//xslFile = convertId;		

		try {
			//src = new InputFile(sourceURL);
			src = new InputFile(sourceURL);
			//xsl = new InputFile(xslURL);
			if (db == null)
				db = GDEMServices.getDbModule();

			try {
				//HashMap styleSheetData = db.getStylesheetInfo(convertId);

				//if (styleSheetData == null)
				//	throw new GDEMException(
				//			"No stylesheet info for convertID= " + convertId);
				//xslFile = xslFolder + (String) styleSheetData.get("xsl");
				//cnvTypeOut = (String) styleSheetData.get("content_type_out");

				cnvTypeOut="XML";
				
				Hashtable convType = db.getConvType(cnvTypeOut);

				if (convType != null) {
					try {
						cnvContentType = (String) convType.get("content_type");
						cnvFileExt = (String) convType.get("file_ext");
					} catch (Exception e) {
						// Take no action, use default params
					}
				}
				
				if (cnvContentType == null)
					cnvContentType = "text/plain";
				if (cnvFileExt == null)
					cnvFileExt = "txt";
					
				
			} catch (Exception e) {
				throw new GDEMException(
						"Error getting stylesheet info from repository for "
								+ xslFile, e);
			}
			if (res != null) {
				try {
					this.result = res.getOutputStream();
					res.setContentType(cnvContentType);
				} catch (IOException e) {
					throw new GDEMException(
							"Error getting response outputstream "
									+ e.toString(), e);
				}
			}
			if (cnvTypeOut.equals("HTML")) {
				outputFileName = convertHTML(src.getSrcInputStream(), xslFile);
			} else if (cnvTypeOut.equals("PDF")) {
				outputFileName = convertPDF(src.getSrcInputStream(), xslFile);
			} else if (cnvTypeOut.equals("EXCEL")) {
				outputFileName = convertExcel(src.getSrcInputStream(), xslFile);
			} else if (cnvTypeOut.equals("XML")) {
				outputFileName = convertXML(src.getSrcInputStream(), xslFile);
			} else {
				outputFileName = convertTextOutput(src.getSrcInputStream(),
						xslFile);
			}
			// else
			// throw new GDEMException("Unknown conversion type or converter not
			// implemented: " + cnvTypeOut);

		} catch (MalformedURLException mfe) {
			throw new GDEMException("Bad URL : " + mfe.toString(), mfe);
		} catch (IOException ioe) {
			throw new GDEMException("Error opening URL " + ioe.toString(), ioe);
		} catch (Exception e) {
			throw new GDEMException("Error converting: " + e.toString(), e);
		} finally {
			try {
				if (src != null)
					src.close();
			} catch (Exception e) {
			}
		}

		h.put("content-type", cnvContentType);
		if (res != null) {
			try {
				result.close();
			} catch (IOException e) {
				// throw new GDEMException("Error closing result
				// ResponseOutputStream " + convertId);
			}
			return h;
		}
		// log("========= going to bytes " + htmlFileName);

		byte[] file = fileToBytes(outputFileName);
		// log("========= bytes ok");

		h.put("content", file);
		try {
			// Utils.deleteFile(sourceFile);
			// deleteFile(htmlFileName);
			Utils.deleteFile(outputFileName);
		} catch (Exception e) {
			_logger.error("Couldn't delete the result file: " + outputFileName);
		}

		return h;

	}

  
  

  /**
  * Request from XML/RPC client
  * Converts DataDictionary MS Excel file to XML
  * @param String url: URL of the srouce Excel file
  * @return Vector result: error_code, xml_url, error_message
  */
  public Vector convertDD_XML(String sourceURL) throws GDEMException {
    return convertDD_XML(sourceURL, null);
  }
  /**
  * Request from WebBrowser
  * Converts DataDictionary MS Excel file to XML
  * @param String url: URL of the srouce Excel file
  * @param HttpServletResponse res: Servlet response
  * @return Vector result: error_code, xml_url, error_message
  */
  public Vector convertDD_XML(String sourceURL, HttpServletResponse res) throws GDEMException {

    InputFile src= null;
    Vector v_result = new Vector();
    String str_result = null;
    String outFileName=tmpFolder + "gdem_" + System.currentTimeMillis() + ".xml";
    String error_mess = null;
    
    try {
    
      src = new InputFile(sourceURL);
      if (res!=null){  
        try {
          result = res.getOutputStream();
        } catch (IOException e ) {
          throw new GDEMException("Error getting response outputstream " + e.toString(), e);
        }
      }
      if (result==null)
        result = new FileOutputStream(outFileName);

      Excel2XML converter = new Excel2XML();
      str_result = converter.convertDD_XML(src.getSrcInputStream(), result);
    }
    catch (MalformedURLException mfe ) {
      if (res!=null){
        throw new GDEMException("Bad URL : " + mfe.toString(), mfe);
      }
      else{
        error_mess = "Bad URL : " + mfe.toString();
      }
    } 
    catch (IOException ioe ) {
      if (res!=null){
        throw new GDEMException("Error opening URL " + ioe.toString(), ioe);
      }
      else{
        error_mess = "Error opening URL " + ioe.toString();
      }
    } 
    catch (Exception e ) {
      if (res!=null){

        throw new GDEMException(e.toString(), e);
      }
      else{
        error_mess = e.toString();
      }
    }
    finally{
      try{
        if (src!=null) src.close();
      //  if (result!=null) result.close();
	    }
	    catch(Exception e){}
    }
    
    if (res!=null){
      try{
        res.setContentType("text/xml");
        result.close();
      } catch (IOException e ) {
        throw new GDEMException("Error closing result ResponseOutputStream ", e);
      }
      return v_result;
    }
//Creates response Vector    
    int result_code = 1;
    if (!Utils.isNullStr(str_result)){
      if (str_result.equals("OK"))
        result_code=0;
    }
    byte[] file = Utils.fileToBytes(outFileName);
    
    v_result.add(String.valueOf(result_code));
    if (result_code==0)
      v_result.add(file);
    else
      v_result.add(error_mess);

    try{
      Utils.deleteFile(outFileName);
    }
    catch(Exception e){
      _logger.error("Couldn't delete the result file");
    }
    
    
    return v_result;  
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
       throw new GDEMException("File not found " + fileName, fne);
     } catch (Exception e) {
       throw new GDEMException("Exception " + e.toString(), e);
     }    
       return baos.toByteArray();    
   }
  
  
  
  /**
   * Request from XML/RPC client
   * Converts DataDictionary MS Excel sheets to different XML files, where one xml file is dataset table.
   * @param String url: URL of the srouce Excel file
   * @return Vector result: error_code, xml_url, error_message
   */
   public Vector convertDD_XML_split(String sourceURL, String sheet_param) throws GDEMException {
     return convertDD_XML_split(sourceURL, sheet_param, null);
   }
   /**
    * Request from WebBrowser
   * Converts DataDictionary MS Excel sheets to different XML files, where one xml file is dataset table.
    * @param String url: URL of the srouce Excel file
    * @param HttpServletResponse res: Servlet response
    * @return Vector result: error_code, xml_url, error_message
    */
    public Vector convertDD_XML_split(String sourceURL, String sheet_param, HttpServletResponse res) throws GDEMException {

      InputFile src= null;
      String error_mess = null;
      Vector v_result = null;
      
      try {
      
        src = new InputFile(sourceURL);
        if (res!=null){  
          try {
            result = res.getOutputStream();
          } catch (IOException e ) {
            throw new GDEMException("Error getting response outputstream " + e.toString(), e);
          }
        }

        Excel2XML converter = new Excel2XML();
        v_result = converter.convertDD_XML_split(src.getSrcInputStream(), result, sheet_param);
      }
      catch (MalformedURLException mfe ) {
        if (res!=null){
          throw new GDEMException("Bad URL : " + mfe.toString(), mfe);
        }
        else{
          error_mess = "Bad URL : " + mfe.toString();
        }
      } 
      catch (IOException ioe ) {
        if (res!=null){
          throw new GDEMException("Error opening URL " + ioe.toString(), ioe);
        }
        else{
          error_mess = "Error opening URL " + ioe.toString();
        }
      } 
      catch (Exception e ) {
        if (res!=null){

          throw new GDEMException(e.toString(), e);
        }
        else{
          error_mess = e.toString();
        }
      }
      finally{
        try{
          if (src!=null) src.close();
        //  if (result!=null) result.close();
  	    }
  	    catch(Exception e){}
      }
      if (res!=null){
        try{
          res.setContentType("text/xml");
          result.close();
        } catch (IOException e ) {
          throw new GDEMException("Error closing result ResponseOutputStream ", e);
        }
        return v_result;
      }
//  Creates response Vector    
      
      if (Utils.isNullVector(v_result) && !Utils.isNullStr(error_mess)){
        v_result.add("1");
        v_result.add(error_mess);
      }
      
      
      return v_result;  
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
          throw new GDEMException("Error creating PDF output file " + e.toString(), e);
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
          throw new GDEMException("Error creating HTML output file " + e.toString(), e);
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
        _logger.error("Couldn't delete the result file: " + xmlFile);
      }

    } catch (FileNotFoundException e ) {
      _logger.error("Error " + e.toString());
        throw new GDEMException("Error transforming Excel " + e.toString(), e);
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
          throw new GDEMException("Error creating XML output file " + e.toString(), e);
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
          throw new GDEMException("Error creating " + cnvTypeOut + " output file with Xalan:" + e.toString(), e);
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
    }  
    catch (TransformerConfigurationException tce ) {
        throw new GDEMException("Error transforming XML - incorrect stylesheet file: " + tce.toString(), tce);
        //throw new GDEMException(e);
    }
    catch (TransformerException tfe ) {
        throw new GDEMException("Error transforming XML - it's not probably well-formed xml file: " + tfe.toString(), tfe);
        //throw new GDEMException(e);
    }
    catch (Throwable e ) {
        _logger.error("Error " + e.toString());
        e.printStackTrace(System.out);    
        throw new GDEMException("Error transforming XML " + e.toString());
        //throw new GDEMException(e);
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
  
  public ArrayList getXMLSchemas() throws GDEMException {
	  Vector conv = listConversions();
	  ArrayList schemas = new ArrayList();
	  
	  for (int i = 0; i < conv.size(); i++){
		  Hashtable schema = (Hashtable)conv.get(i);
		  //System.out.println( i + " - " + schema.get("xml_schema") );		  
		  if(!schemas.contains(schema.get("xml_schema"))){
			  schemas.add(schema.get("xml_schema"));
		  }
	  }
	  
      return schemas;
  }
  
  public boolean existsXMLSchema(String xmlSchema) throws GDEMException {	  	 	  
	  ArrayList schemas = getXMLSchemas();	  
	  return schemas.contains(xmlSchema);
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