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
 * The Original Code is " GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 * Created on 27.04.2006
 */
package eionet.gdem.conversion.odf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

import com.catcode.odf.ODFMetaFileAnalyzer;
import com.catcode.odf.OpenDocumentMetadata;

import eionet.gdem.GDEMException;
import eionet.gdem.conversion.datadict.DDElement;
import eionet.gdem.conversion.datadict.DD_XMLInstance;
import eionet.gdem.conversion.excel.reader.DDXmlElement;
import eionet.gdem.conversion.spreadsheet.DDXMLConverter;
import eionet.gdem.conversion.spreadsheet.SourceReaderIF;
import eionet.gdem.conversion.spreadsheet.SourceReaderLogger;
import eionet.gdem.conversion.spreadsheet.SourceReaderLogger.ReaderTypeEnum;
import eionet.gdem.dto.ConversionResultDto;
import eionet.gdem.utils.Utils;

/**
 * The class is responsible for reading OpenDocument Spreadsheets
 *
 * @author Enriko Käsper
 */

public class OdsReader implements SourceReaderIF {
    private OpenDocumentMetadata metadata = null;

    private OpenDocumentSpreadsheet spreadsheet = null;

    public final static String SCHEMA_ATTR_NAME = "schema-url";

    public final static String TBL_SCHEMAS_ATTR_NAME = "table-schema-urls";

    private final static String TBL_SEPARATOR = ";";

    private final static String TBL_PROPERTIES_SEPARATOR = ",";

    public final static String TABLE_NAME = "tableName=";

    public final static String TABLE_SCHEMA_URL = "tableSchemaURL=";

    private SourceReaderLogger readerLogger;

    List<String> odsSheetNames = new ArrayList<String>();
    /**
     * Ods file size.
     */
    private long inputFileLength = 0;

    @Override
    public String getXMLSchema() {
        String ret = null;
        Hashtable usermetadata = metadata.getUserDefined();
        if (usermetadata.containsKey(SCHEMA_ATTR_NAME)) {
            ret = (String) usermetadata.get(SCHEMA_ATTR_NAME);
        }
        return ret;
    }

