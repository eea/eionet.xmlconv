package eionet.gdem.validation;

import eionet.gdem.XMLConvException;
import eionet.gdem.dto.ValidateDto;
import eionet.gdem.exceptions.DCMException;

import java.io.InputStream;
import java.util.List;

/**
 *
 */
public interface ValidationService {

    public String validate(String xml) throws XMLConvException;
    public String validateSchema(String xml, String schema) throws XMLConvException;
    public String validateSchema(String sourceUrl, InputStream srcStream, String schema) throws DCMException, XMLConvException;
    public List<ValidateDto> getErrorList();
    public String getWarningMessage();
    public List<String> getOriginalSchemas();
    public List<String> getValidatedSchemasURL();

}
