package eionet.gdem.web.spring.xmlfile;

import eionet.gdem.web.spring.FileUploadWrapper;

/**
 *
 */
public class UplXmlFileForm {

    private FileUploadWrapper xmlfile;
    private String title;
    private String lastModified;

    public FileUploadWrapper getXmlfile() {
        return xmlfile;
    }

    public void setXmlfile(FileUploadWrapper xmlfile) {
        this.xmlfile = xmlfile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
}
