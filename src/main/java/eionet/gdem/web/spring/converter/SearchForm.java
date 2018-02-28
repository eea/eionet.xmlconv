package eionet.gdem.web.spring.converter;

import eionet.gdem.dto.Schema;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 *
 */
public class SearchForm {
    private Schema schema;
    @NotEmpty(message = "{label.conversion.xmlSchema.notselected}")
    private String schemaUrl;

    public Schema getSchema() {
        return this.schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public String getSchemaUrl() {
        return this.schemaUrl;
    }

    public void setSchemaUrl(String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }
}
