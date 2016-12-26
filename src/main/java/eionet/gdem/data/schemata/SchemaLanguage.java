package eionet.gdem.data.schemata;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public enum SchemaLanguage {
    EMPTY, XSD, DTD, EXCEL;

    public String getText() {
        switch (this) {
            case EMPTY:
                return "";
            case XSD:
                return "XSD";
            case DTD:
                return "DTD";
            case EXCEL:
                return "EXCEL";
            default:
                throw new IllegalArgumentException("Unknown" + this);
        }
    }

    public static List<String> getList() {
        List<String> list = new ArrayList<>();
        list.add(EMPTY.getText());
        list.add(XSD.getText());
        list.add(DTD.getText());
        list.add(EXCEL.getText());
        return list;
    }
}
