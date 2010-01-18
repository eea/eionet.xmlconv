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

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.qascript.QAScriptListHolder;

/**
 * EditQAScriptInSandboxAction
 * Find all the scripts for the given XML schema and allow to execute them in sandox
 * 
 * @author Enriko Käsper, Tieto Estonia
 */

public class OpenQAServiceInSandboxAction extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		ActionErrors errors = new ActionErrors();

		// get the schemas list from the session
		Object schemasInSession = httpServletRequest.getSession().getAttribute("qascript.qascriptList");

		// reset the form in the session
		QASandboxForm cForm = (QASandboxForm) actionForm;
		String userName = (String) httpServletRequest.getSession().getAttribute("user");

		String schemaIdParam = null;
		if (httpServletRequest.getParameter("schemaId") != null) {
			schemaIdParam = (String) httpServletRequest.getParameter("schemaId");
		}
		if (Utils.isNullStr(schemaIdParam)) {

			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.missingSchemaId"));
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("error");
		}

		try {
			// if schemas list is not stored in the session, then load it from
			// the database
			if (schemasInSession == null || ((QAScriptListHolder) schemasInSession).getQascripts().size() == 0) {
				QAScriptListHolder schemas = loadSchemas(userName);
				httpServletRequest.getSession().setAttribute("qascript.qascriptList", schemas);
			}
			cForm.setShowScripts(true);
			cForm.setSourceUrl("");

			SchemaManager sm = new SchemaManager();
			QAScriptListHolder qascripts = sm.getSchemasWithQAScripts(userName, schemaIdParam);
			Schema schema = null;

			if (qascripts == null || qascripts.getQascripts() == null || qascripts.getQascripts().size() == 0) {
				schema = new Schema();
			} else {
				schema = qascripts.getQascripts().get(0);
				cForm.setSchemaId(schema.getId());
				cForm.setSchemaUrl(schema.getSchema());
			}
			cForm.setSchema(schema);
			if (Utils.isNullList(cForm.getSchema().getQascripts()) && cForm.getSchema().isDoValidation()) {
				cForm.setScriptId("-1");
			} else if (!Utils.isNullList(cForm.getSchema().getQascripts())
					&& cForm.getSchema().getQascripts().size() == 1 && !cForm.getSchema().isDoValidation()) {
				cForm.setScriptId(cForm.getSchema().getQascripts().get(0).getScriptId());
			}
		} catch (DCMException e) {
			e.printStackTrace();
			_logger.error("QA Sandbox fomr error error", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
			saveMessages(httpServletRequest, errors);
		}

		saveErrors(httpServletRequest, errors);
		return actionMapping.findForward("success");
	}

	/**
	 * load schemas from db
	 * 
	 * @return
	 * @throws DCMException
	 */
	private QAScriptListHolder loadSchemas(String userName) throws DCMException {

		QAScriptListHolder schemas = null;
		SchemaManager sm = new SchemaManager();

		schemas = sm.getSchemasWithQAScripts(userName);
		return schemas;
	}
}
