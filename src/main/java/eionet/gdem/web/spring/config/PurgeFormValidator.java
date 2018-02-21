package eionet.gdem.web.spring.config;

import eionet.gdem.web.spring.SpringMessages;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 *
 */
public class PurgeFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return PurgeForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PurgeForm form = (PurgeForm) o;
        Integer nofDays = Integer.parseInt(form.getNofDays());
        if (nofDays == null || "".equals(nofDays.toString()) || nofDays <= 0) {
            errors.rejectValue("nofDays", "label.config.purge.validation");
        }
    }
}
