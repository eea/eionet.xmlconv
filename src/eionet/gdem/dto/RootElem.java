package eionet.gdem.dto;

import java.io.Serializable;

public class RootElem implements Serializable {

	private String elemId;
	private String namespace;
	private String name;


	public RootElem() {

	}


	public String getElemId() {
		return elemId;
	}


	public void setElemId(String elemId) {
		this.elemId = elemId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getNamespace() {
		return namespace;
	}


	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

}
