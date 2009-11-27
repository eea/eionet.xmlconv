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
import org.apache.struts.action.RedirectingActionForward;

import eionet.gdem.dcm.business.QAScriptManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

/**
 * @author Enriko Käsper, Tieto Estonia
 * DeleteQAScriptAction
 */

public class DeleteQAScriptAction  extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();


	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

		QAScriptForm form = (QAScriptForm) actionForm;
		String scriptId = (String) httpServletRequest.getParameter("scriptId");
		String schemaId = (String) httpServletRequest.getParameter("schemaId");

		String user = (String) httpServletRequest.getSession().getAttribute("user");
		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();

		httpServletRequest.setAttribute("schemaId", httpServletRequest.getParameter("schemaId"));

		try {
			QAScriptManager qm = new QAScriptManager();
			qm.delete(user, scriptId);
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.qascript.deleted"));
		} catch (DCMException e) {
			e.printStackTrace();
			_logger.error("Error deleting QA script",e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
		}
		// saveErrors(httpServletRequest, errors);

		saveErrors(httpServletRequest.getSession(), errors);
		saveMessages(httpServletRequest.getSession(), messages);

		return findForward(actionMapping, "success", schemaId);
	}
	private ActionForward findForward(ActionMapping actionMapping, String f, String stylesheetId){
		ActionForward forward = actionMapping.findForward(f);
		 StringBuffer path = new StringBuffer(forward.getPath());
		 path.append("?ID=" + stylesheetId);
	    forward = new RedirectingActionForward(path.toString());
	    return forward;
	}
}
