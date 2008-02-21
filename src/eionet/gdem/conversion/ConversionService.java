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
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.dcm.results.HttpMethodResponseWrapper;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Streams;
import eionet.gdem.utils.Utils;

/**
 * Facade class for different conversions for being used through XML/RPC and HTTP POST and GET
 *
 * @author Enriko Käsper
 */

public class ConversionService implements ConversionServiceIF {

	public static final String DEFAULT_CONTENT_TYPE = "text/plain";

	public static final String DEFAULT_FILE_EXT = "txt";

	public static final String DEFAULT_FILE_NAME = "converted";

	private String tmpFolder;

	private String cnvFileName = null;

	private String ticket = null;

	private boolean trustedMode = true;// false for web clients
	
	private boolean isHttpResponse = false;
	
	private HttpMethodResponseWrapper httpResponse = null;
	

	private static LoggerIF _logger = GDEMServices.getLogger();

	public ConversionService() {

		tmpFolder = Properties.tmpFolder + File.separatorChar; // props.getString("tmp.folder");

	}

	/* (non-Javadoc)
	 * @see eionet.gdem.conversion.ConversionServiceIF#listConversions()
	 */

	public Vector listConversions() throws GDEMException {
		return listConversions(null);
	}

	/* (non-Javadoc)
	 * @see eionet.gdem.conversion.ConversionServiceIF#listConversions(java.lang.String)
	 */
	public Vector listConversions(String schema) throws GDEMException {

		ListConversionsMethod method = new ListConversionsMethod();
		Vector v = method.listConversions(schema);
	
		return v;

	}


	public Hashtable convert(String sourceURL, String convertId,
			String username, String password) throws GDEMException {
		try {
			String ticket = Utils.getEncodedAuthentication(username, password);
		
			ConvertXMLMethod convertMethod = new ConvertXMLMethod();
			convertMethod.setTicket(ticket);
			convertMethod.setTrustedMode(false);
			convertMethod.setHttpResult(httpResponse);
			
			return convertMethod.convert(sourceURL, convertId);

		} catch (IOException ex) {
			_logger.error("Error creating ticket ", ex);
			throw new GDEMException("Error creating ticket", ex);
		}
	}

	/* (non-Javadoc)
	 * @see eionet.gdem.conversion.ConversionServiceIF#convert(java.lang.String, java.lang.String)
	 */
	public Hashtable convert(String sourceURL, String convertId) throws GDEMException {
		
		ConvertXMLMethod convertMethod = new ConvertXMLMethod();
		convertMethod.setHttpResult(httpResponse);
		return convertMethod.convert(sourceURL, convertId);	
	}

	/* (non-Javadoc)
	 * @see eionet.gdem.conversion.ConversionServiceIF#convertDD_XML(java.lang.String)
	 */
	public Vector convertDD_XML(String sourceURL) throws GDEMException {
		return convertDD_XML(sourceURL, null);
	}

