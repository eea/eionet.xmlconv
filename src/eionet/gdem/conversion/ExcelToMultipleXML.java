package eionet.gdem.conversion;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.conversion.converters.XMLConverter;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.DCMDaoFactory;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Utils;

/**
 * 
 * @author Vadim Gerassimov
 * 
 */
public class ExcelToMultipleXML {

	private static final Log LOGGER = LogFactory.getLog(ExcelToMultipleXML.class);

	private static final String SCHEMA_SHEET_NAME = "DO_NOT_DELETE_THIS_SHEET";
	private static final int SCHEMA_ROW_IDX = 3;
	private static final int RELEASE_DATE_ROW_IDX = 4;
	private static final int SCHEMA_CELL_IDX = 0;
	private static final int RELEASE_DATE_CELL_IDX = 0;

	/**
	 * 
	 * @param fileUrl URL of the excel file for conversion.
	 * @return {@link ConversionResultDto}
	 * @throws GDEMException if some error occurs.
	 */
	public ConversionResultDto convert(String fileUrl) throws GDEMException {
		ConversionResultDto result = new ConversionResultDto();
		result.setStatusCode(ConversionResultDto.STATUS_OK);
		result.setStatusDescription("OK.");
		
		if (Utils.isNullStr(fileUrl)) {
			result.setStatusCode(ConversionResultDto.STATUS_ERR_VALIDATION);
			result.setStatusDescription("Empty URL.");
		} else {
			InputFile inputFile;
			try {
				inputFile = new InputFile(fileUrl);
				inputFile.setTrustedMode(true);
				result = convert(inputFile.getSrcInputStream(), inputFile.getFileName());
			} catch (MalformedURLException e) {
				result.setStatusCode(ConversionResultDto.STATUS_ERR_SYSTEM);
				result.setStatusDescription(e.getMessage());
				LOGGER.error("", e);
			} catch (IOException e) {
				result.setStatusCode(ConversionResultDto.STATUS_ERR_SYSTEM);
				result.setStatusDescription(e.getMessage());
				LOGGER.error("", e);
			}
		}
		
		return result;
	}
	
