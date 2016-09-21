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
 * Created on 27.04.2006
 */

package eionet.gdem.conversion.odf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eionet.gdem.XMLConvException;
import eionet.gdem.utils.Utils;

/**
 * Analyzer for ODF spreadsheets.
 * @author Unknown
 * @author George Sofianos
 */
public class ODFSpreadsheetAnalyzer {

    protected String officeNamespace;

    protected String dcNamespace;

    protected String tableNamespace;

    protected String textNamespace;

    private static final String OPENDOCUMENT_URI = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";

    private static final String DC_URI = "http://purl.org/dc/elements/1.1/";

    private static final String TABLE_URI = "urn:oasis:names:tc:opendocument:xmlns:table:1.0";

    private static final String TEXT_URI = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";

    /**
     * Analyze the content in an <code>InputStream</code>.
     *
     * <p>
     * Algorithm:
     * </p>
     * <ol>
     * <li>Parse the input stream into a <code>Document</code></li>
     * <li>From the root element, determine the namespace prefixes for that correspond to <code>office:</code>, <code>text:</code>,
     * <code>table:</code>, and <code>dc:</code>.</li>
     * <li>For each child element of the <code>&lt;office:meta&gt;</code> element, process it with the
     * {@link #processTable(Element,OpenDocumentSpreadsheet) processTable()} method.</li>
     * </ol>
     *
     * @param metaStream
     *            an <code>InputStream</code> that contains OpenDocument content.
     * @return an <code>OpenDocumentSpreadsheet</code> structure that represents the file's spreadsheet information.
     */
    public OpenDocumentSpreadsheet analyzeSpreadsheet(InputStream metaStream) {
        DocumentBuilder builder;
        Document doc;
        Element spreadsheetElement;
        OpenDocumentSpreadsheet spreadsheetResult;

        try {
            spreadsheetResult = new OpenDocumentSpreadsheet();
            // TODO check if it is possible to use SAX instead of DOM here. It does not perform well with large XML files.
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.parse(metaStream);
            findNamespaces(doc.getDocumentElement());
            spreadsheetElement = (Element) doc.getElementsByTagName(officeNamespace + "spreadsheet").item(0);
            if (spreadsheetElement != null) {
                NodeList tableNodes = spreadsheetElement.getElementsByTagName(tableNamespace + "table");
                if (tableNodes.getLength() == 0) {
                    throw new XMLConvException("No tables found");
                }

                for (int tblNo = 0; tblNo < tableNodes.getLength(); tblNo++) {
                    Element table = (Element) tableNodes.item(tblNo);

                    processTable(table, spreadsheetResult);

                }
            }
        } catch (Exception e) {
            spreadsheetResult = null;
        }

        return spreadsheetResult;
    }

