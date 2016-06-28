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

package eionet.gdem.conversion.excel;

/**
 * This interface is defing the methods for mapping styles between xml (fo:style) and styles defined in Excel tool.
 *
 * @author Enriko Käsper
 * @author George Sofianos
 */
public interface ExcelStyleIF {

    String STYLE_FAMILY_TABLE = "table";
    String STYLE_FAMILY_TABLE_COLUMN = "table-column";
    String STYLE_FAMILY_TABLE_ROW = "table-row";
    String STYLE_FAMILY_TABLE_CELL = "table-cell";

    /**
     * Get methods returns the differemt parameters defined in the ExcelSTyle object.
     *
     * @return excel style parameters
     */
    /**
     * Get Style name
     * @return Style name
     */
    String getName();

    /**
     * Gets Style family
     * @return Style family
     */
    String getFamily();

    /**
     * Gets Italic style
     * @return True if style is Italic
     */
    boolean getItalic();

    /**
     * Gets Font weight
     * @return Font weight
     */
    short getFontWeight();

    /**
     * Gets Font size
     * @return Font size
     */
    short getFontSize();

    /**
     * Gets Font name
     * @return Font name
     */
    String getFontName();

    /**
     * Gets Text Align
     * @return Text align
     */
    short getTextAlign();

    /**
     * Gets Workbook index
     * @return Workbook index
     */
    short getWorkbookIndex();

    /**
     * Sets the name and family for created Excel style.
     *
     * @param name
     *            - style name
     * @param family
     *            - excel object family, which has the current style (sheet, column, row, cell)
     */
    void setExcelStyle(String name, String family);

    /**
     * Sets the font italic parameter.
     *
     * @param str_italic Italic
     */
    void setItalic(String str_italic);

    /**
     * Sets the font weight parameter.
     *
     * @param str_bold Bold
     */
    void setFontWeight(String str_bold);

    /**
     * Sets the font size parameter.
     *
     * @param str_size Size
     */
    void setFontSize(String str_size);

    /**
     * Sets the font name parameter.
     *
     * @param str_fontname Font name
     */
    void setFontName(String str_fontname);

    /**
     * Sets the font text align parameter.
     *
     * @param str_align Text align
     */
    void setTextAlgin(String str_align);

    /**
     * Compares 2 excel styles (name &amp; family).
     *
     * @param style Style
     * @return True if style is equal
     */
    boolean equals(ExcelStyleIF style);

    /**
     * Sets the index for the style, defined in 1 workbook.
     *
     * @param index Style Index
     */
    void setWorkbookIndex(short index);

    /**
     * Gets the column width.
     * @return Column width
     */
    short getColumnWidth();

    /**
     * sets the column width "12cm" or "11pt".
     *
     * @param column_width Column width
     */
    void setColumnWidth(String column_width);
}
