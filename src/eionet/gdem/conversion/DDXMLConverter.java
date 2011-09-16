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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.XMLReader;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.conversion.datadict.DDElement;
import eionet.gdem.conversion.datadict.DD_XMLInstance;
import eionet.gdem.conversion.datadict.DD_XMLInstanceHandler;
import eionet.gdem.conversion.datadict.DataDictUtil;
import eionet.gdem.conversion.excel.ExcelUtils;
import eionet.gdem.conversion.odf.OpenDocumentUtils;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.ConversionLogDto;
import eionet.gdem.dto.ConversionLogDto.ConversionLogType;
import eionet.gdem.dto.ConversionResultDto;
import eionet.gdem.utils.Utils;

/**
 * Abstract class contains the logic for converting spreadsheet like datafiles into DataDictionary XML Instance format. The
 * spreadsheets should be extracted from DD and include XML Schema information. Currently supported formats are MS Excel and
 * OpenDocument Spreadsheet.
 */
public abstract class DDXMLConverter {

    /** */
    private static final Log LOGGER = LogFactory.getLog(DDXMLConverter.class);

    public static final String META_SHEET_NAME = "-meta";
    public static final String META_SHEET_NAME_ODS = "_meta";

    protected SourceReaderIF sourcefile = null;
    private boolean httpResponse = false;

    public DDXMLConverter() {
    }

    public abstract SourceReaderIF getSourceReader();

    public abstract String getSourceFormatName();

    public static DDXMLConverter getConverter(File file) {

        try {
            if (ExcelUtils.isExcelFile(new FileInputStream(file))) {
                LOGGER.debug("Excel 2003 or older document");
                return new Excel2XML();
            }
        } catch (Exception e) {
        }

        try {
            if (ExcelUtils.isExcel2007File(new FileInputStream(file))) {
                LOGGER.debug("Excel 2007 document");
                return new Excel20072XML();
            }
        } catch (Exception e) {

        }

        // If it is a zip file, then it is OpenDocument
        try {
            if (OpenDocumentUtils.isSpreadsheetFile(new FileInputStream(file))) {
                LOGGER.debug("OpenDocument spreadsheet");
                return new Ods2Xml();
            }
        } catch (Exception e) {

        }

        return null;
    }

    /**
     *
     * @param sIn
     *            Source XML file location in file system.
     * @param sOut
     *            Output Excel file location in file system.
     * @return
     * @throws GDEMException
     */
    public ConversionResultDto convertDD_XML(String sIn, String sOut) throws GDEMException {
        try {
            FileOutputStream outStream = new FileOutputStream(sOut);
            FileInputStream inStream = new FileInputStream(sIn);
            return convertDD_XML(inStream, outStream);
        } catch (Exception e) {
            ConversionResultDto result = new ConversionResultDto();
            result.setStatusCode(ConversionResultDto.STATUS_ERR_SYSTEM);
            result.setStatusDescription("ErrorConversionHandler convertDD_XML_split- couldn't save the source file: "
                    + e.toString());
            return result;
        }
    }

    public ConversionResultDto convertDD_XML_split(String sIn, String sheetParam) throws GDEMException {
        try {
            return convertDD_XML_split(new FileInputStream(sIn), null, sheetParam);

        } catch (Exception e) {
            ConversionResultDto result = new ConversionResultDto();
            result.setStatusCode(ConversionResultDto.STATUS_ERR_SYSTEM);
            result.setStatusDescription("ErrorConversionHandler convertDD_XML_split- couldn't save the source file: "
                    + e.toString());
            return result;
        }

    }

    public ConversionResultDto convertDD_XML_split(InputStream inStream) throws GDEMException {
        return convertDD_XML_split(inStream, null, null);
    }

