package eionet.gdem.web.spring.validation;

import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.utils.Utils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ValidationFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return ValidationForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationForm form = (ValidationForm) o;
        String url = form.getXmlUrl();
        String schemaUrl = form.getSchemaUrl();

        if (Utils.isNullStr(url)) {
            errors.rejectValue("xmlUrl", "label.conversion.selectSource");
        } else if (!Utils.isURL(url)) {
            errors.rejectValue("xmlUrl", BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED);
        } else if (!Utils.isNullStr(schemaUrl) && !Utils.isURL(schemaUrl)) {
            errors.rejectValue("schemaUrl", "label.uplSchema.validation.urlFormat");
        }
    }

}
