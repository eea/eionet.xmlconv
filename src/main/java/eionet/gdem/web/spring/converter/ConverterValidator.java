package eionet.gdem.web.spring.converter;

import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.conversions.ConversionForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 *
 */
public class ConverterValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return ConversionForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        // parse request parameters
        ConversionForm cForm = (ConversionForm) o;

        String url = cForm.getUrl();
        String convert_id = cForm.getConversionId();

        if (Utils.isNullStr(convert_id)) {
            errors.rejectValue("conversionId", "label.conversion.noconversionselected");
        }
        if (Utils.isNullStr(url)) {
            errors.rejectValue("url","label.conversion.selectSource");
        }
        if (!Utils.isURL(url)) {
            errors.rejectValue("url", "label.conversion.url.malformed");
        }
    }

    public void validateFind(Object o, Errors errors) {
        ConversionForm cForm = (ConversionForm) o;

        String url = cForm.getUrl();
        if (Utils.isNullStr(url)) {
            errors.rejectValue("url","label.conversion.selectSource");
        }
    }
}
