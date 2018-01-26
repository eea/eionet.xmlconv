package eionet.gdem.web.spring.schemas;

import eionet.gdem.dto.Schema;
import org.springframework.web.multipart.MultipartFile;

public class UploadSchemaForm {

    // T_SCHEMA table
    private String schemaId;
    private String description;
    private String schemaUrl;
    private boolean doValidation = false;
    private String schemaLang;
    /** Block file submission if Schema validation fails. The flag used in QA service. */
    private boolean blockerValidation = false;

    // T_UPL_SCHEMA
    private String uplSchemaId;
    private MultipartFile schemaFile;
    private String schemaFileName;

    public MultipartFile getSchemaFile() {
        return schemaFile;
    }

    public void setSchemaFile(MultipartFile schema) {
        this.schemaFile = schema;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSchemaUrl() {
        return schemaUrl;
    }

    public void setSchemaUrl(String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getUplSchemaId() {
        return uplSchemaId;
    }

    public void setUplSchemaId(String uplSchemaId) {
        this.uplSchemaId = uplSchemaId;
    }

    public String getSchemaFileName() {
        return schemaFileName;
    }

    public void setSchemaFileName(String schemaFileName) {
        this.schemaFileName = schemaFileName;
    }

    public boolean isDoValidation() {
        return doValidation;
    }

    public void setDoValidation(boolean doValidation) {
        this.doValidation = doValidation;
    }

    public String getSchemaLang() {
        return schemaLang;
    }

    public void setSchemaLang(String schemaLang) {
        this.schemaLang = schemaLang;
    }

    public String[] getSchemaLanguages() {
        return Schema.getSchemaLanguages();
    }

    public String getDefaultSchemaLang() {
        return Schema.getDefaultSchemaLang();
    }

    /**
     * @return the blockerValidation
     */
    public boolean isBlockerValidation() {
        return blockerValidation;
    }

    /**
     * @param blockerValidation the blockerValidation to set
     */
    public void setBlockerValidation(boolean blockerValidation) {
        this.blockerValidation = blockerValidation;
    }

}