    public ConversionResultDto convertDD_XML(InputStream inStream, OutputStream outStream) throws GDEMException {

        ConversionResultDto resultObject = new ConversionResultDto();
        try {
            if (inStream == null) {
                throw new Exception("Could not find InputStream");
            }
            if (outStream == null) {
                throw new Exception("Could not find OutputStream");
            }
            sourcefile = getSourceReader();
            sourcefile.initReader(inStream, resultObject);

            String xmlSchema = sourcefile.getXMLSchema();
            boolean isValidSchema = isValidXmlSchema(xmlSchema, resultObject);
            // execute conversion
            if (isValidSchema){
                doConversion(xmlSchema, outStream, resultObject);
                parseConversionResults(resultObject);
            }
            sourcefile.closeReader();
        } catch (Exception e) {
            throw new GDEMException("Error generating XML file from " + getSourceFormatName() + " file: " + e.toString(), e);
        } finally {
            IOUtils.closeQuietly(inStream);
        }
        return resultObject;
    }

    public ConversionResultDto convertDD_XML_split(InputStream inStream, OutputStream outStream, String sheetParam) throws GDEMException {

        ConversionResultDto resultObject = new ConversionResultDto();
        try {
            if (inStream == null) {
                throw new GDEMException("Could not find InputStream");
            }
            sourcefile = getSourceReader();
            sourcefile.initReader(inStream, resultObject);
            String xmlSchema = sourcefile.getXMLSchema();
            boolean isValidSchema = isValidXmlSchema(xmlSchema, resultObject);
            if (!isValidSchema){
                return resultObject;
            }
            Map<String, String> sheetSchemas = sourcefile.getSheetSchemas();
            boolean isValidSheetSchemas = isValidSheetSchemas(sheetSchemas, xmlSchema, sheetParam, resultObject);
            if (!isValidSheetSchemas){
                return resultObject;
            }

            if (isHttpResponse() && Utils.isNullStr(sheetParam)) {
                sheetParam = sourcefile.getFirstSheetName();
            }

            for (Map.Entry<String, String> entry : sheetSchemas.entrySet()) {
                String sheetName = entry.getKey();
                String sheetSchema = entry.getValue();
                if (sheetSchema == null) {
                    resultObject
                    .addConversionLog(ConversionLogType.WARNING, "could not find xml schema for this sheet!", ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
                    continue;
                }
                if (!Utils.isNullStr(sheetParam)) {
                    // Only 1 sheet is needed.
                    if (!sheetParam.equalsIgnoreCase(sheetName)) {
                        continue;
                    }
                }

                try {
                    // Do not return empty sheets.
                    if (sourcefile.isEmptySheet(sheetName)) {
                        resultObject
                        .addConversionLog(ConversionLogType.INFO, "The sheet is empty: " + sheetName, ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
                        continue;
                    }

                    if (!isHttpResponse()) {
                        outStream = new ByteArrayOutputStream();
                    }
                    doConversion(sheetSchema, outStream, resultObject);
                    // if the respponse is http stream, then it is already
                    // written there and no file available
                    if (!isHttpResponse()) {
                        resultObject.addConvertedXml(sheetName + ".xml", new String(((ByteArrayOutputStream)outStream).toByteArray(), "UTF-8"));
                    }
                } catch (Exception e) {
                    resultObject
                    .addConversionLog(ConversionLogType.ERROR, "Could not find xml schema for this sheet " + sheetName, ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
                } finally {
                    if (!isHttpResponse()) {
                        IOUtils.closeQuietly(outStream);
                    }
                }
                if (!Utils.isNullStr(sheetParam)) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new GDEMException("Error generating XML files from " + getSourceFormatName() + " file: " + e.toString(), e);
        } finally {
            IOUtils.closeQuietly(inStream);
        }
        sourcefile.closeReader();
        parseConversionResults(resultObject);
        return resultObject;
    }

    public boolean isHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(boolean httpResponse) {
        this.httpResponse = httpResponse;
    }

    protected void doConversion(String xmlSchema, OutputStream outStream, ConversionResultDto resultObject) throws Exception {
        String instanceUrl = DataDictUtil.getInstanceUrl(xmlSchema);

        DD_XMLInstance instance = new DD_XMLInstance(instanceUrl);
        DD_XMLInstanceHandler handler = new DD_XMLInstanceHandler(instance);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();

        factory.setValidating(false);
        factory.setNamespaceAware(true);
        reader.setFeature("http://xml.org/sax/features/validation", false);
        reader.setFeature("http://apache.org/xml/features/validation/schema", false);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);

        reader.setContentHandler(handler);
        reader.parse(instanceUrl);

        if (Utils.isNullStr(instance.getEncoding())) {
            String enc_url = getEncodingFromStream(instanceUrl);
            if (!Utils.isNullStr(enc_url)) {
                instance.setEncoding(enc_url);
            }
        }
        importSheetSchemas(sourcefile, instance, xmlSchema, resultObject);
        instance.startWritingXml(outStream);
        sourcefile.writeContentToInstance(instance);
        instance.flushXml();
    }

    /**
     * Reads the XML declaration from instance file
     */
    protected String getEncodingFromStream(String str_url) {
        BufferedReader br = null;
        try {
            URL url = new URL(str_url);
            // ins = new DataInputStream(url.openStream());
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String xml_decl = br.readLine();

            if (xml_decl == null) {
                return null;
            }
            if (!xml_decl.startsWith("<?xml version=") && !xml_decl.endsWith("?>")) {
                return null;
            }
            int idx = xml_decl.indexOf("encoding=");
            if (idx == -1) {
                return null;
            }
            String start = xml_decl.substring(idx + 10);
            int end_idx = start.indexOf("\"");
            if (end_idx == -1) {
                return null;
            }
            String enc = start.substring(0, end_idx);

            return enc;
        } catch (MalformedURLException e) {
            LOGGER.debug("It is not url: " + str_url + "; " + e.toString());
            return null;
        } catch (IOException e) {
            LOGGER.debug("could not read encoding from url: " + str_url + "; " + e.toString());
            return null;
        } catch (Exception e) {
            return null;
            // couldn't read encoding
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * gather all element definitions
     *
     * @param spreadsheet
     * @param instance
     * @throws Exception
     */
    protected void importSheetSchemas(SourceReaderIF spreadsheet, DD_XMLInstance instance, String xmlSchema,
            ConversionResultDto resultObject){
        try {
            // if instance type is TBL, then import only table schema
            if (instance.getType().equals(DD_XMLInstance.TBL_TYPE)) {
                Map<String, DDElement> elemDefs = DataDictUtil.importDDTableSchemaElemDefs(xmlSchema);
                instance.addElemDef(DD_XMLInstance.TBL_TYPE, elemDefs);
            }
            // if instance type is dataset, then import schemas for all pages
            else {
                Map<String, String> sheetSchemas = spreadsheet.getSheetSchemas();
                for (Map.Entry<String, String> entry : sheetSchemas.entrySet()) {
                    String sheetName = entry.getKey();
                    String schemaUrl = entry.getValue();
                    Map<String, DDElement> elemDefs = DataDictUtil.importDDTableSchemaElemDefs(schemaUrl);
                    instance.addElemDef(sheetName, elemDefs);
                }
            }
        } catch (Exception ex) {
            String errMess ="Unable to read element definitions from Data Dictionary XML Schema: " + xmlSchema;
            LOGGER.error(errMess, ex);
            ex.printStackTrace();
            resultObject.addConversionLog(ConversionLogType.WARNING, errMess, "Workbook");
        }
    }

    /**
     * checks if the given schema belongs to the last released dataset in DD. Returns null, if schema is OK. Returns an error
     * message, if the schema is not ok to convert.
     *
     * @param xmlSchema
     * @return error message
     * @throws GDEMException
     */
    public String getInvalidSchemaMessage(String xmlSchema) throws GDEMException {

        String result = null;

        // check latest version only if it Schema from DD
        if (xmlSchema != null && xmlSchema.startsWith(Properties.ddURL)) {
            Map<String, String> dataset = getDataset(xmlSchema);
            if (dataset == null) {
                result =
                    Properties.getMessage(BusinessConstants.ERROR_CONVERSION_INVALID_TEMPLATE,
                            new String[] {getSourceFormatName()});
            } else {
                String status = dataset.get("status");
                boolean isLatestReleased =
                    (dataset.get("isLatestReleased") == null || "true".equals(dataset.get("isLatestReleased"))) ? true : false;
                String dateOfLatestReleased = dataset.get("dateOfLatestReleased");
                String idOfLatestReleased = dataset.get("idOfLatestReleased");

                if (!isLatestReleased && "Released".equalsIgnoreCase(status)) {
                    String formattedReleasedDate = Utils.formatTimestampDate(dateOfLatestReleased);
                    result =
                        Properties.getMessage(BusinessConstants.ERROR_CONVERSION_OBSOLETE_TEMPLATE, new String[] {
                                getSourceFormatName(), formattedReleasedDate == null ? "" : formattedReleasedDate,
                                        idOfLatestReleased});
                }
            }
        }

        return result;
    }

    protected Map<String, String> getDataset(String xmlSchema) {
        return DataDictUtil.getDatasetReleaseInfoForSchema(xmlSchema);
    }
    private boolean isValidXmlSchema(String xmlSchema, ConversionResultDto resultObject) throws GDEMException{
        boolean isValidXmlSchema = true;
        String invalidMess = null;
        if (xmlSchema == null) {
            isValidXmlSchema = false;
            invalidMess = Properties.getMessage(BusinessConstants.ERROR_CONVERSION_INVALID_TEMPLATE,
                    new String[] {getSourceFormatName()});
        }
        else{
            invalidMess = getInvalidSchemaMessage(xmlSchema);
            if (invalidMess != null) {
                isValidXmlSchema = false;
            }
        }
        if (!isValidXmlSchema){
            resultObject.setStatusCode(ConversionResultDto.STATUS_ERR_SCHEMA_NOT_FOUND);
            resultObject.setStatusDescription(invalidMess);
        }
        return isValidXmlSchema;
    }

    private boolean  isValidSheetSchemas(Map<String, String> sheetSchemas, String xmlSchema, String sheetName, ConversionResultDto resultObject){
        boolean isValidSheetSchema = true;

        // could not find sheet schemas
        if (Utils.isNullHashMap(sheetSchemas)) {
            // maybe it's spreadsheet file for DD table
            if (xmlSchema.toLowerCase().indexOf("type=tbl") > -1 || xmlSchema.toLowerCase().indexOf("=tbl") > -1) {
                sheetSchemas.put( sourcefile.getFirstSheetName(), xmlSchema);
            } else {
                isValidSheetSchema = false;
                resultObject.setStatusCode(ConversionResultDto.STATUS_ERR_SCHEMA_NOT_FOUND);
                resultObject.setStatusDescription(Properties.getMessage(
                        BusinessConstants.ERROR_CONVERSION_INVALID_TEMPLATE, new String[] {getSourceFormatName()}));
            }
        }
        if (!Utils.isNullStr(sheetName)) {
            if (!Utils.containsKeyIgnoreCase(sheetSchemas, sheetName)) {
                isValidSheetSchema = false;
                resultObject.setStatusCode(ConversionResultDto.STATUS_ERR_SCHEMA_NOT_FOUND);
                resultObject.setStatusDescription("Could not find sheet with specified name or the XML schema reference was missing on DO_NOT_DELETE_THIS_SHEET: "
                        + sheetName);
            }
        }
        return isValidSheetSchema;
    }
    private void parseConversionResults(ConversionResultDto resultObject){
        if (resultObject.isContainsErrors()){
            resultObject.setStatusCode(ConversionResultDto.STATUS_ERR_SYSTEM);
            resultObject.setStatusDescription("Conversion contains errors.");
        }
        else if (resultObject.isContainsWarnings()){
            resultObject.setStatusCode(ConversionResultDto.STATUS_ERR_VALIDATION);
            resultObject.setStatusDescription("Conversion contains validation warnings.");
        }
        else{
            resultObject.setStatusCode(ConversionResultDto.STATUS_OK);
            resultObject.setStatusDescription("Conversion successful.");
        }
    }
}
