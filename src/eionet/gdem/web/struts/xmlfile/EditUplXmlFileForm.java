/*
 * Created on 20.11.2007
 */
package eionet.gdem.web.struts.xmlfile;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * ActionForm for editing XML file metadata  
 * 
 * @author Enriko Käsper (TietoEnator)
 *
 */

public class EditUplXmlFileForm extends ActionForm {

	private String xmlfile;
	private String xmlfileId;
	private String title;


	public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		return null;
	}


	public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		xmlfile = null;
		xmlfileId = null;
		title = null;
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


	public String getXmlfile() {
		return xmlfile;
	}


	public void setXmlfile(String xmlfile) {
		this.xmlfile = xmlfile;
	}

}
