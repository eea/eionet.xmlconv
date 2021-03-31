package eionet.gdem.services.fme.request;

public abstract class SubmitJobRequest extends FMEJsonRequest{
    private String xmlSourceFile;


    public SubmitJobRequest(String xmlSourceFile) {
        this.xmlSourceFile = xmlSourceFile;
    }


    public String getXmlSourceFile() {
        return xmlSourceFile;
    }

    public void setXmlSourceFile(String xmlSourceFile) {
        this.xmlSourceFile = xmlSourceFile;
    }
}
