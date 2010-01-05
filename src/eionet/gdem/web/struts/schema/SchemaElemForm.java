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

import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.ValidatorForm;

import eionet.gdem.dto.Schema;
import eionet.gdem.utils.Utils;

public class SchemaElemForm extends ValidatorForm {

	private String schema;
	private String schemaId;
	private String description;
	private String elemName;
	private String namespace;
	private String dtdId;
	private boolean doValidation = false;
	private String schemaLang;
	private boolean dtd = false;
	private String expireDate;
	private Date expireDateObj;

	// uploaded schema file
	private String uplSchemaFileName;

	private String uplSchemaFileUrl;

	private FormFile schemaFile;

	private String lastModified;

	private String uplSchemaId;
	public String getDefaultSchemaLang() {
		return Schema.getDefaultSchemaLang();
	}
	public String getDescription() {
		return description;
	}
	public String getDtdId() {
		return dtdId;
	}

	public String getElemName() {
		return elemName;
	}

	public String getExpireDate() {
		return expireDate;
	}
	public String getLongExpireDate() {
		if(expireDateObj==null) return "";
		
		return Utils.getDate(expireDateObj);
	}

	public Date getExpireDateObj() {
		return expireDateObj;
	}

	public String getLastModified() {
		return lastModified;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getSchema() {
		return schema;
	}

	public FormFile getSchemaFile() {
		return schemaFile;
	}

	public String getSchemaId() {
		return schemaId;
	}

	public String getSchemaLang() {
		return schemaLang;
	}

	public String[] getSchemaLanguages() {
		return Schema.getSchemaLanguages();
	}

	public String getUplSchemaFileName() {
		return uplSchemaFileName;
	}

	public String getUplSchemaFileUrl() {
		return uplSchemaFileUrl;
	}

	public String getUplSchemaId() {
		return uplSchemaId;
	}

	public boolean isDoValidation() {
		return doValidation;
	}

	public boolean isDtd() {
		return dtd;
	}

	public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		schema = null;
		description = null;
		description = null;
		namespace = null;
		schemaLang = Schema.getDefaultSchemaLang();
		doValidation = false;
		dtd = false;
		uplSchemaFileName = null;
		uplSchemaFileUrl = null;
		schemaFile = null;
		lastModified = null;
		expireDate=null;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDoValidation(boolean doValidation) {
		this.doValidation = doValidation;
	}

	public void setDtd(boolean dtd) {
		this.dtd = dtd;
	}

	public void setDtdId(String dtdId) {
		this.dtdId = dtdId;
	}

	public void setElemName(String elemName) {
		this.elemName = elemName;
	}

	public void setExpireDateObj(Date expireDate) {
		this.expireDateObj = expireDate;
		this.expireDate = Utils.getFormat(expireDate, "dd/MM/yyyy");
	}

	public void setExpireDate(String strExpireDate) throws ParseException {
		this.expireDate = strExpireDate;
		if(Utils.isNullStr(strExpireDate))
			expireDateObj=null;
		else
			try{
				this.expireDateObj = Utils.parseDate(strExpireDate,"dd/MM/yyyy");
			}
			catch(Exception e){
				//invalid date, validator should catch this
			}
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setSchemaFile(FormFile schemaFile) {
		this.schemaFile = schemaFile;
	}

	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}

	public void setSchemaLang(String schemaLang) {
		this.schemaLang = schemaLang;
	}

	public void setUplSchemaFileName(String uplSchemaFileName) {
		this.uplSchemaFileName = uplSchemaFileName;
	}

	public void setUplSchemaFileUrl(String uplSchemaFileUrl) {
		this.uplSchemaFileUrl = uplSchemaFileUrl;
	}

	public void setUplSchemaId(String uplSchemaId) {
		this.uplSchemaId = uplSchemaId;
	}
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request){
		return super.validate(mapping, request);
	}
}
