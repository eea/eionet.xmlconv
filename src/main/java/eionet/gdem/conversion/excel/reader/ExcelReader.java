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
 */

package eionet.gdem.conversion.excel.reader;

import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.datadict.DDElement;
import eionet.gdem.conversion.datadict.DD_XMLInstance;
import eionet.gdem.conversion.spreadsheet.DDXMLConverter;
import eionet.gdem.conversion.spreadsheet.SourceReaderIF;
import eionet.gdem.conversion.spreadsheet.SourceReaderLogger;
import eionet.gdem.conversion.spreadsheet.SourceReaderLogger.ReaderTypeEnum;
import eionet.gdem.dto.ConversionResultDto;
import eionet.gdem.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The main class, which is calling POI HSSF methods for reading Excel file.
 *
 * @author Enriko Käsper
 */

public class ExcelReader implements SourceReaderIF {
    /**
     * Excel eorkbook to be converted.
     */
    private Workbook wb = null;
    /**
     * Excel sheet name where Data Dictionary writes XML Schema information.
     */
    private static final String SCHEMA_SHEET_NAME = "DO_NOT_DELETE_THIS_SHEET";
    /**
     * Default date format pattern.
     */
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /**
     * Date formatter.
     */
    private static DataFormatter formatter = new DataFormatter(new Locale("en", "US"));
    /**
     * Boolean indicates if Excel file is in 2007 version format or not.
     */
    private boolean isExcel2007 = false;

    /**
     * Logger object for writing conversion log.
     */
    private SourceReaderLogger readerLogger;
    /**
     * List of Excel sheet names.
     */
    private List<String> excelSheetNames = new ArrayList<String>();
    /**
     * Excel file size.
     */
    private long inputFileLength = 0;

    /**
     * Formula evaluator used for calculating formulas in cell.
     */
    private FormulaEvaluator evaluator;
    /**
     * Non-breaking space unicode.
     */
    final char NON_BREAKING_SPACE = 0x00A0;

    /**
     * Class constructor.
     *
     * @param excel2007 true if the file is Excel 2007 or newer.
     */
    public ExcelReader(boolean excel2007) {
        isExcel2007 = excel2007;
    }

