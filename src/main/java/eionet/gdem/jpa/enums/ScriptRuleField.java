package eionet.gdem.jpa.enums;

public enum ScriptRuleField {

    SCRIPT_URL("script url"),
    SCHEMA_URL("schema url"),
    XML_FILE_URL("xml file"),
    XML_FILE_SIZE("xml file size");

    private String value;

    ScriptRuleField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
