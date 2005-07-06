package eionet.gdem.dto;

import java.io.Serializable;
import java.util.List;

public class ConvType implements Serializable{

	private String convType;
	private String contType;
	private String fileExt;
    private String description;
	
    public ConvType() {
        
    }

	public String getContType() {
		return contType;
	}
	

	public void setContType(String contType) {
		this.contType = contType;
	}
	

	public String getConvType() {
		return convType;
	}
	

	public void setConvType(String convType) {
		this.convType = convType;
	}
	

	public String getDescription() {
		return description;
	}
	

	public void setDescription(String description) {
		this.description = description;
	}
	

	public String getFileExt() {
		return fileExt;
	}
	

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}
		

}
