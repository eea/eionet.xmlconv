package eionet.gdem.web.spring.conversions;

import eionet.gdem.web.spring.stylesheet.StylesheetForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 *
 */
public class StylesheetValidator implements Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(StylesheetValidator.class);

    @Override
    public boolean supports(Class<?> aClass) {
        return StylesheetForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

        StylesheetForm stylesheetForm = (StylesheetForm) o;
        MultipartFile xslFile = stylesheetForm.getXslfile();
        String description = stylesheetForm.getDescription();

        if (xslFile == null || xslFile.getSize() == 0) {
            errors.rejectValue("xslfile", "label.stylesheet.validation");
        }
        if (description == null || description.isEmpty()) {
            errors.rejectValue("description","label.stylesheet.error.descriptionMissing");
        }
    }
}
