package eionet.gdem.conversion;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	 * @param fileUrl
	 *            URL of the excel file for conversion.
	 * @return {@link ConversionResultDto}
	 * @throws GDEMException
	 *             if some error occurs.
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

	private static final void applyTransformation(ConversionResultDto result, String xmlTmpFileLocation,
			Vector<Object> stylesheets) throws FileNotFoundException, GDEMException, Exception,
			UnsupportedEncodingException, IOException {
		Map<Object, Object> stylesheet;
		InputStream xslFis;
		InputStream xmlFis;
		ByteArrayOutputStream out;
		XMLConverter xmlConv = new XMLConverter();

		HashMap<String, String> xmls = new HashMap<String, String>();
		Map<String, Map<Object, Object>> stylesheetMap = toMap(stylesheets);
		// key is conversion id, value is XML string.
		Map<String, String> doneConversions = new HashMap<String, String>();
		List<List<String>> conversionChains = buildConversionChains(stylesheetMap);
		String conversionId;
		// set of conversion id-s that are returned to end user.
		Set<String> toReturn = new HashSet<String>();

		for (List<String> chain : conversionChains) {
			for (int i = 0; i < chain.size(); i++) {
				conversionId = chain.get(i);
				stylesheet = stylesheetMap.get(conversionId);
				// avoid conversion duplication.
				if (!doneConversions.containsKey(conversionId)) {
					if (i == 0) {
						// apply transformation against content.xml
						xmlFis = new FileInputStream(xmlTmpFileLocation);
					} else {
						// apply transformation against previous generated XML.
						xmlFis = new ByteArrayInputStream(doneConversions.get(chain.get(i - 1)).getBytes("UTF-8"));
					}

					xslFis = new FileInputStream(Properties.xslFolder + File.separatorChar + stylesheet.get("xsl"));
					out = new ByteArrayOutputStream();
					xmlConv.convert(xmlFis, xslFis, out, "xml");
					doneConversions.put(conversionId, out.toString("UTF-8"));
					
					xslFis.close();
					xmlFis.close();
					out.close();
				}
			}

			// populate toReturn set
			// we return only those conversions which ID-s are last in each
			// chain
			toReturn.add(chain.get(chain.size() - 1));
		}

		// populate xmls map with values that should be returned.
		for (Map.Entry<String, String> me : doneConversions.entrySet()) {
			if (toReturn.contains(me.getKey())) {
				xmls.put(transformFileNameToExtension((String) stylesheetMap.get(me.getKey()).get("xsl"), "xml"), me
						.getValue());
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
		String actualFileExt = Utils.extractExtension(fname, null);

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

	@SuppressWarnings("unchecked")
	private static final Map<String, Map<Object, Object>> toMap(Vector<Object> stylesheets) {
		Map<String, Map<Object, Object>> result = new HashMap<String, Map<Object, Object>>();
		Map<Object, Object> map;
		String convertId;
		for (Object obj : stylesheets) {
			map = (Map<Object, Object>) obj;
			convertId = (String) map.get("convert_id");
			result.put(convertId, map);

		}
		return result;
	}

	private static final Map<String, String> toConvertIdOutputFileName(Map<String, Map<Object, Object>> stylesheetMap) {
		Map<String, String> result = new HashMap<String, String>();

		for (Map.Entry<String, Map<Object, Object>> me : stylesheetMap.entrySet()) {
			result.put(me.getKey(), (String) me.getValue().get("xsl"));
		}

		return result;
	}

	/**
	 * Builds conversion order.
	 * 
	 * @param stylesheetMap
	 * @return list of lists of conversion ID-s where the first element of the
	 *         list (second list) is conversion id that does not depend on any
	 *         of the conversion, the second depends on the first, etc, the last
	 *         has no conversion ID that is depended on it. Example (pairs
	 *         conversion id and depends on): 1 -> null, 2 -> 1, 3 -> 2, 4->
	 *         null, 5 -> 2; the result will be: [[1, 2, 3], [1, 2, 5], [4]]
	 */
	private static final List<List<String>> buildConversionChains(Map<String, Map<Object, Object>> stylesheetMap) {
		List<List<String>> result = new LinkedList<List<String>>();
		// final String convertIdKey = "convert_id";
		final String dependsOnKey = "depends_on";

		Map<String, String> convertIdDependsOnMap = new HashMap<String, String>();
		// map where key is depends on field and value is a collection on
		// convert_ids
		Map<String, Collection<String>> dependOnConvertIdMap = new HashMap<String, Collection<String>>();
		String dependsOn;
		String convId;
		Collection<String> convIds;

		for (Map.Entry<String, Map<Object, Object>> me : stylesheetMap.entrySet()) {
			dependsOn = (String) me.getValue().get(dependsOnKey);
			// populate convertIdDependsOnMap
			convertIdDependsOnMap.put(me.getKey(), dependsOn);

			// populate dependOnConvertIdMap
			if (!Utils.isNullStr(dependsOn)) {
				convIds = dependOnConvertIdMap.get(dependsOn);
				if (convIds == null) {
					convIds = new HashSet<String>();
					dependOnConvertIdMap.put(dependsOn, convIds);
				}

				convIds.add(me.getKey());
			}
		}

		List<String> idsChain;
		for (Map.Entry<String, String> me : convertIdDependsOnMap.entrySet()) {
			convId = me.getKey();
			convIds = dependOnConvertIdMap.get(convId);

			if (convIds == null || convIds.size() == 0) {
				// this is id that is not be depended on.
				// start build a chain
				idsChain = new ArrayList<String>();
				idsChain.add(convId);
				// get a depend_on from convertIdDependsOnMap
				dependsOn = convertIdDependsOnMap.get(convId);
				// if depend_on is null - the idsChain has one element
				// else get a depend_on from convertIdDependsOnMap of depend_on
				// etc
				while (dependsOn != null && !idsChain.contains(dependsOn)) { // avoid
					// potential
					// cyclic
					// dependency
					convId = dependsOn;
					dependsOn = convertIdDependsOnMap.get(convId);
					idsChain.add(convId);
				}
				Collections.reverse(idsChain);
				result.add(idsChain);
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
		 ConversionResultDto res = new ExcelToMultipleXML().convert(new
		 FileInputStream("c:\\temp\\lichab.xls"),
		 "hz.xls");
		 System.err.println(res.getStatusDescription());
		 System.err.println(res.getConvertedXmls());
		//testOrderByByDOn();

	}

	private static void testOrderByByDOn() {
		Map<String, Map<Object, Object>> stylesheets = new HashMap<String, Map<Object, Object>>();
		stylesheets.put("1", getObjectMap("1", null));
		stylesheets.put("2", getObjectMap("2", "1"));
		stylesheets.put("3", getObjectMap("3", "2"));
		stylesheets.put("4", getObjectMap("4", null));
		stylesheets.put("5", getObjectMap("5", "2"));

		System.err.println(buildConversionChains(stylesheets));
	}

	private static Map<Object, Object> getObjectMap(String convertId, String dependsOn) {
		Map<Object, Object> result = new HashMap<Object, Object>();
		result.put("convert_id", convertId);
		result.put("depends_on", dependsOn);
		return result;
	}

}
