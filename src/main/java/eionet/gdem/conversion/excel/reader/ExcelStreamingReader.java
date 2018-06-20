package eionet.gdem.conversion.excel.reader;

import com.monitorjbl.xlsx.StreamingReader;
import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.datadict.DDElement;
import eionet.gdem.conversion.datadict.DD_XMLInstance;
import eionet.gdem.conversion.spreadsheet.DDXMLConverter;
import eionet.gdem.conversion.spreadsheet.SourceReaderIF;
import eionet.gdem.conversion.spreadsheet.SourceReaderLogger;
import eionet.gdem.dto.ConversionResultDto;
import eionet.gdem.utils.Utils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * This class is using com.monitorjbl/xlsx-streamer library to read an .xlsx file in a streaming fashion.
 *
 * @author Thanos Tourikas
 */
public class ExcelStreamingReader implements SourceReaderIF {

    /**
     * Excel sheet name where Data Dictionary writes XML Schema information.
     */
    private static final String SCHEMA_SHEET_NAME = "DO_NOT_DELETE_THIS_SHEET";

    /**
     * Default date format pattern.
     */
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Non-breaking space unicode.
     */
    final char NON_BREAKING_SPACE = 0x00A0;

    /**
     * Logger object for writing conversion log.
     */
    private SourceReaderLogger readerLogger;

    /**
     * List of Excel sheet names.
     */
    private List<String> excelSheetNames;

    /**
     * Excel workbook to be converted.
     */
    private Workbook wb = null;

    /**
     * A map of all sheet schemas
     */
    private Map<String, String> sheetSchemas;

    @Override
    public void initReader(File inputFile) throws XMLConvException {
        try {
            wb = StreamingReader.builder()
                    .rowCacheSize(100)                          // number of rows to keep in memory (defaults to 10)
                    .bufferSize(4096)                           // buffer size to use when reading InputStream to file (defaults to 1024)
                    .open(new FileInputStream(inputFile));      // InputStream or File for XLSX file (required)
        } catch (Exception e) {
            e.printStackTrace();
            throw new XMLConvException("ErrorConversionHandler - couldn't open Excel file: " + e.toString());
        }
    }

    @Override
    public void startReader(ConversionResultDto resultObject) {
        readerLogger = new SourceReaderLogger(resultObject, SourceReaderLogger.ReaderTypeEnum.EXCEL);
        readerLogger.logStartWorkbook();
        excelSheetNames = getSheetNames();
        readerLogger.logNumberOfSheets(wb.getNumberOfSheets(), StringUtils.join(excelSheetNames, ", "));
    }

    /**
     * Returns the list of MS Excel sheet names.
     *
     * @return List of sheet names.
     */
    private List<String> getSheetNames() {
        List<String> list = new ArrayList<String>();

        for ( Sheet sheet : wb ) {
            String sheetName = sheet.getSheetName();
            list.add(sheetName);
        }
        return list;
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

            // we cannot random access rows when reading on a streaming fashion so we have to use iterator
            Row firstRow = sheet.iterator().next();
            Row metaRow = null;

            List<DDXmlElement> elements = instance.getTblElements(tblName);

            setColumnMappings(firstRow, elements, true);

            if ( metaSheet != null && metaSheet.iterator().hasNext() ) {
                metaRow = metaSheet.iterator().next();
                setColumnMappings(metaRow, elements, false);
            }
            try {
                logColumnMappings(tblLocalName, firstRow, metaRow, elements);
            } catch (Exception e) {
                e.printStackTrace();
                readerLogger.logSystemWarning(tblLocalName, "cannot write log about missing or extra columns.");
            }
            instance.writeTableStart(tblName, tblAttrs);
            instance.setCurRow(tblName);

            Map<String, DDElement> elemDefs = instance.getElemDefs(tblLocalName);

            // read data
            // there are no data rows in the Excel file. We create empty table
            int countRows = 0;

            // iterate over the sheet rows using iterator instead of random accessing
            for ( Row row : sheet ) {
                metaRow = (metaSheet != null && metaSheet.iterator().hasNext()) ? metaSheet.iterator().next() : null;
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
     * Get Sheet object by sheet name.
     *
     * @param name sheet name
     * @return Sheet
     */
    private Sheet getSheet(String name) {

        if ( wb.getSheetIndex(name.trim()) < 0 ) {
            for ( Sheet s : wb ) {
                String sheetName = s.getSheetName();
                if (sheetName.trim().equalsIgnoreCase(name.trim())) {
                    return wb.getSheet(sheetName);
                }
            }
        } else {
            return wb.getSheet(name.trim());
        }

        return null;
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
            switch ( cell.getCellType()) {
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
                    value = cell.getStringCellValue();
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

    /**
     * If date formatted cell value is not higher than 4 digit number, then it is probably a year.
     *
     * @param doubleCellValue Numeric cell value.
     * @return boolean is year value.
     */
    private boolean isYearValue(double doubleCellValue) {
        return doubleCellValue < 3000 && doubleCellValue > 0;
    }

    @Override
    public String getXMLSchema() {

        if (wb == null) {
            return null;
        }

        Sheet schemaSheet = wb.getSheet(SCHEMA_SHEET_NAME);
        for ( Row row : schemaSheet ) {
            for ( Cell cell : row ) {
                String cellStr = cell.getStringCellValue();
                if (cellStr.startsWith("http://") && cellStr.toLowerCase().indexOf("/getschema") > 0 && Utils.isURL(cellStr)) {
                    return cellStr;
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getSheetSchemas() {

        if ( sheetSchemas != null && !sheetSchemas.isEmpty() ) {
            return sheetSchemas;
        }

        if ( wb == null ) {
            return null;
        }

        sheetSchemas = new LinkedHashMap<String, String>();
        Sheet schemaSheet = wb.getSheet(SCHEMA_SHEET_NAME);

        for ( Row row : schemaSheet ) {

            if ( row.getLastCellNum() < 1 ) {
                continue;
            }

            Cell sheetCell = row.getCell(0);
            Cell schemaCell = row.getCell(1);
            if ( sheetCell == null || schemaCell == null ) {
                continue;
            }

            String sheetValue  = sheetCell.getStringCellValue();
            String schemaValue = schemaCell.getStringCellValue();

            if (schemaValue.startsWith("http://") && schemaValue.toLowerCase().indexOf("/getschema") > 0  && Utils.isURL(schemaValue)) {
                sheetSchemas.put(sheetValue, schemaValue);
            }
        }

        if ( !sheetSchemas.isEmpty() ) {
            return sheetSchemas;
        }
        return null;
    }

    @Override
    public String getFirstSheetName() {
        return null;
    }

    @Override
    public boolean isEmptySheet(String sheet_name) {
        return false;
    }

    @Override
    public void closeReader() {

    }

    public Workbook getWorkbook() {
        return wb;
    }

}
