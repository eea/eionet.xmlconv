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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
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

public class EditSchemaAction extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();


	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();

		SchemaElemForm form = (SchemaElemForm) actionForm;
		String schemaId = form.getSchemaId();
		String schema = form.getSchema();
		String description = form.getDescription();
		String dtdId = form.getDtdId();

		if (isCancelled(httpServletRequest)) {
			try {
				SchemaManager sm = new SchemaManager();
				Schema sch = sm.getSchema(schemaId);
				httpServletRequest.setAttribute("schema", sch.getSchema());
				return actionMapping.findForward("back");
			} catch (DCMException e) {
				e.printStackTrace();
				_logger.error("Error editing schema",e);
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
			}
		}

		if (schema == null || schema.equals("")) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.schema.validation"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("success");
		}

		String user = (String) httpServletRequest.getSession().getAttribute("user");

		try {
			SchemaManager sm = new SchemaManager();
			sm.update(user, schemaId, schema, description, dtdId);
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.schema.updated"));
			httpServletRequest.setAttribute("schema", schema);
		} catch (DCMException e) {
			e.printStackTrace();
			_logger.error("Error editing schema",e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
		}
		httpServletRequest.getSession().setAttribute("dcm.errors", errors);
		httpServletRequest.getSession().setAttribute("dcm.messages", messages);

		return actionMapping.findForward("success");
	}
}
