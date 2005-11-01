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
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.DynaValidatorForm;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import eionet.gdem.Properties;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.uimanage.IUIManager;
import eionet.gdem.utils.uimanage.UIManager;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.IXmlSerializer;
import eionet.gdem.utils.xml.XmlContext;
import eionet.gdem.web.tags.UIRendererTag;

/**
 * <p>Implementation of Struts <strong>Action</strong> </p>
 * 
 * <p>Creates footer or header element depending on parameter template</p>
 */

public class ProcessTemplateAction extends Action {
	//private static final WDSLogger logger = WDSLogger.getLogger(ProcessTemplateAction.class);
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, NullPointerException {
		DynaValidatorForm df = (DynaValidatorForm) form;
		HashMap hm = (HashMap) df.get("temp");
		IXmlCtx ctx = new XmlContext();

		ActionMessages errors = new ActionMessages();
		String user = (String) request.getSession().getAttribute("user");

		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_CONFIG_PATH, "u")) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.autorization.config.update"));
				request.getSession().setAttribute("dcm.errors", errors);
				return mapping.findForward("home");
			}
		} catch (Exception e) {
			e.printStackTrace();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.exception.unknown"));
			request.getSession().setAttribute("dcm.errors", errors);
			return mapping.findForward("home");
		}

		// get template name (hidden parameter named template on jsp page)
		String temp = request.getParameter("template");
		try {
			// get UITemplate.xml
			//String file = AppConfigurator.getInstance().getApplicationHome() + File.separatorChar + "xsl" + File.separatorChar + "UITemplate.xml";
			String file = Properties.uiFolder + File.separatorChar + "UITemplate.xml";
			ctx.checkFromFile(file);
			Document doc = ctx.getDocument();
			IUIManager manager = new UIManager(doc);
			Element template = (Element) XPathAPI.selectSingleNode(doc, "ui-templates/template[@id='" + temp + "']");
			//create element newTemplate with  id, rows and columns attributes
			Element newTemplate = doc.createElement("template");
			newTemplate.setAttribute("id", temp);
			newTemplate.setAttribute("rows", template.getAttribute("rows"));
			newTemplate.setAttribute("columns", template.getAttribute("columns"));
			int rows = Integer.parseInt(template.getAttribute("rows"));
			int cols = Integer.parseInt(template.getAttribute("columns"));
			//logger.debug("Creating "+temp);				
			// create row and cell elements 
			for (int i = 1; i <= rows; i++) {
				Element row = doc.createElement("row");
				row.setAttribute("id", Integer.toString(i));
				for (int j = 1; j <= cols; j++) {
					//get all information needed for cell creation 
					int cellNo = (i - 1) * cols + j - 1;
					String cellS = "cell" + Integer.toString(cellNo);
					String type = (String) hm.get(cellS + "type");
					String content = (String) hm.get(cellS + "content");
					String link = (String) hm.get(cellS + "link");
					String font = (String) hm.get(cellS + "font");
					String fontSize = (String) hm.get(cellS + "fontsize");
					String fontStyle = (String) hm.get(cellS + "fontstyle");
					String fontColor = (String) hm.get(cellS + "color");
					String textAlign = (String) hm.get(cellS + "position");
					String vertical = (String) hm.get(cellS + "vertical");
					try {
						//create cell with content, link(optional) and style elements
						HashMap attrib = new HashMap();
						if (!textAlign.equals("") && textAlign != null) {
							attrib.put("text-align", textAlign);
						}
						if (!vertical.equals("") && vertical != null) {
							attrib.put("vertical-align", vertical);
						}
						if (type.equals("blank")) {
							Element cell = doc.createElement("cell");
							cell.setAttribute("cols", Integer.toString(j));
							cell.setAttribute("type", type);
							row.appendChild(cell);
						} else if (type.equals("Picture")) {
							Element cell = (Element) manager.createCell(j, type, content, attrib);
							row.appendChild(cell);
						} else if (type.equals("Text")) {
							if (!fontSize.equals("") && fontSize != null) {
								attrib.put("font-size", fontSize);
							}
							if (!fontColor.equals("") && fontColor != null) attrib.put("color", fontColor);
							if (!font.equals("") && font != null) {
								attrib.put("font-family", font);
							}
							if (fontStyle.indexOf("i") > -1) {
								attrib.put("font-style", "italic");
							}
							if (fontStyle.indexOf("b") > -1) {
								attrib.put("font-weight", "bold");
							}
							if (fontStyle.indexOf("u") > -1) {
								attrib.put("text-decoration", "underline");
							}
							Element cell = (Element) manager.createCell(j, type, content, attrib);
							row.appendChild(cell);
						} else if (type.equals("Link")) {
							if (!fontSize.equals("") && fontSize != null) {
								attrib.put("font-size", fontSize);
							}
							if (!fontColor.equals("") && fontColor != null) attrib.put("color", fontColor);
							if (!font.equals("") && font != null) {
								attrib.put("font-family", font);
							}
							if (fontStyle.indexOf("i") > -1) {
								attrib.put("font-style", "italic");
							}
							if (fontStyle.indexOf("b") > -1) {
								attrib.put("font-weight", "bold");
							}
							if (fontStyle.indexOf("u") > -1) {
								attrib.put("text-decoration", "underline");
							}
							Element cell = (Element) manager.createCell(j, type, link, content, attrib);
							row.appendChild(cell);
						} else if (type.equals("LinkWithPic")) {
							Element cell = (Element) manager.createCell(j, type, link, content, attrib);
							row.appendChild(cell);
						}

					} catch (Exception e) {
						//logger.error(e.getMessage());
					}

				}
				newTemplate.appendChild(row);
			}
			Node ui_template = XPathAPI.selectSingleNode(doc, "ui-templates");
			//logger.debug("Saving "+temp);
			//replace old template with new one
			ui_template.replaceChild(newTemplate, template);
			IXmlSerializer s = ctx.getSerializer();
			s.serializeToFs(file);
			UIRendererTag.invalidateCache();

		} catch (Exception e) {
			//logger.error(e.getMessage());
		}
		return mapping.findForward(temp);

	}
}
