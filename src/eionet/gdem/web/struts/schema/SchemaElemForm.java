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
 * The Original Code is Web Dashboards Service
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 * 
 * Contributors(s):
 *    Original code: Istvan Alfeldi (ED) 
 */

package eionet.gdem.web.struts.schema;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.dto.Schema;

public class SchemaElemForm extends ActionForm{

	private String schema;
	private String schemaId;
	private String description;
	private String elemName;
	private String namespace;
	private String dtdId;
	private String backToConv;
	private boolean doValidation=false;
	private String schemaLang;
	private boolean dtd=false;
	private String uplSchemaFileName;
	private String uplSchemaFileUrl;
	
	
	public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		    return null;
		  }
	  public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
			schema=null;
			description=null;
			description=null;
			namespace=null;			
			schemaLang = Schema.getDefaultSchemaLang();
			doValidation=false;	
			dtd=false;
			uplSchemaFileName=null;
			uplSchemaFileUrl=null;
		  }
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getElemName() {
		return elemName;
	}
	
	public void setElemName(String elemName) {
		this.elemName = elemName;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public String getSchema() {
		return schema;
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public String getSchemaId() {
		return schemaId;
	}
	
	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}
	public String getDtdId() {
		return dtdId;
	}
	
	public void setDtdId(String dtdId) {
		this.dtdId = dtdId;
	}
	public String getBackToConv() {
		return backToConv;
	}
	
	public void setBackToConv(String backToConv) {
		this.backToConv = backToConv;
	}
	public boolean isDoValidation() {
		return doValidation;
	}
	public void setDoValidation(boolean doValidation) {
		this.doValidation = doValidation;
	}
	public String getSchemaLang() {
		return schemaLang;
	}
	public void setSchemaLang(String schemaLang) {
		this.schemaLang = schemaLang;
	}
	public String[] getSchemaLanguages(){
		return Schema.getSchemaLanguages();
	}

	public String getDefaultSchemaLang(){
		return Schema.getDefaultSchemaLang();
	}
	public boolean isDtd() {
		return dtd;
	}
	public void setDtd(boolean dtd) {
		this.dtd = dtd;
	}
	public String getUplSchemaFileName() {
		return uplSchemaFileName;
	}
	public void setUplSchemaFileName(String uplSchemaFileName) {
		this.uplSchemaFileName = uplSchemaFileName;
	}
	public String getUplSchemaFileUrl() {
		return uplSchemaFileUrl;
	}
	public void setUplSchemaFileUrl(String uplSchemaFileUrl) {
		this.uplSchemaFileUrl = uplSchemaFileUrl;
	}
}
