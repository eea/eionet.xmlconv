package eionet.gdem.utils.xml.vtd;

import eionet.gdem.utils.xml.*;
import org.w3c.dom.Document;

import java.io.InputStream;

/**
 * TODO: Implement methods.
 * @author George Sofianos
 */
public class VtdContext implements IXmlCtx {
    @Override
    public void setWellFormednessChecking() throws XmlException {

    }

    @Override
    public void setValidationChecking() throws XmlException {

    }

    @Override
    public void checkFromInputStream(InputStream inputStream) throws XmlException {

    }

    @Override
    public void checkFromFile(String fullFileName) throws XmlException {

    }

    @Override
    public void checkFromString(String xmlString) throws XmlException {

    }

    @Override
    public void createXMLDocument() throws XmlException {

    }

    @Override
    public void createXMLDocument(String docTypeName, String systemId) throws XmlException {

    }

    @Override
    public XmlUpdater getManager() {
        return null;
    }

    @Override
    public XmlSerializer getSerializer() {
        return null;
    }

    @Override
    public XPathQuery getQueryManager() {
        return null;
    }

    @Override
    public Document getDocument() {
        return null;
    }

    @Override
    public void setDocument(Document document) {

    }
}
