/*
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
 * The Original Code is Web Dashboards Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Nedeljko Pavlovic (ED)
 */

package eionet.gdem.utils.xml;

import java.io.InputStream;

import org.w3c.dom.Document;

/**
 * XML Context Interface.
 * @author Unknown
 * @author George Sofianos
 */
public interface IXmlCtx {

    /**
     * Sets checking welformedness.
     * @throws XmlException If an error occurs.
     */
    void setWellFormednessChecking() throws XmlException;

    /**
     * Sets validation checking.
     * @throws XmlException If an error occurs.
     */
    void setValidationChecking() throws XmlException;

    /**
     * Validates XML content from input stream.
     * @param inputStream File InputStream
     * @throws XmlException If an error occurs.
     */
    void checkFromInputStream(InputStream inputStream) throws XmlException;

    /**
     * Validates XML content from file.
     * @param fullFileName File name
     * @throws XmlException If an error occurs.
     */
    void checkFromFile(String fullFileName) throws XmlException;

    /**
     * Validates XML content from string.
     * @param xmlString XML String
     * @throws XmlException If an error occurs.
     */
    void checkFromString(String xmlString) throws XmlException;

    /**
     * Creates XML Document
     * @throws XmlException If an error occurs.
     */
    void createXMLDocument() throws XmlException;

    /**
     * Creates XML Document
     * @param docTypeName Doctype name
     * @param systemId System Id
     * @throws XmlException If an error occurs.
     */
    void createXMLDocument(String docTypeName, String systemId) throws XmlException;

    /**
     * Returns XML Manager
     * @return XML Manager
     */
    XmlUpdater getManager();

    /**
     * Returns serializer.
     * @return serializer
     */
    XmlSerializer getSerializer();

    /**
     * Returns query manager.
     * @return query manager.
     */
    XPathQuery getQueryManager();

    /**
     * Returns document.
     * @return document
     */
    Document getDocument();

    /**
     * Sets Document
     * @param document Document
     */
    void setDocument(Document document);

}
