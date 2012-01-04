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

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * Reads DTD information from the header of XML.
 *
 * @author Enriko Käsper, TietoEnator Estonia AS SAXDoctypeReader
 */
public class SAXDoctypeReader implements LexicalHandler {

    private String dtdSystemId = null;
    private String dtdPublicId = null;

    public void startDTD(String name, String publicId, String systemId) throws SAXException {

        dtdSystemId = systemId;
        dtdPublicId = publicId;
        // System.out.println("dtd: " + name + "-" + publicId + "-" +systemId);
        // throw new SAXException("OK");

    }

    public void endDTD() throws SAXException {
    }

    public void startEntity(String name) throws SAXException {
    }

    public void endEntity(String name) throws SAXException {
    }

    public void startCDATA() throws SAXException {
    }

    public void endCDATA() throws SAXException {
    }

    public void comment(char[] text, int start, int length) throws SAXException {

        // String comment = new String(text, start, length);
        // System.out.println(comment);
        // System.out.println("1");

    }

    public String getDTD() {
        return this.dtdSystemId;
    }

    public String getDTDPublicId() {
        return this.dtdPublicId;
    }
}
