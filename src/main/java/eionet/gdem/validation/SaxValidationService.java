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
 * The Original Code is XMLCONV - Conversion and QA Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Enriko Käsper (TripleDev)
 */

package eionet.gdem.validation;

import eionet.gdem.XMLConvException;
import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.ValidateDto;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.qa.QAFeedbackType;
import eionet.gdem.qa.QAResultPostProcessor;
import eionet.gdem.utils.Utils;


import org.apache.xerces.util.XMLCatalogResolver;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

/**
 * The class offers validation methods for XMLCONV and remote clients.
 *
 * @author Enriko Käsper, TripleDev
 * @author George Sofianos
 */
//TODO Not used any more, check if possible to remove it, or make it an implementation of ValidationService.
public class SaxValidationService {
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(SaxValidationService.class);

    /** ErrorHandler to use when doing XML Schema validation. */
    private ValidatorErrorHandler errHandler;
    /** Validation result object.*/
    private ValidationServiceFeedback validationFeedback;
    /** Ticket for reading password protected files. */
    private String ticket = null;
    /** false for web clients. */
    private boolean trustedMode = true;

    /** Original URL of XML Schema. */
    private String originalSchema = null;
    /** System URL if Schema has cached copy in XMLCONV. */
    private String validatedSchema = null;
    /** Public URL displayed for user. */
    private String validatedSchemaURL = null;

    /** Message displayed for user on web UI. */
    private String warningMessage = null;
    /** Manager to read XML Schema data from database.*/
    private SchemaManager schemaManager = new SchemaManager();

    /**
     * Constructor initializes ErrorHandler and validation feedback object.
     */
    public SaxValidationService() {
        errHandler = new ValidatorErrorHandler();
        validationFeedback = new ValidationServiceFeedback();
    }

    /**
     * Validate XML, read the schema or DTD from the header of XML.
     *
     * @param srcUrl URL of XML file to be validated.
     * @return Validation result as HTML snippet.
     * @throws DCMException in case of unknown system error.
     */
    public String validate(String srcUrl) throws DCMException {
        return validateSchema(srcUrl, null);
    }

    /**
     * Validate XML. If schema is null, then read the schema or DTD from the header of XML. If schema or DTD is defined, then ignore
     * the defined schema or DTD.
     *
     * @param srcUrl XML file URL to be validated.
     * @param schema XML Schema URL.
     * @return Validation result as HTML snippet.
     * @throws DCMException in case of unknown system error.
     */
    public String validateSchema(String srcUrl, String schema) throws DCMException {
        HttpFileManager fileManager = new HttpFileManager();
        InputStream file = null;
        try {
            file = fileManager.getFileInputStream(srcUrl, ticket, true);
            return validateSchema(srcUrl, file, schema);
        } catch (MalformedURLException mfe) {
            throw new DCMException(BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED);
        } catch (IOException ioe) {
            throw new DCMException(BusinessConstants.EXCEPTION_CONVERT_URL_ERROR);
        } catch (Exception e) {
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        } finally {
            fileManager.closeQuietly();
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }

    }

    /**
     * Validate XML. If schema is null, then read the schema or DTD from the header of XML. If schema or DTD is defined, then ignore
     * the defined schema or DTD.schema
     *
     * @param srcStream XML file as InputStream to be validated.
     * @param schema XML Schema URL.
     * @return Validation result as HTML snippet.
     * @throws DCMException in case of unknnown system error.
     * @throws XMLConvException in case of parser error.
     */
    public String validateSchema(String sourceUrl, InputStream srcStream, String schema) throws DCMException, XMLConvException {

        String result = "";
        boolean isDTD = false;
        boolean isBlocker = false;

        if (Utils.isNullStr(schema)) {
            schema = null;
        }

        try {

            SAXParserFactory spfact = SAXParserFactory.newInstance();
            SAXParser parser = spfact.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            reader.setErrorHandler(errHandler);
            XmlconvCatalogResolver catalogResolver = new XmlconvCatalogResolver();
            CustomCatalogResolver resolver = new CustomCatalogResolver();
            String[] catalogs = {Properties.catalogPath};
            resolver.setPreferPublic(true);
            resolver.setCatalogList(catalogs);
            reader.setEntityResolver(resolver);

            // make parser to validate
            reader.setFeature("http://xml.org/sax/features/validation", true);
            reader.setFeature("http://apache.org/xml/features/validation/schema", true);
            reader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);

            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);

            InputAnalyser inputAnalyser = new InputAnalyser();
            inputAnalyser.parseXML(sourceUrl);
            String namespace = inputAnalyser.getSchemaNamespace();

            // if schema is not in the parameter, then sniff it from the header of xml
            if (schema == null) {
                schema = inputAnalyser.getSchemaOrDTD();
                isDTD = inputAnalyser.isDTD();
            } else {
                // if the given schema ends with dtd, then don't do schema validation
                isDTD = schema.endsWith("dtd");
            }

