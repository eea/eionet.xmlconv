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
 * Created on 28.04.2006
 */

package eionet.gdem.conversion;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.conversion.excel.DD_XMLInstance;
import eionet.gdem.conversion.excel.DD_XMLInstanceHandler;
import eionet.gdem.conversion.excel.ExcelUtils;
import eionet.gdem.conversion.odf.OpenDocumentUtils;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;


/*
 * Abstract class contains the logic for converting spreadsheet like datafiles into
 * DataDictionary XML Instance format. The spreadsheets should be exctracted from DD
 * and include XML Schema information.
 * Currently supported formats are MS Excel and OpenDocument Spreadsheet.
 */


public abstract class DDXMLConverter {

	protected static LoggerIF _logger;

	private static final String INSTANCE_SERVLET = "GetXmlInstance";
	private static final String SCHEMA_SERVLET = "GetSchema";
    public  static final String META_SHEET_NAME = "-meta";
    public  static final String META_SHEET_NAME_ODS = "_meta";

	protected SourceReaderIF sourcefile = null;

	public DDXMLConverter() {
		_logger = GDEMServices.getLogger();
	}

	public abstract SourceReaderIF getSourceReader();
	public abstract String getSourceFormatName();

	public static DDXMLConverter getConverter(ByteArrayOutputStream outstream){

		try{
			if (ExcelUtils.isExcelFile(new ByteArrayInputStream(outstream.toByteArray())))
				return new Excel2XML();
		}
		catch(Exception e){

		}
		//If it is a zip file, then it is OpenDocument
		try{
			if (OpenDocumentUtils.isSpreadsheetFile(new ByteArrayInputStream(outstream.toByteArray())))
				return new Ods2Xml();
		}
		catch(Exception e){

		}

		return null;
	}

	public String convertDD_XML(String sIn, String sOut) throws GDEMException {
		try {
			FileOutputStream outStream = new FileOutputStream(sOut);
			FileInputStream inStream = new FileInputStream(sIn);
			return convertDD_XML(inStream, outStream);

			// InputSource is = new InputSource(

		} catch (Exception e) {
			return "ErrorConversionHandler - couldn't save the source file: "
					+ e.toString();
		}
	}

	public Vector convertDD_XML_split(String sIn, String sheet_param)
			throws GDEMException {
		try {
			FileInputStream inStream = new FileInputStream(sIn);
			return convertDD_XML_split(inStream, null, sheet_param);

		} catch (Exception e) {
			Vector result = new Vector();
			result
					.add("ErrorConversionHandler convertDD_XML_split- couldn't save the source file: "
							+ e.toString());
			return result;
		}

	}

