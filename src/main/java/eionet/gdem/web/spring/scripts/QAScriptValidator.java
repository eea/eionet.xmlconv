package eionet.gdem.web.spring.scripts;

import eionet.gdem.qa.XQScript;
import eionet.gdem.utils.Utils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 *
 */
public class QAScriptValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return QAScriptForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        QAScriptForm form = (QAScriptForm) o;
        String schemaId = form.getSchemaId();
        String shortName = form.getShortName();
        String desc = form.getDescription();
        String schema = form.getSchema();
        String resultType = form.getResultType();
        String scriptType = form.getScriptType();
        String url = form.getUrl();
        String upperLimit = form.getUpperLimit();
        MultipartFile scriptFile = form.getScriptFile();

        // FME script type validations
        if (XQScript.SCRIPT_LANG_FME.equals(scriptType)) {
            if (url == null || url.equals("")) {
                errors.rejectValue("scriptType", "label.qascript.fme.url.validation");
//                errors.add(messageService.getMessage());
//                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            }
            // Other script type validations
        } else {
            if ((scriptFile == null || scriptFile.getSize() == 0) && Utils.isNullStr(url)) {
                errors.rejectValue("scriptFile", "label.qascript.file.validation");
//                errors.add(messageService.getMessage());
//                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            }

            // Zip result type can only be selected for FME scripts
            if (XQScript.SCRIPT_RESULTTYPE_ZIP.equals(resultType)) {
                errors.rejectValue("resultType", "label.qascript.zip.validation");
//                errors.add(messageService.getMessage());
//                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            }
        }

        if (schema == null || schema.equals("")) {
            errors.rejectValue("schema", "label.qascript.schema.validation");
//            errors.add(messageService.getMessage());
//            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        }

        // upper limit between 0 and 10Gb
        if (upperLimit == null || !Utils.isNum(upperLimit) || Integer.parseInt(upperLimit) <= 0
                || Integer.parseInt(upperLimit) > 10000) {
            errors.rejectValue("upperLimit", "label.qascript.upperlimit.validation");
//            errors.add(messageService.getMessage());
//            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        }
    }
}
