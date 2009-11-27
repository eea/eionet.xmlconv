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
 * Contributor(s):* Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.dto;

/**
 * @author Enriko Käsper, Tieto Estonia
 * QAScript
 */

public class QAScript{

	String scriptId;
    String description;
    String shortName;
    String fileName;
    String schemaId;
    String schema;
	String resultType;
    String scriptType;
	String modified;
	String checksum;
	String scriptContent;
	
    public String getScriptContent() {
		return scriptContent;
	}
	public void setScriptContent(String scriptContent) {
		this.scriptContent = scriptContent;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	String filePath;
    
    public String getModified() {
		return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	public QAScript() {
		super();
	}
	public String getScriptId() {
		return scriptId;
	}
	public void setScriptId(String queryId) {
		this.scriptId = queryId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSchemaId() {
		return schemaId;
	}
	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getScriptType() {
		return scriptType;
	}
	public void setScriptType(String queryType) {
		this.scriptType = queryType;
	}
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
}
