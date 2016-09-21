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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Utils;

/**
 * The class anayses XML file and extracts XML Schema, DTD, namespace and root element information.
 *
 * @author Enriko Käsper, TietoEnator Estonia AS InputAnalyser
 * @author George Sofianos
 */
public class InputAnalyser {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputAnalyser.class);
    private String schemaOrDTD = null;
    private String rootElement = null;
    private String namespace = null;
    private String dtdPublicId = null;
    private boolean hasNamespace = false;
    private String schemaNamespace = null;
    private boolean isDTD = false;

    /**
     * Default Constructor
     */
    public InputAnalyser() {

    }

    /**
     * Parse XML and load information from XML.
     *
     * @param srcUrl Source url
     * @return Parsed XML
     * @throws DCMException If an error occurs.
     */
    public String parseXML(String srcUrl) throws DCMException {
        InputFile src = null;
        try {
            src = new InputFile(srcUrl);
            src.setTrustedMode(true);
            return parseXML(src.getSrcInputStream());
        } catch (MalformedURLException mfe) {
            // throw new XMLConvException("Bad URL : " + mfe.toString());
            throw new DCMException(BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED);
        } catch (IOException ioe) {
            // throw new XMLConvException("Error opening URL " + ioe.toString());
            throw new DCMException(BusinessConstants.EXCEPTION_CONVERT_URL_ERROR);
        } catch (SAXException e) {
            e.printStackTrace();
            throw new DCMException(BusinessConstants.EXCEPTION_XMLPARSING_ERROR);
        } catch (XMLConvException e) {
            e.printStackTrace();
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        finally {
            try {
                if (src != null)
                    src.close();
            } catch (Exception e) {
            }
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

            SAXDoctypeReader doctype_reader = new SAXDoctypeReader();
            // turn on dtd handling
            try {
                parser.setProperty("http://xml.org/sax/properties/lexical-handler", doctype_reader);
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
                schemaOrDTD = Utils.isURL(doctype_reader.getDTD()) ? doctype_reader.getDTD() : null;
                dtdPublicId = doctype_reader.getDTDPublicId();
                setDTD(true);
            }
        } catch (SAXParseException se) {
            se.printStackTrace(System.err);
            throw (SAXException) se;
        } catch (SAXException se) {
            se.printStackTrace(System.err);
            throw se;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new XMLConvException("Error parsing: " + e.toString(), e);
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