    @Override
    public void initReader(File inputFile) throws XMLConvException {
        if (inputFile == null) {
            throw new XMLConvException("Input file is missing");
        }
        try {
            if (!isExcel2007) {
                POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(inputFile));
                wb = new HSSFWorkbook(fs);
            } else {
                OPCPackage p = OPCPackage.open(new FileInputStream(inputFile));
                wb = WorkbookFactory.create(p);
            }
        } catch (Exception e) {
            throw new XMLConvException("ErrorConversionHandler - couldn't open Excel file: " + e.toString());
        }
        inputFileLength = inputFile.length();
        evaluator = wb.getCreationHelper().createFormulaEvaluator();

    }

    @Override
    public void startReader(ConversionResultDto resultObject) {
        readerLogger = new SourceReaderLogger(resultObject, ReaderTypeEnum.EXCEL);
        readerLogger.logStartWorkbook();
        excelSheetNames = getSheetNames();
        readerLogger.logNumberOfSheets(wb.getNumberOfSheets(), StringUtils.join(excelSheetNames, ", "));
    }

    @Override
    public void closeReader() {
        readerLogger.logEndWorkbook(inputFileLength);
    }

    @Override
    public String getXMLSchema() {

        if (wb == null) {
            return null;
        }

        Sheet schemaSheet = wb.getSheet(SCHEMA_SHEET_NAME);

        if (schemaSheet == null) {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                schemaSheet = wb.getSheetAt(i);
                String schema = findSchemaFromSheet(schemaSheet);
                if (schema != null) {
                    return schema;
                }
            }
        } else {
            return findSchemaFromSheet(schemaSheet);
        }
        return null;
    }

    @Override
    public String getFirstSheetName() {

        if (wb == null) {
            return null;
        }
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            String sheetName = wb.getSheetName(i).trim();
            if (sheetName.equalsIgnoreCase(SCHEMA_SHEET_NAME)) {
                continue;
            }
            return sheetName;
        }
        return null;

    }

    @Override
    public Map<String, String> getSheetSchemas() {

        if (wb == null) {
            return null;
        }

        Sheet schemaSheet = wb.getSheet(SCHEMA_SHEET_NAME);

        if (schemaSheet == null) {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                schemaSheet = wb.getSheetAt(i);
                Map<String, String> schemas = findSheetSchemas(schemaSheet);
                if (schemas != null) {
                    if (!schemas.isEmpty()) {
                        return schemas;
                    }
                }
            }
        } else {
            return findSheetSchemas(schemaSheet);
        }
        return null;
    }

    @Override
    public void writeContentToInstance(DD_XMLInstance instance) throws Exception {

        List<DDXmlElement> tables = instance.getTables();
        if (tables == null || wb == null) {
            readerLogger.logNoDefinitionsForTables();
            return;
        }

        for (int i = 0; i < tables.size(); i++) {
            DDXmlElement table = tables.get(i);
            String tblLocalName = table.getLocalName();
            if (tblLocalName != null && tblLocalName.length() > 31) {
                tblLocalName = tblLocalName.substring(0,31);
            }
            String tblName = table.getName();
            String tblAttrs = table.getAttributes();

            readerLogger.logStartSheet(tblLocalName);
            readerLogger.logSheetSchema(instance.getInstanceUrl(), tblLocalName);
            if (!excelSheetNames.contains(tblLocalName)) {
                readerLogger.logSheetNotFound(tblLocalName);
            }
            Sheet sheet = getSheet(tblLocalName);
            Sheet metaSheet = getMetaSheet(tblLocalName);

            if (sheet == null) {
                readerLogger.logEmptySheet(tblLocalName);
                continue;
            }
            int firstRow = sheet.getFirstRowNum();
            int lastRow = sheet.getLastRowNum();
            Row row = sheet.getRow(firstRow);
            Row metaRow = null;

            List<DDXmlElement> elements = instance.getTblElements(tblName);

            setColumnMappings(row, elements, true);

            if (metaSheet != null) {
                metaRow = metaSheet.getRow(firstRow);
                setColumnMappings(metaRow, elements, false);
            }
            try {
                logColumnMappings(tblLocalName, row, metaRow, elements);
            } catch (Exception e) {
                e.printStackTrace();
                readerLogger.logSystemWarning(tblLocalName, "cannot write log about missing or ectra columns.");
            }
            instance.writeTableStart(tblName, tblAttrs);
            instance.setCurRow(tblName);

            Map<String, DDElement> elemDefs = instance.getElemDefs(tblLocalName);

            // read data
            // there are no data rows in the Excel file. We create empty table
            firstRow = (firstRow == lastRow) ? lastRow : firstRow + 1;
            int countRows = 0;

            for (int j = firstRow; j <= lastRow; j++) {
                row = (firstRow == 0) ? null : sheet.getRow(j);
                metaRow = (metaSheet != null && firstRow != 0) ? metaSheet.getRow(j) : null;
                // don't convert empty rows.
                if (isEmptyRow(row)) {
                    continue;
                }
                countRows++;

                instance.writeRowStart();
                for (int k = 0; k < elements.size(); k++) {
                    DDXmlElement elem = elements.get(k);
                    String elemName = elem.getName();
                    String elemLocalName = elem.getLocalName();
                    String elemAttributes = elem.getAttributes();
                    int colIdx = elem.getColIndex();
                    boolean isMainTable = elem.isMainTable();
                    String schemaType = null;
                    boolean hasMultipleValues = false;
                    String delim = null;

                    // get element definition info
                    if (elemDefs != null && elemDefs.containsKey(elemLocalName)) {
                        schemaType = elemDefs.get(elemLocalName).getSchemaDataType();
                        delim = elemDefs.get(elemLocalName).getDelimiter();
                        hasMultipleValues = elemDefs.get(elemLocalName).isHasMultipleValues();
                    }

                    String data = "";
                    if (colIdx > -1) {
                        data = (isMainTable) ? getCellValue(row, colIdx, schemaType) : getCellValue(metaRow, colIdx, null);
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
            }
            instance.writeTableEnd(tblName);
            readerLogger.logNumberOfRows(countRows, tblLocalName);
            readerLogger.logEndSheet(tblLocalName);
        }
    }

    /**
     * Check if row is empty or not.
     *
     * @param row MS Excel row.
     * @return boolean
     */
    public boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }

        for (int j = 0; j <= row.getLastCellNum(); j++) {
            Cell cell = row.getCell(j);
            if (cell == null) {
                continue;
            }
            if (!Utils.isNullStr(cellValueToString(cell, null))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmptySheet(String sheetName) {

        Sheet sheet = getSheet(sheetName);
        int rowCount = sheet.getLastRowNum();
        if (rowCount < 1) {
            return true;
        }

        // check if the first row has any data
        for (int i = 1; i <= rowCount; i++) {
            Row row = sheet.getRow(i);
            if (isEmptyRow(row)) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * Method goes through 4 rows and search the best fit of XML Schema. The deault row is 4.
     *
     * @param schemaSheet Schema sheet name.
     * @return schema URL.
     */
    private String findSchemaFromSheet(Sheet schemaSheet) {
        Row schemaRow = null;
        Cell schemaCell = null;

        for (int i = 3; i > -1; i--) {
            if (schemaSheet.getLastRowNum() < i) {
                continue;
            }
            schemaRow = schemaSheet.getRow(i);
            if (schemaRow == null) {
                continue;
            }
            if (schemaRow.getLastCellNum() < 0) {
                continue;
            }
            schemaCell = schemaRow.getCell(0);
            String val = schemaCell.getRichStringCellValue().toString();

            if (val.startsWith("http://") && val.toLowerCase().indexOf("/getschema") > 0 && Utils.isURL(val)) {
                return val;
            }
        }
        return null;
    }

    /**
     * Method goes through rows after XML Schema and finds schemas for Excel sheets (DataDict tables). cell(0) =sheet name;
     * cell(1)=XML schema
     *
     * @param schemaSheet sheet name
     * @return Map
     */
    private Map<String, String> findSheetSchemas(Sheet schemaSheet) {

        Row schemaRow = null;
        Cell schemaCell = null;
        Cell sheetCell = null;

        Map<String, String> result = new LinkedHashMap<String, String>();
        if (schemaSheet.getLastRowNum() < 1) {
            return null;
        }

        for (int i = 0; i <= schemaSheet.getLastRowNum(); i++) {
            schemaRow = schemaSheet.getRow(i);
            if (schemaRow == null) {
                continue;
            }
            if (schemaRow.getLastCellNum() < 1) {
                continue;
            }
            schemaCell = schemaRow.getCell(1);
            if (schemaCell == null) {
                continue;
            }
            String schemaValue = schemaCell.getRichStringCellValue().toString();

            if (schemaValue.startsWith("http://") && schemaValue.toLowerCase().indexOf("/getschema") > 0
                    && Utils.isURL(schemaValue)) {

                sheetCell = schemaRow.getCell(0);
                String sheetValue = sheetCell.getRichStringCellValue().toString();
                if (sheetValue == null) {
                    continue;
                }
                if (sheetValue != null && sheetValue.length() > 31) {
                    sheetValue = sheetValue.substring(0, 31);
                }
                Sheet sheet = getSheet(sheetValue);
                if (sheet != null && !result.containsKey(sheetValue)) {
                    result.put(sheetValue, schemaValue);
                }
            }
        }
        return result;
    }

    /**
     * Get Sheet object by sheet name.
     *
     * @param name sheet name
     * @return Sheet
     */
    private Sheet getSheet(String name) {
        Sheet sheet = wb.getSheet(name.trim());

        if (sheet == null) {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                String sheetName = wb.getSheetName(i);
                if (sheetName.trim().equalsIgnoreCase(name.trim())) {
                    return wb.getSheet(sheetName);
                }
            }

        } else {
            return sheet;
        }

        return null;
    }

    /**
     * Returns the list of MS Excel sheet names.
     *
     * @return List of sheet names.
     */
    private List<String> getSheetNames() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            String sheetName = wb.getSheetName(i);
            list.add(sheetName);
        }
        return list;
    }

    /**
     * Reads cell value and formats it according to element type defined in XML Schema. If the cell contains formula,
     * then calculated value is returned.
     *
     * @param cell       Spreadsheet Cell object.
     * @param schemaType XML Schema data type for given cell.
     * @return string value of the cell.
     */
    protected String cellValueToString(Cell cell, String schemaType) {
        String value = "";

        if (cell != null) {
            switch (evaluator.evaluateInCell(cell).getCellType()) {
                case HSSFCell.CELL_TYPE_NUMERIC:
                    if (HSSFDateUtil.isCellDateFormatted(cell) && !isYearValue(cell.getNumericCellValue())) {
                        Date dateValue = cell.getDateCellValue();
                        value = Utils.getFormat(dateValue, DEFAULT_DATE_FORMAT);
                    } else if (HSSFDateUtil.isValidExcelDate(cell.getNumericCellValue()) && schemaType != null
                            && schemaType.equals("xs:date") && !isYearValue(cell.getNumericCellValue())) {
                        Date dateValue = cell.getDateCellValue();
                        value = Utils.getFormat(dateValue, DEFAULT_DATE_FORMAT);
                    } else {
                        value = NumberToTextConverter.toText(cell.getNumericCellValue());
                    }
                    break;
                case HSSFCell.CELL_TYPE_STRING:
                    RichTextString richText = cell.getRichStringCellValue();
                    value = richText.toString();
                    break;
                case HSSFCell.CELL_TYPE_BOOLEAN:
                    value = Boolean.toString(cell.getBooleanCellValue());
                    break;
                case HSSFCell.CELL_TYPE_ERROR:
                    break;
                case HSSFCell.CELL_TYPE_FORMULA:
                    break;
                default:
                    break;
            }
        }
        return StringUtils.strip(value.trim(), String.valueOf(NON_BREAKING_SPACE)).trim();
    }

    /**
     * DD can generate additional "-meta" sheets with GIS elements for one DD table. In XML these should be handled as 1 table. This
     * is method for finding these kind of sheets and parsing these in parallel with the main sheet
     *
     * @param mainSheetName Main sheet name.
     * @return Spreadsheet Sheet object.
     */
    private Sheet getMetaSheet(String mainSheetName) {
        return getSheet(mainSheetName + DDXMLConverter.META_SHEET_NAME);
    }

    /**
     * Read column header.
     *
     * @param row       Excel row object
     * @param elements  List of DD table elements
     * @param mainTable true if the table is main table.
     */
    private void setColumnMappings(Row row, List<DDXmlElement> elements, boolean mainTable) {

        if (row == null || elements == null) {
            return;
        }
        int firstCell = row.getFirstCellNum();
        int lastCell = row.getLastCellNum();

        for (int j = 0; j < elements.size(); j++) {
            DDXmlElement elem = elements.get(j);
            String elemLocalName = elem.getLocalName();
            for (int k = firstCell; k < lastCell; k++) {
                Cell cell = row.getCell(k);
                String colName = cellValueToString(cell, null);
                colName = colName != null ? colName.trim() : "";
                if (colName.equalsIgnoreCase(elemLocalName)) {
                    elem.setColIndex(k);
                    elem.setMainTable(mainTable);
                    break;
                }
            }
        }

    }

    /**
     * Goes through all columns and logs missing and redundant columns into conversion log.
     *
     * @param sheetName Excel sheet name.
     * @param row       Excel Row object
     * @param metaRow   Excel meta sheet row
     * @param elements  List of XML elements
     */
    private void logColumnMappings(String sheetName, Row row, Row metaRow, List<DDXmlElement> elements) {

        int nofColumns = row.getLastCellNum() - row.getFirstCellNum();
        readerLogger.logNumberOfColumns(nofColumns, sheetName);
        if (metaRow != null) {
            int nofMetaColumns = row.getLastCellNum() - row.getFirstCellNum();
            readerLogger.logNumberOfColumns(nofMetaColumns, sheetName + DDXMLConverter.META_SHEET_NAME);
        }

        List<String> missingColumns = new ArrayList<String>();
        List<String> elemNames = new ArrayList<String>();
        for (DDXmlElement element : elements) {
            if (element.getColIndex() < 0) {
                missingColumns.add(element.getLocalName());
            }
            elemNames.add(element.getLocalName().toLowerCase());
        }
        if (missingColumns.size() > 0) {
            readerLogger.logMissingColumns(StringUtils.join(missingColumns, ", "), sheetName);
        }
        List<String> extraColumns = getExtraColumns(sheetName, row, elemNames);
        if (extraColumns.size() > 0) {
            readerLogger.logExtraColumns(StringUtils.join(extraColumns, ", "), sheetName);
        }

        if (metaRow != null) {
            List<String> extraMetaColumns = getExtraColumns(sheetName, metaRow, elemNames);
            if (extraMetaColumns.size() > 0) {
                readerLogger.logExtraColumns(StringUtils.join(extraColumns, ", "), sheetName + DDXMLConverter.META_SHEET_NAME);
            }
        }

    }

    /**
     * Find redundant columns from the list of columns.
     *
     * @param sheetName Excel sheet name.
     * @param row       Excel row.
     * @param elemNames DD element names.
     * @return List of extra columns added to sheet.
     */
    private List<String> getExtraColumns(String sheetName, Row row, List<String> elemNames) {
        List<String> extraColumns = new ArrayList<String>();
        List<Integer> emptyColumns = new ArrayList<Integer>();
        for (int k = row.getFirstCellNum(); k < row.getLastCellNum(); k++) {
            Cell cell = row.getCell(k);
            String colName = (cell != null) ? cellValueToString(cell, null) : null;
            colName = colName != null ? colName.trim() : "";

            if (colName.equals("")) {
                emptyColumns.add(k);
            } else if (!Utils.isNullStr(colName) && !elemNames.contains(colName.toLowerCase())) {
                extraColumns.add(colName);
            }
        }
        if (emptyColumns.size() > 0) {
            readerLogger.logInfo(sheetName, "Found data from column(s): " + StringUtils.join(emptyColumns, ", ")
                    + ", but no column heading is available. The column(s) will be ignored.");
        }

        return extraColumns;
    }

    /**
     * Get cell String value.
     *
     * @param row        Excel row.
     * @param colIdx     Column index
     * @param schemaType Schema type
     * @return Textual cell value.
     */
    private String getCellValue(Row row, Integer colIdx, String schemaType) {
        Cell cell = (colIdx == null || row == null) ? null : row.getCell(colIdx);
        String data = (cell == null) ? "" : cellValueToString(cell, schemaType);
        return data;
    }

    /**
     * If date formatted cell value is not higher than 4 digit number, then it is probably a year.
     *
     * @param doubleCellValue Numeric cell value.
     * @return boolean is year value.
     */
    private boolean isYearValue(double doubleCellValue) {
        return doubleCellValue < 3000 && doubleCellValue > 0;
    }

    /**
     * Return Workbook object.
     *
     * @return Workbook object.
     */
    protected Workbook getWorkbook() {
        return this.wb;
    }
}
