package eionet.gdem.web.spring.validation;

import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.SpringMessages;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 *
 */
public class ValidationFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return ValidationForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationForm form = (ValidationForm) o;
        String url = form.getXmlUrl();

        if (Utils.isNullStr(url)) {
            errors.rejectValue("url", "label.conversion.selectSource");
        }
        if (!Utils.isURL(url)) {
            errors.rejectValue("url", BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED);
        }
    }
}
