package eionet.gdem.web.struts.schema;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

public class UplSchemaForm extends ActionForm{

	private FormFile schema;
	
	
	  public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		    return null;
		  }
	  public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
			schema=null;
		  }
	public FormFile getSchema() {
		return schema;
	}
	
	public void setSchema(FormFile schema) {
		this.schema = schema;
	}
	
	
}
