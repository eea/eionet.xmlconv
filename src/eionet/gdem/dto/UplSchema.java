package eionet.gdem.dto;

import java.io.Serializable;

public class UplSchema implements Serializable {

	private String id;
	private String schema;
	private String description;


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


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}

}
