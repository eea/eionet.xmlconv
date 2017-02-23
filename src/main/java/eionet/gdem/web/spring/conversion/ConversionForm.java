package eionet.gdem.web.spring.conversion;

import eionet.gdem.dto.Schema;

import java.util.List;

/**
 *
 *
 */
public class ConversionForm {

    private String schemaUrl;
    private String url;
    private String insertedUrl;
    private String conversionId;
    private Schema schema;
    private List<Schema> schemas;
    private boolean showSchemaSelection = false;
    private boolean converted = true;
    private String searchAction;
    private String convertAction;
    private String action;
    /*private String errorForward = DEFAULT_ERROR_FORWARD;*/

    public String getSchemaUrl() {
        return this.schemaUrl;
    }

    public void setSchemaUrl(String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInsertedUrl() {
        return this.insertedUrl;
    }

    public void setInsertedUrl(String insertedUrl) {
        this.insertedUrl = insertedUrl;
    }

    public String getConversionId() {
        return this.conversionId;
    }

    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }

    public Schema getSchema() {
        return this.schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public List<Schema> getSchemas() {
        return this.schemas;
    }

    public void setSchemas(List<Schema> schemas) {
        this.schemas = schemas;
    }

    public boolean isShowSchemaSelection() {
        return this.showSchemaSelection;
    }

    public void setShowSchemaSelection(boolean showSchemaSelection) {
        this.showSchemaSelection = showSchemaSelection;
    }

    public boolean isConverted() {
        return this.converted;
    }

    public void setConverted(boolean converted) {
        this.converted = converted;
    }

    public String getSearchAction() {
        return this.searchAction;
    }

    public void setSearchAction(String searchAction) {
        this.searchAction = searchAction;
    }

    public String getConvertAction() {
        return this.convertAction;
    }

    public void setConvertAction(String convertAction) {
        this.convertAction = convertAction;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
