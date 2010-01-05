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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.DDServiceClient;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.xml.IXQuery;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;


/*
 * Abstract class contains the logic for converting spreadsheet like datafiles into
 * DataDictionary XML Instance format. The spreadsheets should be exctracted from DD
 * and include XML Schema information.
 * Currently supported formats are MS Excel and OpenDocument Spreadsheet.
 */


public abstract class DDXMLConverter {

	protected static LoggerIF _logger=GDEMServices.getLogger();

	private static final String INSTANCE_SERVLET = "GetXmlInstance";
	private static final String SCHEMA_SERVLET = "GetSchema";
	private static final String CONTAINER_SCHEMA_SERVLET = "GetContainerSchema";
    public  static final String META_SHEET_NAME = "-meta";
    public  static final String META_SHEET_NAME_ODS = "_meta";

	protected SourceReaderIF sourcefile = null;

 	boolean httpResponse = false;


	public DDXMLConverter() {
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
			result.add("ErrorConversionHandler convertDD_XML_split- couldn't save the source file: "
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
	          throw new Exception(Properties.getMessage(
              		BusinessConstants.ERROR_CONVERSION_INVALID_TEMPLATE, new String[]{getSourceFormatName()}));
	        }
	        String invalidMess = getInvalidSchemaMessage(xml_schema); 
	        if(invalidMess!=null){
		          throw new Exception(invalidMess);	        	
	        }
	        //execute conversion
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

	   	  Vector result = new Vector();
	   	  String outFileName=null;
	      if (inStream == null) throw new GDEMException("Could not find InputStream");
	      try{
	        sourcefile = getSourceReader();
	        sourcefile.initReader(inStream);
	        String xml_schema = sourcefile.getXMLSchema();

	        if (xml_schema==null){
	        	return buildWorkbookErrorMessage(result,null,Properties.getMessage(
                		BusinessConstants.ERROR_CONVERSION_INVALID_TEMPLATE, new String[]{getSourceFormatName()}));
	          }
	        String invalidMess = getInvalidSchemaMessage(xml_schema); 
	        if(invalidMess!=null){
	        	return buildWorkbookErrorMessage(result,null,invalidMess);
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
		        	return buildWorkbookErrorMessage(result,null,Properties.getMessage(
	                		BusinessConstants.ERROR_CONVERSION_INVALID_TEMPLATE, new String[]{getSourceFormatName()}));
	        	}
	        }
	        if (!Utils.isNullStr(sheet_param)){
	        	if (!Utils.containsKeyIgnoreCase(sheet_schemas,sheet_param)){
		        	return buildWorkbookErrorMessage(result,sheet_param,"Could not find sheet with specified name or the XML schema reference was missing on DO_NOT_DELETE_THIS_SHEET: " + sheet_param);
	        	}
	        }
	        if (isHttpResponse() && Utils.isNullStr(sheet_param))
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
	    		        	result = buildWorkbookErrorMessage(result,sheet_param,"The sheet is empty: " + sheet_name + "!");
	            			continue;
	            		}

	            		if (!isHttpResponse()){
	            			outFileName=Properties.tmpFolder + "gdem_" + System.currentTimeMillis() + ".xml";
	            	        outStream = new FileOutputStream(outFileName);
	            		}
	            		doConversion(sheet_schema, outStream);

