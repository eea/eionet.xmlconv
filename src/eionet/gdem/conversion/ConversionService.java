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
 * Original Code: Enriko K�sper (TietoEnator)
 * Contributors:   Nedeljko Pavlovic (ED)
 */

package eionet.gdem.conversion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.conversion.converters.ConvertContext;
import eionet.gdem.conversion.converters.ConvertStartegy;
import eionet.gdem.conversion.converters.ExcelConverter;
import eionet.gdem.conversion.converters.HTMLConverter;
import eionet.gdem.conversion.converters.PDFConverter;
import eionet.gdem.conversion.converters.TextConverter;
import eionet.gdem.conversion.converters.XMLConverter;
import eionet.gdem.dcm.Conversion;
import eionet.gdem.dcm.XslGenerator;
import eionet.gdem.dcm.business.DDServiceClient;
import eionet.gdem.dto.ConversionDto;
import eionet.gdem.services.DbModuleIF;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Utils;


/**
 * Container for different conversions
 * for being used through XML/RPC
 * @author Kaido Laine
 */

public class ConversionService {

	public static final String DEFAULT_CONTENT_TYPE = "text/plain";
	public static final String DEFAULT_FILE_EXT = "txt";

	private String xslFolder;
	private String tmpFolder;

	private DbModuleIF db;

	private HashMap convTypes;

	private String cnvContentType = null;
	private String cnvFileExt = null;
	private String cnvTypeOut = null;
	
	private String ticket = null;
	private boolean trustedMode=true;//false for web clients

	private static LoggerIF _logger;


	/***
	 * Constant values for HttpResponse content types
	 * deprecated
	 */
	private void initCnvTypes() {
		convTypes = new HashMap();

		convTypes.put("EXCEL", "application/vnd.ms-excel");
		convTypes.put("PDF", "application/pdf");
		convTypes.put("HTML", "text/html");
		convTypes.put("XML", "text/xml");
		convTypes.put("SQL", "text/plain");
	}


