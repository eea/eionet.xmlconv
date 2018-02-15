package eionet.gdem.web.spring.converter;

import eionet.gdem.utils.Utils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 *
 */
public class Excel2xmlValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return Excel2xmlForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Excel2xmlForm form = (Excel2xmlForm) o;
        String url = form.getUrl();
        String split = form.getSplit();
        String sheet = form.getSheet();
        boolean showConversionLog = form.isConversionLog();

        // parse request parameters
        if (Utils.isNullStr(url)) {
            errors.rejectValue("url", "label.conversion.insertExcelUrl");
        }
        if (Utils.isNullStr(split)) {
            errors.rejectValue("split", "label.conversion.insertSplit");
        }
        if ("split".equals(split) && Utils.isNullStr(sheet) && !showConversionLog) {
            errors.reject("label.conversion.insertSheet");
        }
    }
}
