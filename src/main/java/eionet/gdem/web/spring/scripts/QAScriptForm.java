package eionet.gdem.web.spring.scripts;

import eionet.gdem.web.spring.FileUploadWrapper;
import org.springframework.web.multipart.MultipartFile;

public class QAScriptForm {
    private String scriptId;
    private String description;
    private String shortName;
    private String fileName;
    private String filePath;
    private String schemaId;
    private String resultType;
    private String scriptType;
    private String modified;
    private String checksum;
    private String scriptContent;
    private MultipartFile scriptFile;
    private String schema;
    private String upperLimit;
    private String url;
    private boolean active;

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean isActive) {
        this.active = isActive;
    }
    
    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String queryId) {
        this.scriptId = queryId;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public MultipartFile getScriptFile() {
        return this.scriptFile;
    }

    public void setScriptFile(MultipartFile scriptFile) {
        this.scriptFile = scriptFile;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