	public Vector convertDD_XML_split(InputStream inStream)
			throws GDEMException {
		return convertDD_XML_split(inStream, null, null);
	}
	public String convertDD_XML(InputStream inStream, OutputStream outStream) throws GDEMException{

	      if (inStream == null) return "Could not find InputStream";
	      if (outStream == null) return "Could not find OutputStream";
	      try{
	        sourcefile = getSourceReader();
	        sourcefile.initReader(inStream);
	        String xml_schema = sourcefile.getXMLSchema();
	        if (xml_schema==null){
	          throw new Exception("The " + getSourceFormatName() + " file must be based on a template generated " +
	          			"from Data Dictionary for conversion to work. Could not find XML Schema!");
	        }
	   		doConversion(xml_schema, outStream);
	      }
	      catch (Exception e){
	        throw new GDEMException("Error generating XML file from " + getSourceFormatName() + " file: " + e.toString(), e);
	      }
		    finally{
		        try{
		            if (inStream != null) inStream.close();
		        }
		        catch(Exception e){}
		    }
	      return "OK";
	  }
	   public Vector convertDD_XML_split(InputStream inStream, OutputStream outStream, String sheet_param) throws GDEMException{

	   	  boolean http_response = (outStream==null)? false:true;
	   	  Vector result = new Vector();
	   	  String outFileName=null;
	      if (inStream == null) throw new GDEMException("Could not find InputStream");
	      try{
	        sourcefile = getSourceReader();
	        sourcefile.initReader(inStream);
	        String xml_schema = sourcefile.getXMLSchema();

	        if (xml_schema==null){
	    		if (http_response)
	                throw new Exception("The " + getSourceFormatName() + " file must be based on a template generated " +
	              			"from Data Dictionary for conversion to work. Could not find XML Schema!");
	    		else{
	            	result.add(createResultForSheet("1","Workbook","The " + getSourceFormatName() + " file must be based on a template generated " +
	              			"from Data Dictionary for conversion to work. Could not find XML Schema!"));
	            	return result;
	    		}
	          }

	        Hashtable sheet_schemas = sourcefile.getSheetSchemas();
			String first_sheet_name=sourcefile.getFirstSheetName();

	        //could not find sheet schemas
	        if (Utils.isNullHashtable(sheet_schemas)){
	        	//maybe it's spreadsheet file for DD table
	        	if (xml_schema.toLowerCase().indexOf("type=tbl")>-1
	        			|| xml_schema.toLowerCase().indexOf("=tbl")>-1){
	        		sheet_schemas.put(first_sheet_name,xml_schema);
	        	}
	        	else{
	        		if (http_response)
	                    throw new GDEMException("The " + getSourceFormatName() + " file must be based on a template generated " +
	                  			"from Data Dictionary for conversion to work. Could not find XML Schemas for sheets!");
	        		else{
	                	result.add(createResultForSheet("1","Workbook","The " + getSourceFormatName() + " file must be based on a template generated " +
	                  			"from Data Dictionary for conversion to work. Could not find XML Schemas for sheets!"));
	                	return result;
	        		}
	        	}
	        }
	        if (!Utils.isNullStr(sheet_param)){
	        	if (!Utils.containsKeyIgnoreCase(sheet_schemas,sheet_param)){
	        		if (http_response)
	        			throw new GDEMException("Could not find sheet with specified name or the XML schema reference was missing on DO_NOT_DELETE_THIS_SHEET: " + sheet_param);
	        		else{
	                	result.add(createResultForSheet("1",sheet_param,"Could not find sheet with specified name or the XML schema reference was missing on DO_NOT_DELETE_THIS_SHEET: " + sheet_param));
	                	return result;
	        		}
	        	}
	        }
	        if (http_response && Utils.isNullStr(sheet_param))
	        	sheet_param=first_sheet_name;

	  		Enumeration sheets = sheet_schemas.keys();
	        while (sheets.hasMoreElements()){
	            String sheet_name = sheets.nextElement().toString();
	            String sheet_schema = (String)sheet_schemas.get(sheet_name);
	            if (sheet_schema==null){
	            	result.add(createResultForSheet("1",sheet_name,"could not find xml schema for this sheet!"));
	            	continue;
	            }
	            	if (!Utils.isNullStr(sheet_param)){
	                	//Only 1 sheet is needed.
	            		if (!sheet_param.equalsIgnoreCase(sheet_name)){
	            			continue;
	            		}
	            	}

	            	try{
	            		//Do not return empty sheets.
	            		if (sourcefile.isEmptySheet(sheet_name)){
	            			if (http_response)
	                			throw new GDEMException("The sheet is empty: " + sheet_name + "!");
	            			else
	            				result.add(createResultForSheet("1",sheet_name,"The sheet is empty: " + sheet_name + "!"));
	            			continue;
	            		}

	            		if (!http_response){
	            			outFileName=Properties.tmpFolder + "gdem_" + System.currentTimeMillis() + ".xml";
	            	        outStream = new FileOutputStream(outFileName);
	            		}
	            		doConversion(sheet_schema, outStream);

	            		// if the respponse is http stream, then it is already written there and no file available
	            		if (!http_response){
	            			byte[] file = Utils.fileToBytes(outFileName);
	            			Vector sheet_result = new Vector();
	            			sheet_result.add("0");
	            			sheet_result.add(sheet_name + ".xml");
	            			sheet_result.add(file);
	            			result.add(sheet_result);
	            			/*try{
	            				Utils.deleteFile(outFileName);
	            			}
	            			catch(Exception e){
	            				_logger.error("Couldn't delete the result file" + outFileName);
	            			}*/
	            		}
	            	}
	            	catch(Exception e){
	            		if (http_response)
	            			throw new GDEMException(e.toString());
	            		else
	            			result.add(createResultForSheet("1",sheet_name,"Could not find xml schema for this sheet " + sheet_name + "! " + e.toString()));
	            	}
	            	finally{
	            		if(!http_response){
	            			if (outStream!=null) outStream.close();
	            		}
	            	}
	            	if (!Utils.isNullStr(sheet_param)){
	            		break;
	            	}
	            }
	      }
	      catch (Exception e){
	        throw new GDEMException("Error generating XML files from " + getSourceFormatName() + " file: " + e.toString(), e);
	      }
		    finally{
		        try{
		            if (inStream != null) inStream.close();
		        }
		        catch(Exception e){}
		    }
	      return result;
	  }
	protected void doConversion(String xml_schema, OutputStream outStream)
			throws Exception {
		String instance_url = getInstanceUrl(xml_schema);

		DD_XMLInstance instance = new DD_XMLInstance();
		DD_XMLInstanceHandler handler = new DD_XMLInstanceHandler(instance);

		SAXParserFactory spfact = SAXParserFactory.newInstance();
		SAXParser parser = spfact.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		spfact.setValidating(false);
		spfact.setNamespaceAware(true);
		reader.setFeature("http://xml.org/sax/features/validation", false);
		reader.setFeature("http://apache.org/xml/features/validation/schema",
				false);
		reader
				.setFeature(
						"http://apache.org/xml/features/nonvalidating/load-external-dtd",
						false);
		reader.setFeature("http://xml.org/sax/features/namespaces", true);

		reader.setContentHandler(handler);
		reader.parse(instance_url);

		if (Utils.isNullStr(instance.getEncoding())) {
			String enc_url = getEncodingFromStream(instance_url);
			if (!Utils.isNullStr(enc_url))
				instance.setEncoding(enc_url);
		}
		sourcefile.writeContentToInstance(instance);
		instance.flush(outStream);
	}

