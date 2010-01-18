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

package eionet.gdem.web.struts.qasandbox;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.dcm.business.QAScriptManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;

/**
 * EditQAScriptInSandboxAction
 * The action saves the changes of QA script content into file system.
 * 
 * @author Enriko Käsper, Tieto Estonia
 */

public class SaveQAScriptContentAction extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

		ActionErrors errors = new ActionErrors();
		ActionMessages messages = new ActionMessages();

		QASandboxForm cForm = (QASandboxForm) actionForm;
		String scriptId = cForm.getScriptId();
		String content = cForm.getScriptContent();

		if (Utils.isNullStr(scriptId)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.missingId"));
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("error");
		}
		if (Utils.isNullStr(content)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.missingContent"));
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("error");
		}
		try {
			String userName = (String) httpServletRequest.getSession().getAttribute("user");

			QAScriptManager qm = new QAScriptManager();
			qm.storeQAScriptFromString(userName, scriptId, content);
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("message.qasandbox.contentSaved"));
		} catch (DCMException e) {
			// e.printStackTrace();
			_logger.error("Error saving script content", e);
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("error");
		} catch (Exception e) {
			// e.printStackTrace();
			_logger.error("Error saving script content", e);
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("error");
		}

		saveMessages(httpServletRequest, messages);
		return actionMapping.findForward("success");
	}

}
