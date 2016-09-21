/**
 * The contents of this file are subject to the Mozilla
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

import java.io.OutputStream;

import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.excel.ExcelStyleIF;

/**
 * The main class, which is calling POI HSSF methods for creating Excel fiile and adding data into it works together with
 * ExcelXMLHandler.
 *
 * @author Enriko Käsper
 * @author George Sofianos
 */

public interface ExcelConversionHandlerIF {

    /**
     * Sets the filename to output file
     *
     * @param name
     *            - MS Excel file name (full path)
     */
    void setFileName(String name);

    /**
     * Adds a new worksheet into workbook
     *
     * @param sheetName
     *            - name of the new worksheet
     */
    void addWorksheets(String sheetName);

    /**
     * Adds a new row to the active worksheet
     *
     * @param def_style
     *            - default style name
     * @param def_type
     *            - default data type
     */
    void addRow(String def_style, String def_type);

    /**
     * Adds several new rows to the active worksheet
     *
     * @param def_style
     *            - default style name
     * @param def_type
     *            - default data type
     * @param repeated
     *            - the number of new rows
     */
    void addRows(String def_style, String def_type, int repeated);

    /**
     * Adds a new column to the active worksheet
     *
     * @param def_style
     *            - default style name
     * @param def_type
     *            - default data type
     */
    void addColumn(String def_style, String def_type);

    /**
     * Adds several new columns to the active worksheet
     *
     * @param def_style
     *            - default style name
     * @param def_type
     *            - default data type
     * @param repeated
     *            - the number of new rows
     */
    void addColumns(String def_style, String def_type, int repeated);

    /**
     * Adds a new cell to the active worksheet and active row
     *
     * @param type
     *            - data type for the new cell, if not defined, then inherited from parent level
     * @param str_value
     *            - cell value
     * @param style_name
     *            - style name
     */
    void addCell(String type, String str_value, String style_name);

    /**
     * Adds several empty cells to the active worksheet and active row
     *
     * @param type
     *            - data type for the new cell, if not defined, then inherited from parent level
     * @param style_name
     *            - style name
     * @param repeated
     *            - the number of new cells
     */
    void addCells(String type, String style_name, int repeated);

    /**
     * Adds a new Excel style to the active workbook
     *
     * @param style
     *            - predefined excel style
     */
    void addStyle(ExcelStyleIF style);

    /**
     * Returns the excel style by style name
     *
     * @param name
     *            - Excel style name
     * @param family
     *            - Excel objects family (sheet, row, column, cell)
     * @return excel style object
     */
    ExcelStyleIF getStyleByName(String name, String family);

    /**
     * Writes the EXCEL workbook object into file
     * @throws XMLConvException In case an error occurs.
     */
    void writeToFile() throws XMLConvException;

    /**
     * Writes the EXCEL workbook object into output stream
     * @param outstream OutputStream
     * @throws XMLConvException In case an error occurs.
     */
    void writeToFile(OutputStream outstream) throws XMLConvException;
}
