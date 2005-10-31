package eionet.gdem.dto;

import java.io.Serializable;

public class ValidateDto implements Serializable {

	private String type;
	private int line;
	private int column;
	private String description;


	public ValidateDto() {

	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public void setColumn(int column) {
		this.column = column;
	}


	public void setLine(int line) {
		this.line = line;
	}


	public int getColumn() {
		return column;
	}


	public int getLine() {
		return line;
	}

}
