package eionet.gdem.web.struts.schema;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class SchemaElemForm extends ActionForm{

	private String schema;
	private String schemaId;
	private String description;
	private String elemName;
	private String namespace;
	private String dtdId;
	private String backToConv;
	
	
	  public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		    return null;
		  }
	  public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
			schema=null;
			description=null;
			description=null;
			namespace=null;			
		  }
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getElemName() {
		return elemName;
	}
	
	public void setElemName(String elemName) {
		this.elemName = elemName;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public String getSchema() {
		return schema;
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public String getSchemaId() {
		return schemaId;
	}
	
	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}
	public String getDtdId() {
		return dtdId;
	}
	
	public void setDtdId(String dtdId) {
		this.dtdId = dtdId;
	}
	public String getBackToConv() {
		return backToConv;
	}
	
	public void setBackToConv(String backToConv) {
		this.backToConv = backToConv;
	}
	
	
	
	
	
	
	
}
