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

package eionet.gdem.conversion.excel.writer;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.excel.ExcelStyleIF;

/**
 * The main class, which is calling POI HSSF methods for creating Excel file and adding data into it works together with
 * ExcelXMLHandler.
 *
 * @author Enriko Käsper
 */

public class ExcelConversionHandler implements ExcelConversionHandlerIF {
    private String fileName = null;
    private HSSFWorkbook wb = null;

    // pointers
    private int currentSheet = 0;
    private int currentRow = 0;
    private int currentCell = 0;

    private List<ExcelStyleIF> styles = null;
    private List<RowColumnDefinition> columns = null;
    private List<RowColumnDefinition> rows = null;

    /**
     * Default constructor.
     */
    public ExcelConversionHandler() {
        wb = new HSSFWorkbook();
    }

    @Override
    public void setFileName(String name) {
        this.fileName = name;
    }

    @Override
    public void addWorksheets(String sheetName) {
        if (wb == null) {
            return;
        }

        if (sheetName == null) {
            sheetName = "Sheet" + String.valueOf(currentSheet + 1);
        }

        wb.createSheet(sheetName);
        currentSheet = wb.getNumberOfSheets() - 1;
        // System.out.println("Worksheet" + currentSheet);
        currentRow = -1;
        currentCell = 0;
        columns = new ArrayList<RowColumnDefinition>();
    }

    @Override
    public void addRow(String defStyle, String defType) {
        HSSFSheet sh = wb.getSheetAt(currentSheet);
        currentRow++;
        sh.createRow(currentRow);
        currentRow = sh.getPhysicalNumberOfRows() - 1;

        currentCell = 0;

        if (rows == null) {
            rows = new ArrayList<RowColumnDefinition>();
        }

        short idx = getStyleIdxByName(defStyle, ExcelStyleIF.STYLE_FAMILY_TABLE_CELL);

        RowColumnDefinition row = new RowColumnDefinition(defType, idx, defStyle);
        rows.add(row);

    }

    @Override
    public void addRows(String def_style, String def_type, int repeated) {
        for (int i = 0; i < repeated; i++) {
            addRow(def_style, def_type);
        }
    }

    @Override
    public void addColumn(String defStyle, String defType) {
        if (columns == null) {
            columns = new ArrayList<RowColumnDefinition>();
        }

        short idx = getStyleIdxByName(defStyle, ExcelStyleIF.STYLE_FAMILY_TABLE_CELL);

        RowColumnDefinition column = new RowColumnDefinition(defType, idx, defStyle);
        columns.add(column);
    }

    @Override
    public void addColumns(String defStyle, String defType, int repeated) {
        for (int i = 0; i < repeated; i++) {
            addColumn(defStyle, defType);
        }
    }

