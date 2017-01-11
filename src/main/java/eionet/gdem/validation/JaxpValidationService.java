package eionet.gdem.validation;

import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dto.ValidateDto;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.qa.QAFeedbackType;
import eionet.gdem.qa.QAResultPostProcessor;
import org.apache.xerces.util.XMLCatalogResolver;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 *
 */
public class JaxpValidationService implements ValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxpValidationService.class);

    private ValidatorErrorHandler errorHandler = new ValidatorErrorHandler();

    private ValidationServiceFeedback validationFeedback = new ValidationServiceFeedback();

    private QAResultPostProcessor postProcessor = new QAResultPostProcessor();

    private InputAnalyser inputAnalyser = new InputAnalyser();

    private SchemaManager schemaManager = new SchemaManager();

    private String originalSchema;
    private String validatedSchema;

    private String warningMessage;

    @Override
    public String getOriginalSchema() {
        return this.originalSchema;
    }

    @Override
    public String getValidatedSchema() {
        return this.validatedSchema;
    }

    @Override
    public String getWarningMessage() {
        return warningMessage;
    }

    @Override
    public List<ValidateDto> getErrorList() {
        return errorHandler.getErrors();
    }


    @Override
    public String validate(String xml) throws DCMException {
        return validateSchema(xml, null);
    }

    @Override
    public String validateSchema(String xml, String schema) throws DCMException {
        HttpFileManager fileManager = new HttpFileManager();
        InputStream is = null;
        try {
            is = fileManager.getFileInputStream(xml, null, true);
            return validateSchema(xml, is, schema);
        } catch (MalformedURLException mfe) {
            throw new DCMException(BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED);
        } catch (IOException ioe) {
            throw new DCMException(BusinessConstants.EXCEPTION_CONVERT_URL_ERROR);
        } catch (Exception e) {
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        } finally {
            fileManager.closeQuietly();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

    @Override
    public String validateSchema(String sourceUrl, InputStream srcStream, String schemaUrl) throws DCMException, XMLConvException {
        inputAnalyser.parseXML(sourceUrl);
        schemaUrl = inputAnalyser.getSchemaOrDTD();
        if (schemaUrl == null) {
            return validationFeedback.formatFeedbackText("XML schema is missing, or it could not be detected.", QAFeedbackType.BLOCKER, true);
        }
        originalSchema = schemaUrl;
        validatedSchema = schemaUrl;
        validationFeedback.setSchema(originalSchema);

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        CustomCatalogResolver resolver = new CustomCatalogResolver();
        String[] catalogs = {Properties.catalogPath};
        resolver.setPreferPublic(true);
        resolver.setCatalogList(catalogs);
        sf.setResourceResolver(resolver);

        boolean isBlocker = false;
        String resultXML = "";

        try {
            Schema schema = sf.newSchema(new URL(schemaUrl));
            Validator validator = schema.newValidator();
            validator.setErrorHandler(errorHandler);
            validator.validate(new StreamSource(srcStream));

            eionet.gdem.dto.Schema schemaObj = schemaManager.getSchema(schemaUrl);
            if (schemaObj != null) {
                isBlocker = schemaObj.isBlocker();
            }
            LOGGER.info("Validation completed");
            validationFeedback.setValidationErrors(getErrorList());
            resultXML = validationFeedback.formatFeedbackText(isBlocker);
            resultXML = postProcessor.processQAResult(resultXML, schemaUrl);
            warningMessage = postProcessor.getWarningMessage(schemaUrl);

        } catch (SAXException e) {
            LOGGER.error("Error: ", e);
            return validationFeedback.formatFeedbackText("Document is not well-formed: " + e.getMessage(), QAFeedbackType.BLOCKER, true);
        } catch (MalformedURLException e) {
            LOGGER.error("Error: ", e);
            return validationFeedback.formatFeedbackText("The parser could not check the document. " + e.getMessage(), QAFeedbackType.BLOCKER, true);
        } catch (IOException e) {
            LOGGER.error("Error: ", e);
            return validationFeedback.formatFeedbackText("The parser could not check the document. " + e.getMessage(), QAFeedbackType.BLOCKER, true);
        }

        return resultXML;
    }

    public class CustomCatalogResolver extends XMLCatalogResolver {
        @Override
        public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier) throws IOException {
            LOGGER.info("Validation Service resolves entity with publicId=" + resourceIdentifier.getPublicId() + " ; systemId=" + resourceIdentifier.getBaseSystemId());
            return super.resolveEntity(resourceIdentifier);
        }
    }

}
