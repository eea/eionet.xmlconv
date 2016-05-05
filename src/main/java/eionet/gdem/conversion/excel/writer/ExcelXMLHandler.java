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

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import eionet.gdem.conversion.excel.ExcelStyleIF;
import eionet.gdem.conversion.excel.ExcelUtils;

/**
 * Handler for parsing xml document extending SAX BaseHandler This class is calling different ExcelConversionhandler methods, which
 * is actuially creating Excel file.
 *
 * @author Enriko Käsper
 */

public class ExcelXMLHandler extends DefaultHandler implements ExcelXMLTags {

    protected ExcelConversionHandlerIF excel;

    private StringBuffer fieldData = new StringBuffer(); // buffer for collecting characters

    private String cell_value = null;
    private String cell_type = null;
    private String cell_style = null;

    private ExcelStyleIF style = null;

    private static final int sheet_level = 1;
    private static final int row_level = 2;
    private static final int cell_level = 3;

    private int level;

    private boolean bOK = false;

    /**
     * Excel to XML handler constructor.
     * @param excel Conversion handler
     */
    public ExcelXMLHandler(ExcelConversionHandlerIF excel) {
        this.excel = excel;
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) {

        if (name.equals(SHEET_TAG)) {
            excel.addWorksheets(attributes.getValue(SHEET_NAME_ATTR));
            level = sheet_level;
        } else if (name.equals(COLUMN_TAG)) {
            String s_repeated = attributes.getValue(COLUMN_REPEATED_ATTR);
            String def_cell_style = attributes.getValue(DEF_CELL_STYLE_NAME_ATTR);
            String def_cell_type = attributes.getValue(DEF_CELL_VALUE_TYPE_ATTR);

            int i_repeated = 1;
            if (s_repeated != null) {
                try {
                    i_repeated = Integer.parseInt(s_repeated);
                } catch (Exception e) {
                }
            }
            excel.addColumns(def_cell_style, def_cell_type, i_repeated);
        } else if (name.equals(ROW_TAG)) {
            String s_repeated = attributes.getValue(ROW_REPEATED_ATTR);
            String def_cell_style = attributes.getValue(DEF_CELL_STYLE_NAME_ATTR);
            String def_cell_type = attributes.getValue(DEF_CELL_VALUE_TYPE_ATTR);
            // System.out.println("rida--------");
            int i_repeated = 1;
            if (s_repeated != null) {
                try {
                    i_repeated = Integer.parseInt(s_repeated);
                } catch (Exception e) {
                }
            }
            excel.addRows(def_cell_style, def_cell_type, i_repeated);
            level = row_level;
        } else if (name.equals(CELL_TAG)) {
            String s_repeated = attributes.getValue(COLUMN_REPEATED_ATTR);
            cell_style = attributes.getValue(TABLE_STYLE_NAME_ATTR);
            cell_type = attributes.getValue(VALUE_TYPE_ATTR);
            int i_repeated = 1;
            if (s_repeated != null) {
                try {
                    i_repeated = Integer.parseInt(s_repeated);
                } catch (Exception e) {
                }
            }
            excel.addCells(cell_type, cell_style, i_repeated);

            level = cell_level;
            cell_value = null;
        } else if (name.equals(STYLE_TAG)) {
            String style_name = attributes.getValue(STYLE_NAME_ATTR);
            String style_family = attributes.getValue(STYLE_FAMILY_ATTR);

            style = ExcelUtils.getExcelStyle();
            style.setExcelStyle(style_name, style_family);
        } else if (name.equals(STYLE_PROP_TAG)) {
            style.setFontWeight(attributes.getValue(FONT_WEIGHT_ATTR));
            style.setFontName(attributes.getValue(FONT_FAMILY_ATTR));
            style.setFontSize(attributes.getValue(FONT_SIZE_ATTR));
            style.setItalic(attributes.getValue(FONT_STYLE_ATTR));
            style.setTextAlgin(attributes.getValue(TEXT_ALIGN_ATTR));
            style.setColumnWidth(attributes.getValue(STYLE_COLUMN_WIDTH_ATTR));
        }
        if (name.equals(DATA_TAG)) {
            bOK = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int len) {
        if (bOK) {
            fieldData.append(ch, start, len);
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) {

        if (name.equals(CELL_TAG)) {
            // cells.add(cell);
            excel.addCell(cell_type, cell_value, cell_style);

        } else if (name.equals(STYLE_TAG)) {
            excel.addStyle(style);

        } else {
            if (bOK) {
                if (level == cell_level) {
                    cell_value = fieldData.toString().trim();
                }
                bOK = false;
            }
            fieldData = new StringBuffer();
        }
    }

}
