package eionet.gdem.web.spring.qasandbox;

import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.SpringMessages;
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
        QASandboxForm cForm = (QASandboxForm) o;

        String sourceUrl = cForm.getSourceUrl();
        String content = cForm.getScriptContent();
        String scriptType = cForm.getScriptType();
        String schemaUrl = cForm.getSchemaUrl();
        String scriptId = cForm.getScriptId();
        String schemaId = cForm.getSchemaId();
        String scriptContent = cForm.getScriptContent();
        boolean showScripts = cForm.isShowScripts();
        String action = cForm.getAction();

        if ("findScripts".equals(action)) {
            if (Utils.isNullStr(schemaUrl)) {
                errors.rejectValue("schemaUrl", "error.qasandbox.missingSchemaUrl");
            }
        } else if ("extractSchema".equals(action)) {
            if (Utils.isNullStr(sourceUrl)) {
                errors.rejectValue("sourceUrl", "error.qasandbox.missingUrl");
            }
            if (!Utils.isURL(sourceUrl)) {
                errors.rejectValue("sourceUrl", "error.qasandbox.notUrl");
            }
        } else if ("addToWorkqueue".equals(action)) {
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
        } else if ("runScript".equals(action)) {
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
        } else if ("editScript".equals(action)) {
            if (Utils.isNullStr(scriptId)) {
                errors.rejectValue("scriptId", "error.qasandbox.missingId");
            }
        } else if ("saveScript".equals(action)) {
            if (Utils.isNullStr(scriptId)) {
                errors.rejectValue("scriptId", "error.qasandbox.missingId");
            }
            if (Utils.isNullStr(content)) {
                errors.rejectValue("content", "error.qasandbox.missingContent");
            }
        } else if ("openQA".equals(action)) {
            if (Utils.isNullStr(schemaId)) {
                errors.rejectValue("schemaId", "error.qasandbox.missingSchemaId");
            }
        }
    }
}
