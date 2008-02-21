/*
 * Created on 20.02.2008
 */
package eionet.gdem.conversion;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Hashtable;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.conversion.converters.ConvertContext;
import eionet.gdem.conversion.converters.ConvertStartegy;
import eionet.gdem.conversion.converters.ExcelConverter;
import eionet.gdem.conversion.converters.HTMLConverter;
import eionet.gdem.conversion.converters.OdsConverter;
import eionet.gdem.conversion.converters.PDFConverter;
import eionet.gdem.conversion.converters.TextConverter;
import eionet.gdem.conversion.converters.XMLConverter;
import eionet.gdem.dcm.Conversion;
import eionet.gdem.dcm.XslGenerator;
import eionet.gdem.dcm.results.HttpMethodResponseWrapper;
import eionet.gdem.dto.ConversionDto;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.db.dao.IConvTypeDao;
import eionet.gdem.services.db.dao.IStyleSheetDao;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Utils;

/**
 * Conversion Service methods that executes XML conversions to other file types using XSL transformations.
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS
 */

public class ConvertXMLMethod extends ConversionServiceMethod {

	private IStyleSheetDao styleSheetDao = GDEMServices.getDaoService()
			.getStyleSheetDao();
	private IConvTypeDao convTypeDao = GDEMServices.getDaoService()
			.getConvTypeDao();

	private static LoggerIF _logger = GDEMServices.getLogger();

	public Hashtable convert(String sourceURL, String convertId)
			throws GDEMException {
		OutputStream result = null;
		String convError = null;
		String cnvFileName = null;
		String cnvTypeOut = null;
		String cnvFileExt = null;
		String cnvContentType = null;

		_logger.debug("sourceURL=" + sourceURL + "convertId=" + convertId);
		if (convertId.startsWith("DD")) {
			return convertDDTable(sourceURL, convertId);
		} else {

			Hashtable h = new Hashtable();
			String xslFile = null;
			String outputFileName = null;
			InputFile src = null;

			try {
				src = new InputFile(sourceURL);
				src.setAuthentication(getTicket());
				src.setTrustedMode(isTrustedMode());
				cnvFileName = Utils.isNullStr(src.getFileNameNoExtension()) ? DEFAULT_FILE_NAME
						: src.getFileNameNoExtension();

				try {
					HashMap styleSheetData = styleSheetDao
							.getStylesheetInfo(convertId);

					if (styleSheetData == null)
						throw new GDEMException(
								"No stylesheet info for convertID= "
										+ convertId);
					xslFile = getXslFolder()
							+ (String) styleSheetData.get("xsl");
					cnvTypeOut = (String) styleSheetData
							.get("content_type_out");

					Hashtable convType = convTypeDao.getConvType(cnvTypeOut);

					if (convType != null) {
						try {
							cnvContentType = (String) convType
									.get("content_type");
							cnvFileExt = (String) convType.get("file_ext");
						} catch (Exception e) {
							_logger.error("error getting conv types", e);
							// Take no action, use default params
						}
					}
					if (cnvContentType == null)
						cnvContentType = "text/plain;charset=UTF-8";
					if (cnvFileExt == null)
						cnvFileExt = "txt";

				} catch (Exception e) {
					_logger.error("error getting con types", e);
					throw new GDEMException(
							"Error getting stylesheet info from repository for "
									+ convertId, e);
				}
				if (isHttpRequest()) {
					try {
						HttpMethodResponseWrapper httpResponse = getHttpResponse();
						httpResponse.setContentType(cnvContentType);
						httpResponse.setContentDisposition(cnvFileName + "." + cnvFileExt);
						result = httpResponse.getOutputStream();
					} catch (IOException e) {
						_logger
								.error("Error getting response outputstream ",
										e);
						throw new GDEMException(
								"Error getting response outputstream "
										+ e.toString(), e);
					}
				}

				outputFileName = executeConversion(src.getSrcInputStream(),
						xslFile, result, src.getCdrParams(),cnvFileExt, cnvContentType);

			} catch (MalformedURLException mfe) {
				_logger.error("Bad URL", mfe);
				throw new GDEMException("Bad URL", mfe);
			} catch (IOException ioe) {
				_logger.error("Error opening URL", ioe);
				throw new GDEMException("Error opening URL", ioe);
			} catch (GDEMException ge) {
				_logger.error("Error converting", ge);
				throw ge;
			} catch (Exception e) {
				_logger.error("Error converting", e);
				convError = "Error converting";
				throw new GDEMException("Convert error: " + e.toString(), e);
			} finally {
				try {
					if (src != null)
						src.close();
				} catch (Exception e) {
				}
			}

			h.put("content-type", cnvContentType);
			h.put("filename", cnvFileName + "." + cnvFileExt);
			
			if (isHttpRequest()) {
				return h;
			}

			byte[] file = Utils.fileToBytes(outputFileName);
			h.put("content", file);
			try {
				Utils.deleteFile(outputFileName);
			} catch (Exception e) {

				_logger.error("Couldn't delete the result file: "
						+ outputFileName, e);
			}

			return h;
		}
	}

