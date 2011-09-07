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

package eionet.gdem.utils.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlCommon {

    protected Document document = null;
    private CustomDomParser parser;
    private ErrorStorage errorStorage;

    public XmlCommon() {
        parser = new CustomDomParser();
        errorStorage = new ErrorStorage();
        parser.setErrorHandler(new DefaultHandler() {
            public void error(SAXParseException ex) throws SAXException {
                errorStorage.setErrorMessage(ex.getMessage());
            }

            public void fatalError(SAXParseException ex) throws SAXException {
                errorStorage.setFatalErrorMessage(ex.getMessage());
            }

            public void warning(SAXParseException ex) throws SAXException {
                errorStorage.setWaringMessage(ex.getMessage());
            }
        });
    }

    public void checkFromInputStream(InputStream inputStream) throws XmlException {
        try {
            InputSource contentForParsing = new InputSource(inputStream);
            parser.parse(contentForParsing);
            if (!errorStorage.isEmpty()) {
                throw new XmlException("Failure reasons: " + errorStorage.getErrors());
            }
            document = parser.getDocument();
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

    public void checkFromFile(String fullFileName) throws XmlException {
        try {
            parser.parse(fullFileName);
            document = parser.getDocument();
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
        }
    }

    public void setWellFormednessChecking() throws XmlException {
        try {
            parser.setFeature("http://apache.org/xml/features/validation/schema", false);
            // parser.setFeature("http://xml.org/sax/features/namespaces", false);

            parser.setFeature("http://xml.org/sax/features/validation", false);

            parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
            parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
            parser.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
        } catch (SAXException saxe) {
            throw new XmlException("Error occurred while setting Xerces features. Reason: " + saxe.getMessage());
        }

    }

    public void setValidationChecking() throws XmlException {
        try {
            parser.setFeature("http://xml.org/sax/features/validation", true);

            parser.setFeature("http://xml.org/sax/features/external-general-entities", true);
            parser.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);

            parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", true);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
            parser.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
        } catch (SAXException saxe) {
            throw new XmlException("Error occurred while setting Xerces features. Reason: " + saxe.getMessage());
        }
    }

    public void createXMLDocument() throws XmlException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new XmlException(e);
        }

    }

    public void createXMLDocument(String docTypeName, String systemId) throws XmlException {
        try {
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

    }

    public void checkFromString(String xml) throws XmlException {
        StringReader strR = null;
        try {
            // strReader = new StringReader(stringOut.toString());
            // source = new InputSource(strReader);
            strR = new StringReader(xml);
            InputSource input = new InputSource(strR);
            // setWellFormednessChecking();
            parser.parse(input);
            document = parser.getDocument();
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
