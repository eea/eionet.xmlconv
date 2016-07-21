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

/**
 * Defines XML tag names and attributes used in intermediate XML file.<BR>
 * <BR>
 * ExcelXML is implementing a small part of OpenOffice XML
 *
 * @author Enriko Käsper
 */

public interface ExcelXMLTags {
    /**
     * Root tag - <CODE>&lt;office:document-content&gt;</CODE>
     */
    String ROOT_TAG = "office:document-content";
    /**
     * Worksheet tag - <CODE>&lt;table:table table:name='Sheet1' table:style-name='ta1'&gt;</CODE>
     */
    String SHEET_TAG = "table:table";
    /**
     * Header Rows tag - <CODE>&lt;table:table-rows&gt;</CODE>
     */
    String HEADER_ROWS_TAG = "table:table-header-rows";
    /**
     * Rows tag - <CODE>&lt;table:table-row table:number-rows&gt;</CODE>
     */
    String ROWS_TAG = "table:table-rows";
    /**
     * Row tag - <CODE>&lt;table:table-row table:number-rows-repeated='6' table:style-name='ro1'&gt;</CODE>
     */
    String ROW_TAG = "table:table-row";
    /**
     * Columns tag - <CODE>&lt;table:table:table-columns&gt;</CODE>
     */
    String COLUMNS_TAG = "table:table-columns";
    /**
     * Column tag - <CODE>&lt;table:table:table-column table:default-cell-style-name='Default' table:style-name='co1'&gt;</CODE>
     */
    String COLUMN_TAG = "table:table-column";
    /**
     * Cell tag - <CODE>&lt;table:table-cell table:value-type='float' table:value='56.3378' table:style-name='ce1'&gt;</CODE>
     */
    String CELL_TAG = "table:table-cell";
    /**
     * data tag - <CODE>&lt;text:p&gt;</CODE>
     */
    String DATA_TAG = "text:p";

    /**
     * Styles tag - <CODE>&lt;office:automatic-styles&gt;</CODE>
     */
    String STYLES_TAG = "office:automatic-styles";
    /**
     * Style tag - <CODE>&lt;style:style style:name='ce1' style:family='table-cell'&gt;</CODE>
     */
    String STYLE_TAG = "style:style";
    /**
     * Style properties tag - <CODE>&lt;style:properties fo:text-align='center' fo:font-size='16pt'&gt;</CODE>
     */
    String STYLE_PROP_TAG = "style:properties";
    // ATTRIBUTES

    /**
     * Sheet name attribute - <CODE>&lt;table:name='Sheet1'&gt;</CODE>
     */
    String SHEET_NAME_ATTR = "table:name";
    /**
     * Row number of repeated rows attribute - <CODE>&lt;table:number-rows-repeated='6'&gt;</CODE>
     */
    String ROW_REPEATED_ATTR = "table:number-rows-repeated";
    /**
     * Column number of repeated columns attribute - <CODE>&lt;table:number-columns-repeated='6'&gt;</CODE>
     */
    String COLUMN_REPEATED_ATTR = "table:number-columns-repeated";
    /**
     * Cell value type attribute - <CODE>&lt;table:value-type='float'&gt;</CODE>
     */
    String VALUE_TYPE_ATTR = "table:value-type";
    /**
     * Default cell value type attribute - <CODE>&lt;table:default-cell-value-type='string'&gt;</CODE>
     */
    String DEF_CELL_VALUE_TYPE_ATTR = "table:default-cell-value-type";

    /**
     * Formula attribute - <CODE>&lt;table:formula='=[.B1]+4'&gt;</CODE>
     */
    String FORMULA_ATTR = "table:formula";
    /**
     * Style name attribute - <CODE>&lt;style:name='col1'&gt;</CODE>
     */
    String STYLE_NAME_ATTR = "style:name";
    /**
     * Style family attribute - <CODE>&lt;style:family='table-cell'&gt;</CODE>
     */
    String STYLE_FAMILY_ATTR = "style:family";
    /**
     * Style family attribute - <CODE>&lt;style:column-width='table-cell'&gt;</CODE>
     */
    String STYLE_COLUMN_WIDTH_ATTR = "style:column-width";
    /**
     * Table style name attribute - <CODE>&lt;table:style-name='col1'&gt;</CODE>
     */
    String TABLE_STYLE_NAME_ATTR = "table:style-name";

    /**
     * Default cell style name attribute - <CODE>&lt;table:default-cell-style-name='Heading1'&gt;</CODE>
     */
    String DEF_CELL_STYLE_NAME_ATTR = "table:default-cell-style-name";

    // FO ATTRIBUTES
    /**
     * Font size attribute - <CODE>&lt;fo:font-size='16pt'&gt;</CODE>
     */
    String FONT_SIZE_ATTR = "fo:font-size";
    /**
     * Text align attribute - <CODE>&lt;fo:text-align='center'&gt;</CODE>
     */
    String TEXT_ALIGN_ATTR = "fo:text-align";
    /**
     * Font style attribute - <CODE>&lt;fo:font-style='italic'&gt;</CODE>
     */
    String FONT_STYLE_ATTR = "fo:font-style";
    /**
     * Font weight attribute - <CODE>&lt;fo:font-weight='bold'&gt;</CODE>
     */
    String FONT_WEIGHT_ATTR = "fo:font-weight";
    /**
     * Font family attribute - <CODE>&lt;fo:font-family='&quot;Luxi Sans&quot;'&gt;</CODE>
     */
    String FONT_FAMILY_ATTR = "fo:font-family";

}
