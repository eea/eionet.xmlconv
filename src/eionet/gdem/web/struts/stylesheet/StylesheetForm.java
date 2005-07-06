package eionet.gdem.web.struts.stylesheet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

public class StylesheetForm extends ActionForm{

	private String schema;
	private String outputtype;
	private String description;
	private FormFile xslfile;
	private String schemaId;
	private String xsl;
	private String stylesheetId;
	
	  public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		    return null;
		  }
	  public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
			schema=null;
			outputtype=null;
			description=null;
			xslfile=null;			
		  }
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getOutputtype() {
		return outputtype;
	}
	
	public void setOutputtype(String outputtype) {
		this.outputtype = outputtype;
	}
	
	public String getSchema() {
		return schema;
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public FormFile getXslfile() {
		return xslfile;
	}
	
	public void setXslfile(FormFile xslfile) {
		this.xslfile = xslfile;
	}
	public String getSchemaId() {
		return schemaId;
	}
	
	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}
	public String getXsl() {
		return xsl;
	}
	
	public void setXsl(String xsl) {
		this.xsl = xsl;
	}
	public String getStylesheetId() {
		return stylesheetId;
	}
	
	public void setStylesheetId(String stylesheetId) {
		this.stylesheetId = stylesheetId;
	}
	
	
	
}
