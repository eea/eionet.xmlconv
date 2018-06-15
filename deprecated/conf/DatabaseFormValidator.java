package eionet.gdem.web.spring.config;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 *
 */
public class DatabaseFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return DatabaseForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        DatabaseForm form = (DatabaseForm) o;

        String dbUrl = form.getUrl();
        if (dbUrl == null || dbUrl.equals("")) {
            errors.rejectValue("dbUrl", "label.config.ldap.url.validation");
        }
    }
}
