package eionet.gdem.web.spring.converter;

/**
 *
 *
 */
public class Excel2xmlForm {

    private String url;
    private String split;
    private String sheet;
    private boolean conversionLog;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public String getSheet() {
        return sheet;
    }

    public void setSheet(String sheet) {
        this.sheet = sheet;
    }

    public boolean isConversionLog() {
        return conversionLog;
    }

    public void setConversionLog(boolean conversionLog) {
        this.conversionLog = conversionLog;
    }
}
