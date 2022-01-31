package eionet.gdem.jpa.enums;

public enum ScriptRuleType {

    INCLUDES("includes"),
    GREATER_THAN("greater than"),
    SMALLER_THAN("smaller than");

    private String value;

    ScriptRuleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
