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

package eionet.gdem.conversion.datadict;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eionet.gdem.conversion.excel.reader.DDXmlElement;
import eionet.gdem.utils.Utils;

/**
 * DataDict XML Instance.
 * @author Unknown
 * @author George Sofianos
 */
public class DD_XMLInstance {

    public static final String DST_TYPE = "DST";
    public static final String TBL_TYPE = "TBL";
    private static final String DEFAULT_ENCODING = "UTF-8";

    protected String lineTerminator = "\n";
    private OutputStreamWriter writer = null;

    private String type = TBL_TYPE; // by default it's table
    private DDXmlElement root_tag;
    private List<DDXmlElement> tables;
    private HashMap<String, DDXmlElement> row_attrs;
    private Map<String, List<DDXmlElement>> elements;
    private StringBuffer namespaces;
    private Map<String, String> leads;
    private Map<String, Map<String, DDElement>> elemDefs;

    private String currentRowName;
    private String currentRowAttrs;
    private String encoding;
    private String instanceUrl;

    /**
     * Constructor
     * @param instanceUrl XML instance URL
     */
    public DD_XMLInstance(String instanceUrl) {
        this.tables = new ArrayList<DDXmlElement>();
        this.row_attrs = new HashMap<String, DDXmlElement>();
        this.elements = new HashMap<String, List<DDXmlElement>>();
        this.namespaces = new StringBuffer();
        this.leads = new HashMap<String, String>();
        this.elemDefs = new HashMap<String, Map<String, DDElement>>();
        this.encoding = DEFAULT_ENCODING;
        this.instanceUrl = instanceUrl;
        this.lineTerminator = File.separator.equals("/") ? "\r\n" : "\n";
    }

    /**
     * Inserts the root_tag name and attributes into Hashtable.
     * @param name Tag name
     * @param localName Tag local name
     * @param attributes Tag attributes
     */
    public void setRootTag(String name, String localName, String attributes) {
        root_tag = new DDXmlElement(name, localName, attributes);
    }

    /**
     * Creates the table name, localName and attributes into Hashtable and inserts it into Vector.
     * @param name Table name
     * @param localName Table local name
     * @param attributes Table attributes
     */
    public void addTable(String name, String localName, String attributes) {
        DDXmlElement table = new DDXmlElement(name, localName, attributes);
        tables.add(table);
    }

    /**
     * Adds table to table list.
     * @param table Table
     */
    public void addTable(DDXmlElement table) {
        tables.add(table);
    }

    /**
     * Inserts row attributes into Hashtable, where keys are table names.
     * @param tblName Table name
     * @param rowName Row name
     * @param attributes Row attributes
     */
    public void addRowAttributes(String tblName, String rowName, String attributes) {
        if (tblName == null) {
            return;
        }
        DDXmlElement attribute = new DDXmlElement(rowName, null, attributes);

        row_attrs.put(tblName, attribute);

    }

    /**
     * Inserts element names, localNames and attributes into Hashtable, where keys are table names.
     * @param tblName Table name
     * @param name Element name
     * @param localName Element local name
     * @param attributes Element attributes
     */
    public void addElement(String tblName, String name, String localName, String attributes) {
        if (tblName == null) {
            return;
        }
        DDXmlElement element = new DDXmlElement(name, localName, attributes);
        List<DDXmlElement> tblElements = null;

        if (elements.containsKey(tblName)) {
            tblElements = elements.get(tblName);
        }
        if (tblElements == null) {
            tblElements = new ArrayList<DDXmlElement>();
        }
        tblElements.add(element);
        elements.put(tblName, tblElements);
    }

    /**
     * Adds namespace to namespaces list.
     * @param prefix Namespace prefix
     * @param uri Namespace uri
     */
    public void addNamespace(String prefix, String uri) {
        namespaces.append(" xmlns:");
        namespaces.append(prefix);
        namespaces.append("=\"");
        namespaces.append(uri);
        namespaces.append("\"");
    }

    /**
     * Sets dataset type.
     */
    public void setTypeDataset() {
        leads.put("tbl", "\t");
        leads.put("row", "\t\t");
        leads.put("elm", "\t\t\t");
        this.type = DST_TYPE;
    }

