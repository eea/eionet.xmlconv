package eionet.gdem.web.struts.schema;
import java.util.List;

public class UplSchemaHolder {

	private List schemas;
	boolean ssiPrm;
	boolean ssdPrm;

	public UplSchemaHolder() {
	}

	public List getSchemas() {
		return schemas;
	}
	

	public void setSchemas(List schemas) {
		this.schemas = schemas;
	}
	

	public boolean isSsdPrm() {
		return ssdPrm;
	}
	

	public void setSsdPrm(boolean ssdPrm) {
		this.ssdPrm = ssdPrm;
	}
	

	public boolean isSsiPrm() {
		return ssiPrm;
	}
	

	public void setSsiPrm(boolean ssiPrm) {
		this.ssiPrm = ssiPrm;
	}
	

}
