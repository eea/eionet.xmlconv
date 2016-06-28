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
 * TODO: REMOVE deprecated methods
 * @author Enriko Käsper, TietoEnator Estonia AS SAXDoctypeReader
 * @author George Sofianos
 */
public class SAXDoctypeReader implements LexicalHandler {

    private String dtdSystemId = null;
    private String dtdPublicId = null;

    /**
     * Starts DTD
     * @param name Name
     * @param publicId Public Id
     * @param systemId System Id
     * @throws SAXException If an error occurs.
     */
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        dtdSystemId = systemId;
        dtdPublicId = publicId;
    }

    /**
     * Does nothing
     * @throws SAXException If an error occurs.
     */
    public void endDTD() throws SAXException {
    }

    /**
     * Does nothing
     * @param name Name
     * @throws SAXException If an error occurs.
     */
    public void startEntity(String name) throws SAXException {
    }

    /**
     * Does nothing
     * @param name Name
     * @throws SAXException If an error occurs.
     */
    public void endEntity(String name) throws SAXException {
    }

    /**
     * Does nothing
     * @throws SAXException If an error occurs.
     */
    public void startCDATA() throws SAXException {
    }

    /**
     * Does nothing
     * @throws SAXException If an error occurs.
     */
    public void endCDATA() throws SAXException {
    }

    /**
     * Does nothing
     * @param text text
     * @param start start
     * @param length length
     * @throws SAXException If an error occurs.
     */
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
