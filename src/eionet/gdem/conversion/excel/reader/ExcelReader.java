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

import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import eionet.gdem.GDEMException;
import eionet.gdem.conversion.DDXMLConverter;
import eionet.gdem.conversion.SourceReaderIF;
import eionet.gdem.conversion.datadict.DDElement;
import eionet.gdem.conversion.datadict.DD_XMLInstance;
import eionet.gdem.utils.Utils;

/**
 * The main class, which is calling POI HSSF methods for reading Excel file
 *
 * @author Enriko Käsper
 */

public class ExcelReader implements SourceReaderIF {
    private Workbook wb = null;
    private static final String SCHEMA_SHEET_NAME = "DO_NOT_DELETE_THIS_SHEET";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static DataFormatter formatter = new DataFormatter(new Locale("en", "US"));

    private boolean isExcel2007 = false;

    public ExcelReader(boolean excel2007) {
        isExcel2007 = excel2007;
    }

    @Override
    public void initReader(InputStream input) throws GDEMException {
        try {
            if (!isExcel2007) {
                POIFSFileSystem fs = new POIFSFileSystem(input);
                wb = new HSSFWorkbook(fs);
            } else {
                OPCPackage p = OPCPackage.open(input);
                wb = WorkbookFactory.create(p);
            }
        } catch (Exception e) {
            throw new GDEMException("ErrorConversionHandler - couldn't open Excel file: " + e.toString());
        }
    }

    @Override
    public String getXMLSchema() {

        if (wb == null) {
            return null;
        }

        Sheet schema_sheet = wb.getSheet(SCHEMA_SHEET_NAME);

        if (schema_sheet == null) {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                schema_sheet = wb.getSheetAt(i);
                String schema = findSchemaFromSheet(schema_sheet);
                if (schema != null) {
                    return schema;
                }
            }
        } else {
            return findSchemaFromSheet(schema_sheet);
        }
        return null;
    }

    @Override
    public String getFirstSheetName() {

        if (wb == null) {
            return null;
        }

        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            String sheet_name = wb.getSheetName(i).trim();
            if (sheet_name.equalsIgnoreCase(SCHEMA_SHEET_NAME)) {
                continue;
            }
            return sheet_name;
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
        if (tables == null) {
            throw new GDEMException("could not find tables from DD instance file");
        }
        if (wb == null) {
            return;
        }

        for (int i = 0; i < tables.size(); i++) {
            DDXmlElement table = tables.get(i);
            String tblLocalname = table.getLocalName();
            String tblName = table.getName();
            String tblAttrs = table.getAttributes();

            Sheet sheet = getSheet(tblLocalname);
            Sheet metaSheet = getMetaSheet(tblLocalname);
            if (sheet == null) {
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

            instance.writeTableStart(tblName, tblAttrs);
            instance.setCurRow(tblName);

            Map<String, DDElement> elemDefs = instance.getElemDefs(tblLocalname);

            // read data
            // there are no data rows in the Excel file. We create empty table
            firstRow = (firstRow == lastRow) ? lastRow : firstRow + 1;

            for (int j = firstRow; j <= lastRow; j++) {
                row = (firstRow == 0) ? null : sheet.getRow(j);
                metaRow = (metaSheet != null && firstRow != 0) ? metaSheet.getRow(j) : null;
                // don't convert empty rows.
                if (isEmptyRow(row)) {
                    continue;
                }

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
        }

    }

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
    public boolean isEmptySheet(String sheet_name) {

        Sheet sheet = getSheet(sheet_name);
        int row_count = sheet.getLastRowNum();
        if (row_count < 1) {
            return true;
        }

        // check if the first row has any data
        for (int i = 1; i <= row_count; i++) {
            Row row = sheet.getRow(i);
            if (isEmptyRow(row)) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    /*
     * method goes through 4 rows and search the best fit of XML Schema. The deault row is 4.
     */
    private String findSchemaFromSheet(Sheet schema_sheet) {
        Row schemaRow = null;
        Cell schemaCell = null;

        for (int i = 3; i > -1; i--) {
            if (schema_sheet.getLastRowNum() < i) {
                continue;
            }
            schemaRow = schema_sheet.getRow(i);
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

    /*
     * method goes through rows after XML Schema and finds schemas for Excel sheets (DataDict tables). cell(0) =sheet name; cell(1)=
     * XML schema
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
                Sheet sheet = getSheet(sheetValue);
                if (sheet != null && !result.containsKey(sheetValue)) {
                    result.put(sheetValue, schemaValue);
                }
            }
        }
        return result;
    }

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

    protected String cellValueToString(Cell cell, String schemaType) {
        String value = "";
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_FORMULA:
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell) && !isYearValue(cell.getNumericCellValue())) {
                    Date dateValue = cell.getDateCellValue();
                    value = Utils.getFormat(dateValue, DEFAULT_DATE_FORMAT);
                } else if (HSSFDateUtil.isValidExcelDate(cell.getNumericCellValue()) && schemaType != null
                        && schemaType.equals("xs:date") && !isYearValue(cell.getNumericCellValue())) {
                    Date dateValue = cell.getDateCellValue();
                    value = Utils.getFormat(dateValue, DEFAULT_DATE_FORMAT);
                } else {
                    value = formatter.formatCellValue(cell);
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
        }

        return value.trim();
    }

    /*
     * DD can generate additional "-meta" sheets with GIS elements for one DD table. In XML these should be handled as 1 table. This
     * is method for finding these kind of sheets and parsing these in parallel with the main sheet
     */
    private Sheet getMetaSheet(String main_sheet_name) {
        return getSheet(main_sheet_name + DDXMLConverter.META_SHEET_NAME);
    }

    private void setColumnMappings(Row row, List<DDXmlElement> elements, boolean mainTable) {
        // read column header

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

    private String getCellValue(Row row, Integer col_idx, String schemaType) {
        Cell cell = (col_idx == null || row == null) ? null : row.getCell(col_idx);
        String data = (cell == null) ? "" : cellValueToString(cell, schemaType);
        return data;
    }

    // if date formatted cell value is not higher than 4 digit number, then it is probably a year
    private boolean isYearValue(double doubleCellValue) {
        return doubleCellValue < 3000 && doubleCellValue > 0;
    }
}