            // schema is already given as a parameter. Read the default namespace from XML file and set external schema.
            if (schema != null) {
                if (!isDTD) {
                    if (Utils.isNullStr(namespace)) {
                        // XML file does not have default namespace
                        setNoNamespaceSchemaProperty(reader, schema);
                    } else {
                        setNamespaceSchemaProperty(reader, namespace, schema);
                    }
                } else {
                    // validate against DTD
                    setLocalSchemaUrl(schema);
                    LocalEntityResolver localResolver = new LocalEntityResolver(schema, getValidatedSchema());
                    reader.setEntityResolver(localResolver);
                }
            } else {
                return validationFeedback.formatFeedbackText("Could not validate XML file. Unable to locate XML Schema reference.", QAFeedbackType.WARNING, isBlocker);
            }
            // TODO: remove duplicate http client from resourceExists check.
            // if schema is not available, then do not parse the XML and throw error
            if (!Utils.resourceExists(getValidatedSchema())) {
                return validationFeedback.formatFeedbackText("Failed to read schema document from the following URL: "
                        + getValidatedSchema(), QAFeedbackType.BLOCKER, isBlocker);
            }
            Schema schemaObj = schemaManager.getSchema(getOriginalSchema());
            if (schemaObj != null) {
                isBlocker = schemaObj.isBlocker();
            }
            validationFeedback.setSchema(getOriginalSchema());
            InputSource is = new InputSource(srcStream);
            reader.parse(is);

        } catch (SAXParseException se) {
            return validationFeedback.formatFeedbackText("Document is not well-formed. Column: " + se.getColumnNumber()
                    + "; line:" + se.getLineNumber() + "; " + se.getMessage(), QAFeedbackType.BLOCKER, isBlocker);
        } catch (IOException ioe) {
            return validationFeedback.formatFeedbackText("Due to an IOException, the parser could not check the document. "
                    + ioe.getMessage(), QAFeedbackType.BLOCKER, isBlocker);
        } catch (Exception e) {
            Exception se = e;
            if (e instanceof SAXException) {
                se = ((SAXException) e).getException();
            }
            if (se != null) {
                LOGGER.error("SAX Exception", se.getMessage());
            } else {
                LOGGER.error("Unknown exception", e.getStackTrace());
            }
            return validationFeedback.formatFeedbackText("The parser could not check the document. " + e.getMessage(), QAFeedbackType.BLOCKER, isBlocker);
        }

        validationFeedback.setValidationErrors(getErrorList());
        result = validationFeedback.formatFeedbackText(isBlocker);

        // validation post-processor
        QAResultPostProcessor postProcessor = new QAResultPostProcessor();
        result = postProcessor.processQAResult(result, schema);
        warningMessage = postProcessor.getWarningMessage(schema);

        return result;
    }

    /**
     * Set the noNamespaceSchemaLocation property.
     *
     * @param reader XMLReader.
     * @param schema XML Schema URL.
     * @throws SAXNotRecognizedException If an error occurs.
     * @throws SAXNotSupportedException If an error occurs.
     */
    private void setNoNamespaceSchemaProperty(XMLReader reader, String schema) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        setLocalSchemaUrl(schema);
        reader.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", getValidatedSchema());
    }

    /**
     * Set the schemaLocation property. The value is "namespace schemaLocation".
     *
     * @param reader XMLReader.
     * @param namespace XML Schema default namespace.
     * @param schema XML Schema URL.
     * @throws SAXNotRecognizedException If an error occurs.
     * @throws SAXNotSupportedException If an error occurs.
     */
    private void setNamespaceSchemaProperty(XMLReader reader, String namespace, String schema) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        setLocalSchemaUrl(schema);
        reader.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation", namespace + " "
                + getValidatedSchema());
    }

    /**
     * Sets the local URL (cached) of given schema, if available.
     *
     * @param schema XML Schema URL.
     */
    protected void setLocalSchemaUrl(String schema) {
        String systemURL = schema;
        String publicURL = schema;

        try {
            String schemaFileName = schemaManager.getUplSchemaURL(schema);
            if (!schema.equals(schemaFileName)) {
                systemURL = "file:///".concat(Properties.schemaFolder).concat("/").concat(schemaFileName);
                publicURL = Properties.gdemURL.concat("/schema/").concat(schemaFileName);
            }
        } catch (DCMException e) {
            // ignore local schema, use the original schema from remote URL
            LOGGER.error(e.getMessage());
        }
        setOriginalSchema(schema);
        setValidatedSchema(systemURL);
        setValidatedSchemaURL(publicURL);
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public void setTrustedMode(boolean mode) {
        this.trustedMode = mode;
    }

    public List<ValidateDto> getErrorList() {
        return errHandler.getErrors();
    }

    public String getValidatedSchema() {
        return validatedSchema;
    }

    public void setValidatedSchema(String validatedSchema) {
        this.validatedSchema = validatedSchema;
    }

    public String getValidatedSchemaURL() {
        return validatedSchemaURL;
    }

    public void setValidatedSchemaURL(String validatedSchemaURL) {
        this.validatedSchemaURL = validatedSchemaURL;
    }

    public String getOriginalSchema() {
        return originalSchema;
    }

    public void setOriginalSchema(String originalSchema) {
        this.originalSchema = originalSchema;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

    /**
     *
     * Extends CatalogResolver used by validation service to be able to log the usage of resources.
     *
     * @author Enriko Käsper
     */
    public class CustomCatalogResolver extends XMLCatalogResolver {
        @Override
        public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier) throws IOException {
            LOGGER.info("Validation Service resolves entity with publicId=" + resourceIdentifier.getPublicId() + " ; systemId=" + resourceIdentifier.getBaseSystemId());
            return super.resolveEntity(resourceIdentifier);
        }
    }

    public class XmlconvCatalogResolver extends CatalogResolver {
        @Override
        public Source resolve(String href, String base) throws TransformerException {
            LOGGER.info("Validation service resolves uri=" + href + " ; base=" + base);
            return super.resolve(href, base);
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
            LOGGER.info("Validation Service resolves entity with publicId=" + publicId + " ; systemId=" + systemId);
            return super.resolveEntity(publicId, systemId);
        }
    }
}