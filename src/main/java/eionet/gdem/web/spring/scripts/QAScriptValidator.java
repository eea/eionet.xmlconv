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

    }

    public void validateAdd(Object o, Errors errors) {
        QAScriptForm form = (QAScriptForm) o;
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
            }
            // async fme scripts have zip as result type
            if (form.isAsynchronousExecution() && !XQScript.SCRIPT_RESULTTYPE_ZIP.equals(resultType)) {
                errors.rejectValue("resultType", "label.qascript.async.fme.zip.validation");
            }
        } else {
            if ((scriptFile == null || scriptFile.getSize() == 0) && Utils.isNullStr(url)) {
                errors.rejectValue("scriptFile", "label.qascript.file.validation");
            }

            // Zip result type can only be selected for FME scripts
            if (XQScript.SCRIPT_RESULTTYPE_ZIP.equals(resultType)) {
                errors.rejectValue("resultType", "label.qascript.zip.validation");
            }
        }

        if (schema == null || schema.equals("")) {
            errors.rejectValue("schema", "label.qascript.schema.validation");
        }

        // upper limit between 0 and 10Gb
        if (upperLimit == null || !Utils.isNum(upperLimit) || Integer.parseInt(upperLimit) <= 0
                || Integer.parseInt(upperLimit) > 10000) {
            errors.rejectValue("upperLimit", "label.qascript.upperlimit.validation");
        }
    }

    public void validateUpdate(Object o, Errors errors) {
        QAScriptForm form = (QAScriptForm) o;
        String resultType = form.getResultType();
        String scriptType = form.getScriptType();
        String upperLimit = form.getUpperLimit();

        // Zip result type can only be selected for FME scripts
        if (!XQScript.SCRIPT_LANG_FME.equals(scriptType) && XQScript.SCRIPT_RESULTTYPE_ZIP.equals(resultType)) {
            errors.rejectValue("scriptType", "label.qascript.zip.validation");
        }

        // async fme scripts have zip as result type
        if (XQScript.SCRIPT_LANG_FME.equals(scriptType) && form.isAsynchronousExecution() && !XQScript.SCRIPT_RESULTTYPE_ZIP.equals(resultType)) {
            errors.rejectValue("resultType", "label.qascript.async.fme.zip.validation");
        }

        if (!XQScript.SCRIPT_LANG_XQUERY3.equals(scriptType) && !XQScript.SCRIPT_LANG_XQUERY1.equals(scriptType)
                && !XQScript.SCRIPT_LANG_FME.equals(scriptType) && !XQScript.SCRIPT_LANG_XGAWK.equals(scriptType)
                && !XQScript.SCRIPT_LANG_XSL.equals(scriptType)) {
            errors.rejectValue("scriptType", "label.qascript.error.wrongtype");
        }

        // upper limit between 0 and 10Gb
        if (upperLimit == null || !Utils.isNum(upperLimit) || Integer.parseInt(upperLimit) <= 0
                || Integer.parseInt(upperLimit) > 10000) {
            errors.rejectValue("upperLimit", "label.qascript.upperlimit.validation");
        }
    }
}
