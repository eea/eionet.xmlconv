package eionet.gdem.web.spring.hosts;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 *
 */
public class HostsFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return HostForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        HostForm form = (HostForm) o;
        String host = form.getHost();
        String username = form.getUsername();
        String password = form.getPassword();

        if (StringUtils.isEmpty(host)) {
            errors.rejectValue("host", "label.hosts.error.url");
        }
        if (StringUtils.isEmpty(username)) {
            errors.rejectValue("username", "label.hosts.error.username");
        }
        if (StringUtils.isEmpty(password)) {
            errors.rejectValue("password", "label.hosts.error.password");
        }
    }
}