	/**
	 * Converts Excel file to XML by specified XSL-s.
	 * 
	 * @param file
	 *            file binary data.
	 * @param fileName
	 *            file name.
	 * @return {@link ConversionResultDto}
	 * @throws GDEMException
	 *             if some error occurs.
	 */
	@SuppressWarnings("unchecked")
	public ConversionResultDto convert(InputStream source, String fileName) throws GDEMException {
		ConversionResultDto result = new ConversionResultDto();
		result.setStatusCode(ConversionResultDto.STATUS_OK);
		result.setStatusDescription("OK.");

		String[] metadata = new String[2];
		String xlsTmpFileLocation = null;
		String xmlTmpFileLocation = null;

		// save xls to tmp file
		try {
			xlsTmpFileLocation = saveXlsToTmpFile(source, fileName);
		} catch (Exception e) {
			LOGGER.error("", e);
			result.setStatusCode(ConversionResultDto.STATUS_ERR_SYSTEM);
			result.setStatusDescription(e.getMessage());
		}

		// extract metadata
		if (!ConversionResultDto.STATUS_ERR_SYSTEM.equals(result.getStatusCode())) {
			try {
				metadata = getSchemaAndReleaseDate(new FileInputStream(xlsTmpFileLocation));
			} catch (Exception e) {
				// delete tmp xls file
				Utils.deleteFile(xlsTmpFileLocation);
				LOGGER.error("Error during extraction methadata from excel file", e);
				result.setStatusCode(ConversionResultDto.STATUS_ERR_SYSTEM);
				result.setStatusDescription(e.getMessage());
			}
		}

		if (!ConversionResultDto.STATUS_ERR_SYSTEM.equals(result.getStatusCode())) {
			if (Utils.isNullStr(metadata[0]) || Utils.isNullStr(metadata[1])) {
				result.setStatusCode(ConversionResultDto.STATUS_ERR_SCHEMA_NOT_FOUND);
				result.setStatusDescription("Unknown format. Schema and version are mandatory.");
			} else {
				try {
					// convert xls to xml and save result to tmp file
					xmlTmpFileLocation = convertToXml(xlsTmpFileLocation, fileName);
					ISchemaDao schemaDao = DCMDaoFactory.getDaoFactory(DCMDaoFactory.MYSQL_DB).getSchemaDao();
					String schemaUrl = metadata[0] + "?" + metadata[1];
					String schemaId = schemaDao.getSchemaID(schemaUrl);

					if (schemaId != null) {
						HashMap<String, Object> schemaInfo = schemaDao.getSchema(schemaId);
						// find stylesheets by schema url with version
						Vector<Object> stylesheets = schemaDao.getSchemaStylesheets(schemaId);
						// validate schema language - EXCEL expected.
						if (!"EXCEL".equalsIgnoreCase((String) schemaInfo.get("schema_lang"))) {
							result.setStatusCode(ConversionResultDto.STATUS_ERR_SCHEMA_NOT_FOUND);
							result.setStatusDescription("Schema '" + schemaUrl
									+ "' with schema lang: EXCEL not found. Found schema lang: "
									+ schemaInfo.get("schema_lang"));
						} else if (stylesheets != null) {
							applyTransformation(result, xmlTmpFileLocation, stylesheets);
						}
					} else {
						result.setStatusCode(ConversionResultDto.STATUS_ERR_SCHEMA_NOT_FOUND);
						result.setStatusDescription("Schema '" + schemaUrl + "' was not found");
					}
				} catch (Exception e) {
					LOGGER.error("", e);
					result.setStatusCode(ConversionResultDto.STATUS_ERR_SYSTEM);
					result.setStatusDescription(e.getMessage());
				} finally {
					Utils.deleteFile(xmlTmpFileLocation);
					Utils.deleteFile(xlsTmpFileLocation);
				}
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private static final void applyTransformation(ConversionResultDto result, String xmlTmpFileLocation,
			Vector<Object> stylesheets) throws FileNotFoundException, GDEMException, Exception,
			UnsupportedEncodingException, IOException {
		HashMap<Object, Object> stylesheet;
		FileInputStream xslFis;
		FileInputStream xmlFis;
		ByteArrayOutputStream out;
		XMLConverter xmlConv = new XMLConverter();

		HashMap<String, String> xmls = new HashMap<String, String>();

		// apply transformation of the xml file (converted from excel) with each
		// template
		for (Object st : stylesheets) {
			stylesheet = (HashMap<Object, Object>) st;
			// support only XML style sheets.
			if ("XML".equalsIgnoreCase((String) stylesheet.get("content_type_out"))) {
				xslFis = new FileInputStream(Properties.xslFolder + File.separatorChar + stylesheet.get("xsl"));
				xmlFis = new FileInputStream(xmlTmpFileLocation);
				out = new ByteArrayOutputStream();

				xmlConv.convert(xmlFis, xslFis, out, "xml");
				xmls.put(transformFileNameToExtension((String) stylesheet.get("xsl"), "xml"), out.toString("UTF-8"));

				xslFis.close();
				xmlFis.close();
				out.close();
			} else {
				LOGGER.warn("Unsupported content type out: " + stylesheet.get("content_type_out")
						+ "; XML is only supported");
			}
		}

		result.setConvertedXmls(xmls);
	}

	private static String convertToXml(String xlsFilePath, String fileName) throws Exception {
		String tmpOds = Utils.getUniqueTmpFileName(transformFileNameToExtension(fileName, "ods"));
		String result = Utils.getUniqueTmpFileName(transformFileNameToExtension(fileName, "xml"));

		try {
			// connect to an OpenOffice.org instance
			OpenOfficeConnection connection = new SocketOpenOfficeConnection(Properties.openOfficePort);
			connection.connect();

			// convert
			DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
			converter.convert(new File(xlsFilePath), new File(tmpOds));

			// close the connection
			connection.disconnect();
			// extract content.xml from ods (zip archive)
			ZipFile zipFile = new ZipFile(tmpOds);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			ZipEntry entry;

			while (entries.hasMoreElements()) {
				entry = entries.nextElement();
				if (!entry.isDirectory() && "content.xml".equals(entry.getName())) {
					copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(
							new FileOutputStream(result)));
				}
			}

			zipFile.close();
		} catch (Exception e) {
			throw e;
		} finally {
			// delete tmp ods file
			Utils.deleteFile(tmpOds);
		}

		return result;
	}

	private static final String saveXlsToTmpFile(InputStream source, String fileName) throws Exception {
		String result = Utils.getUniqueTmpFileName(transformFileNameToExtension(fileName, "xls"));
		copyInputStream(source, new BufferedOutputStream(new FileOutputStream((result))));

		return result;
	}

	private static final String transformFileNameToExtension(String fileName, String ext) {
		String fname = fileName;
		String actualFileExt = Utils.extractExtension(fname);

		if (Utils.isNullStr(actualFileExt)) {
			fname = fname + "." + ext;
		} else if (!ext.equalsIgnoreCase(actualFileExt)) {
			fname = fname.substring(0, fname.lastIndexOf('.') + 1) + ext;
		}

		return fname;
	}

	/*
	 * First element of the array is schema, second - release date.
	 */
	private String[] getSchemaAndReleaseDate(InputStream xlsSource) throws GDEMException {
		String[] result = new String[2];

		result[0] = null;
		result[1] = null;

		POIFSFileSystem fs;
		HSSFWorkbook wb;
		try {
			fs = new POIFSFileSystem(xlsSource);
			wb = new HSSFWorkbook(fs);
		} catch (Exception e) {
			throw new GDEMException("ErrorConversionHandler - couldn't open Excel file: " + e.toString());
		}

		HSSFSheet schemaSheet = wb.getSheet(SCHEMA_SHEET_NAME);

		result[0] = extractCellValue(schemaSheet, SCHEMA_ROW_IDX, SCHEMA_CELL_IDX);
		result[1] = extractCellValue(schemaSheet, RELEASE_DATE_ROW_IDX, RELEASE_DATE_CELL_IDX);

		try {
			xlsSource.close();
		} catch (IOException e) {
			throw new GDEMException(e.getMessage(), e);
		}

		return result;
	}

	@SuppressWarnings("deprecation")
	private static final String extractCellValue(HSSFSheet schemaSheet, int rowId, int cellId) {
		String result = null;

		if (schemaSheet != null) {
			HSSFRow row = null;
			HSSFCell cell = null;

			// first get schema
			row = schemaSheet.getRow(rowId);
			if (row != null) {
				cell = row.getCell((short) cellId);
				if (cell != null) {
					result = cell.getStringCellValue();

				}
			}
		}

		return result;
	}

	private static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

	/**
	 * The DTO structure that keeps conversion result from Excel to XML.
	 * 
	 * @author Vadim Gerassimov
	 * 
	 */
	public static class ConversionResultDto {

		/**
		 * Value: 0 Indicates that conversion went OK.
		 */
		public static final String STATUS_OK = "0";

		/**
		 * Value: 1 Indicates validation errors (not enough some data)
		 */
		public static final String STATUS_ERR_VALIDATION = "1";

		/**
		 * Value: 2 Indicates that some unpredictable system error occurred.
		 */
		public static final String STATUS_ERR_SYSTEM = "2";

		/**
		 * Value 3: Indicates that the schema by URL and version was not found.
		 */
		public static final String STATUS_ERR_SCHEMA_NOT_FOUND = "3";

		/**
		 * Conversion status code. See Dto public constants.
		 */
		private String statusCode;

		/**
		 * Status description. In case of errors - explained error information
		 */
		private String statusDescription;

		/**
		 * Converted XML files according to style sheets. Map key is file name,
		 * map value is file content.
		 */
		private Map<String, String> convertedXmls;

		public ConversionResultDto() {
			super();
		}

		/**
		 * @return the statusCode
		 */
		public String getStatusCode() {
			return statusCode;
		}

		/**
		 * @param statusCode
		 *            the statusCode to set
		 */
		public void setStatusCode(String statusCode) {
			this.statusCode = statusCode;
		}

		/**
		 * @return the statusDescription
		 */
		public String getStatusDescription() {
			return statusDescription;
		}

		/**
		 * @param statusDescription
		 *            the statusDescription to set
		 */
		public void setStatusDescription(String statusDescription) {
			this.statusDescription = statusDescription;
		}

		/**
		 * @return the convertedXmls
		 */
		public Map<String, String> getConvertedXmls() {
			return convertedXmls;
		}

		/**
		 * @param convertedXmls
		 *            the convertedXmls to set
		 */
		public void setConvertedXmls(Map<String, String> convertedXmls) {
			this.convertedXmls = convertedXmls;
		}

	}

	public static void main(String[] args) throws Exception {
		GDEMServices.setTestConnection(true);
		ConversionResultDto res = new ExcelToMultipleXML().convert(new FileInputStream("c:\\temp\\lichab.xls"),
				"hz.xls");
		System.err.println(res.getStatusDescription());
		System.err.println(res.getConvertedXmls());
	}

}
