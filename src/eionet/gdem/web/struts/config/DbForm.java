package eionet.gdem.web.struts.config;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class DbForm extends ActionForm{

	private String dbUrl;
	private String user;
	private String password;

	
	 public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		  ActionErrors errors = new ActionErrors();
	        if (dbUrl.equals("")) {
	            errors.add("displayType", new ActionError("label.config.db.url.validation"));
	        } else
	            return super.validate(actionMapping, httpServletRequest);
	        return errors;	        
		  
		  }
	  public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		  dbUrl=null;
		  user=null;
		  password=null;
		  }
	public String getDbUrl() {
		return dbUrl;
	}
	
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	

		
	
}
