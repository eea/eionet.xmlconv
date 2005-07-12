package eionet.gdem.web.struts.conversion;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

public class TestConvForm extends ActionForm{

	private String url;
	private String xmlSchema;
	private String stylesheetId;
	
	
	  public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		    return null;
		  }
	  public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		  url=null;
		  xmlSchema=null;
		  stylesheetId=null;		
		  }
	
	public String getXmlSchema() {
		return xmlSchema;
	}
	
	public void setXmlSchema(String xmlSchema) {
		this.xmlSchema = xmlSchema;
	}
	

	public String getStylesheetId() {
		return stylesheetId;
	}
	
	public void setStylesheetId(String stylesheetId) {
		this.stylesheetId = stylesheetId;
	}
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
	
	
}
