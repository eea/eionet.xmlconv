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

package eionet.gdem.web.struts.qasandbox;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.dto.Schema;

/**
 * @author Enriko Käsper, Tieto Estonia
 * SandboxForm
 */

public class QASandboxForm extends ActionForm {
	private String scriptId;
	private String schemaId;
	private String schemaUrl;
	private String sourceUrl;
	private String scriptContent;
	private String scriptType;
	private Schema schema;
	private boolean showScripts;
	private String result;
	
	public void resetAll(ActionMapping actionMapping,
			HttpServletRequest httpServletRequest) {
		super.reset(actionMapping, httpServletRequest);
		scriptId=null;
		schemaId=null;
		sourceUrl=null;
		scriptContent=null;
		scriptType=null;
		schema=new Schema();
		schemaUrl=null;
		showScripts=false;
		result=null;
	}



	public String getResult() {
		return result;
	}



	public void setResult(String result) {
		this.result = result;
	}



	public String getScriptId() {
		return scriptId;
	}



	public void setScriptId(String scriptId) {
		this.scriptId = scriptId;
	}



	public String getSchemaId() {
		return schemaId;
	}



	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}



	public String getSourceUrl() {
		return sourceUrl;
	}



	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}



	public String getScriptContent() {
		return scriptContent;
	}



	public void setScriptContent(String scriptContent) {
		this.scriptContent = scriptContent;
	}



	public String getScriptType() {
		return scriptType;
	}



	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}



	public Schema getSchema() {
		return schema;
	}



	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public String getSchemaUrl() {
		return schemaUrl;
	}



	public void setSchemaUrl(String schemaUrl) {
		this.schemaUrl = schemaUrl;
	}
	public boolean isShowScripts() {
		return showScripts;
	}



	public void setShowScripts(boolean showScripts) {
		this.showScripts = showScripts;
	}
	public boolean isScriptsPresent() {
		
		return showScripts && 
			schema!=null && (schema.isDoValidation()
					|| (schema.getQascripts()!=null && schema.getQascripts().size()>0 ));
	}
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request){
		ActionErrors errors = super.validate(mapping, request);
		
		return errors;
	}
	
}
