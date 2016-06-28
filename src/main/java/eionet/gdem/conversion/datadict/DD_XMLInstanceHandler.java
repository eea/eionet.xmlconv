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

import java.lang.reflect.Method;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

import eionet.gdem.utils.Utils;

/**
 * Handler for parsing xml instance document from datadictionary extening SAX helpers DefaultHandler.
 *
 * @author Enriko Käsper
 * @author George Sofianos
 */

public class DD_XMLInstanceHandler extends DefaultHandler {

    private DD_XMLInstance instance = null;

    private static final int root_level = 0;
    private static final int table_level = 1;
    private static final int row_level = 2;
    private static final int element_level = 3;
    private static final String ROW_TAG = "Row";

    private int level = 0;
    private String cur_table = null;

    /**
     * Constructor.
     * @param instance XML instance
     */
    public DD_XMLInstanceHandler(DD_XMLInstance instance) {
        this.instance = instance;
    }

    /**
     * Adds namespaces.
     * @param prefix Namespace prefix
     * @param uri Namespace uri
     */
    public void startPrefixMapping(String prefix, String uri) {
        instance.addNamespace(prefix, uri);
    }

    /**
     * Starts element.
     * @param uri Uri
     * @param localName Local name
     * @param name Name
     * @param attributes Attributes
     */
    public void startElement(String uri, String localName, String name, Attributes attributes) {

        if (level == root_level) { // root level
            instance.setRootTag(name, localName, attributesToString(attributes));
            level = table_level;
        } else if (level == table_level) { // table_level
            if (localName.equalsIgnoreCase(ROW_TAG)) { // it's table schema an there is only 1 table
                cur_table = instance.getRootTagName();
                instance.setTypeTable();
                instance.addTable(instance.getRootTag());
                instance.addRowAttributes(cur_table, name, attributesToString(attributes));
                level = element_level;
            } else { // it's dataset schema with several tables
                cur_table = name;
                instance.setTypeDataset();
                instance.addTable(name, localName, attributesToString(attributes));
                level = row_level;
            }
        } else if (level == row_level) {
            instance.addRowAttributes(cur_table, name, attributesToString(attributes));
            level = element_level;
        } else if (level == element_level) { // element_level
            instance.addElement(cur_table, name, localName, attributesToString(attributes));
        }
    }

    /**
     * Nothing
     * @param ch ch
     * @param start start
     * @param len len
     */
    public void characters(char[] ch, int start, int len) {
    }

    /**
     * Ends element
     * @param uri Uri
     * @param localName local name
     * @param name name
     */
    public void endElement(String uri, String localName, String name) {
        if (level > table_level) {
            if (localName.equalsIgnoreCase(ROW_TAG)) {
                level = table_level;
            }
        }
    }

    /**
     * Converts attributes to String.
     * @param attributes Attributes
     * @return String of attributes
     */
    private String attributesToString(Attributes attributes) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < attributes.getLength(); i++) {
            buf.append(" ");
            buf.append(attributes.getQName(i));
            buf.append("=\"");
            buf.append(attributes.getValue(i));
            buf.append("\"");
        }
        return buf.toString();

    }

    /**
     * Sets document locator
     * @param locator Locator
     */
    public void setDocumentLocator(Locator locator) {
        Locator startloc = new LocatorImpl(locator);
        String encoding = getEncoding(startloc);
        if (!Utils.isNullStr(encoding))
            instance.setEncoding(encoding);
    }

    /**
     * Gets Locator encoding.
     * @param locator Locator
     * @return Locator encoding
     */
    private String getEncoding(Locator locator) {
        String encoding = null;
        Method getEncoding = null;
        try {
            getEncoding = locator.getClass().getMethod("getEncoding", new Class[] {});
            if (getEncoding != null) {
                encoding = (String) getEncoding.invoke(locator);
            }
        } catch (Exception e) {
            // either this locator object doesn't have this
            // method, or we're on an old JDK
        }
        return encoding;
    }
}
