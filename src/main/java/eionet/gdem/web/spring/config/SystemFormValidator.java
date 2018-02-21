package eionet.gdem.web.spring.config;

import eionet.gdem.web.spring.SpringMessages;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 *
 */
public class SystemFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return SystemForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        SystemForm form = (SystemForm) o;

        String cmdXGawk = form.getCmdXGawk();
        Long qaTimeout = form.getQaTimeout();
        if (qaTimeout == null || "".equals(qaTimeout.toString()) || qaTimeout <= 0) {
            errors.rejectValue("qaTimeout", "label.config.system.qatimeout.validation");
        }
    }
}
