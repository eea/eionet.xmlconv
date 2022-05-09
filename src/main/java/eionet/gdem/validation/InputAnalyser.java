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

import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * The class analyses XML file and extracts XML Schema, DTD, namespace and root element information.
 */
public class InputAnalyser {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputAnalyser.class);
    private String schemaOrDTD;
    private String rootElement;
    private String namespace;
    private String dtdPublicId;
    private boolean hasNamespace;
    private String schemaNamespace;
    private boolean isDTD;

    /**
     * Parse XML and load information from XML.
     *
     * @param srcUrl Source url
     * @return Parsed XML
     * @throws DCMException If an error occurs.
     */
    public String parseXML(String srcUrl) throws DCMException {
        HttpFileManager fileManager = new HttpFileManager();
        InputStream stream = null;
        try {
            stream = fileManager.getInputStream(srcUrl, null, true);

            return parseXML(stream);
        } catch (MalformedURLException mfe) {
            throw new DCMException(BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED,mfe.getMessage());
        } catch (IOException ioe) {
            throw new DCMException(BusinessConstants.EXCEPTION_CONVERT_URL_ERROR,ioe.getMessage());
        } catch (SAXException e) {
            throw new DCMException(BusinessConstants.EXCEPTION_XMLPARSING_ERROR,e.getMessage());
        } catch (XMLConvException e) {
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL,e.getMessage());
        } catch (Exception e) {
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL,e.getMessage());
        } finally {
            IOUtils.closeQuietly(stream);
        }

    }

    /**
     * Parse info from InputStream.
     *
     * @param input InputStream
     * @return Parsed XML
     * @throws XMLConvException If an error occurs.
     * @throws SAXException If an error occurs.
     */
    public String parseXML(InputStream input) throws XMLConvException, SAXException {
        try {
            InputSource is = new InputSource(input);
            SchemaFinder handler = new SchemaFinder();
            SAXParserFactory spfact = SAXParserFactory.newInstance();
            SAXParser parser = spfact.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            spfact.setValidating(false);

            // make parser to not validate
            reader.setFeature("http://xml.org/sax/features/validation", false);
            reader.setFeature("http://apache.org/xml/features/validation/schema", false);
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setFeature("http://xml.org/sax/features/external-general-entities",false);
            reader.setFeature("http://xml.org/sax/features/external-parameter-entities",false);

            SAXDoctypeReader doctypeReader = new SAXDoctypeReader();
            // turn on dtd handling
            try {
                parser.setProperty("http://xml.org/sax/properties/lexical-handler", doctypeReader);
            } catch (SAXNotRecognizedException e) {
                LOGGER.error("Installed XML parser does not provide lexical events...");
            } catch (SAXNotSupportedException e) {
                LOGGER.error("Cannot turn on comment processing here");
            }

            reader.setContentHandler(handler);

            try {
                reader.parse(is);
            } catch (SAXException e) {
                if (!e.getMessage().equals("OK"))
                    throw new SAXException(e);
            }
            schemaOrDTD = !Utils.isNullStr(handler.getSchemaLocation()) ? handler.getSchemaLocation() : null;
            rootElement = handler.getStartTag();
            namespace = handler.getStartTagNamespace();
            hasNamespace = handler.hasNamespace();
            schemaNamespace = handler.getSchemaNamespace();

            // Find DTD, if schema is null
            if (schemaOrDTD == null) {
                schemaOrDTD = Utils.isURL(doctypeReader.getDTD()) ? doctypeReader.getDTD() : null;
                dtdPublicId = doctypeReader.getDTDPublicId();
                setDTD(true);
            }
        } catch (SAXParseException e) {
            LOGGER.error("XML Parsing exception: " + e);
            throw (SAXException) e;
        } catch (SAXException e) {
            LOGGER.error("XML Parsing exception: " + e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("XML Parsing exception: " + e);
            throw new XMLConvException("Error parsing: " + e, e);
        }

        return "OK";
    }

    public String getSchemaOrDTD() {
        return this.schemaOrDTD;
    }

    public String getRootElement() {
        return this.rootElement;
    }

    public String getNamespace() {
        return this.namespace;
    }

    /**
     * Returns if xml has namespace.
     * @return True if xml has namespace
     */
    public boolean hasNamespace() {
        return this.hasNamespace;
    }

    public String getSchemaNamespace() {
        return schemaNamespace;
    }

    public void setSchemaNamespace(String schemaNamespace) {
        this.schemaNamespace = schemaNamespace;
    }

    public boolean isDTD() {
        return isDTD;
    }

    public void setDTD(boolean isDTD) {
        this.isDTD = isDTD;
    }

    public String getDtdPublicId() {
        return dtdPublicId;
    }

    public void setDtdPublicId(String dtdPublicId) {
        this.dtdPublicId = dtdPublicId;
    }

    public boolean isHasNamespace() {
        return hasNamespace;
    }

    public void setHasNamespace(boolean hasNamespace) {
        this.hasNamespace = hasNamespace;
    }

    public void setSchemaOrDTD(String schemaOrDTD) {
        this.schemaOrDTD = schemaOrDTD;
    }

    public void setRootElement(String rootElement) {
        this.rootElement = rootElement;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

}
