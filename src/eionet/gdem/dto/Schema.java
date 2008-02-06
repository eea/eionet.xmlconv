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
import java.util.Date;
import java.util.List;

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
	private Date datasetReleased;
	

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
		return isDTD;
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
}
