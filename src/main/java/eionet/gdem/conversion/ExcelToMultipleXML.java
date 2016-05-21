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
import java.util.Date;
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

import org.apache.commons.io.IOUtils;


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
import eionet.gdem.dto.ConversionResultDto;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.DCMDaoFactory;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Splits Excel to multiple XML files.
 * @author Vadim Gerassimov
 * @author George Sofianos
 */
public class ExcelToMultipleXML {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelToMultipleXML.class);

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
            InputFile inputFile = null;
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
            } finally {
                try {
                    if (inputFile != null) {
                        inputFile.close();
                    }
                } catch (Exception e) {
                }
            }

        }

        return result;
    }

    /**
     * Converts Excel file to XML by specified XSL-s.
     *
     * @param source
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
                                    + "' with schema lang: EXCEL not found. Found schema lang: " + schemaInfo.get("schema_lang"));
                        }
                        // validate expiration date - should be in the future.
                        else if (!Utils.isNullStr(schemaInfo.get("expire_date"))
                                && (new Date()).after(Utils.parseDate((String) schemaInfo.get("expire_date"),
                                        "yyyy-MM-dd HH:mm:ss"))) {
                            result.setStatusCode(ConversionResultDto.STATUS_ERR_SCHEMA_NOT_FOUND);
                            result.setStatusDescription("The conversion to XML is not allowed for obsolete Schemas! "
                                    + " This version of Schema '" + schemaUrl + "' expired on: "
                                    + (String) schemaInfo.get("expire_date"));
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
                    if (!LOGGER.isDebugEnabled()) {
                        Utils.deleteFile(xmlTmpFileLocation);
                        Utils.deleteFile(xlsTmpFileLocation);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Applies transformation
     * @param result Result transfer object
     * @param xmlTmpFileLocation XML temporary file location
     * @param stylesheets Stylesheets
     * @throws FileNotFoundException If file is not found
     * @throws GDEMException If an error occurs
     * @throws Exception If an error occurs
     * @throws UnsupportedEncodingException Unsupported encoding exception
     * @throws IOException IO Exception
     */
    private static void
            applyTransformation(ConversionResultDto result, String xmlTmpFileLocation, Vector<Object> stylesheets)
                    throws FileNotFoundException, GDEMException, Exception, UnsupportedEncodingException, IOException {
        Map<Object, Object> stylesheet;
        InputStream xslFis = null;
        InputStream xmlFis = null;
        ByteArrayOutputStream out = null;
        XMLConverter xmlConv = new XMLConverter();

        HashMap<String, byte[]> xmls = new HashMap<String, byte[]>();
        Map<String, Map<Object, Object>> stylesheetMap = toMap(stylesheets);
        // key is conversion id, value is XML string.
        Map<String, byte[]> doneConversions = new HashMap<String, byte[]>();
        List<List<String>> conversionChains = buildConversionChains(stylesheetMap);
        String conversionId;
        // set of conversion id-s that are returned to end user.
        Set<String> toReturn = new HashSet<String>();

        for (List<String> chain : conversionChains) {
            for (int i = 0; i < chain.size(); i++) {
                conversionId = chain.get(i);
                stylesheet = stylesheetMap.get(conversionId);
                LOGGER.debug("convert->i=" + i + ";conversionId=" + conversionId + ";xsl=" + stylesheet.get("xsl"));
                // avoid conversion duplication.
                if (!doneConversions.containsKey(conversionId)) {
                    if (i == 0) {
                        // apply transformation against content.xml
                        xmlFis = new FileInputStream(xmlTmpFileLocation);
                        LOGGER.debug("use content.xml");
                    } else {
                        // apply transformation against previous generated XML.
                        xmlFis = new ByteArrayInputStream(doneConversions.get(chain.get(i - 1)));

                        LOGGER.debug("use previous generated XML");
                    }
                    try {
                        xslFis = new FileInputStream(Properties.xslFolder + File.separatorChar + stylesheet.get("xsl"));
                        out = new ByteArrayOutputStream();
                        xmlConv.convert(xmlFis, xslFis, out, "xml");
                        doneConversions.put(conversionId, out.toByteArray());

                        if (!LOGGER.isDebugEnabled()) {
                            // store tmp files in server, if debug is enabled
                            ByteArrayInputStream tmpFis = null;
                            FileOutputStream tmpFile = null;
                            try {
                                tmpFis = new ByteArrayInputStream(out.toByteArray());
                                tmpFile =
                                        new FileOutputStream(Utils.getUniqueTmpFileName(transformFileNameToExtension("tmpOutput",
                                                "xml")));
                                IOUtils.copy(tmpFis, tmpFile);
                            } finally {
                                IOUtils.closeQuietly(tmpFile);
                                IOUtils.closeQuietly(tmpFis);
                            }
                        }
                    } finally {
                        IOUtils.closeQuietly(xslFis);
                        IOUtils.closeQuietly(xmlFis);
                        IOUtils.closeQuietly(out);
                    }
                }
            }

            // populate toReturn set
            // we return only those conversions which ID-s are last in each
            // chain
            toReturn.add(chain.get(chain.size() - 1));
        }

        // populate xmls map with values that should be returned.
        for (Map.Entry<String, byte[]> me : doneConversions.entrySet()) {
            if (toReturn.contains(me.getKey())) {
                xmls.put(transformFileNameToExtension((String) stylesheetMap.get(me.getKey()).get("xsl"), "xml"), me.getValue());
            }
        }

        result.setConvertedXmls(xmls);
    }

    /**
     * Converts Excel file to XML
     * @param xlsFilePath Excel file
     * @param fileName File name
     * @return Converted file
     * @throws Exception If an error occurs.
     */
    private static String convertToXml(String xlsFilePath, String fileName) throws Exception {
        String tmpOds = Utils.getUniqueTmpFileName(transformFileNameToExtension(fileName, "ods"));
        String result = Utils.getUniqueTmpFileName(transformFileNameToExtension(fileName, "xml"));

        try {
            // connect to an OpenOffice.org instance
            OpenOfficeConnection connection = new SocketOpenOfficeConnection(Properties.openOfficeHost, Properties.openOfficePort);
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
                    copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(result)));
                }
            }

            zipFile.close();
        } catch (Exception e) {
            throw e;
        } finally {
            // delete tmp ods file
            if (!LOGGER.isDebugEnabled()) {
                Utils.deleteFile(tmpOds);
            }
        }

        return result;
    }

    /**
     * Saves Excel file to temporary file
     * @param source Source InputStream
     * @param fileName  File name
     * @return Result
     * @throws Exception If an error occurs
     */
    private static String saveXlsToTmpFile(InputStream source, String fileName) throws Exception {
        String result = Utils.getUniqueTmpFileName(transformFileNameToExtension(fileName, "xls"));
        copyInputStream(source, new BufferedOutputStream(new FileOutputStream((result))));

        return result;
    }

    /**
     * Adds extension to file name
     * @param fileName File name
     * @param ext Extension
     * @return Transformed name
     */
    private static String transformFileNameToExtension(String fileName, String ext) {
        String fname = fileName;
        String actualFileExt = Utils.extractExtension(fname, null);

        if (Utils.isNullStr(actualFileExt)) {
            fname = fname + "." + ext;
        } else if (!ext.equalsIgnoreCase(actualFileExt)) {
            fname = fname.substring(0, fname.lastIndexOf('.') + 1) + ext;
        }

        return fname;
    }

    /**
     * First element of the array is schema, second - release date.
     * @param xlsSource Excel InputStream
     * @throws GDEMException If an error occurs.
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

    /**
     * Extracts cell value
     * @param schemaSheet Schema sheet
     * @param rowId Row id
     * @param cellId Cell id
     * @return Cell value
     */
    @SuppressWarnings("deprecation")
    private static String extractCellValue(HSSFSheet schemaSheet, int rowId, int cellId) {
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

    /**
     * Converts stylesheets vector to map
     * @param stylesheets Stylesheets
     * @return Map
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Map<Object, Object>> toMap(Vector<Object> stylesheets) {
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

    /**
     * Converts stylesheetmap
     * @param stylesheetMap Stylesheet map
     * @return Converted map
     */
    private static Map<String, String> toConvertIdOutputFileName(Map<String, Map<Object, Object>> stylesheetMap) {
        Map<String, String> result = new HashMap<String, String>();

        for (Map.Entry<String, Map<Object, Object>> me : stylesheetMap.entrySet()) {
            result.put(me.getKey(), (String) me.getValue().get("xsl"));
        }

        return result;
    }

    /**
     * Builds conversion order.
     *
     * @param stylesheetMap Stylesheet map
     * @return list of lists of conversion ID-s where the first element of the list (second list) is conversion id that does not
     *         depend on any of the conversion, the second depends on the first, etc, the last has no conversion ID that is depended
     *         on it. Example (pairs conversion id and depends on): 1 -> null, 2 -> 1, 3 -> 2, 4-> null, 5 -> 2; the result will be:
     *         [[1, 2, 3], [1, 2, 5], [4]]
     */
    private static List<List<String>> buildConversionChains(Map<String, Map<Object, Object>> stylesheetMap) {
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

    /**
     * Copies InputStream
     * @param in InputStream
     * @param out OutputStream
     * @throws IOException IO Exception
     */
    private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        try {
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Gets object map
     * @param convertId Convert id
     * @param dependsOn Depends on
     * @return Object map
     */
    private static Map<Object, Object> getObjectMap(String convertId, String dependsOn) {
        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("convert_id", convertId);
        result.put("depends_on", dependsOn);
        return result;
    }

}
