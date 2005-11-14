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

package eionet.gdem.web.struts.conversion;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public class ListConvForm extends ActionForm {

	private String xmlUrl;
	private String xmlSchema;
	private String validate;


	public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
			return null;
	}


	public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
		xmlUrl = null;
		xmlSchema = null;
		validate = null;
	}


	public String getValidate() {
		return validate;
	}


	public void setValidate(String validate) {
		this.validate = validate;
	}


	public String getXmlSchema() {
		return xmlSchema;
	}


	public void setXmlSchema(String xmlSchema) {
		this.xmlSchema = xmlSchema;
	}


	public String getXmlUrl() {
		return xmlUrl;
	}


	public void setXmlUrl(String xmlUrl) {
		this.xmlUrl = xmlUrl;
	}

}
