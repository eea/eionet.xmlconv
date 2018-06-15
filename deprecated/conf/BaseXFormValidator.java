package eionet.gdem.web.spring.config;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 *
 */
public class BaseXFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return BaseXForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

    }
}
