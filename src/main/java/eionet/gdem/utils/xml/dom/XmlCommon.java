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
 * The Original Code is Web Dashboards Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Nedeljko Pavlovic (ED)
 */

package eionet.gdem.utils.xml.dom;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import eionet.gdem.utils.xml.ErrorStorage;
import eionet.gdem.utils.xml.XmlException;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML Common class.
 * @author Unknown
 * @author George Sofianos
 */
public class XmlCommon {

    protected Document document = null;
    private DocumentBuilderFactory factory;
    private ErrorStorage errorStorage;
    private ErrorHandler handler;

    /**
     * Default constructor.
     */
    public XmlCommon() {
        factory = DocumentBuilderFactory.newInstance();
        //The namespace aware factory is very important. Or else the XML parsing will fail.
        factory.setNamespaceAware(true);
        errorStorage = new ErrorStorage();

        handler = new DefaultHandler() {
            public void error(SAXParseException ex) throws SAXException {
                errorStorage.setErrorMessage(ex.getMessage());
            }

            public void fatalError(SAXParseException ex) throws SAXException {
                errorStorage.setFatalErrorMessage(ex.getMessage());
            }

            public void warning(SAXParseException ex) throws SAXException {
                errorStorage.setWaringMessage(ex.getMessage());
            }
        };
    }

    /**
     * Parses file from InputStream
     * @param inputStream InputStream
     * @throws XmlException If an error occurs.
     */
    public void checkFromInputStream(InputStream inputStream) throws XmlException {
        try {
            InputSource contentForParsing = new InputSource(inputStream);
            DocumentBuilder parser = factory.newDocumentBuilder();
            parser.setErrorHandler(handler);
            document = parser.parse(contentForParsing);
            if (!errorStorage.isEmpty()) {
                throw new XmlException("Failure reasons: " + errorStorage.getErrors());
            }
        } catch (IOException ioe) {
            throw new XmlException("Failure reasons: " + ioe.getMessage());
        } catch (XmlException xmle) {
            throw xmle;
        } catch (Exception e) {
            throw new XmlException("Failure reasons: " + e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Parses XML from file.
     * @param fullFileName File name
     * @throws XmlException If an error occurs.
     */
    public void checkFromFile(String fullFileName) throws XmlException {
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();
            document = parser.parse(fullFileName);
            parser.setErrorHandler(handler);
            if (!errorStorage.isEmpty()) {
                throw new XmlException("Failure reasons:" + errorStorage.getErrors());
            }

        } catch (SAXException saxe) {
            // saxe.printStackTrace();
            throw new XmlException("Failure reasons: " + saxe.getMessage());
        } catch (IOException ioe) {
            throw new XmlException("Failure reasons: " + ioe.getMessage());
        } catch (XmlException e) {
            throw e;
        } catch (ParserConfigurationException e) {
            throw new XmlException("Failure reasons: " + e.getMessage());
        }
    }

    /**
     * Sets wellformedness checking.
     * @throws XmlException If an error occurs.
     */
    public void setWellFormednessChecking() throws XmlException {
        try {
            factory.setFeature("http://apache.org/xml/features/validation/schema", false);
            // parser.setFeature("http://xml.org/sax/features/namespaces", false);

            factory.setFeature("http://xml.org/sax/features/validation", false);

            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            factory.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
            factory.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
        } catch (ParserConfigurationException e) {
            throw new XmlException("Error occurred while setting Xerces features. Reason: " + e.getMessage());
        }

    }

    /**
     * Sets Validation checking
     * @throws XmlException If an error occurs.
     */
    public void setValidationChecking() throws XmlException {
        try {
            factory.setFeature("http://xml.org/sax/features/validation", true);

            factory.setFeature("http://xml.org/sax/features/external-general-entities", true);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);

            factory.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", true);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
            factory.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
            factory.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
        } catch (ParserConfigurationException e) {
            throw new XmlException("Error occurred while setting Xerces features. Reason: " + e.getMessage());
        }
    }

    /**
     * Creates XML Document
     * @throws XmlException If an error occurs.
     */
    public void createXMLDocument() throws XmlException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new XmlException(e);
        }

    }

    /**
     * Creates XML Document
     * TODO: Check if we need to enable this
     * @param docTypeName Doctype name
     * @param systemId System Id
     * @throws XmlException If an error occurs.
     */
    public void createXMLDocument(String docTypeName, String systemId) throws XmlException {
       /** try {
            DocumentImpl xmlDoc = new DocumentImpl();
            DocumentTypeImpl dtd = new DocumentTypeImpl(xmlDoc, docTypeName, null, systemId);
            Element name = xmlDoc.createElement(docTypeName);
            xmlDoc.appendChild(name);
            xmlDoc.appendChild(dtd);
            Document res = (Document) xmlDoc;
            document = res;
        } catch (Exception e) {
            throw new XmlException(e);
        }
*/
    }

    /**
     * Parses XML from string
     * TODO: check if we need to improve this
     * @param xml XML file
     * @throws XmlException If an error occurs.
     */
    public void checkFromString(String xml) throws XmlException {
        StringReader strR = null;
        try {
            // strReader = new StringReader(stringOut.toString());
            // source = new InputSource(strReader);
            strR = new StringReader(xml);
            InputSource input = new InputSource(strR);
            // setWellFormednessChecking();
            DocumentBuilder parser = factory.newDocumentBuilder();
            parser.setErrorHandler(handler);
            document = parser.parse(input);
            if (!errorStorage.isEmpty()) {
                throw new XmlException("Failure reasons:" + errorStorage.getErrors());
            }

        } catch (SAXException saxe) {
            // saxe.printStackTrace();
            throw new XmlException("Failure reasons: " + saxe.getMessage());
        } catch (IOException ioe) {
            throw new XmlException("Failure reasons: " + ioe.getMessage());
        } catch (XmlException e) {
            throw e;
        } catch (ParserConfigurationException e) {
            throw new XmlException("Failure reasons: " + e.getMessage());
        } finally {
            try {
                if (strR != null) {
                    strR.close();
                }
            } catch (Exception e) {
            }
        }
    }

}
