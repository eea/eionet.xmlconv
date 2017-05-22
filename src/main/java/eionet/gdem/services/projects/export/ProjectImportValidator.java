package eionet.gdem.services.projects.export;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validate metadata and file structure within the zip file.
 *
 */
@Component
public class ProjectImportValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        ProjectImportWrapper wrapper = (ProjectImportWrapper) o;
        if (true == false) {
            errors.rejectValue("test", "test.test");
        }
    }
}
