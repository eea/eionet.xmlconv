package eionet.gdem.web.spring.config;

import eionet.gdem.web.spring.SpringMessages;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 *
 */
public class LdapFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return LdapForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        LdapForm form = (LdapForm) o;
        String url = form.getUrl();

        if (url == null || url.equals("")) {
            errors.rejectValue("url", "label.config.ldap.url.validation");
        }
    }
}