	public static String getInstanceUrl(String schema_url) throws GDEMException {

		try {
			URL SchemaURL = new URL(schema_url);

			int path_idx = schema_url.toLowerCase().indexOf(
					SCHEMA_SERVLET.toLowerCase());
			String path = schema_url.substring(0, path_idx);

			int id_idx = schema_url.indexOf("id=");
			String id = schema_url.substring(id_idx + 3);
			if (id.indexOf("&") > -1)
				id = id.substring(0, id.indexOf("&"));

			String type = id.substring(0, 3);
			id = id.substring(3);

			String instance_url = path + INSTANCE_SERVLET + "?id=" + id
					+ "&type=" + type.toLowerCase();
			URL InstanceURL = new URL(instance_url);
			return instance_url;
		} catch (MalformedURLException e) {
			throw new GDEMException("Error getting Instance file URL: "
					+ e.toString() + " - " + schema_url);
		} catch (Exception e) {
			throw new GDEMException("Error getting Instance file URL: "
					+ e.toString() + " - " + schema_url);
		}
	}

	// Reads the XML declaration from instance file
	// It is called only, when SAX coudn't read it
	protected String getEncodingFromStream(String str_url) {
		BufferedReader br = null;
		try {
			URL url = new URL(str_url);
			// ins = new DataInputStream(url.openStream());
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			String xml_decl = br.readLine();

			if (xml_decl == null)
				return null;
			if (!xml_decl.startsWith("<?xml version=")
					&& !xml_decl.endsWith("?>"))
				return null;
			int idx = xml_decl.indexOf("encoding=");
			if (idx == -1)
				return null;
			String start = xml_decl.substring(idx + 10);
			int end_idx = start.indexOf("\"");
			if (end_idx == -1)
				return null;
			String enc = start.substring(0, end_idx);

			return enc;
		} catch (MalformedURLException e) {
			_logger.debug("It is not url: " + str_url + "; " + e.toString());
			return null;
		} catch (IOException e) {
			_logger.debug("could not read encoding from url: " + str_url + "; "
					+ e.toString());
			return null;
		} catch (Exception e) {
			return null;
			// couldn't read encoding
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
			}
		}
	}

	protected Vector createResultForSheet(String code, String sheet_name,
			String error_mess) {
		Vector sheet_result = new Vector();

		sheet_result.add(code);
		sheet_result.add(sheet_name);
		sheet_result.add(error_mess);

		return sheet_result;
	}
}