    /*
     * Intializes OdsReader. Reades ODS file into Java objects
     *
     * @see eionet.gdem.conversion.SourceReaderIF#initReader(java.io.InputStream)
     *
     * @param InputStream input: Source ods file
     */
    @Override
    public void initReader(File inFile) throws GDEMException {
        if (inFile == null){
            throw new GDEMException("Input file is missing");
        }
        try {
            // ODF analyzer closes the stream after parsing content.
            ODFSpreadsheetAnalyzer odfSpreadsheetAnalyzer = new ODFSpreadsheetAnalyzer();
            spreadsheet = odfSpreadsheetAnalyzer.analyzeZip(new FileInputStream(inFile));

            ODFMetaFileAnalyzer odfMetaAnalyzer = new ODFMetaFileAnalyzer();
            metadata = odfMetaAnalyzer.analyzeZip(new FileInputStream(inFile));

        } catch (IOException e) {
            // throw e;
        }
        inputFileLength = inFile.length();
    }
    @Override
    public void startReader(ConversionResultDto resultObject){
        readerLogger = new SourceReaderLogger(resultObject, ReaderTypeEnum.ODS);
        readerLogger.logStartWorkbook();
        odsSheetNames = getSheetNames();
        readerLogger.logNumberOfSheets(spreadsheet.getTables().size(), StringUtils.join(odsSheetNames, ", "));
    }
    @Override
    public void closeReader(){
        readerLogger.logEndWorkbook(inputFileLength);
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.gdem.conversion.SourceReaderIF#writeContentToInstance(eionet.gdem.conversion.excel.DD_XMLInstance)
     */
    @Override
    public void writeContentToInstance(DD_XMLInstance instance) throws Exception {
        List<DDXmlElement> tables = instance.getTables();
        if (tables == null || spreadsheet == null) {
            readerLogger.logNoDefinitionsForTables();
            return;
        }

        for (int i = 0; i < tables.size(); i++) {
            DDXmlElement table = tables.get(i);
            String tblLocalName = table.getLocalName();
            String tblName = table.getName();
            String tblAttrs = table.getAttributes();

            readerLogger.logStartSheet(tblLocalName);
            readerLogger.logSheetSchema(instance.getInstanceUrl(), tblLocalName);
            if (!odsSheetNames.contains(tblLocalName)) {
                readerLogger.logSheetNotFound(tblLocalName);
            }

            List<List<String>> listTableData = spreadsheet.getTableData(tblLocalName);
            List<List<String>> listMetaTableData = getMetaTableData(tblLocalName);

            if (listTableData == null) {
                readerLogger.logEmptySheet(tblLocalName);
                continue;
            }
            List<DDXmlElement> elements = instance.getTblElements(tblName);
            List<String> headerRow = spreadsheet.getTableHeader(tblLocalName);
            List<String> headerRowMetaTable = getMetaTableHeader(tblLocalName);
            setColumnMappings(headerRow, elements, true);

            if (listMetaTableData != null) {
                setColumnMappings(headerRowMetaTable, elements, false);
            }
            logColumnMappings(tblLocalName, headerRow, headerRowMetaTable, elements);

            instance.writeTableStart(tblName, tblAttrs);
            instance.setCurRow(tblName);

            Map<String, DDElement> elemDefs = instance.getElemDefs(tblName);

            // read data
            // there are no data rows in the Excel file. We create empty table
            // first_row = (first_row == last_row) ? last_row : first_row+1;
            boolean emptySheet = spreadsheet.isEmptySheet(tblLocalName);
            int countRows = 0;

            for (int j = 0; j < listTableData.size() || emptySheet; j++) {
                List<String> list_row = listTableData.get(j);
                List<String> list_metarow =
                    (listMetaTableData != null && listMetaTableData.size() > j) ? listMetaTableData.get(j) : null;

                    // don't convert empty rows.
                    if (Utils.isEmptyList(list_row) && !emptySheet) {
                        continue;
                    }
                    countRows++;

                    instance.writeRowStart();
                    for (int k = 0; k < elements.size(); k++) {
                        DDXmlElement elem = elements.get(k);
                        String elemName = elem.getName();
                        String elemLocalName = elem.getLocalName();
                        String elemAttributes = elem.getAttributes();
                        int colIndex = elem.getColIndex();
                        boolean isMainTable = elem.isMainTable();

                        boolean hasMultipleValues = false;
                        String delim = null;

                        // get element definition info
                        if (elemDefs != null && elemDefs.containsKey(elemLocalName)) {
                            delim = elemDefs.get(elemLocalName).getDelimiter();
                            hasMultipleValues = elemDefs.get(elemLocalName).isHasMultipleValues();
                        }

                        String data = "";
                        if (colIndex > -1 && !emptySheet) {
                            data = (isMainTable) ? getListStringValue(list_row, colIndex) : getListStringValue(list_metarow, colIndex);
                        }
                        if (hasMultipleValues && !Utils.isNullStr(delim)) {
                            String[] values = data.split(delim);
                            for (String value : values) {
                                instance.writeElement(elemName, elemAttributes, value.trim());
                            }
                        } else {
                            instance.writeElement(elemName, elemAttributes, data);
                        }

                    }
                    instance.writeRowEnd();
                    if (emptySheet) {
                        break;
                    }
            }
            instance.writeTableEnd(tblName);
            readerLogger.logNumberOfRows(countRows, tblLocalName);
            readerLogger.logEndSheet(tblLocalName);
        }

    }

    /*
     * Returns the name of the first table
     */
    @Override
    public String getFirstSheetName() {

        if (spreadsheet == null) {
            return null;
        }

        return spreadsheet.getTableName(0);
    }
    /**
     * Returns the list of MS Excel sheet names.
     *
     * @return
     */
    private List<String> getSheetNames() {
        List<String> list = new ArrayList<String>();
        for (String sheetName : spreadsheet.getTables()) {
            list.add(sheetName);
        }
        return list;
    }

    @Override
    public Map<String, String> getSheetSchemas() {
        Map<String, String> resultMap = new LinkedHashMap<String, String>();
        Hashtable userMetadata = metadata.getUserDefined();

        if (userMetadata.containsKey(TBL_SCHEMAS_ATTR_NAME)) {
            String ret = (String) userMetadata.get(TBL_SCHEMAS_ATTR_NAME);
            if (Utils.isNullStr(ret)) {
                return resultMap;
            }

            StringTokenizer st_tbl = new StringTokenizer(ret, TBL_SEPARATOR);
            if (st_tbl.countTokens() == 0) {
                return resultMap;
            }
            resultMap = new HashMap<String, String>();
            while (st_tbl.hasMoreTokens()) {
                String tbl = st_tbl.nextToken();
                StringTokenizer st_tbl_props = new StringTokenizer(tbl, TBL_PROPERTIES_SEPARATOR);
                if (st_tbl_props.countTokens() < 2) {
                    continue;
                }

                String tbl_name = null;
                String tbl_schema = null;

                while (st_tbl_props.hasMoreTokens()) {
                    String token = st_tbl_props.nextToken();
                    if (token.startsWith(TABLE_NAME)) {
                        tbl_name = token.substring(TABLE_NAME.length());
                    }
                    if (token.startsWith(TABLE_SCHEMA_URL)) {
                        tbl_schema = token.substring(TABLE_SCHEMA_URL.length());
                    }
                }
                if (Utils.isNullStr(tbl_name) || Utils.isNullStr(tbl_schema)) {
                    continue;
                }

                // check if table exists
                if (spreadsheet != null) {
                    if (!spreadsheet.tableExists(tbl_name)) {
                        continue;
                    }
                }
                if (!resultMap.containsKey(tbl_name)) {
                    resultMap.put(tbl_name, tbl_schema);
                }
            }

        }
        return resultMap;
    }

    @Override
    public boolean isEmptySheet(String sheet_name) {
        if (spreadsheet == null) {
            return true;
        }

        return spreadsheet.isEmptySheet(sheet_name);
    }
    /**
     * DD can generate additional "-meta" sheets with GIS elements for one DD table. In XML these should be handled as 1 table. This
     * is method for finding these kind of sheets and parsing these in parallel with the main sheet
     * @param mainSheetName
     * @return
     */
    private List<List<String>> getMetaTableData(String mainSheetName) {
        return spreadsheet.getTableData(mainSheetName + DDXMLConverter.META_SHEET_NAME_ODS);
    }

    /**
     * Get the list of meta table column names.
     * @param mainSheetName
     * @return
     */
    private List<String> getMetaTableHeader(String mainSheetName) {
        return spreadsheet.getTableHeader(mainSheetName + DDXMLConverter.META_SHEET_NAME_ODS);
    }
    /**
     * Set mappings in case user has changed columns ordering.
     * @param listHeaderRow
     * @param elements
     * @param isMainTable
     */
    private void setColumnMappings(List<String> listHeaderRow, List<DDXmlElement> elements, boolean isMainTable) {
        for (int j = 0; j < elements.size(); j++) {
            DDXmlElement elem = elements.get(j);
            String elemLocalName = elem.getLocalName();
            int k = listHeaderRow.indexOf(elemLocalName);

            if (k > -1) {
                elem.setColIndex(k);
                elem.setMainTable(isMainTable);
            }
        }
    }
    /**
     * Goes through all columns and logs missing and redundant columns into conversion log.
     * @param sheetName
     * @param row
     * @param metaRow
     * @param elements
     */
    private void logColumnMappings(String sheetName,  List<String> row,  List<String> metaRow, List<DDXmlElement> elements) {

        readerLogger.logNumberOfColumns(row.size(), sheetName);
        if (metaRow != null) {
            readerLogger.logNumberOfColumns(metaRow.size(), sheetName + DDXMLConverter.META_SHEET_NAME);
        }

        List<String> missingColumns = new ArrayList<String>();
        List<String> elemNames = new ArrayList<String>();
        for (DDXmlElement element : elements) {
            if (element.getColIndex() < 0) {
                missingColumns.add(element.getLocalName());
            }
            elemNames.add(element.getLocalName().toLowerCase());
        }
        if(missingColumns.size() > 0){
            readerLogger.logMissingColumns(StringUtils.join(missingColumns, ", "), sheetName);
        }
        List<String> extraColumns = getExtraColumns(row, elemNames);
        if(extraColumns.size() > 0){
            readerLogger.logExtraColumns(StringUtils.join(extraColumns, ", "), sheetName);
        }

        if (metaRow != null) {
            List<String> extraMetaColumns = getExtraColumns(metaRow, elemNames);
            if(extraMetaColumns.size() > 0){
                readerLogger.logExtraColumns(StringUtils.join(extraColumns, ", "), sheetName  + DDXMLConverter.META_SHEET_NAME);
            }
        }

    }
    /**
     * Find redundant columns from the list of columns
     * @param row list of column names
     * @param elemNames list of XML element names
     * @return
     */
    private List<String> getExtraColumns(List<String> row, List<String> elemNames) {
        List<String> extraColumns = new ArrayList<String>();
        for (String colName : row) {
            colName = colName != null ? colName.trim() : "";
            if (!Utils.isNullStr(colName) && !elemNames.contains(colName.toLowerCase())) {
                extraColumns.add(colName);
            }
        }
        return extraColumns;
    }
    /**
     * Get cell value.
     * @param list
     * @param colIdx
     * @return
     */
    private String getListStringValue(List<String> list, Integer colIdx) {

        if (list == null) {
            return "";
        }
        if (list.size() < colIdx) {
            return "";
        }
        String data = list.get(colIdx);
        if (data == null) {
            return "";
        }

        return data.trim();
    }

}
