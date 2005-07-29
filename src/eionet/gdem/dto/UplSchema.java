package eionet.gdem.dto;

import java.io.Serializable;
import java.util.List;

public class UplSchema implements Serializable{

	private String id;
	private String schema;
	
    public UplSchema() {
        
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
	


}