	public ConversionService() {
		_logger = GDEMServices.getLogger();

		xslFolder = Properties.xslFolder+ File.separatorChar; //props.getString("xsl.folder");
		tmpFolder = Properties.tmpFolder+ File.separatorChar; //props.getString("tmp.folder");

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

		if (db == null) db = GDEMServices.getDbModule();

		Vector v = null;
		v = new Vector();

		if (schema != null && schema.startsWith(Properties.ddURL)) {

			// schema is from DD
			// parse tbl id
			// check tbl id
			//

			String tblId = schema.substring(schema.indexOf("id=TBL") + 6, schema.length());

			List convs = Conversion.getConversions();

			for (int i = 0; i < convs.size(); i++) {
				Hashtable h = new Hashtable();
				h.put("convert_id", "DD_TBL" + tblId + "_CONV" + ((ConversionDto) convs.get(i)).getConvId());
				h.put("xsl", Properties.gdemURL + "/do/getStylesheet?id=" + tblId + "&conv=" + ((ConversionDto) convs.get(i)).getConvId());
				h.put("description", ((ConversionDto) convs.get(i)).getDescription());
				h.put("content_type_out", ((ConversionDto) convs.get(i)).getResultType());
				h.put("xml_schema", schema);
				v.add(h);
			}

		}
		//
		if (schema == null) {
			List ddTables = DDServiceClient.getDDTables();
			List convs = Conversion.getConversions();

			for (int i = 0; i < ddTables.size(); i++) {
				Hashtable schemaDD = (Hashtable) ddTables.get(i);
				String tblId = (String) schemaDD.get("tblId");
				String schemaUrl = Properties.ddURL + "/GetSchema?id=TBL" + tblId;

				for (int j = 0; j < convs.size(); j++) {
					Hashtable h = new Hashtable();
					h.put("convert_id", "DD_TBL" + tblId + "_CONV" + ((ConversionDto) convs.get(j)).getConvId());
					h.put("xsl", Properties.gdemURL + "/do/getStylesheet?id=" + tblId + "&conv=" + ((ConversionDto) convs.get(j)).getConvId());
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

			for (int i = 0; i < vDb.size(); i++) {
				v.add(vDb.get(i));
			}

		} catch (Exception e) {
			_logger.error("Error getting data from the DB", e);
			throw new GDEMException("Error getting data from the DB " + e.toString(), e);
		}

		return v;

	}


	/**
	 * Converts the XML file to a specific format.
	 * 
	 * @param sourceURL	URL of the XML file to be converted
	 * @param convertId		ID of desired conversion as the follows:
	 * 									- If conversion ID begins with the DD DCM will generate appropriate stylesheet on the fly.
	 * 									- If conversion ID is number the DCM will consider consider hand coded conversion
	 * @return				   Hashtable containing two elements:
	 * 									- content-type (String)
	 * 									- content	     (Byte array)
	 * @throws GDEMException Thrown in case of errors
	 */
	public Hashtable convert(String sourceURL, String convertId) throws GDEMException {
		return convert(sourceURL, convertId, null);
		
	}


	public Hashtable convert(String sourceURL, String convertId, HttpServletResponse response) throws GDEMException {
		OutputStream result=null;
		_logger.debug("sourceURL=" + sourceURL + "convertId=" + convertId + "res=" + response);
		if (convertId.startsWith("DD")) {
			return convertDDTable(sourceURL, convertId, response);
		} else {

			Hashtable h = new Hashtable();
			String xslFile = null;
			String outputFileName = null;
			InputFile src = null;

			try {
				src = new InputFile(sourceURL);
				src.setAuthentication(ticket);
				src.setTrustedMode(trustedMode);
				
				if (db == null) db = GDEMServices.getDbModule();

				try {
					HashMap styleSheetData = db.getStylesheetInfo(convertId);

					if (styleSheetData == null) throw new GDEMException("No stylesheet info for convertID= " + convertId);
					xslFile = xslFolder + (String) styleSheetData.get("xsl");
					cnvTypeOut = (String) styleSheetData.get("content_type_out");

					Hashtable convType = db.getConvType(cnvTypeOut);

					if (convType != null) {
						try {
							cnvContentType = (String) convType.get("content_type");
							cnvFileExt = (String) convType.get("file_ext");
						} catch (Exception e) {
							_logger.error("error getting con types",e);
							// Take no action, use default params
						}
					}
					if (cnvContentType == null) cnvContentType = "text/plain";
					if (cnvFileExt == null) cnvFileExt = "txt";

				} catch (Exception e) {
					_logger.error("error getting con types",e);
					throw new GDEMException("Error getting stylesheet info from repository for " + convertId, e);
				}
				if (response != null) {
					try {
						result = response.getOutputStream();
						response.setContentType(cnvContentType);
					} catch (IOException e) {
						_logger.error("Error getting response outputstream ",e);
						throw new GDEMException("Error getting response outputstream " + e.toString(), e);
					}
				}
				
				outputFileName = executeConversion(src.getSrcInputStream(), xslFile, result);

			} catch (MalformedURLException mfe) {
				//throw new GDEMException("Bad URL : " + mfe.toString(), mfe);
				_logger.error("Bad URL", mfe);
				throw new GDEMException("Bad URL", mfe);
			} catch (IOException ioe) {
				//throw new GDEMException("Error opening URL " + ioe.toString(), ioe);
				_logger.error("Error opening URL", ioe);
				throw new GDEMException("Error opening URL", ioe);
			} catch (Exception e) {
				_logger.error("Error converting", e);
				//throw new GDEMException("Error converting: " + e.toString(), e);
				throw new GDEMException("Error converting", e);
			} finally {
				try {
					if (src != null) src.close();
				} catch (Exception e) {
				}
			}

			h.put("content-type", cnvContentType);
			if (response != null) {
				try {
					result.close();
				} catch (IOException e) {
					throw new GDEMException("Error closing result", e);
				}
				return h;
			}

			byte[] file = fileToBytes(outputFileName);
			h.put("content", file);
			try {
				Utils.deleteFile(outputFileName);
			} catch (Exception e) {

				_logger.error("Couldn't delete the result file: " + outputFileName, e);
			}

			return h;
		}
	}


	public Hashtable convertDDTable(String sourceURL, String convertId, HttpServletResponse res) throws GDEMException {
		OutputStream result=null;
		Hashtable h = new Hashtable();
		String outputFileName = null;
		InputFile src = null;
		String tblId = "";
		String convId = "";

		// prase idtable and id conversion
		if (convertId.startsWith("DD")) {
			tblId = convertId.substring(6, convertId.indexOf("_CONV"));
			convId = convertId.substring(convertId.indexOf("_CONV") + 5, convertId.length());
		}

		ConversionDto conv = Conversion.getConversionById(convId);
		String format = Properties.metaXSLFolder + File.separatorChar+ conv.getStylesheet();
		String url = Properties.ddURL + "/GetTableDef?id=" + tblId;
		//xslFile = Properties.gdemURL + "/do/getStylesheet?id=" + tblId + "&conv=" + convId;
		

		// pozvati konverziju za sourceURL i xslURL
		try {
			ByteArrayInputStream byteIn=XslGenerator.convertXML(url, format);
			src = new InputFile(sourceURL);
			src.setAuthentication(ticket);
			src.setTrustedMode(trustedMode);
			
			if (db == null) db = GDEMServices.getDbModule();
			try {
				cnvTypeOut = conv.getResultType();
				Hashtable convType = db.getConvType(cnvTypeOut);

				if (convType != null) {
					try {
						cnvContentType = (String) convType.get("content_type");
						cnvFileExt = (String) convType.get("file_ext");
					} catch (Exception e) {
						_logger.error("Error getting conversion types ",e);
						// Take no action, use default params
					}
				}
				if (cnvContentType == null) cnvContentType = "text/plain";
				if (cnvFileExt == null) cnvFileExt = "txt";

			} catch (Exception e) {
				_logger.error("Error getting stylesheet info from repository for " + convertId,e);
				throw new GDEMException("Error getting stylesheet info from repository for " + convertId, e);
			}
			if (res != null) {
				try {
					result = res.getOutputStream();
					res.setContentType(cnvContentType);
				} catch (IOException e) {
					_logger.error("Error getting response outputstream ",e);
					throw new GDEMException("Error getting response outputstream " + e.toString(), e);
				}
			}
			
			outputFileName = executeConversion(src.getSrcInputStream(), byteIn, result);

		} catch (MalformedURLException mfe) {
			_logger.error("Bad URL", mfe);
			throw new GDEMException("Bad URL", mfe);
		} catch (IOException ioe) {
			_logger.error("Error opening URL", ioe);
			throw new GDEMException("Error opening URL", ioe);
		} catch (Exception e) {
			_logger.error("Error converting", e);
			throw new GDEMException("Error converting", e);
		} finally {
			try {
				if (src != null) src.close();
			} catch (Exception e) {
				_logger.error("Error converting", e);
			}
		}

		h.put("content-type", cnvContentType);
		if (res != null) {
			try {
				result.close();
			} catch (IOException e) {
				_logger.error("Error closing result", e);
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
			_logger.error("Couldn't delete the result file: " + outputFileName, e);
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
		OutputStream result=null;
		InputFile src = null;
		Vector v_result = new Vector();
		String str_result = null;
		String outFileName = tmpFolder + "gdem_" + System.currentTimeMillis() + ".xml";
		String error_mess = null;

		try {

			src = new InputFile(sourceURL);
			src.setAuthentication(ticket);
			src.setTrustedMode(trustedMode);
			if (res != null) {
				try {
					result = res.getOutputStream();
				} catch (IOException e) {
					_logger.error("Error getting response outputstream ", e);
					throw new GDEMException("Error getting response outputstream " + e.toString(), e);
				}
			}
			if (result == null) result = new FileOutputStream(outFileName);

			Excel2XML converter = new Excel2XML();
			str_result = converter.convertDD_XML(src.getSrcInputStream(), result);
		} catch (MalformedURLException mfe) {
			_logger.error("Bad URL ", mfe);
			if (res != null) {
				throw new GDEMException("Bad URL : " + mfe.toString(), mfe);
			} else {
				error_mess = "Bad URL : " + mfe.toString();
			}
		} catch (IOException ioe) {
			_logger.error("Error opening URL ", ioe);
			if (res != null) {
				throw new GDEMException("Error opening URL " + ioe.toString(), ioe);
			} else {
				error_mess = "Error opening URL " + ioe.toString();
			}
		} catch (Exception e) {
			_logger.error("", e);
			if (res != null) {

				throw new GDEMException(e.toString(), e);
			} else {
				error_mess = e.toString();
			}
		} finally {
			try {
				if (src != null) src.close();
				//  if (result!=null) result.close();
			} catch (Exception e) {
				_logger.error("", e);
			}
		}

		if (res != null) {
			try {
				res.setContentType("text/xml");
				result.close();
			} catch (IOException e) {
				_logger.error("Error closing result ResponseOutputStream ", e);
				throw new GDEMException("Error closing result ResponseOutputStream ", e);
			}
			return v_result;
		}
		//Creates response Vector    
		int result_code = 1;
		if (!Utils.isNullStr(str_result)) {
			if (str_result.equals("OK")) result_code = 0;
		}
		byte[] file = Utils.fileToBytes(outFileName);

		v_result.add(String.valueOf(result_code));
		if (result_code == 0)
			v_result.add(file);
		else
			v_result.add(error_mess);

		try {
			Utils.deleteFile(outFileName);
		} catch (Exception e) {
			_logger.error("Couldn't delete the result file", e);
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
			FileInputStream fis = new FileInputStream(fileName);
			//log("========= fis opened");

			baos = new ByteArrayOutputStream();

			int bufLen = 0;
			byte[] buf = new byte[1024];

			while ((bufLen = fis.read(buf)) != -1)
				baos.write(buf, 0, bufLen);

			fis.close();

		} catch (FileNotFoundException fne) {
			_logger.error("File not found " + fileName, fne);
			throw new GDEMException("File not found " + fileName, fne);
		} catch (Exception e) {
			_logger.error("", e);
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
		OutputStream result=null;

		InputFile src = null;
		String error_mess = null;
		Vector v_result = null;

		try {

			src = new InputFile(sourceURL);
			src.setAuthentication(ticket);
			src.setTrustedMode(trustedMode);
			if (res != null) {
				try {
					result = res.getOutputStream();
				} catch (IOException e) {
					_logger.error("Error getting response outputstream " , e);
					throw new GDEMException("Error getting response outputstream " + e.toString(), e);
				}
			}

			Excel2XML converter = new Excel2XML();
			v_result = converter.convertDD_XML_split(src.getSrcInputStream(), result, sheet_param);
		} catch (MalformedURLException mfe) {
			_logger.error("Bad URL " , mfe);
			if (res != null) {
				throw new GDEMException("Bad URL : " + mfe.toString(), mfe);
			} else {
				error_mess = "Bad URL : " + mfe.toString();
			}
		} catch (IOException ioe) {			
			_logger.error("Error opening URL " , ioe);
			if (res != null) {
				throw new GDEMException("Error opening URL " + ioe.toString(), ioe);
			} else {
				error_mess = "Error opening URL " + ioe.toString();
			}
		} catch (Exception e) {
			_logger.error("" , e);
			if (res != null) {
				throw new GDEMException(e.toString(), e);
			} else {
				error_mess = e.toString();
			}
		} finally {
			try {
				if (src != null) src.close();
				//  if (result!=null) result.close();
			} catch (Exception e) {
			}
		}
		if (res != null) {
			try {
				res.setContentType("text/xml");
				result.close();
			} catch (IOException e) {
				_logger.error("Error closing result ResponseOutputStream ", e);
				throw new GDEMException("Error closing result ResponseOutputStream ", e);
			}
			return v_result;
		}
		//  Creates response Vector    

		if (Utils.isNullVector(v_result) && !Utils.isNullStr(error_mess)) {
			v_result.add("1");
			v_result.add(error_mess);
		}

		return v_result;
	}


	public ArrayList getXMLSchemas() throws GDEMException {
		Vector conv = listConversions();
		ArrayList schemas = new ArrayList();

		for (int i = 0; i < conv.size(); i++) {
			Hashtable schema = (Hashtable) conv.get(i);
			//System.out.println( i + " - " + schema.get("xml_schema") );		  
			if (!schemas.contains(schema.get("xml_schema"))) {
				schemas.add(schema.get("xml_schema"));
			}
		}

		return schemas;
	}
	
	private String executeConversion(InputStream source, Object xslt, OutputStream result) throws Exception {
		ConvertContext ctx=new ConvertContext(source, xslt, result, cnvFileExt);
		ConvertStartegy cs=null;
		if (cnvTypeOut.equals("HTML")) {
			cs=new HTMLConverter();
		} else if (cnvTypeOut.equals("PDF")) {
			cs=new PDFConverter();
		} else if (cnvTypeOut.equals("EXCEL")) {
			cs=new ExcelConverter();
		} else if (cnvTypeOut.equals("XML")) {
			cs=new XMLConverter();
		} else {
			cs=new TextConverter();
		}
		return ctx.executeConversion(cs);
	}


	public boolean existsXMLSchema(String xmlSchema) throws GDEMException {
		ArrayList schemas = getXMLSchemas();
		return schemas.contains(xmlSchema);
	}
	
	public void setTicket(String _ticket){
	   	this.ticket =  _ticket;	
	  }
	  public void setTrustedMode(boolean mode){
		  this.trustedMode=mode;
		}


}