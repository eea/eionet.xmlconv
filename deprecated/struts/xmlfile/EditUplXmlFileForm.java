/*
 * Created on 20.11.2007
 */
package eionet.gdem.web.struts.xmlfile;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 * ActionForm for editing XML file metadata
 * 
 * @author Enriko Käsper (TietoEnator)
 * 
 */

public class EditUplXmlFileForm extends ActionForm {

    private String xmlFileName;
    private String xmlFilePath;
    private String xmlfileId;
    private String title;
    private String lastModified;
    private FormFile xmlFile;

    public FormFile getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(FormFile xmlFile) {
        this.xmlFile = xmlFile;
    }

    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        return null;
    }

    public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        xmlFileName = null;
        xmlFilePath = null;
        xmlfileId = null;
        title = null;
        xmlFile = null;
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