    /**
     * Sets table type.
     */
    public void setTypeTable() {
        leads.put("tbl", "");
        leads.put("row", "\t");
        leads.put("elm", "\t\t");

        this.type = TBL_TYPE;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public DDXmlElement getRootTag() {
        return root_tag;
    }

    public String getRootTagName() {
        return root_tag.getName();
    }

    public String getRootTagAttributes() {
        return root_tag.getAttributes();
    }

    public List<DDXmlElement> getTables() {
        return this.tables;
    }

    /**
     * Gets table elements
     * @param tbl_name Table name
     * @return List of DD elements
     */
    public List<DDXmlElement> getTblElements(String tbl_name) {
        return elements.get(tbl_name);
    }

    /**
     * Flush the written content into the output stream.
     * @param outStream OutputStream
     * @throws Exception If an error occurs.
     */
    public void startWritingXml(OutputStream outStream) throws Exception {

        this.writer = new OutputStreamWriter(outStream, getEncoding());
        writeHeader();
        startRootElement();
    }

    /**
     * Flush the written content into the output stream.
     * @throws Exception If an error occurs.
     */
    public void flushXml() throws Exception {
        endRootElement();
        writer.flush();
    }

    /**
     * Write data into XML element.
     *
     * @param elemName
     *            XML element name.
     * @param attributes
     *            Element attributes part.
     * @param data
     *            Text added between XML tags.
     * @throws Exception
     *             Exception when writing data.
     */
    public void writeElement(String elemName, String attributes, String data) throws Exception {
        addString(getLead("elm") + "<" + elemName + attributes + ">");
        addString(Utils.escapeXML(data));
        addString("</" + elemName + ">");
        newLine();

    }

    /**
     * Writes row start.
     * @throws Exception If an error occurs.
     */
    public void writeRowStart() throws Exception {
        addString(getLead("row") + "<" + currentRowName + currentRowAttrs + ">");
        newLine();
    }

    /**
     * Writes row end.
     * @throws Exception If an error occurs.
     */
    public void writeRowEnd() throws Exception {
        addString(getLead("row") + "</" + currentRowName + ">");
        newLine();
    }

    /**
     * Writes table start.
     * @param tblName Table name
     * @param attributes Attributes
     * @throws Exception If an error occurs.
     */
    public void writeTableStart(String tblName, String attributes) throws Exception {
        if (type.equals(DST_TYPE)) {
            addString(getLead("tbl") + "<" + tblName + attributes + ">");
            newLine();
        }
    }

    /**
     * Writes table end.
     * @param tblName Table name
     * @throws Exception If an error occurs.
     */
    public void writeTableEnd(String tblName) throws Exception {
        if (type.equals(DST_TYPE)) {
            addString(getLead("tbl") + "</" + tblName + ">");
            newLine();
        }
    }

    /**
     * Set current row
     * @param tblName Table name
     */
    public void setCurRow(String tblName) {
        DDXmlElement rowElement = row_attrs.get(tblName);
        currentRowName = rowElement.getName();
        currentRowAttrs = rowElement.getAttributes();
    }

    /**
     * Adds string to writer
     * @param s String
     * @throws Exception If an error occurs.
     */
    protected void addString(String s) throws Exception {
        writer.write(s);
    }

    /**
     * Adds a new line.
     * @throws Exception If an error occurs.
     */
    protected void newLine() throws Exception {
        writer.write(lineTerminator);
    }

    /**
     * Writes XML Header.
     * @throws IOException If an error occurs.
     */
    private void writeHeader() throws IOException {
        // writer.print("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        String enc = (Utils.isNullStr(getEncoding())) ? DEFAULT_ENCODING : encoding;
        writer.write("<?xml version=\"1.0\" encoding=\"" + enc + "\"?>");
        writer.write(lineTerminator);
    }

    /**
     * Writes Root element.
     * @throws IOException If an error occurs.
     */
    private void startRootElement() throws IOException {
        String root_attributes = getRootTagAttributes();
        if (root_attributes == null) {
            root_attributes = "";
        }
        String rootTagOut = getRootTagName();

        if (root_attributes.indexOf("xmlns:xsi") > -1) {
            rootTagOut += root_attributes;
        } else {
            rootTagOut += namespaces.toString() + root_attributes;
        }
        writer.write("<" + rootTagOut + ">");
        writer.write(lineTerminator);
    }

    /**
     * Writes root element end.
     * @throws IOException If an error occurs.
     */
    private void endRootElement() throws IOException {
        writer.write("</" + getRootTagName() + ">");
    }

    /**
     * Escapes special characters
     * @param s String
     * @return Escaped string
     */
    protected String escape(String s) {

        if (s == null) {
            return null;
        }

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '<') {
                buf.append("&lt;");
            } else if (c == '>') {
                buf.append("&gt;");
            } else if (c == '&') {
                buf.append("&amp;");
            } else {
                buf.append(c);
            }
        }

        return buf.toString();
    }

    /**
     * Get leads
     * @param leadName Lead name
     * @return Lead
     */
    protected String getLead(String leadName) {

        if (leads == null || leads.size() == 0) {
            setTypeTable();
        }

        String lead = leads.get(leadName);
        if (lead == null) {
            lead = "";
        }

        return lead;
    }

    public void setElemDefs(Map<String, Map<String, DDElement>> elemDefs) {
        this.elemDefs = elemDefs;
    }

    /**
     * Adds element definitions.
     * @param sheet Sheet
     * @param elemDefs Element definitions
     */
    public void addElemDef(String sheet, Map<String, DDElement> elemDefs) {
        this.elemDefs.put(sheet, elemDefs);
    }

    /**
     * Gets element definitions
     * @param sheet Sheet
     * @return Element definitions
     */
    public Map<String, DDElement> getElemDefs(String sheet) {
        if (elemDefs != null) {
            if (elemDefs.containsKey(sheet)) {
                return elemDefs.get(sheet);
            } else if (elemDefs.containsKey(TBL_TYPE)) {
                return elemDefs.get(TBL_TYPE);
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    /**
     * @return the instanceUrl
     */
    public String getInstanceUrl() {
        return instanceUrl;
    }

    /**
     * @param instanceUrl
     *            the instanceUrl to set
     */
    public void setInstanceUrl(String instanceUrl) {
        this.instanceUrl = instanceUrl;
    }
}
