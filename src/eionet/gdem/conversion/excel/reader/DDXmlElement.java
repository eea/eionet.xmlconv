/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is XMLCONV.
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 * 
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.conversion.excel.reader;

/**
 * @author Enriko Käsper, Tieto Estonia
 * DDXmlElement
 */

public class DDXmlElement {

    private String name;
    private String localName;
    private String attributes;
    private boolean hasMultipleValues=false;
    private String delimiter;
    private int colIndex=-1;
    private boolean isMainTable=false;
    
	public DDXmlElement(String name, String localName, String attributes) {
		super();
		this.name = name;
		this.localName = localName;
		this.attributes = attributes;
	}
	
	public boolean isHasMultipleValues() {
		return hasMultipleValues;
	}
	public void setHasMultipleValues(boolean hasMultipleValues) {
		this.hasMultipleValues = hasMultipleValues;
	}
	public String getDelimiter() {
		return delimiter;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	public String getName() {
		return name;
	}
	public String getLocalName() {
		return localName;
	}
	public String getAttributes() {
		return attributes;
	}

	public int getColIndex() {
		return colIndex;
	}

	public void setColIndex(int colIndex) {
		this.colIndex = colIndex;
	}

	public boolean isMainTable() {
		return isMainTable;
	}

	public void setMainTable(boolean isMainTable) {
		this.isMainTable = isMainTable;
	}
	
    
}