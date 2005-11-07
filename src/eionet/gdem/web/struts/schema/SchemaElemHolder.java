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

import java.util.List;

import eionet.gdem.dto.Schema;

public class SchemaElemHolder {

	private Schema schema;
	private List rootElem;
	private boolean xsduPrm;


	public SchemaElemHolder() {
	}


	public List getRootElem() {
		return rootElem;
	}


	public void setRootElem(List rootElem) {
		this.rootElem = rootElem;
	}


	public Schema getSchema() {
		return schema;
	}


	public void setSchema(Schema schema) {
		this.schema = schema;
	}


	public boolean isXsduPrm() {
		return xsduPrm;
	}


	public void setXsduPrm(boolean xsduPrm) {
		this.xsduPrm = xsduPrm;
	}

}
