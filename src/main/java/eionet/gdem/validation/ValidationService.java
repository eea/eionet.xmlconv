/**
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
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dto.ValidateDto;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.QAResultPostProcessor;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Utils;

/**
 * The class offers validation methods for XMLCONV and remote clients
 *
 * @author Enriko Käsper, TietoEnator Estonia AS ValidationService
 */

public class ValidationService {
    /** */
    private static final Log LOGGER = LogFactory.getLog(ValidationService.class);

    private StringBuffer errors;
    private StringBuffer htmlErrors;
    private String uriXml;
    private ArrayList errorsList;
    private ErrorHandler errHandler;
    private String ticket = null;
    private boolean trustedMode = true;// false for web clients
    private String originalSchema = null; // original URL

    private String validatedSchema = null; // system URL
    private String validatedSchemaURL = null; // public URL

    private String warningMessage = null;

    /**
     * Constructor for remote methods
     */
    public ValidationService() {
        errors = new StringBuffer();
        htmlErrors = new StringBuffer();
        errHandler = new GErrorHandler(errors, htmlErrors);
    }

    /**
     * Constructor for web client
     *
     * @param list
     */
    public ValidationService(boolean list) {

        errorsList = new ArrayList();
        errHandler = new ValidatorErrorHandler(errorsList);
    }

    /**
     * validate XML, read the schema or DTD from the header of XML
     *
     * @param srcUrl
     * @return
     * @throws DCMException
     */
    public String validate(String srcUrl) throws DCMException {
        return validateSchema(srcUrl, null);
    }

    /**
     * Validate XML. If schema is null, then read the schema or DTD from the header of XML. If schema or DTD is defined, then ignore
     * the defined schema or DTD
     *
     * @param srcUrl
     * @param schema
     * @return Formatted text with results (errors or OK)
     * @throws DCMException
     */
    public String validateSchema(String srcUrl, String schema) throws DCMException {
        InputFile src = null;
        uriXml = srcUrl;
        try {
            src = new InputFile(srcUrl);
            src.setTrustedMode(trustedMode);
            src.setAuthentication(ticket);
            return validateSchema(src.getSrcInputStream(), schema);
        } catch (MalformedURLException mfe) {
            throw new DCMException(BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED);
        } catch (IOException ioe) {
            throw new DCMException(BusinessConstants.EXCEPTION_CONVERT_URL_ERROR);
        } catch (Exception e) {
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        } finally {
            if (src != null) {
                try {
                    src.close();
                } catch (Exception e) {
                }
                ;
            }
        }

    }

    public String validateSchema(InputStream src_stream, String schema) throws DCMException {
        boolean isDTD = false;
        schema = Utils.isNullStr(schema) ? null : schema;

        try {

            SAXParserFactory spfact = SAXParserFactory.newInstance();
            SAXParser parser = spfact.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            reader.setErrorHandler(errHandler);

            // make parser to validate
            reader.setFeature("http://xml.org/sax/features/validation", true);
            reader.setFeature("http://apache.org/xml/features/validation/schema", true);
            reader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);

            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);

            InputAnalyser inputAnalyser = new InputAnalyser();
            inputAnalyser.parseXML(uriXml);
            String namespace = inputAnalyser.getSchemaNamespace();

            // if schema is not in the parameter, then sniff it from the header of xml
            if (schema == null) {
                schema = inputAnalyser.getSchemaOrDTD();
                isDTD = inputAnalyser.isDTD();
            } else {
                // if the given schema ends with dtd, then don't do schema validation
                isDTD = schema.endsWith("dtd");
            }

            // schmea is already given as a parameter. Read the default namespace from XML file and set external schema.
            if (schema != null) {
                if (!isDTD) {
                    // String namespace = getDefaultNamespace();
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
                return GErrorHandler.formatResultText(
                        "WARNING: Could not validate XML file. Unable to locate XML Schema reference.", null);
            }
            // if schema is not available, then do not parse the XML and throw error
            if (!Utils.resourceExists(getValidatedSchema())) {
                return GErrorHandler.formatResultText("ERROR: Failed to read schema document from the following URL: "
                        + getValidatedSchema(), null);
            }
            if (errHandler instanceof GErrorHandler) {
                ((GErrorHandler) errHandler).setSchema(getOriginalSchema());
            }
            InputSource is = new InputSource(src_stream);
            reader.parse(is);

        } catch (SAXParseException se) {
            return GErrorHandler.formatResultText("ERROR: Document is not well-formed. Column: " + se.getColumnNumber()
                    + "; line:" + se.getLineNumber() + "; " + se.getMessage(), null);
            // ignore
        } catch (IOException ioe) {
            return GErrorHandler.formatResultText(
                    "ERROR: Due to an IOException, the parser could not check the document. " + ioe.getMessage(), null);
        } catch (Exception e) {
            Exception se = e;
            if (e instanceof SAXException) {
                se = ((SAXException) e).getException();
            }
            if (se != null) {
                se.printStackTrace(System.err);
            } else {
                e.printStackTrace(System.err);
            }
            return GErrorHandler.formatResultText("ERROR: The parser could not check the document. " + e.getMessage(), null);
            // throw new GDEMException("Error parsing: " + e.toString());
        }