	/**
	 * Request from WebBrowser Converts DataDictionary MS Excel file to XML
	 *
	 * @param String
	 *            url: URL of the srouce Excel file
	 * @param HttpServletResponse
	 *            res: Servlet response
	 * @return Vector result: error_code, xml_url, error_message
	 */
	public Vector convertDD_XML(String sourceURL, HttpServletResponse res)
			throws GDEMException {
		OutputStream result = null;
		ByteArrayOutputStream out_stream_tmp = new ByteArrayOutputStream();
		ByteArrayInputStream in_stream_tmp = null;
		InputFile src = null;
		Vector v_result = new Vector();
		String str_result = null;
		String outFileName = tmpFolder + "gdem_" + System.currentTimeMillis()
				+ ".xml";
		String error_mess = null;

		try {

			src = new InputFile(sourceURL);
			src.setAuthentication(ticket);
			src.setTrustedMode(trustedMode);
			cnvFileName = Utils.isNullStr(src.getFileNameNoExtension()) ?
					DEFAULT_FILE_NAME:src.getFileNameNoExtension();

			if (res != null) {
				try {
					result = res.getOutputStream();
				} catch (IOException e) {
					_logger.error("Error getting response outputstream ", e);
					throw new GDEMException(
							"Error getting response outputstream "
									+ e.toString(), e);
				}
			}
			if (result == null)
				result = new FileOutputStream(outFileName);

			// Read inputstream into Bytearrayoutputstream
			Streams.drain(src.getSrcInputStream(), out_stream_tmp);
			// Detect the file format
			DDXMLConverter converter = DDXMLConverter
					.getConverter(out_stream_tmp);

			if (converter == null) {
				_logger
						.error(
								"Could not detect the format of source file. Converter waits MS Excel or OpenDocument Spreadsheet file.",
								null);
				throw new GDEMException(
						"Could not detect the format of source file. Converter waits MS Excel or OpenDocument Spreadsheet file.");
			}
			// create new inputstrema from tmp Bytearrayoutputstream
			in_stream_tmp = new ByteArrayInputStream(out_stream_tmp
					.toByteArray());

			str_result = converter.convertDD_XML(in_stream_tmp, result);

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
				throw new GDEMException("Error opening URL " + ioe.toString(),
						ioe);
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
				if (src != null)
					src.close();
				// if (result!=null) result.close();
			} catch (Exception e) {
				_logger.error("", e);
			}
			try {
				if (in_stream_tmp != null)
					in_stream_tmp.close();
			} catch (Exception e) {
			}
			try {
				if (out_stream_tmp != null)
					out_stream_tmp.close();
			} catch (Exception e) {
			}

		}

		if (res != null) {
			try {
				res.setContentType("text/xml");
				res.setHeader("Content-Disposition","inline;filename=\"" + cnvFileName + ".xml\"");
				result.close();
			} catch (IOException e) {
				_logger.error("Error closing result ResponseOutputStream ", e);
				throw new GDEMException(
						"Error closing result ResponseOutputStream ", e);
			}
			return v_result;
		}
		// Creates response Vector
		int result_code = 1;
		if (!Utils.isNullStr(str_result)) {
			if (str_result.equals("OK"))
				result_code = 0;
		}
		byte[] file = Utils.fileToBytes(outFileName);

		v_result.add(String.valueOf(result_code));
		if (result_code == 0){
			v_result.add(file);
			v_result.add(cnvFileName + ".xml");
		}
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

			// log("========= open fis " + fileName);
			FileInputStream fis = new FileInputStream(fileName);
			// log("========= fis opened");

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

	/* (non-Javadoc)
	 * @see eionet.gdem.conversion.ConversionServiceIF#convertDD_XML_split(java.lang.String, java.lang.String)
	 */
	public Vector convertDD_XML_split(String sourceURL, String sheet_param)
			throws GDEMException {
		return convertDD_XML_split(sourceURL, sheet_param, null);
	}

