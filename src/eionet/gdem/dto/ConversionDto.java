package eionet.gdem.dto;

import java.io.Serializable;


public class ConversionDto implements Serializable {
   private String convId;
   private String description;
   private String resultType;
   private String stylesheet;
   
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public String getConvId() {
		return convId;
	}
	
	public void setConvId(String convId) {
		this.convId = convId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getResultType() {
		return resultType;
	}
	
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	
	public String getStylesheet() {
		return stylesheet;
	}
	
	public void setStylesheet(String stylesheet) {
		this.stylesheet = stylesheet;
	}

	public String toString(){
		return "convId=" + convId  + " description=" + description+ " resultType="+ resultType +" stylesheet=" + stylesheet;
	}
	
}
