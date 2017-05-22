package eionet.gdem.data.transformations;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public enum TransformationType {

    EMPTY, HTML, XML, EXCEL, RDF, KML;

    public String getText() {
        switch (this) {
            case EMPTY:
                return "";
            case HTML:
                return "HTML";
            case XML:
                return "XML";
            case EXCEL:
                return "EXCEL";
            case RDF:
                return "RDF";
            case KML:
                return "KML";
            default:
                throw new IllegalArgumentException("Unknown" + this);
        }
    }

    public static Map<TransformationType, String> getMap() {
        Map<TransformationType, String> map = new LinkedHashMap<>();
        map.put(HTML, HTML.getText());
        map.put(XML, XML.getText());
        map.put(EXCEL, EXCEL.getText());
        map.put(RDF, RDF.getText());
        map.put(KML, KML.getText());
        return map;
    }

}