    @Override
    public void addCell(String type, String str_value, String style_name) {
        HSSFSheet _sheet = wb.getSheetAt(currentSheet);
        HSSFRow _row = _sheet.getRow(currentRow);
        HSSFCell _cell = _row.createCell((currentCell));

        Double number_value = null;
        Boolean boolean_value = null;
        boolean isNumber = false;
        boolean isBoolean = false;
        if (type == null) {
            type = (String) getDefaultParams("data_type");
        }
        if (type != null) {
            if (type.equals("float") || type.equals("number")) {
                if (str_value != null) {
                    try {
                        number_value = new Double(str_value);
                        isNumber = true;
                    } catch (Exception e) {
                        // the value is not number, it will be inserted as a string
                        // System.out.println(e.toString());
                    }
                } else {
                    isNumber = true;
                }
            } else if (type.equals("boolean")) {
                if (str_value != null) {
                    try {
                        boolean_value = new Boolean(str_value);
                        isBoolean = true;
                    } catch (Exception e) {
                        // the value is not boolean, it will be inserted as a string
                        // System.out.println(e.toString());
                    }
                } else {
                    isBoolean = true;
                }
            } else if (type.equals("date")) {
                if (str_value != null) {
                    try {
                        // cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyymmdd"));

                        /*
                         *
                         * The way how to handle user defined formats not supported right now HSSFDataFormat format =
                         * wb.createDataFormat(); HSSFCellStyle style = wb.createCellStyle();
                         * style.setDataFormat(format.getFormat("yyyymmdd")); _cell.setCellStyle(style);
                         */
                        // cellStyle.setDataFormat(new HSSFDataFormat("yyyymmdd"));
                        /*
                         * try{ l_value=Long.parseLong(value); System.out.println(String.valueOf(l_value)); isLong=true; }
                         * catch(Exception e){ System.out.println(e.toString()); }
                         */
                        /*
                         * if (isLong){ Date d = new Date(); _cell.setCellStyle(cellStyle); //_cell.setCellValue(d);
                         * _cell.setCellValue(value); //System.out.println(d.toString()); isDate=true; } else
                         * _cell.setCellValue(value);
                         */
                        // System.out.println("hh");

                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
            }
        }
        if (isNumber) {
            if (number_value != null) {
                _cell.setCellValue(number_value.doubleValue());
            }
            _cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        } else if (isBoolean) {
            if (boolean_value != null) {
                _cell.setCellValue(boolean_value.booleanValue());
            }
            _cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
        } else {
            _cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            // _cell.setEncoding(HSSFCell.ENCODING_UTF_16 );//
            _cell.setCellValue(str_value);
        }

        short idx = -1;
        if (style_name != null) {
            idx = getStyleIdxByName(style_name, ExcelStyleIF.STYLE_FAMILY_TABLE_CELL);
        }

        if (idx < 0) {
            Short short_idx = (Short) getDefaultParams("style");
            if (short_idx != null) {
                idx = short_idx.shortValue();
            }
        }

        if (idx > -1) {
            _cell.setCellStyle(wb.getCellStyleAt(idx));
        }
        // calculates the col with according to the first row
        if (currentRow == 0 && idx > -1) {
            short colStyleWidth = 0;
            HSSFCellStyle style = wb.getCellStyleAt(idx);
            int f_i = style.getFontIndex();
            HSSFFont font = wb.getFontAt((short) f_i);
            // character size
            short size = font.getFontHeightInPoints();
            if (columns.size() > currentCell) {
                RowColumnDefinition column = columns.get(currentCell);
                String column_style_name = column.getStyleName() == null ? "" : column.getStyleName();
                ExcelStyleIF definedStyle = getStyleByName(column_style_name, ExcelStyleIF.STYLE_FAMILY_TABLE_CELL);
                if (definedStyle != null) {
                    colStyleWidth = definedStyle.getColumnWidth();
                }
            }
            short width = (short) (_sheet.getDefaultColumnWidth() * size * 25);
            if (colStyleWidth > 0) {
                width = colStyleWidth;
            } else if (str_value.length() > 0) {
                width = (short) (str_value.length() * size * 50);
            }
            _sheet.setColumnWidth(currentCell, width);
        }
        currentCell = _cell.getColumnIndex() + 1;
        // System.out.println("Cell" + currentCell+ "-" + value);
    }

    @Override
    public void addCells(String type, String style_name, int repeated) {
        for (int i = 1; i < repeated; i++) {
            addCell(type, null, style_name);
        }
    }

    @Override
    public void addStyle(ExcelStyleIF style) {
        if (style == null) {
            return;
        }
        if (styles == null) {
            styles = new ArrayList<ExcelStyleIF>();
        }

        if (!styleExists(style)) {
            styles.add(style);
        }
        if (style.getFamily().equals(ExcelStyleIF.STYLE_FAMILY_TABLE_CELL)) {
            addStyleToWorkbook(style);
        }
    }

    /**
     * Adds style to workbook.
     * @param style Style
     */
    private void addStyleToWorkbook(ExcelStyleIF style) {
        HSSFFont font = wb.createFont();
        // Font Size eg.12
        short height = style.getFontSize();
        if (height > 0) {
            font.setFontHeightInPoints(height);
        }
        // Font Name eg.Arial
        String font_name = style.getFontName();
        if (font_name != null) {
            font.setFontName(font_name);
        }
        // Italic
        font.setItalic(style.getItalic());
        // Font Weight eg.bold
        short weight = style.getFontWeight();
        font.setBoldweight(weight);

        // Fonts are set into a style so create a new one to use.
        HSSFCellStyle HSSFStyle = wb.createCellStyle();
        HSSFStyle.setFont(font);
        // Text alignment eg.center
        short align = style.getTextAlign();
        HSSFStyle.setAlignment(align);

        style.setWorkbookIndex(HSSFStyle.getIndex());

    }

    /**
     * Checks if style exists in style list.
     * @param style Style
     * @return True if style exists.
     */
    private boolean styleExists(ExcelStyleIF style) {
        if (style == null) {
            return false;
        }

        for (int i = 0; i < styles.size(); i++) {
            if (style.equals(styles.get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ExcelStyleIF getStyleByName(String name, String family) {

        if (styles == null) {
            return null;
        }
        if (name == null || family == null) {
            return null;
        }

        for (int i = 0; i < styles.size(); i++) {
            ExcelStyleIF style = styles.get(i);
            if (name.equals(style.getName()) && family.equals(style.getFamily())) {
                return style;
            }
        }
        return null;
    }

    /**
     * Gets style id by name
     * @param name Name of style
     * @param family Family of style
     * @return Style id if found, -1 if not.
     */
    private short getStyleIdxByName(String name, String family) {

        if (styles == null) {
            return -1;
        }
        if (name == null || family == null) {
            return -1;
        }

        for (int i = 0; i < styles.size(); i++) {
            ExcelStyleIF style = styles.get(i);
            if (name.equals(style.getName()) && family.equals(style.getFamily())) {
                return style.getWorkbookIndex();
            }
        }
        return -1;
    }

    /**
     * Gets default parameters object
     * @param param Parameters
     * @return default parameters object
     */
    private Object getDefaultParams(String param) {
        if (param == null) {
            return null;
        }
        if (columns == null) {
            return null;
        }
        if (rows == null) {
            return null;
        }
        if (rows.size() < currentRow) {
            return null;
        }
        if (columns.size() < currentCell) {
            return null;
        }

        // Find default value defined at row level
        RowColumnDefinition rowDef = rows.get(currentRow);
        if (param.equals("data_type")) {
            if (rowDef.getDataType() != null) {
                return rowDef.getDataType();
            }
        } else if (param.equals("style")) {
            short idx = rowDef.getStyleIndex();
            if (idx > -1) {
                return idx;
            }
        }

        // Find default value defined at column level
        RowColumnDefinition colDef = columns.get(currentCell);
        if (param.equals("data_type")) {
            return colDef.getDataType();
        } else if (param.equals("style")) {
            return colDef.getStyleIndex();
        }

        return null;
    }

    @Override
    public void writeToFile() throws XMLConvException {
        // Write the output to a file
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
        } catch (Exception e) {
            throw new XMLConvException("ErrorConversionHandler - couldn't save the Excel file: " + e.toString());
        } finally {
            IOUtils.closeQuietly(fileOut);
        }
    }

    @Override
    public void writeToFile(OutputStream outstream) throws XMLConvException {
        // Write the output to Outputstream
        try {
            wb.write(outstream);
        } catch (Exception e) {
            throw new XMLConvException("ErrorConversionHandler - couldn't save the Excel file: " + e.toString());
        }
    }

}
