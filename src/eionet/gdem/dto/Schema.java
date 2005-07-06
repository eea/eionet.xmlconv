package eionet.gdem.dto;
import java.io.Serializable;
import java.util.List;

public class Schema implements Serializable {

	private String id;
	private String schema;
	private String description;
    private List stylesheets;
	boolean isDTD = false;
	private String dtdPublicId; 
	
    public Schema() {
        
    }

	public String getDescription() {
		return description;
	}
	

	public void setDescription(String description) {
		this.description = description;
	}
	

	public String getId() {
		return id;
	}
	

	public void setId(String id) {
		this.id = id;
	}
	

	public String getSchema() {
		return schema;
	}
	

	public void setSchema(String schema) {
		this.schema = schema;
	}
	

	public List getStylesheets() {
		return stylesheets;
	}
	

	public void setStylesheets(List stylesheets) {
		this.stylesheets = stylesheets;
	}

	public boolean getIsDTD() {
		return isDTD;
	}
	

	public void setIsDTD(boolean isDTD) {
		this.isDTD = isDTD;
	}

	public String getDtdPublicId() {
		return dtdPublicId;
	}
	

	public void setDtdPublicId(String dtdPublicId) {
		this.dtdPublicId = dtdPublicId;
	}
	
	
	
	
}
