package eionet.gdem.web.spring.qasandbox;

import eionet.gdem.dto.Schema;

public class QASandboxForm {
    private String scriptId;
    private String schemaId;
    private String schemaUrl;
    private String sourceUrl;
    private String scriptContent;
    private String scriptType;
    private Schema schema;
    private boolean showScripts;
    private String result;
    private String action;

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /*   public void resetAll(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
            super.reset(actionMapping, httpServletRequest);
            scriptId = null;
            schemaId = null;
            sourceUrl = null;
            scriptContent = null;
            scriptType = null;
            schema = new Schema();
            schemaUrl = null;
            showScripts = false;
            result = null;
        }*/
    public QASandboxForm() {
        this.schema = new Schema();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public String getSchemaUrl() {
        return schemaUrl;
    }

    public void setSchemaUrl(String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }

    public boolean isShowScripts() {
        return showScripts;
    }

    public void setShowScripts(boolean showScripts) {
        this.showScripts = showScripts;
    }

    public boolean isScriptsPresent() {

        return showScripts && schema != null
                && (schema.isDoValidation() || (schema.getQascripts() != null && schema.getQascripts().size() > 0));
    }

}
