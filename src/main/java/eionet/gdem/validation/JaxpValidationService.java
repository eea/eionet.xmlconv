package eionet.gdem.validation;

import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.dto.ValidateDto;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.http.FollowRedirectException;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.qa.QAFeedbackType;
import eionet.gdem.qa.QAResultPostProcessor;
import org.apache.commons.lang.StringUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.transform.Source;

public class JaxpValidationService implements ValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxpValidationService.class);

    private ValidatorErrorHandler errorHandler = new ValidatorErrorHandler();

    private ValidationServiceFeedback validationFeedback = new ValidationServiceFeedback();

    private QAResultPostProcessor postProcessor = new QAResultPostProcessor();

    private InputAnalyser inputAnalyser = new InputAnalyser();

    private SchemaManager schemaManager = new SchemaManager();

    private List<String> originalSchemas = new ArrayList<>();
    private List<String> validatedSchemasURL = new ArrayList<>();

    private String warningMessage;


    @Override
    public List<String> getOriginalSchemas() {
        return this.originalSchemas;
    }

    @Override
    public List<String> getValidatedSchemasURL() {
        return this.validatedSchemasURL;
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
    public String validate(String xml) throws XMLConvException {
        return validateSchema(xml, null);
    }

    @Override
    public String validateSchema(String xml, String schema) throws XMLConvException {
        HttpFileManager fileManager = new HttpFileManager();
        InputStream is = null;
        try {
            is = fileManager.getFileInputStream(xml, null, true);
            return validateSchema(xml, is, schema);
        } catch (MalformedURLException mfe) {
            throw new XMLConvException(BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED, mfe);
        } catch (IOException ioe) {
            throw new XMLConvException(BusinessConstants.EXCEPTION_CONVERT_URL_ERROR, ioe);
        } catch (Exception e) {
            throw new XMLConvException(BusinessConstants.EXCEPTION_GENERAL, e);
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

        String resultXML = "";
        boolean isBlocker = false;

        List<String> schemas = new ArrayList<>();;
        
        if (StringUtils.isEmpty(schemaUrl)) {
            inputAnalyser.parseXML(sourceUrl);
            schemas.addAll(inputAnalyser.getSchemas());
        } else {
            String[] possibleSchemas = schemaUrl.split("\\s+");
            Arrays.stream(possibleSchemas).forEach(possibleSchema -> {
                if (possibleSchema.endsWith(".xsd")) {
                    schemas.add(possibleSchema);
                }
            });
        }
        
        if (schemas.isEmpty()) {
            return validationFeedback.formatFeedbackText("Could not validate XML file. Unable to locate XML Schema reference.", QAFeedbackType.ERROR, true);
        }
        
        originalSchemas = schemas;
        validationFeedback.setSchemas(schemas);

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        CustomCatalogResolver resolver = new CustomCatalogResolver();
        String[] catalogs = {Properties.catalogPath};
        resolver.setPreferPublic(true);
        resolver.setCatalogList(catalogs);
        sf.setResourceResolver(resolver);
        sf.setErrorHandler(errorHandler);

        List<Source> sources = new ArrayList<>();
        for (String schema : schemas) {
            String localSchema = schemaManager.getUplSchemaURL(schema);
            String validatedSchema;
            if (!StringUtils.equals(schema, localSchema)) {
                //XXX: replace file://
                validatedSchema = "file:///".concat(Properties.schemaFolder).concat("/").concat(localSchema);
                validatedSchemasURL.add(Properties.gdemURL.concat("/schema/").concat(localSchema));
            } else {
                validatedSchema = schema;
                validatedSchemasURL.add(schema);
            }

            try {
                URL schemaLocationUrl = HttpFileManager.followUrlRedirectIfNeeded(new URL(validatedSchema), null);
                sources.add(new StreamSource(schemaLocationUrl.toString()));
            } catch(MalformedURLException | FollowRedirectException ex) {
                LOGGER.info("Malformed schema URL: " + ex.getMessage());
            }
        }
        
        try {
            Source[] sourceArray =  new Source[sources.size()];
            sources.toArray(sourceArray);
            Schema schema = sf.newSchema(sourceArray);
            if (errorHandler.getErrors() != null && errorHandler.getErrors().size() > 0) {
                validationFeedback.setValidationErrors(errorHandler.getErrors());
                return validationFeedback.formatFeedbackText("Document is not well-formed: ", QAFeedbackType.ERROR, isBlocker);
            }
            Validator validator = schema.newValidator();
            validator.setErrorHandler(errorHandler);

            // make parser to validate
            validator.setFeature("http://xml.org/sax/features/validation", true);
            validator.setFeature("http://apache.org/xml/features/validation/schema", true);
            validator.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
            validator.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
            validator.validate(new StreamSource(srcStream));
            LOGGER.info("Validation completed");

            // Refs #53839 for multiple schema validation, validation blocks delivery if at least one schema is blocker
            for (String originalSchema : originalSchemas) {
                eionet.gdem.dto.Schema schemaObj = schemaManager.getSchema(originalSchema);
                if (schemaObj != null && schemaObj.isBlocker()) {
                    isBlocker = true;
                    break;
                }
            }

            validationFeedback.setValidationErrors(errorHandler.getErrors());
            resultXML = validationFeedback.formatFeedbackText(isBlocker);

            for (String originalSchema : originalSchemas) {
                resultXML = postProcessor.processQAResult(resultXML, originalSchema);
                warningMessage = warningMessage == null ?
                        postProcessor.getWarningMessage(originalSchema) :
                        warningMessage + " " + postProcessor.getWarningMessage(originalSchema);
            }

        } catch (SAXException e) {
            LOGGER.info("Document is not well-formed: " + e.getMessage());
            return validationFeedback.formatFeedbackText("Document is not well-formed: " + e.getMessage(), QAFeedbackType.ERROR, true);
        } catch (MalformedURLException e) {
            LOGGER.error("Error: ", e);
            return validationFeedback.formatFeedbackText("The parser could not check the document. " + e.getMessage(), QAFeedbackType.ERROR, true);
        } catch (IOException e) {
            LOGGER.error("Error: ", e);
            return validationFeedback.formatFeedbackText("The parser could not check the document. " + e.getMessage(), QAFeedbackType.ERROR, true);
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
