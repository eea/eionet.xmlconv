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
 *    Original code: Nenad Popovic (ED)
 */

package eionet.gdem.web.struts.uimanage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;

import eionet.gdem.Properties;
import eionet.gdem.utils.xml.IXQuery;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;
/**
* <p>Implementation of Struts <strong>Action</strong> </p>
* 
* <p>Sets temp property in dynamicItemForm that holds information about each cell</p>
*/

public class ViewTemplateAction extends Action {
// private static final WDSLogger logger = WDSLogger.getLogger(ViewTemplateAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	throws IOException {
		DynaValidatorForm df = (DynaValidatorForm) form;
		HashMap hm = (HashMap) df.get("temp");
		IXmlCtx ctx = new XmlContext();
		//template name is in parameter attrib in action path
		String template=mapping.getParameter();
		try{
		//String file = AppConfigurator.getInstance().getApplicationHome() + File.separatorChar + "xsl" + File.separatorChar + "UITemplate.xml";
		String file = Properties.uiFolder + File.separatorChar + "UITemplate.xml";
			
		ctx.checkFromFile(file);
		IXQuery xq=ctx.getQueryManager();
		int rows = Integer.parseInt(xq.getAttributeValue(template,"rows"));
		int cols = Integer.parseInt(xq.getAttributeValue(template,"columns"));
		// prepare dynamicItemForm property temp
		for (int i = 0; i < rows * cols; i++) {
			hm.put("cell" + Integer.toString(i) + "type", "");
			hm.put("cell" + Integer.toString(i) + "link", "");
			hm.put("cell" + Integer.toString(i) + "content", "");
			hm.put("cell" + Integer.toString(i) + "font", "");
			hm.put("cell" + Integer.toString(i) + "fontsize", "");
			hm.put("cell" + Integer.toString(i) + "color", "");
			hm.put("cell" + Integer.toString(i) + "position", "");
			hm.put("cell" + Integer.toString(i) + "fontstyle", "");
			hm.put("cell" + Integer.toString(i) + "vertical", "");
			}
		} catch (Exception e) {
			//logger.error(e.getMessage());
		}

		return mapping.findForward(template);
		}
}
