package eionet.gdem.dto;
import java.io.Serializable;
import java.util.List;


public class Stylesheet implements Serializable {

	private String xsl;
	private String type;
	private String xsl_descr;
    private String convId;
	private String modified;
	private boolean ddConv;
	private String schema;
	
	public String getConvId() {
		return convId;
	}
	
	public void setConvId(String convId) {
		this.convId = convId;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getXsl() {
		return xsl;
	}
	
	public void setXsl(String xsl) {
		this.xsl = xsl;
	}
	
	public String getXsl_descr() {
		return xsl_descr;
	}
	
	public void setXsl_descr(String xsl_descr) {
		this.xsl_descr = xsl_descr;
	}

	public String getModified() {
		return modified;
	}
	

	public void setModified(String modified) {
		this.modified = modified;
	}

	public boolean isDdConv() {
		return ddConv;
	}
	

	public void setDdConv(boolean ddConv) {
		this.ddConv = ddConv;
	}

	public String getSchema() {
		return schema;
	}
	

	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	
	
	
	
	
	
}
