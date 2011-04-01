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
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
import eionet.gdem.dcm.business.DDServiceClient;
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

    protected static LoggerIF _logger=GDEMServices.getLogger();

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
            if (ExcelUtils.isExcelFile(new ByteArrayInputStream(outstream.toByteArray()))) {
                return new Excel2XML();
            }
        }
        catch(Exception e){

        }

        try {
             if (ExcelUtils.isExcel2007File( new ByteArrayInputStream(outstream.toByteArray()))) {
                 _logger.debug("Excel 2007 document");
                return new Excel20072XML();
            }
        } catch (Exception e) {

        }

        //If it is a zip file, then it is OpenDocument
        try{
            if (OpenDocumentUtils.isSpreadsheetFile(new ByteArrayInputStream(outstream.toByteArray()))) {
                return new Ods2Xml();
            }
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

            Map<String, String> sheetSchemas = sourcefile.getSheetSchemas();
            String first_sheet_name=sourcefile.getFirstSheetName();

            //could not find sheet schemas
            if (Utils.isNullHashMap(sheetSchemas)){
                //maybe it's spreadsheet file for DD table
                if (xml_schema.toLowerCase().indexOf("type=tbl")>-1
                        || xml_schema.toLowerCase().indexOf("=tbl")>-1){
                    sheetSchemas.put(first_sheet_name,xml_schema);
                }
                else{
                    return buildWorkbookErrorMessage(result,null,Properties.getMessage(
                            BusinessConstants.ERROR_CONVERSION_INVALID_TEMPLATE, new String[]{getSourceFormatName()}));
                }
            }
            if (!Utils.isNullStr(sheet_param)){
                if (!Utils.containsKeyIgnoreCase(sheetSchemas,sheet_param)){
                    return buildWorkbookErrorMessage(result,sheet_param,"Could not find sheet with specified name or the XML schema reference was missing on DO_NOT_DELETE_THIS_SHEET: " + sheet_param);
                }
            }
            if (isHttpResponse() && Utils.isNullStr(sheet_param))
                sheet_param=first_sheet_name;

            for (Map.Entry<String, String> entry : sheetSchemas.entrySet()){
                String sheetName = entry.getKey();
                String sheetSchema = entry.getValue();
                if (sheetSchema==null){
                    result.add(createResultForSheet("1",sheetName,"could not find xml schema for this sheet!"));
                    continue;
                }
                    if (!Utils.isNullStr(sheet_param)){
                        //Only 1 sheet is needed.
                        if (!sheet_param.equalsIgnoreCase(sheetName)){
                            continue;
                        }
                    }

                    try{
                        //Do not return empty sheets.
                        if (sourcefile.isEmptySheet(sheetName)){
                            result = buildWorkbookErrorMessage(result,sheet_param,"The sheet is empty: " + sheetName + "!");
                            continue;
                        }

                        if (!isHttpResponse()){
                            outFileName=Properties.tmpFolder + "gdem_" + System.currentTimeMillis() + ".xml";
                            outStream = new FileOutputStream(outFileName);
                        }
                        doConversion(sheetSchema, outStream);

                        // if the respponse is http stream, then it is already written there and no file available
                        if (!isHttpResponse()){
                            byte[] file = Utils.fileToBytes(outFileName);
                            Vector sheet_result = new Vector();
                            sheet_result.add("0");
                            sheet_result.add(sheetName + ".xml");
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
                        result = buildWorkbookErrorMessage(result,sheet_param,"Could not find xml schema for this sheet " + sheetName + "! " + e.toString());
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
        String instance_url = DataDictUtil.getInstanceUrl(xml_schema);

        DD_XMLInstance instance = new DD_XMLInstance();
        DD_XMLInstanceHandler handler = new DD_XMLInstanceHandler(instance);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();

        factory.setValidating(false);
        factory.setNamespaceAware(true);
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
        instance.startWritingXml(outStream);
        sourcefile.writeContentToInstance(instance);
        instance.flushXml();
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
                Map<String, DDElement> elemDefs = DataDictUtil.importDDTableSchemaElemDefs(xml_schema);
                instance.addElemDef(DD_XMLInstance.TBL_TYPE, elemDefs);
            }
            //if instance type is dataset, then import schemas for all pages
            else{
                Map<String, String> sheetSchemas = spreadsheet.getSheetSchemas();
                for (Map.Entry<String, String> entry : sheetSchemas.entrySet()){
                    String sheetName = entry.getKey();
                    String schemaUrl = entry.getValue();
                    Map<String, DDElement> elemDefs = DataDictUtil.importDDTableSchemaElemDefs(schemaUrl);
                    instance.addElemDef(sheetName, elemDefs);
                    }
                }
        } catch (Exception ex) {
            _logger.error("Error reading elements from schema files ", ex);
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
     * @param xmlSchema
     * @return error message
     * @throws GDEMException
     */
    public String getInvalidSchemaMessage(String xmlSchema)
            throws GDEMException {

        String result = null;

        Map dataset = getDataset(xmlSchema);

        if (dataset == null) {
            result = Properties.getMessage(
                    BusinessConstants.ERROR_CONVERSION_INVALID_TEMPLATE,
                    new String[] { getSourceFormatName() });
        } else {
            String status = (String) dataset.get("status");
            boolean isLatestReleased = (dataset.get("isLatestReleased") == null ||
                        "true".equals((String) dataset.get("isLatestReleased"))) ?
                                true
                                : false;
            String dateOfLatestReleased = (String) dataset.get("dateOfLatestReleased");
            String idOfLatestReleased = (String) dataset.get("idOfLatestReleased");

            if (!isLatestReleased && "Released".equalsIgnoreCase(status)) {
                String formattedReleasedDate = Utils
                        .formatTimestampDate(dateOfLatestReleased);
                result = Properties.getMessage(
                        BusinessConstants.ERROR_CONVERSION_OBSOLETE_TEMPLATE,
                        new String[] {
                                getSourceFormatName(),
                                formattedReleasedDate == null ? ""
                                        : formattedReleasedDate
                                , idOfLatestReleased });
            }
        }

        return result;
    }

    protected Map getDataset(String xmlSchema) {
        return DataDictUtil.getDatasetReleaseInfoForSchema(xmlSchema);
    }
}
