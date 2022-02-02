package eionet.gdem.jpa.enums;

public enum ScriptRuleField {

    COLLECTION_PATH("collection path"),
    XML_FILE_SIZE("xml file size (in MB)");

    private String value;

    ScriptRuleField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