        QAResultPostProcessor postProcessor = new QAResultPostProcessor();
        String result = null;

        // we have errors!
        if ((errors != null && errors.length() > 0)) {
            // return errors.toString();
            htmlErrors.append("</table></div>");

            result = postProcessor.processQAResult(htmlErrors.toString(), schema);
        } else if ((errorsList != null && errorsList.size() > 0)) {
            this.warningMessage = postProcessor.getWarningMessage(schema);
            result = getErrorList().toString();
        } else {
            result = GErrorHandler.formatResultText(Properties.getMessage("label.validation.result.ok"), getOriginalSchema());
            result = postProcessor.processQAResult(result, schema);
            this.warningMessage = postProcessor.getWarningMessage(schema);
        }
        return result;
    }

    /**
     * Read default namespace from XML file
     *
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws FactoryConfigurationError
     * @throws SAXException
     */
    private String getDefaultNamespace() throws IOException, ParserConfigurationException, FactoryConfigurationError, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputFile src = null;
        String namespace = null;
        try {
            src = new InputFile(uriXml);
            src.setTrustedMode(trustedMode);
            src.setAuthentication(ticket);
            Document doc = builder.parse(src.getSrcInputStream());

            Element root = doc.getDocumentElement();
            String rootName = root.getTagName();

            String schema = root.getAttribute("xsi:schemaLocation");

            if (rootName.indexOf(":") > 0) {
                String attName1 = "xmlns:" + rootName.substring(0, rootName.indexOf(":"));
                namespace = root.getAttribute(attName1);
            }
        } finally {
            if (src != null) {
                try {
                    src.close();

                } catch (Exception e) {
                }
            }
        }
        return namespace;
    }

    /**
     * Set the noNamespaceSchemaLocation property
     *
     * @param reader
     * @param schema
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private void setNoNamespaceSchemaProperty(XMLReader reader, String schema) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        setLocalSchemaUrl(schema);
        reader.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", getValidatedSchema());
    }

    /**
     * Set the schemaLocation property. The value is "namespace schemaLocation".
     *
     * @param reader
     * @param namespace
     * @param schema
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private void setNamespaceSchemaProperty(XMLReader reader, String namespace, String schema) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        setLocalSchemaUrl(schema);
        reader.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation", namespace + " "
                + getValidatedSchema());
    }

    /**
     * sets the local URL of given schema, if available
     *
     * @param schema
     * @return
     */
    protected void setLocalSchemaUrl(String schema) {
        String systemURL = schema;
        String publicURL = schema;
        SchemaManager sm = new SchemaManager();

        try {
            String schemaFileName = sm.getUplSchemaURL(schema);
            if (!schema.equals(schemaFileName)) {
                String rootPackageFolder = getClass().getClassLoader().getResource("gdem.properties").getFile();
                systemURL = "file:///".concat(Properties.schemaFolder).concat("/").concat(schemaFileName);
                publicURL = Properties.gdemURL.concat("/schema/").concat(schemaFileName);
            }
        } catch (DCMException e) {
            // ignore local schema, use the original schema from remote URL
            LOGGER.error(e);
        }
        setOriginalSchema(schema);
        setValidatedSchema(systemURL);
        setValidatedSchemaURL(publicURL);
    }

    public void setTicket(String _ticket) {
        this.ticket = _ticket;
    }

    public void setTrustedMode(boolean mode) {
        this.trustedMode = mode;
    }

    public ArrayList getErrorList() {
        return errorsList;
    }

    public void printList() {
        for (int j = 0; j < errorsList.size(); j++) {
            ValidateDto val = (ValidateDto) errorsList.get(j);
        }
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

    public static void main(String[] s) {

        try {
            GDEMServices.setTestConnection(true);
            // String xml = "http://reportek2.eionet.eu.int/colqaj8nw/envqe8zva/countrynames.tmx";
            // String xml = "http://cdrtest.eionet.europa.eu/ee/eea/ewn3/envrmtmhw/EE bodies.xml";
            String xml = "http://localhost:8080/xmlconv/tmp/xliff.xml";
            // String xml = "http://localhost:8080/xmlconv/tmp/seed-gw-valid.xml";
            // xml="http://cdrtest.eionet.europa.eu/ee/eu/art17/envriytkg/general-report.xml";
            String sch = "http://www.oasis-open.org/committees/xliff/documents/xliff.dtd";
            // String sch = "http://dd.eionet.europa.eu/GetSchema?id=TBL4564";
            // String sch = "http://www.lisa.org/tmx/tmx14.dtd";
            // String sch = "http://roddev.eionet.eu.int/waterdemo/water_measurements.xsd";

            ValidationService v = new ValidationService(true);

            // String result = v.validate("http://reporter.ceetel.net:18180/nl/eea/ewn3/envqyyafg/BG_bodies_Rubi.xml");
            System.out.println(v.validateSchema(xml, null));
            ArrayList errs = v.getErrorList();
            if (errs != null && errs.size() > 0) {
                for (int i = 0; i < errs.size(); i++) {
                    System.out.println(((ValidateDto) errs.get(i)).toString());
                }
            }
            // System.out.println(result);
            // v.log(v.validate(xml));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("===== " + e.toString());
        }

    }

}
