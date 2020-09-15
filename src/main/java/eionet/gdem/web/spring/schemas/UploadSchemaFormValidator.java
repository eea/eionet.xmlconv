package eionet.gdem.web.spring.schemas;

import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.SpringMessages;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 */
public class UploadSchemaFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return UploadSchemaForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        UploadSchemaForm form = (UploadSchemaForm) o;
        MultipartFile schemaFile = form.getSchemaFile();
        String desc = form.getDescription();
        String schemaUrl = form.getSchemaUrl();
        boolean doValidation = form.isDoValidation();
        String schemaLang = form.getSchemaLang();
        boolean blocker = form.isBlockerValidation();
        Long maxExecutionTime = form.getMaxExecutionTime();

        if ((schemaFile == null || schemaFile.getSize() == 0) && Utils.isNullStr(schemaUrl)) {
            errors.rejectValue("schemaFile", "label.uplSchema.validation");
        }

        if (!(new SchemaUrlValidator().isValidUrlSet(schemaUrl))) {
            errors.rejectValue("schemaUrl", "label.uplSchema.validation.urlFormat");
        }

        if (maxExecutionTime == null) {
            errors.rejectValue("maxExecutionTime", "label.uplSchema.validation.null.maxExecutionTime");
        }

        if (maxExecutionTime!=null && maxExecutionTime == 0) {
            errors.rejectValue("maxExecutionTime", "label.uplSchema.validation.zero.maxExecutionTime");
        }
    }
}