	            		// if the respponse is http stream, then it is already written there and no file available
	            		if (!isHttpResponse()){
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
    		        	result = buildWorkbookErrorMessage(result,sheet_param,"Could not find xml schema for this sheet " + sheet_name + "! " + e.toString());
	            	}
	            	finally{
	            		if(!isHttpResponse()){
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
 	public boolean isHttpResponse() {
		return httpResponse;
	}

	public void setHttpResponse(boolean httpResponse) {
		this.httpResponse = httpResponse;
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
		importSheetSchemas(sourcefile, instance, xml_schema);
		sourcefile.writeContentToInstance(instance);
		instance.flush(outStream);
	}

	public static String getInstanceUrl(String schema_url) throws GDEMException {

		try {
			
			//throws Exception, if not correct URL
			URL schemaURL = new URL(schema_url);

			String id = getSchemaIdParam(schema_url);
			
			String type = id.substring(0, 3);
			id = id.substring(3);

			int path_idx = schema_url.toLowerCase().indexOf(
					SCHEMA_SERVLET.toLowerCase());
			String path = schema_url.substring(0, path_idx);

			String instance_url = path + INSTANCE_SERVLET + "?id=" + id
					+ "&type=" + type.toLowerCase();

			//throws Exception, if not correct URL
			URL instanceURL = new URL(instance_url);
			return instance_url;
		} catch (MalformedURLException e) {
			throw new GDEMException("Error getting Instance file URL: "
					+ e.toString() + " - " + schema_url);
		} catch (Exception e) {
			throw new GDEMException("Error getting Instance file URL: "
					+ e.toString() + " - " + schema_url);
		}
	}
	public static String getSchemaIdParam(String schema_url) throws GDEMException {
		
		String ret = "";
		
		int id_idx = schema_url.indexOf("id=");
		String id = schema_url.substring(id_idx + 3);
		if (id.indexOf("&") > -1)
			id = id.substring(0, id.indexOf("&"));

		return id;
	}

	/**
	 * Returns the DD container schema URL. It holds the elements definitions
	 * @param schema_url
	 * @return
	 * @throws GDEMException
	 */
	public static String getContainerSchemaUrl(String schema_url) throws GDEMException {

		try {
			URL SchemaURL = new URL(schema_url);

			String containerSchemaUrl = schema_url.replace(SCHEMA_SERVLET, CONTAINER_SCHEMA_SERVLET);

			URL InstanceURL = new URL(containerSchemaUrl);
			return containerSchemaUrl;
		} catch (MalformedURLException e) {
			throw new GDEMException("Error getting Container Schema URL: "
					+ e.toString() + " - " + schema_url);
		} catch (Exception e) {
			throw new GDEMException("Error getting Container Schema URL: "
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
	/**
	 * gather all element definitions
	 * @param spreadsheet 
	 * @param instance
	 */
	protected void importSheetSchemas(SourceReaderIF spreadsheet, DD_XMLInstance instance, String xml_schema){
		try{
			//if instance type is TBL, then import only table schema
			if(instance.getType().equals(DD_XMLInstance.TBL_TYPE)){
				Map elemDefs = importSchemaElemDefs(xml_schema);
				instance.addElemDef(DD_XMLInstance.TBL_TYPE, elemDefs);
			}
			//if instance type is dataset, then import schemas for all pages
			else{
				Hashtable sheetSchemas = spreadsheet.getSheetSchemas();
				Enumeration sheets = sheetSchemas.keys();
				while (sheets.hasMoreElements()){
					String sheet_name = sheets.nextElement().toString();
					String schemaUrl = (String)sheetSchemas.get(sheet_name);
					Map elemDefs = importSchemaElemDefs(schemaUrl);
					instance.addElemDef(sheet_name, elemDefs);				
					}
				}
		} catch (Exception ex) {
			_logger.error("Error reading elements from schema files ", ex);
		}
	}
	/**
	 * gather all element definitions
	 * @param instance
	 * @param schemaUrl
	 */
	protected Map importSchemaElemDefs(String schemaUrl){
		InputStream inputStream =null;
		Map elemDefs = new HashMap();
		try {
			//get element definitions for given schema
			Map schemaElemDefs = getSchemaElemDefs(schemaUrl);
			elemDefs.putAll(schemaElemDefs);
			
			//load imported schema URLs
			IXmlCtx ctx=new XmlContext();
			URL url = new URL(schemaUrl);
			inputStream = url.openStream();
			ctx.checkFromInputStream(inputStream);
			
			IXQuery xQuery=ctx.getQueryManager();
			
			//run recursively the same function for importing elem defs for imported schemas
			List schemas = xQuery.getSchemaImports();
			for (int i = 0; i < schemas.size(); i++) {
				String schema=(String) schemas.get(i);
				Map impSchemaElemeDefs = getSchemaElemDefs(schema);
				elemDefs.putAll(impSchemaElemeDefs);
			}
		} catch (Exception ex) {
			_logger.error("Error reading schema file ", ex);
		}
		finally{
			try{
				inputStream.close();
			}catch(Exception e){}
		}
		return elemDefs;
	}
	protected Map getSchemaElemDefs(String schemaUrl){
		InputStream inputStream =null;
		Map elemDefs = new HashMap();
		try {
			IXmlCtx ctx=new XmlContext();
			URL url = new URL(schemaUrl);
			inputStream = url.openStream();
			ctx.checkFromInputStream(inputStream);
			
			IXQuery xQuery=ctx.getQueryManager();
			List elemNames = xQuery.getSchemaElements();
			for (int i = 0; i < elemNames.size(); i++) {
				String elemName=(String) elemNames.get(i);
				String dataType = xQuery.getSchemaElementType(elemName);
				elemDefs.put(elemName,dataType);
			}
		} catch (Exception ex) {
			_logger.error("Error reading schema file ", ex);
		}
		finally{
			try{
				inputStream.close();
			}catch(Exception e){}
		}
		return elemDefs;
		
	}
	protected Vector createResultForSheet(String code, String sheet_name,
			String error_mess) {
		Vector sheet_result = new Vector();

		sheet_result.add(code);
		sheet_result.add(sheet_name);
		sheet_result.add(error_mess);

		return sheet_result;
	}
	/**
	 * Throws Exception if the result should go directlt into HTTP response, 
	 * otherwise the method builds result structure including error message
	 * @param result
	 * @param sheet
	 * @param message
	 * @return
	 * @throws Exception
	 */
	protected Vector buildWorkbookErrorMessage(Vector result, String sheet, String message) throws Exception{
		
		String sheetParam = (Utils.isNullStr(sheet))?"Workbook":sheet;
		if (isHttpResponse()){
            throw new Exception(message);
		}
        result.add(createResultForSheet("1",sheetParam,message));

        return result;
	}
	
	/**
	 * checks if the given schema belongs to the last released dataset in DD. Returns null, if schema is OK.
	 * Returns an error message, if the schema is not ok to convert.
	 * @param xml_schema
	 * @return error message
	 * @throws GDEMException 
	 */
	public String getInvalidSchemaMessage(String xml_schema) throws GDEMException {
		
		String result = null;
		Map dataset = null;
		boolean isLatestReleased = false;
		String status = "";
		String dateOfLatestReleased = "";
		String idOfLatestReleased = "";
		
		String id = getSchemaIdParam(xml_schema);

		if(id.length()>4 && (id.startsWith(DD_XMLInstance.DST_TYPE) || id.startsWith(DD_XMLInstance.TBL_TYPE))){
			
			String type = id.substring(0,3);
			String dsId = id.substring(3);
			dataset = getDataset(type.toLowerCase(),dsId);
		
			status = (String)dataset.get("status");
			isLatestReleased = (dataset.get("isLatestReleased")==null || 
						"true".equals((String)dataset.get("isLatestReleased")))?
							true:false;
			dateOfLatestReleased = (String)dataset.get("dateOfLatestReleased");
			idOfLatestReleased = (String)dataset.get("idOfLatestReleased");	
		}
		if(dataset==null){
			result = Properties.getMessage(
            		BusinessConstants.ERROR_CONVERSION_INVALID_TEMPLATE, new String[]{getSourceFormatName()});
		}
		else if(!isLatestReleased && "Released".equalsIgnoreCase(status)){
			String formattedReleasedDate = Utils.formatTimestampDate(dateOfLatestReleased);
			result = Properties.getMessage(
            		BusinessConstants.ERROR_CONVERSION_OBSOLETE_TEMPLATE, 
            			new String[]{getSourceFormatName(),formattedReleasedDate==null?"":formattedReleasedDate
            					,idOfLatestReleased});			
		}

		return result;
	}
	protected Map getDataset(String type, String dsId){
		if(GDEMServices.isTestConnection()){
			return DDServiceClient.getDataset(type,dsId);
		}
		else{
			return DDServiceClient.getMockDataset(type,dsId);
		}
	}
}
