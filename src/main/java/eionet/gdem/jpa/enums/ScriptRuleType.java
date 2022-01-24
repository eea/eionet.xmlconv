package eionet.gdem.jpa.enums;

public enum ScriptRuleType {

    MATCH_EXACTLY("match exactly"),
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
