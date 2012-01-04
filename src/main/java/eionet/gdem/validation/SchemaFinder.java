/*
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

package eionet.gdem.validation;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import eionet.gdem.utils.Utils;

/**
 * Handler for parsing xml document extening SAX DefaultHandler. This class is calling different ExcelConversionhandler methods,
 * which is actuially creating Excel file
 *
 * @author Enriko Käsper
 */

public class SchemaFinder extends DefaultHandler {

    private static String SCHEMA_REFERENCE = "schemaLocation";
    private static String NO_NS_SCHEMA_REFERENCE = "noNamespaceSchemaLocation";

    private String startTag = null;
    private String startTagNamespace = null;
    private String schemaLocation = null;
    private String schemaNamespace = null;
    private boolean hasNamespace = false;

    public void startElement(String uri, String localName, String name, Attributes attrs) throws SAXException {
        // System.out.println("element:" + uri + "||" + localName + "||" + name);

        startTag = (localName == null) ? name : localName; // we want the tag name without ns prefix, if ns processing is turned
                                                           // off, them we use name
        startTagNamespace = uri;

        // String schema_location_attr = (Utils.isNullStr(startTagNamespace))? NO_NS_SCHEMA_REFERENCE:SCHEMA_REFERENCE;

        // System.out.println("("+name);
        int length = attrs != null ? attrs.getLength() : 0;
        for (int i = 0; i < length; i++) {
            String attrName = attrs.getLocalName(i);
            if (attrName.equalsIgnoreCase(NO_NS_SCHEMA_REFERENCE)) {
                setSchemaLocation(attrs.getValue(i));
            } else if (attrName.equalsIgnoreCase(SCHEMA_REFERENCE)) {
                String sch_val = attrs.getValue(i);

                if (!Utils.isNullStr(sch_val)) {
                    // int l = sch_val.indexOf(" ");
                    // schemaLocation=sch_val.substring(l+1);
                    String schemaWithNS = attrs.getValue(i);
                    String[] splitted = schemaWithNS.split(" ");
                    if (splitted.length > 1) {
                        setSchemaNamespace(splitted[0]);
                        setSchemaLocation(splitted[1]);
                    } else
                        setSchemaNamespace(schemaWithNS);

                    hasNamespace = true;
                }
            }
        }
        throw new SAXException("OK");

    }

    public void error(SAXParseException e) {
        System.out.println("error on finding schema from xml");
    }

    public void fatalError(SAXParseException e) {
        System.out.println("fatal error on finding schema from xml");
    }

    public String getStartTag() {
        return this.startTag;
    }

    public String getStartTagNamespace() {
        return this.startTagNamespace;
    }

    public boolean hasNamespace() {
        return this.hasNamespace;
    }

    public String getSchemaLocation() {
        return this.schemaLocation;
    }

    public String getSchemaNamespace() {
        return schemaNamespace;
    }

    public void setSchemaNamespace(String schemaNamespace) {
        this.schemaNamespace = schemaNamespace;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }
}
