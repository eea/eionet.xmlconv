package eionet.gdem.web.struts.schema;
import java.util.List;

import eionet.gdem.dto.Schema;

public class SchemaElemHolder {

	private Schema schema;
	private List rootElem;
	private boolean xsduPrm ;
	
	

	public SchemaElemHolder() {
	}



	public List getRootElem() {
		return rootElem;
	}
	



	public void setRootElem(List rootElem) {
		this.rootElem = rootElem;
	}
	



	public Schema getSchema() {
		return schema;
	}
	



	public void setSchema(Schema schema) {
		this.schema = schema;
	}
	



	public boolean isXsduPrm() {
		return xsduPrm;
	}
	



	public void setXsduPrm(boolean xsduPrm) {
		this.xsduPrm = xsduPrm;
	}
	
	

}
