package eionet.gdem.utils.xml.sax;

import eionet.gdem.utils.xml.*;
import eionet.gdem.utils.xml.dom.XmlManager;
import eionet.gdem.utils.xml.dom.XmlSerialization;
import org.apache.xerces.parsers.SAXParser;
import org.w3c.dom.Document;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author George Sofianos
 *
 */
public class SaxContext implements IXmlCtx {

    private SAXParser parser;
    private ErrorStorage errorStorage;

    public SaxContext() {
        parser = new SAXParser();
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

    @Override
    public void setWellFormednessChecking() throws XmlException {
        try {
            parser.setFeature("http://apache.org/xml/features/validation/schema", false);
            // parser.setFeature("http://xml.org/sax/features/namespaces", false);

            parser.setFeature("http://xml.org/sax/features/validation", false);

            parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
            parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            //parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
            parser.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
        } catch (SAXNotSupportedException e) {
            throw new XmlException("Error occurred while setting Xerces features. Reason: " + e.getMessage());
        } catch (SAXNotRecognizedException e) {
            throw new XmlException("Error occurred while setting Xerces features. Reason: " + e.getMessage());
        }
    }

    @Override
    public void setValidationChecking() throws XmlException {
        try {
            parser.setFeature("http://xml.org/sax/features/validation", true);

            parser.setFeature("http://xml.org/sax/features/external-general-entities", true);
            parser.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);

            //parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", true);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
            parser.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
        } catch (SAXNotSupportedException e) {
            throw new XmlException("Error occurred while setting Xerces features. Reason: " + e.getMessage());
        } catch (SAXNotRecognizedException e) {
            throw new XmlException("Error occurred while setting Xerces features. Reason: " + e.getMessage());
        }
    }

    @Override
    public void checkFromInputStream(InputStream inputStream) throws XmlException {
        InputSource source = new InputSource(inputStream);
        try {
            parser.parse(source);
            if (!errorStorage.isEmpty()) {
                throw new XmlException("Parsing failed: " + errorStorage.getErrors());
            }
        } catch (SAXException e) {
            throw new XmlException("Parsing failed: " + e.getMessage());
        } catch (IOException e) {
            throw new XmlException("Parsing failed: " + e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    @Override
    public void checkFromFile(String fullFileName) throws XmlException {
        try {
            parser.parse(fullFileName);
            if (!errorStorage.isEmpty()) {
                throw new XmlException("Parsing failed: " + errorStorage.getErrors());
            }
        } catch (SAXException e) {
            throw new XmlException("Parsing failed: " + e.getMessage());
        } catch (IOException e) {
            throw new XmlException("Parsing failed: " + e.getMessage());
        }
    }

    @Override
    public void checkFromString(String xmlString) throws XmlException {
        InputStream is = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
        InputSource source = new InputSource(is);
        try {
            parser.parse(source);
            if (!errorStorage.isEmpty()) {
                throw new XmlException("Parsing failed: " + errorStorage.getErrors());
            }
        } catch (SAXException e) {
            throw new XmlException("Parsing failed: " + e.getMessage());
        } catch (IOException e) {
            throw new XmlException("Parsing failed: " + e.getMessage());
        }


    }

    @Override
    public void createXMLDocument() throws XmlException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createXMLDocument(String docTypeName, String systemId) throws XmlException {
        throw new UnsupportedOperationException();
    }

    @Override
    public XmlUpdater getManager() {
        return new XmlManager(this);
    }

    @Override
    public XmlSerializer getSerializer() {
        return new XmlSerialization(this);
    }

    @Override
    public XPathQuery getQueryManager() {
        // Don't use SaX for Xpath.
        return null;
    }

    @Override
    public Document getDocument() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDocument(Document document) {
        throw new UnsupportedOperationException();
    }
}
