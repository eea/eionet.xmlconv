package eionet.gdem.web.struts.schema;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class EditUplSchemaForm extends ActionForm{

	private String schema;
	private String idSchema;
	private String description;
	
	
	  public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		    return null;
		  }
	  public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
			schema=null;
			idSchema=null;
			description=null;
		  }
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getIdSchema() {
		return idSchema;
	}
	
	public void setIdSchema(String idSchema) {
		this.idSchema = idSchema;
	}
	
	public String getSchema() {
		return schema;
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	
	
}
