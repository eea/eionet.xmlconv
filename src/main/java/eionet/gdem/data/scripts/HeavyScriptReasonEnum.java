package eionet.gdem.data.scripts;

public enum HeavyScriptReasonEnum {

    LONG_RUNNING("Long running", 1),
    OUT_OF_MEMORY("Out of memory", 2),
    OTHER("Other", 3);

    private String text;
    private Integer code;

    HeavyScriptReasonEnum(String text, Integer code) {
        this.text = text;
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public Integer getCode() {
        return code;
    }

}
