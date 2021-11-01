package eionet.gdem.web.spring.scripts;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 *
 *
 */
public class QAScriptSyncForm {
    private static final long serialVersionUID = 1L;

    private String scriptId;
    private String url;
    private String scriptFile;
    private String fileName;
    private String description;
    private String shortName;
    private String filePath;
    private String schemaId;
    private String resultType;
    private String scriptType;
    private String modified;
    private String checksum;
    private String scriptContent;
    private String schema;
    private String upperLimit;
    private boolean active;
    private boolean asynchronousExecution;
    private boolean markedHeavy;
    private String markedHeavyReason;
    private String markedHeavyReasonOther;

    /*@Override
    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        return null;
    }*/

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String id) {
        this.scriptId = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(String scriptFile) {
        this.scriptFile = scriptFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAsynchronousExecution() {
        return asynchronousExecution;
    }

    public void setAsynchronousExecution(boolean asynchronousExecution) {
        this.asynchronousExecution = asynchronousExecution;
    }


    public boolean isMarkedHeavy() {
        return markedHeavy;
    }

    public void setMarkedHeavy(boolean markedHeavy) {
        this.markedHeavy = markedHeavy;
    }

    public String getMarkedHeavyReason() {
        return markedHeavyReason;
    }

    public void setMarkedHeavyReason(String markedHeavyReason) {
        this.markedHeavyReason = markedHeavyReason;
    }

    public String getMarkedHeavyReasonOther() {
        return markedHeavyReasonOther;
    }

    public void setMarkedHeavyReasonOther(String markedHeavyReasonOther) {
        this.markedHeavyReasonOther = markedHeavyReasonOther;
    }

/*    @Override
    public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        scriptId = null;
        url = null;
        scriptFile = null;
        fileName = null;
    }*/
}
