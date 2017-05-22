package eionet.gdem.web.spring.validation;

/**
 *
 *
 */
public class ValidationForm {

    private String XmlUrl;
    private String schemaUrl;

    public String getXmlUrl() {
        return this.XmlUrl;
    }

    public void setXmlUrl(String xmlUrl) {
        this.XmlUrl = xmlUrl;
    }

    public String getSchemaUrl() {
        return this.schemaUrl;
    }

    public void setSchemaUrl(String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }
}
