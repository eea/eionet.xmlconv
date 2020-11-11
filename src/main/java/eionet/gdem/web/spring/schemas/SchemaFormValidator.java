package eionet.gdem.web.spring.schemas;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 */
public class SchemaFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return SchemaForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        SchemaForm form = (SchemaForm) o;
        String schemaId = form.getSchemaId();
        String schema = form.getSchema();
        String description = form.getDescription();
        String dtdId = form.getDtdId();
        String schemaLang = form.getSchemaLang();
        Long maxExecutionTime = form.getMaxExecutionTime();

        if (schema == null || schema.equals("")) {
            errors.rejectValue("schema","label.schema.validation");
        }

        if (!(new SchemaUrlValidator().isValidUrlSet(schema))) {
            errors.rejectValue("schema", "label.uplSchema.validation.urlFormat");
        }

        if (maxExecutionTime == null) {
            errors.rejectValue("maxExecutionTime", "label.schema.validation.null.maxExecutionTime");
        }

        if (maxExecutionTime!=null && maxExecutionTime == 0) {
            errors.rejectValue("maxExecutionTime", "label.uplSchema.validation.zero.maxExecutionTime");
        }
    }

    public void validateDelete(Object o, Errors errors) {
        SchemaForm form = (SchemaForm) o;
        String schemaId = form.getSchemaId();

        if (StringUtils.isEmpty(schemaId)) {
            errors.rejectValue("schemaId", "label.uplSchema.error.emptyid");
        }
    }

}
