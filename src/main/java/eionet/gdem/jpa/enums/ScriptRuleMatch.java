package eionet.gdem.jpa.enums;

public enum ScriptRuleMatch {

    ALL("all"),
    AT_LEAST_ONE("atLeastOne");

    private String value;

    ScriptRuleMatch(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
