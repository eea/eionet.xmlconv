/*
 * Created on 20.11.2007
 */
package eionet.gdem.web.spring.xmlfile;

import eionet.gdem.web.spring.FileUploadWrapper;

/**
 * XML File form.
 *
 */

public class XmlFileForm {

    private String xmlFileName;
    private String xmlFilePath;
    private String xmlfileId;
    private String title;
    private String lastModified;
    private FileUploadWrapper xmlFile;

    public XmlFileForm() {

    }

    public XmlFileForm(String xmlFileName, String xmlFilePath, String xmlfileId, String title, String lastModified, FileUploadWrapper xmlFile) {
        this.xmlFileName = xmlFileName;
        this.xmlFilePath = xmlFilePath;
        this.xmlfileId = xmlfileId;
        this.title = title;
        this.lastModified = lastModified;
        this.xmlFile = xmlFile;
    }

    public FileUploadWrapper getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(FileUploadWrapper xmlFile) {
        this.xmlFile = xmlFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String description) {
        this.title = description;
    }

    public String getXmlfileId() {
        return xmlfileId;
    }

    public void setXmlfileId(String xmlfileId) {
        this.xmlfileId = xmlfileId;
    }

    public String getXmlFileName() {
        return xmlFileName;
    }

    public void setXmlFileName(String xmlFileName) {
        this.xmlFileName = xmlFileName;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public void setXmlFilePath(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    public String getXmlFilePath() {
        return xmlFilePath;
    }

}
