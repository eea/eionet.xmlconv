package eionet.gdem.web.spring.qasandbox;

import eionet.gdem.utils.Utils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 *
 */
public class QASandboxValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return QASandboxForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

    }

    public void validateSaveScript(Object o, Errors errors) {
        QASandboxForm cForm = (QASandboxForm) o;

        String scriptId = cForm.getScriptId();
        String content = cForm.getScriptContent();
        if (Utils.isNullStr(scriptId)) {
            errors.rejectValue("scriptId", "error.qasandbox.missingId");
        }
        if (Utils.isNullStr(content)) {
            errors.rejectValue("content", "error.qasandbox.missingContent");
        }
    }

    public void validateExtract(Object o, Errors errors) {
        QASandboxForm cForm = (QASandboxForm) o;

        String sourceUrl = cForm.getSourceUrl();
        if (Utils.isNullStr(sourceUrl)) {
            errors.rejectValue("sourceUrl", "error.qasandbox.missingUrl");
        }
        if (!Utils.isURL(sourceUrl)) {
            errors.rejectValue("sourceUrl", "error.qasandbox.notUrl");
        }
    }

    public void validateFind(Object o, Errors errors) {
        QASandboxForm cForm = (QASandboxForm) o;
        String schemaUrl = cForm.getSchemaUrl();

        if (Utils.isNullStr(schemaUrl)) {
            errors.rejectValue("schemaUrl", "error.qasandbox.missingSchemaUrl");
        }
    }

    public void validateWorkQueue(Object o, Errors errors) {
        QASandboxForm cForm = (QASandboxForm) o;

        String sourceUrl = cForm.getSourceUrl();
        String content = cForm.getScriptContent();
        String schemaUrl = cForm.getSchemaUrl();

        if (Utils.isNullStr(sourceUrl)) {
            errors.rejectValue("sourceUrl", "error.qasandbox.missingUrl");
        }

        if (Utils.isNullStr(content) && !cForm.isShowScripts()) {
            errors.rejectValue("content", "error.qasandbox.missingContent");
        }
        if (Utils.isNullStr(schemaUrl) && cForm.isShowScripts()) {
            errors.rejectValue("schemaUrl", "error.qasandbox.error.qasandbox.missingSchemaUrl");
        }
        if (!Utils.isURL(sourceUrl)) {
            errors.rejectValue("sourceUrl", "error.qasandbox.notUrl");
        }
    }

    public void validateRunScript(Object o, Errors errors) {
        QASandboxForm cForm = (QASandboxForm) o;

        String sourceUrl = cForm.getSourceUrl();
        String scriptId = cForm.getScriptId();
        String scriptContent = cForm.getScriptContent();
        boolean showScripts = cForm.isShowScripts();

        if (showScripts && Utils.isNullStr(scriptId)) {
            errors.rejectValue("scriptId", "error.qasandbox.missingId");
        }
        if (!showScripts && Utils.isNullStr(scriptContent)) {
            errors.rejectValue("scriptContent", "error.qasandbox.missingContent");
        }
        if (Utils.isNullStr(sourceUrl)) {
            errors.rejectValue("sourceUrl", "error.qasandbox.missingUrl");
        }
        if (!Utils.isURL(sourceUrl)) {
            errors.rejectValue("sourceUrl", "error.qasandbox.notUrl");
        }
    }

    public void validateEdit(Object o, Errors errors) {
        QASandboxForm cForm = (QASandboxForm) o;

        String scriptId = cForm.getScriptId();
        if (Utils.isNullStr(scriptId)) {
            errors.rejectValue("scriptId", "error.qasandbox.missingId");
        }
    }
}
