package eionet.gdem.web.struts.config;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class LdapForm extends ActionForm{

	private String url;

	
	 public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		  ActionErrors errors = new ActionErrors();
	        if (url.equals("")) {
	            errors.add("displayType", new ActionError("label.config.ldap.url.validation"));
	        } else
	            return super.validate(actionMapping, httpServletRequest);
	        return errors;	        
		  
		  }
	  public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		  url=null;
		  }
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
		
	
}
