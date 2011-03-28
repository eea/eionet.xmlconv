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
 * ActionForm for viewing the list of
 *
 * @author Enriko Käsper (TietoEnator)
 *
 */
public class UplXmlFileForm extends ActionForm {

    private FormFile xmlfile;
    private String title;
    private String lastModified;


    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        return null;
    }


    public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        xmlfile = null;
        title = null;
    }


    public FormFile getXmlfile() {
        return xmlfile;
    }


    public void setXmlfile(FormFile xmlfile) {
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
