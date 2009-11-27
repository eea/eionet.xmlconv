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
 * The Original Code is XMLCONV.
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 * 
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.web.struts.qascript;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

/**
 * @author Enriko Käsper, Tieto Estonia
 * SchemaQAScriptsFormAction
 */

public class SchemaQAScriptsFormAction  extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();


	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

		QAScriptListHolder st = new QAScriptListHolder();
		ActionMessages messages = new ActionMessages();
		String user_name = (String) httpServletRequest.getSession().getAttribute("user");

		String schemaId = (String) httpServletRequest.getParameter("schemaId");

		if (schemaId == null || schemaId.equals("")) {
			schemaId = (String) httpServletRequest.getAttribute("schemaId");
		}

		
		if (schemaId == null || schemaId.equals("")) {
			return actionMapping.findForward("home");
		}

		httpServletRequest.setAttribute("schemaId", schemaId);
		

		try {
			SchemaManager sm = new SchemaManager();
			st = sm.getSchemasWithQAScripts(user_name, schemaId);

		} catch (DCMException e) {
			e.printStackTrace();
			_logger.error("Error getting schema QA scripts",e);
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
		}
		saveErrors(httpServletRequest, messages);

		httpServletRequest.getSession().setAttribute("schema.qascripts", st);
		return actionMapping.findForward("success");
	}
}
