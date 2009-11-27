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
import org.apache.struts.upload.FormFile;

import eionet.gdem.dcm.business.QAScriptManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

/**
 * @author Enriko Käsper, Tieto Estonia
 * AddQAScriptAction
 */

public class AddQAScriptAction  extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();


	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

		QAScriptForm form = (QAScriptForm) actionForm;
		String scriptId = form.getScriptId();
		String schemaId = form.getSchemaId();
		String shortName = form.getShortName();
		String desc = form.getDescription();
		String schema = form.getSchema();
		String resultType = form.getResultType();
		String scriptType = form.getScriptType();
		FormFile scriptFile = form.getScriptFile();

		String user = (String) httpServletRequest.getSession().getAttribute("user");

		httpServletRequest.setAttribute("schemaId", schemaId);

		if (isCancelled(httpServletRequest)) {
			if(schema!=null)
				return findForward(actionMapping, "cancel", schemaId);
			else
				return actionMapping.findForward("list");
		}

		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();

		if (scriptFile == null || scriptFile.getFileSize() == 0) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.qascript.file.validation"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
		}
				
		if (schema == null || schema.equals("")) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.qascript.schema.validation"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
		}
		if(errors.size()>0){
			return actionMapping.findForward("fail");			
		}

		try {
			QAScriptManager qm = new QAScriptManager();
			qm.add(user, scriptId, shortName, schemaId, schema, resultType, desc, scriptType, scriptFile);
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.qascript.inserted"));
		} catch (DCMException e) {
			e.printStackTrace();
			_logger.error("Add QA Script error", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
		}
		httpServletRequest.getSession().setAttribute("dcm.errors", errors);
		httpServletRequest.getSession().setAttribute("dcm.messages", messages);
		//new schema might be added, remove the schemas list form the session.
		httpServletRequest.getSession().removeAttribute("conversion.schemas");
		return new ActionForward("/do/schemaStylesheets?schema="+schema,true);  //actionMapping.findForward("success");
	}

	private ActionForward findForward(ActionMapping actionMapping, String f, String scriptId) {
		ActionForward forward = actionMapping.findForward(f);
		StringBuffer path = new StringBuffer(forward.getPath());
		path.append("?scriptId=" + scriptId);
		forward = new RedirectingActionForward(path.toString());
		return forward;
	}
}
