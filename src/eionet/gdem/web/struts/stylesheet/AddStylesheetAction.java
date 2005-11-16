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

import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;

public class AddStylesheetAction extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();


	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

		StylesheetForm form = (StylesheetForm) actionForm;
		String desc = form.getDescription();
		String schema = form.getSchema();
		String type = form.getOutputtype();
		FormFile xslFile = form.getXslfile();
		String user = (String) httpServletRequest.getSession().getAttribute("user");
		httpServletRequest.setAttribute("schema", schema);

		if (isCancelled(httpServletRequest)) {
			return actionMapping.findForward("success");
		}

		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();

		if (xslFile == null || xslFile.getFileSize() == 0) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.validation"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("fail");
		}

		IXmlCtx x = new XmlContext();
		try {
			x.setWellFormednessChecking();
			x.checkFromInputStream(new ByteArrayInputStream(xslFile.getFileData()));
		} catch (Exception e) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.error.notvalid"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("fail");
		}

		try {
			IXmlCtx xml = new XmlContext();
			xml.setWellFormednessChecking();
			xml.checkFromInputStream((new InputFile(schema)).getSrcInputStream());
		} catch (Exception e) {
			_logger.error("schema not valid",e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.schema.error.notvalid"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("fail");
		}
		
		
		
		if (schema == null || schema.equals("")) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.schema.validation"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("fail");
		}

		try {
			StylesheetManager st = new StylesheetManager();
			st.add(user, schema, xslFile, type, desc);
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.inserted"));
		} catch (DCMException e) {
			e.printStackTrace();
			_logger.error("Add stylesheet error", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
		}
		httpServletRequest.getSession().setAttribute("dcm.errors", errors);
		httpServletRequest.getSession().setAttribute("dcm.messages", messages);

		return actionMapping.findForward("success");
	}

}