	public Hashtable convertDDTable(String sourceURL, String convertId)
			throws GDEMException {
		OutputStream result = null;
		Hashtable h = new Hashtable();
		String outputFileName = null;
		InputFile src = null;
		String tblId = "";
		String convId = "";
		String convError = null;
		String cnvFileName = null;
		String cnvTypeOut = null;
		String cnvFileExt = null;
		String cnvContentType = null;

		// prase idtable and id conversion
		if (convertId.startsWith("DD")) {
			tblId = convertId.substring(6, convertId.indexOf("_CONV"));
			convId = convertId.substring(convertId.indexOf("_CONV") + 5,
					convertId.length());
		}

		ConversionDto conv = Conversion.getConversionById(convId);
		String format = Properties.metaXSLFolder + File.separatorChar
				+ conv.getStylesheet();
		String url = Properties.ddURL + "/GetTableDef?id=" + tblId;
		// xslFile = Properties.gdemURL + "/do/getStylesheet?id=" + tblId +
		// "&conv=" + convId;

		// pozvati konverziju za sourceURL i xslURL
		try {
			ByteArrayInputStream byteIn = XslGenerator.convertXML(url, format);
			src = new InputFile(sourceURL);
			src.setAuthentication(getTicket());
			src.setTrustedMode(isTrustedMode());
			cnvFileName = Utils.isNullStr(src.getFileNameNoExtension()) ? DEFAULT_FILE_NAME
					: src.getFileNameNoExtension();

			try {
				cnvTypeOut = conv.getResultType();
				Hashtable convType = convTypeDao.getConvType(cnvTypeOut);

				if (convType != null) {
					try {
						cnvContentType = (String) convType.get("content_type");
						cnvFileExt = (String) convType.get("file_ext");
					} catch (Exception e) {
						_logger.error("Error getting conversion types ", e);
						// Take no action, use default params
					}
				}
				if (cnvContentType == null)
					cnvContentType = "text/plain";
				if (cnvFileExt == null)
					cnvFileExt = "txt";

			} catch (Exception e) {
				_logger.error(
						"Error getting stylesheet info from repository for "
								+ convertId, e);
				throw new GDEMException(
						"Error getting stylesheet info from repository for "
								+ convertId, e);
			}
			if (isHttpRequest()) {
				try {
					HttpMethodResponseWrapper httpResult = getHttpResponse();
					httpResult.setContentType(cnvContentType);
					httpResult.setContentDisposition(cnvFileName + "."
							+ cnvFileExt);
					result = httpResult.getOutputStream();

				} catch (IOException e) {
					_logger.error("Error getting response outputstream ", e);
					throw new GDEMException(
							"Error getting response outputstream "
									+ e.toString(), e);
				}
			}

			outputFileName = executeConversion(src.getSrcInputStream(), byteIn,
					result, src.getCdrParams(),cnvFileExt, cnvContentType);

		} catch (MalformedURLException mfe) {
			_logger.error("Bad URL", mfe);
			throw new GDEMException("Bad URL", mfe);
		} catch (IOException ioe) {
			_logger.error("Error opening URL", ioe);
			throw new GDEMException("Error opening URL", ioe);
		} catch (GDEMException ge) {
			throw ge;
		} catch (Exception e) {
			_logger.error("Error converting", e);
			throw new GDEMException("Error converting", e);
		} finally {
			try {
				if (src != null)
					src.close();
			} catch (Exception e) {
				_logger.error("Error converting", e);
			}
		}

		h.put("content-type", cnvContentType);
		h.put("filename", cnvFileName + "." + cnvFileExt);
		if (isHttpRequest()) {
			return h;
		}
		// log("========= going to bytes " + htmlFileName);

		byte[] file = Utils.fileToBytes(outputFileName);
		// log("========= bytes ok");

		h.put("content", file);
		try {
			// Utils.deleteFile(sourceFile);
			// deleteFile(htmlFileName);
			Utils.deleteFile(outputFileName);
		} catch (Exception e) {
			_logger.error("Couldn't delete the result file: " + outputFileName,
					e);
		}

		return h;

	}
	public Hashtable convertPush(InputStream fileInput, String convertId, String fileName) throws GDEMException {
		
		try{
		//Store the file into temporar folder
		String folderName =Utils.createUniqueTmpFolder();
		String filePath = folderName + File.separator + (Utils.isNullStr(fileName)?DEFAULT_FILE_NAME:fileName);

		File file = new File(filePath);
		//store inputstream into file

		String fileUri = Utils.getURIfromPath(fileName,false);

		//TODO - unzip the file if it is a zip file
		// 		- check if it is a XML file
		//		- call convert method
		}
		finally{
			try{
				Utils.deleteParentFolder(fileName);		
			} catch (Exception e) {

				_logger.error("Couldn't delete the temporary file: "
					+ fileName, e);
			}
		}

		return convert(fileName, convertId);
		
	}
	private String executeConversion(InputStream source, Object xslt,
			OutputStream result, HashMap params, String cnvFileExt,
			String cnvTypeOut) throws Exception {
		ConvertContext ctx = new ConvertContext(source, xslt, result,
				cnvFileExt);
		ConvertStartegy cs = null;
		if (cnvTypeOut.startsWith("HTML")) {
			cs = new HTMLConverter();
		} else if (cnvTypeOut.equals("PDF")) {
			cs = new PDFConverter();
		} else if (cnvTypeOut.equals("EXCEL")) {
			cs = new ExcelConverter();
		} else if (cnvTypeOut.equals("XML")) {
			cs = new XMLConverter();
		} else if (cnvTypeOut.equals("ODS")) {
			cs = new OdsConverter();
		} else {
			cs = new TextConverter();
		}
		cs.setXslParams(params);
		return ctx.executeConversion(cs);
	}
}
