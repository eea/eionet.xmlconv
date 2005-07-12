package eionet.gdem.web.struts.conversion;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

public class ListConvForm extends ActionForm{

	private String xmlUrl;
	private String xmlSchema;
	private String validate;
	
	
	  public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		    return null;
		  }
	  public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		  xmlUrl=null;
		  xmlSchema=null;
		  validate=null;		
		  }
	public String getValidate() {
		return validate;
	}
	
	public void setValidate(String validate) {
		this.validate = validate;
	}
	
	public String getXmlSchema() {
		return xmlSchema;
	}
	
	public void setXmlSchema(String xmlSchema) {
		this.xmlSchema = xmlSchema;
	}
	
	public String getXmlUrl() {
		return xmlUrl;
	}
	
	public void setXmlUrl(String xmlUrl) {
		this.xmlUrl = xmlUrl;
	}
	
	
	
}
