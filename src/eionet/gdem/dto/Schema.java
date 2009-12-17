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

package eionet.gdem.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import eionet.gdem.Properties;

public class Schema implements Serializable {

	private String id;
	private String schema;
	private String description;
	private List stylesheets;
	boolean isDTD = false;
	private String dtdPublicId;
	private String table;
	private String dataset;
	private List cdrfiles;
	private List crfiles;
	private Date datasetReleased;
	private boolean doValidation=false;
	private String schemaLang;
	private String uplSchemaFileName;
	private List<QAScript> qascripts;
	private UplSchema uplSchema;
	private Date expireDate;
	
	public Date getExpireDate() {
		return expireDate;
	}


	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}


	public UplSchema getUplSchema() {
		return uplSchema;
	}


	public void setUplSchema(UplSchema uplSchema) {
		this.uplSchema = uplSchema;
	}


	public List<QAScript> getQascripts() {
		return qascripts;
	}


	public void setQascripts(List<QAScript> qascripts) {
		this.qascripts = qascripts;
	}


	private static String[] schemaLanguages = { "XSD", "DTD", "EXCEL" };
	private static String defaultSchemaLang = "XSD";
	
	public Schema() {

	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getSchema() {
		return schema;
	}


	public void setSchema(String schema) {
		this.schema = schema;
	}


	public List getStylesheets() {
		return stylesheets;
	}


	public void setStylesheets(List stylesheets) {
		this.stylesheets = stylesheets;
	}


	public boolean getIsDTD() {
		return getSchemaLang().equals("DTD");
	}


	public void setIsDTD(boolean isDTD) {
		this.isDTD = isDTD;
	}


	public String getDtdPublicId() {
		return dtdPublicId;
	}


	public void setDtdPublicId(String dtdPublicId) {
		this.dtdPublicId = dtdPublicId;
	}


	public String getDataset() {
		return dataset;
	}


	public void setDataset(String dataset) {
		this.dataset = dataset;
	}


	public String getTable() {
		return table;
	}


	public void setTable(String table) {
		this.table = table;
	}

	public List getCdrfiles() {
		return cdrfiles;
	}


	public void setCdrfiles(List cdrfiles) {
		this.cdrfiles = cdrfiles;
	}


	public Date getDatasetReleased() {
		return datasetReleased;
	}


	public void setDatasetReleased(Date datasetReleased) {
		this.datasetReleased = datasetReleased;
	}


	public List getCrfiles() {
		return crfiles;
	}


	public void setCrfiles(List crfiles) {
		this.crfiles = crfiles;
	}
	public boolean equals(Object oSchema){
		if(oSchema instanceof Schema){
			if (oSchema!=null && ((Schema)oSchema).getSchema()!=null && getSchema()!=null){
				return ((Schema)oSchema).getSchema().equals(getSchema());
			}
		}
		return false;
	}
	public String getLabel(){
		StringBuilder label = new StringBuilder(schema);
		if(id!=null && isDDSchema() && getTable()!=null){
			label.append(" - ");
			label.append(getTable());
			label.append(" (");
			label.append(getDataset());
			if(getDatasetReleased()!=null){
				label.append(" - ");
				SimpleDateFormat formatter = new SimpleDateFormat(Properties.dateFormatPattern);
				String strDate = formatter.format(getDatasetReleased());
				label.append(strDate);			
			}
			label.append(")");
		}
		return label.toString();
	}
	public boolean isDDSchema(){
		boolean ret = false;
		
		if(id!=null)
			ret = id.startsWith("TBL");
		
		return ret;
	}
	public static String[] getSchemaLanguages(){
		return schemaLanguages;
	}
	public static String getDefaultSchemaLang(){
		return defaultSchemaLang;
	}


	public boolean isDoValidation() {
		return doValidation;
	}


	public void setDoValidation(boolean doValidation) {
		this.doValidation = doValidation;
	}


	public String getSchemaLang() {
		if(schemaLang==null) schemaLang=getDefaultSchemaLang();
		return schemaLang;
	}


	public void setSchemaLang(String schemaLang) {
		this.schemaLang = schemaLang;
	}


	public String getUplSchemaFileName() {
		return uplSchemaFileName;
	}


	public void setUplSchemaFileName(String uplSchemaFile) {
		this.uplSchemaFileName = uplSchemaFile;
	}
}