	/**
	 * Request from WebBrowser Converts DataDictionary MS Excel sheets to
	 * different XML files, where one xml file is dataset table.
	 *
	 * @param String
	 *            url: URL of the srouce Excel file
	 * @param HttpServletResponse
	 *            res: Servlet response
	 * @return Vector result: error_code, xml_url, error_message
	 */
	public Vector convertDD_XML_split(String sourceURL, String sheet_param,
			HttpServletResponse res) throws GDEMException {
		OutputStream result = null;
		ByteArrayOutputStream out_stream_tmp = new ByteArrayOutputStream();
		ByteArrayInputStream in_stream_tmp = null;

		InputFile src = null;
		String error_mess = null;
		Vector v_result = null;

		try {

			src = new InputFile(sourceURL);
			src.setAuthentication(ticket);
			src.setTrustedMode(trustedMode);

			cnvFileName = Utils.isNullStr(src.getFileNameNoExtension()) ?
					DEFAULT_FILE_NAME:src.getFileNameNoExtension();
			if (res != null) {
				try {
					result = res.getOutputStream();
				} catch (IOException e) {
					_logger.error("Error getting response outputstream ", e);
					throw new GDEMException(
							"Error getting response outputstream "
									+ e.toString(), e);
				}
			}
			// Read inputstream into Bytearrayoutputstream
			Streams.drain(src.getSrcInputStream(), out_stream_tmp);
			// Detect the file format
			DDXMLConverter converter = DDXMLConverter
					.getConverter(out_stream_tmp);

			if (converter == null) {
				_logger
						.error(
								"Could not detect the format of source file. Converter waits MS Excel or OpenDocument Spreadsheet file.",
								null);
				throw new GDEMException(
						"Could not detect the format of source file. Converter waits MS Excel or OpenDocument Spreadsheet file.");
			}
			// create new inputstrema from tmp Bytearrayoutputstream
			in_stream_tmp = new ByteArrayInputStream(out_stream_tmp
					.toByteArray());

			v_result = converter.convertDD_XML_split(in_stream_tmp, result,
					sheet_param);

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
				throw new GDEMException("Error opening URL " + ioe.toString(),
						ioe);
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
				if (src != null)
					src.close();
			} catch (Exception e) {
			}
			try {
				if (in_stream_tmp != null)
					in_stream_tmp.close();
			} catch (Exception e) {
			}
			try {
				if (out_stream_tmp != null)
					out_stream_tmp.close();
			} catch (Exception e) {
			}
		}
		if (res != null) {
			try {
				res.setContentType("text/xml");
				res.setHeader("Content-Disposition","inline;filename=\"" + cnvFileName + ".xml\"");
				result.close();
			} catch (IOException e) {
				_logger.error("Error closing result ResponseOutputStream ", e);
				throw new GDEMException(
						"Error closing result ResponseOutputStream ", e);
			}
			return v_result;
		}
		// Creates response Vector

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
			// System.out.println( i + " - " + schema.get("xml_schema") );
			if (!schemas.contains(schema.get("xml_schema"))) {
				schemas.add(schema.get("xml_schema"));
			}
		}

		return schemas;
	}


	public boolean existsXMLSchema(String xmlSchema) throws GDEMException {
		ArrayList schemas = getXMLSchemas();
		return schemas.contains(xmlSchema);
	}

	public void setTicket(String _ticket) {
		this.ticket = _ticket;
	}

	public void setTrustedMode(boolean mode) {
		this.trustedMode = mode;
	}

	public boolean isHTTPRequest() {
		return isHttpResponse;
	}

	public void setHttpResponse(HttpMethodResponseWrapper httpResponse) {
		isHttpResponse=true;
		this.httpResponse = httpResponse; 
	}

	/* (non-Javadoc)
	 * @see eionet.gdem.conversion.ConversionServiceIF#convertPush(byte[],java.lang.String,java.lang.String)
	 */
	public Hashtable convertPush(byte[] fileBase64, String convertId, String fileName)throws GDEMException {
		
		InputStream fileInput = null;
		
		try{
			fileInput = null; // TODO make fileBase64 to InputStream
		}
		catch(Exception e){
			throw new GDEMException("Could not read base64 bytearray. " + e.getMessage());
		}
		ConvertXMLMethod convertMethod = new ConvertXMLMethod();
		convertMethod.setHttpResult(httpResponse);
		return convertMethod.convertPush(fileInput, convertId, fileName);	
		
	}
	/* (non-Javadoc)
	 * @see eionet.gdem.conversion.ConversionServiceIF#convertPush(java.lang.String,java.lang.String)
	 */
	public Hashtable convertPush(InputStream fileInput, String convertId, String fileName) throws GDEMException {
		
		ConvertXMLMethod convertMethod = new ConvertXMLMethod();
		convertMethod.setHttpResult(httpResponse);
		return convertMethod.convertPush(fileInput, convertId, fileName);	
	}
}