    /**
     * Analyze the content file in a <code>File</code> which is a .zip file.
     *
     * @param inputStream
     *            a <code>File</code> that contains OpenDocument content-information information.
     */
    public OpenDocumentSpreadsheet analyzeZip(InputStream inputStream) {
        OpenDocumentSpreadsheet spreadsheet = null;
        ZipInputStream zipStream = null;
        try {
            zipStream = new ZipInputStream(inputStream);
            while (zipStream.available() == 1) {
                // read possible contentEntry
                ZipEntry cententEntry = zipStream.getNextEntry();
                if (cententEntry != null) {
                    if ("content.xml".equals(cententEntry.getName())) {
                        // if real contentEntry we use content to do real
                        // analysis
                        spreadsheet = analyzeSpreadsheet(zipStream);
                        // analyze is made and we can break the loop
                        break;
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            IOUtils.closeQuietly(zipStream);
        }
        return spreadsheet;
    }

    /**
     * Finds the namespace prefixes associated with OpenDocument, Dublin Core, and OpenDocument meta elements.
     * This function presumes that all the namespaces are in the root element. If they aren't, this breaks.
     *
     * @param rootElement
     *            the root element of the document.
     */
    protected void findNamespaces(Element rootElement) {
        NamedNodeMap attributes;
        Node node;
        String value;

        attributes = rootElement.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            node = attributes.item(i);
            value = node.getNodeValue();

            if (value.equals(DC_URI)) {
                dcNamespace = extractNamespace(node.getNodeName());
            } else if (value.equals(TABLE_URI)) {
                tableNamespace = extractNamespace(node.getNodeName());
            } else if (value.equals(TEXT_URI)) {
                textNamespace = extractNamespace(node.getNodeName());
            } else if (value.equals(OPENDOCUMENT_URI)) {
                officeNamespace = extractNamespace(node.getNodeName());
            }
        }
    }

    /**
     * Extract a namespace from a namespace attribute.
     *
     * @param namespaceAttrName
     *            an attribute name in the form <code>xmlns:aaaa</code>.
     * @return the namespace, including the colon separator.
     */
    protected String extractNamespace(String namespaceAttrName) {
        String result;
        int pos = namespaceAttrName.indexOf(":");

        result = (pos > 0) ? namespaceAttrName.substring(pos + 1) + ":" : "";
        return result;
    }

    /**
     * Put the content of the table element's spreadheet object.
     *
     * @param tblElement
     *            the <code>&lt;table:table&gt;</code> element.
     * @param spreadsheetResult
     *            the spreadsheet object to be set.
     *
     */
    protected void processTable(Element tblElement, OpenDocumentSpreadsheet spreadsheetResult) {
        String tbl_name = tblElement.getAttribute(tableNamespace + "name");

        if (Utils.isNullStr(tbl_name)) {
            return;
        }

        spreadsheetResult.addTable(tbl_name);

        NodeList rowNodes = tblElement.getElementsByTagName(tableNamespace + "table-row");
        if (rowNodes.getLength() == 0) {
            return;
        }

        for (int rowNo = 0; rowNo < rowNodes.getLength(); rowNo++) {
            Element row = (Element) rowNodes.item(rowNo);

            if (rowNo == 0) {
                processHeaderRow(row, spreadsheetResult);
            } else {
                processDataRow(row, spreadsheetResult);
            }

        }

    }

    /**
     * Put the content of the table's first row into spreadheet object.
     *
     * @param rowElement
     *            the first element of <code>&lt;table:table-row&gt;</code> element.
     * @param spreadsheetResult
     *            the spreadsheet object to be set.
     *
     */
    protected void processHeaderRow(Element rowElement, OpenDocumentSpreadsheet spreadsheetResult) {

        NodeList cellNodes = rowElement.getElementsByTagName(tableNamespace + "table-cell");
        if (cellNodes.getLength() == 0) {
            return;
        }

        for (int cellNo = 0; cellNo < cellNodes.getLength(); cellNo++) {
            Element cell = (Element) cellNodes.item(cellNo);

            if (cell != null) {
                String cell_value = processCell(cell);
                spreadsheetResult.addTableHeaderValue(null, cell_value);
            }

        }

    }

    /**
     * Put the content of the table's first row into spreadheet object.
     *
     * @param rowElement
     *            the first element of <code>&lt;table:table-row&gt;</code> element.
     * @param spreadsheetResult
     *            the spreadsheet object to be set.
     *
     */
    protected void processDataRow(Element rowElement, OpenDocumentSpreadsheet spreadsheetResult) {

        List<String> list_data_row = new ArrayList<String>();
        // number of columns in the table (the number of header cells)
        int i_tblcol_count = spreadsheetResult.getTableColCount(null);

        // rows-repeated attribute
        String str_rows_repeated = rowElement.getAttribute(tableNamespace + "number-rows-repeated");
        int i_rows_repeated = getNumberRepeated(str_rows_repeated, 1);

        NodeList cellNodes = rowElement.getElementsByTagName(tableNamespace + "table-cell");
        if (cellNodes.getLength() == 0) {
            return;
        }

        for (int cellNo = 0; cellNo < cellNodes.getLength(); cellNo++) {
            Element cell = (Element) cellNodes.item(cellNo);

            String str_cols_repeated = cell.getAttribute(tableNamespace + "number-columns-repeated");
            int i_cols_repeated = getNumberRepeated(str_cols_repeated, 1);

            String cell_value = processCell(cell);

            // repeat values as specified in attribute: number-rows-repeated
            for (int l = 0; l < i_cols_repeated; l++) {
                list_data_row.add(cell_value);
                // don't add more data columns as header columns
                if (i_tblcol_count == list_data_row.size()) {
                    break;
                }
            }
            // don't add more data columns as header columns
            if (i_tblcol_count == list_data_row.size()) {
                break;
            }

        }
        // This is empty row and repeated more than 1 times.
        // It is probably the last one - don't add it to spreadsheet
        if (i_rows_repeated > 100 && Utils.isEmptyList(list_data_row)) {
            return;
        }

        // similar rows can be repeated, add ArrayList into sreadsheet the same
        // number of times
        for (int l = 0; l < i_rows_repeated; l++) {
            spreadsheetResult.addTableDataRow(null, list_data_row);
        }

    }

    /**
     * Get the content from p:text node or officde:value attribute - cell value
     *
     * @param cellElement
     *            the first element of <code>&lt;table:table-cell&gt;</code> element.
     *
     * @return content inside text:p tag
     */
    protected String processCell(Element cellElement) {
        String cellValue = null;
        if (cellElement.hasAttribute(officeNamespace + "value-type")) {
            String valueType = cellElement.getAttribute(officeNamespace + "value-type");
            if ("date".equals(valueType)) {
                if (cellElement.hasAttribute(officeNamespace + "date-value")) {
                    cellValue = cellElement.getAttribute(officeNamespace + "date-value");
                }
            } else if ("time".equals(valueType)) {
                if (cellElement.hasAttribute(officeNamespace + "time-value")) {
                    cellValue = cellElement.getAttribute(officeNamespace + "time-value");
                }
            } else {
                if (cellElement.hasAttribute(officeNamespace + "value")) {
                    cellValue = cellElement.getAttribute(officeNamespace + "value");
                }
            }
        }
        // get value from p:text
        if (Utils.isNullStr(cellValue) && cellElement.hasChildNodes()) {
            Element ptext = (Element) cellElement.getFirstChild();
            if (ptext != null && ptext.hasChildNodes()) {
                cellValue = ptext.getFirstChild().getNodeValue();
            }
        }
        return cellValue;
    }

    /**
     * parses string to int if possible. If the source is not castable as int, then return default_value.
     *
     * @param str_number
     *            the number in String
     * @param default_value
     *            returns this value, if string is not castable as integer
     *
     * @return value int value
     */
    private int getNumberRepeated(String str_number, int default_value) {
        int i_number = default_value;
        if (Utils.isNullStr(str_number)) {
            return i_number;
        }

        try {
            i_number = Integer.parseInt(str_number);
        } catch (Exception e) {
            i_number = default_value;
        }
        return i_number;
    }
}
