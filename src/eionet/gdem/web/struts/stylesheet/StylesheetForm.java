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

package eionet.gdem.web.struts.stylesheet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

public class StylesheetForm extends ActionForm {

	private String schema;
	private String outputtype;
	private String description;
	private FormFile xslfile;
	private String schemaId;
	private String xsl;
	private String stylesheetId;
	private String xslContent;
	private String xslFileName;
	private String modified;
	private String checksum;
	private String dependsOn;

	public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		return null;
	}


	public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		schema = null;
		outputtype = null;
		description = null;
		xslfile = null;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getOutputtype() {
		return outputtype;
	}


	public void setOutputtype(String outputtype) {
		this.outputtype = outputtype;
	}


	public String getSchema() {
		return schema;
	}


	public void setSchema(String schema) {
		this.schema = schema;
	}


	public FormFile getXslfile() {
		return xslfile;
	}


	public void setXslfile(FormFile xslfile) {
		this.xslfile = xslfile;
	}


	public String getSchemaId() {
		return schemaId;
	}


	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}


	public String getXsl() {
		return xsl;
	}


	public void setXsl(String xsl) {
		this.xsl = xsl;
	}


	public String getStylesheetId() {
		return stylesheetId;
	}


	public void setStylesheetId(String stylesheetId) {
		this.stylesheetId = stylesheetId;
	}


	public String getXslContent() {
		return xslContent;
	}


	public void setXslContent(String xslContent) {
		this.xslContent = xslContent;
	}


	public String getXslFileName() {
		return xslFileName;
	}


	public void setXslFileName(String xslFileName) {
		this.xslFileName = xslFileName;
	}


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

	/**
	 * @return the dependsOn
	 */
	public String getDependsOn() {
		return dependsOn;
	}


	/**
	 * @param dependsOn the dependsOn to set
	 */
	public void setDependsOn(String dependsOn) {
		this.dependsOn = dependsOn;
	}

}
