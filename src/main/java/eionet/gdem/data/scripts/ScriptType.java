package eionet.gdem.data.scripts;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public enum ScriptType {

    EMPTY, XQUERY, FME;

    public String getText() {
        switch (this) {
            case EMPTY:
                return "";
            case XQUERY:
                return "XQuery";
            case FME:
                return "FME";
            default:
                throw new IllegalArgumentException("Unknown" + this);
        }
    }

    public static Map<ScriptType, String> getMap() {
        Map<ScriptType, String> map = new LinkedHashMap<>();
        map.put(XQUERY, XQUERY.getText());
        map.put(FME, FME.getText());
        return map;
    }
}
