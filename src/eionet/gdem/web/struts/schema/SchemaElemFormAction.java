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

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;

public class SchemaElemFormAction extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();


	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		ActionMessages errors = new ActionMessages();
		SchemaElemForm form = (SchemaElemForm) actionForm;
		String schemaId = (String) httpServletRequest.getParameter("schemaId");
		String user = (String) httpServletRequest.getSession().getAttribute("user");
		String backToConv = null;
		if (httpServletRequest.getParameter("backToConv") != null && !httpServletRequest.getParameter("backToConv").toString().equals("")) {
			backToConv = (String) httpServletRequest.getParameter("backToConv");
		}

		//System.out.println("backToConv=" + backToConv);

		if (schemaId != null && schemaId != "") {
			httpServletRequest.getSession().setAttribute("schemaId", schemaId);
		} else {
			schemaId = (String) httpServletRequest.getSession().getAttribute("schemaId");
		}
		SchemaElemHolder seHolder = new SchemaElemHolder();

		try {
			SchemaManager sm = new SchemaManager();
			seHolder = sm.getSchemaElems(user, schemaId);
			if (seHolder==null || seHolder.getSchema()==null)
				throw new DCMException(BusinessConstants.EXCEPTION_SCHEMA_NOT_EXIST);
			form.setSchema(seHolder.getSchema().getSchema());
			form.setDescription(seHolder.getSchema().getDescription());
			form.setSchemaId(schemaId);
			form.setDtdId(seHolder.getSchema().getDtdPublicId());
			form.setBackToConv(backToConv);
			form.setElemName("");
			form.setNamespace("");
			form.setDoValidation(seHolder.getSchema().isDoValidation());
			form.setSchemaLang(seHolder.getSchema().getSchemaLang());
			form.setDtd(seHolder.getSchema().getIsDTD());
			String fileName = seHolder.getSchema().getUplSchemaFileName();
			if(seHolder.getSchema().getUplSchema()!=null && !Utils.isNullStr(fileName)){
				form.setUplSchemaId(seHolder.getSchema().getUplSchema().getUplSchemaId());
				form.setUplSchemaFileUrl(seHolder.getSchema().getUplSchema().getUplSchemaFileUrl());
				form.setLastModified(seHolder.getSchema().getUplSchema().getLastModified());				
				form.setUplSchemaFileName(fileName);
				form.setUplSchemaFileUrl(Properties.gdemURL + "/schema/" +fileName);
			}
			seHolder.setSchemaIdRemoteUrl(Utils.isURL(seHolder.getSchema().getSchema()) &&
					!seHolder.getSchema().getSchema().startsWith(SecurityUtil.getUrlWithContextPath(httpServletRequest)));
			httpServletRequest.getSession().setAttribute("schema.rootElements", seHolder);

		} catch (DCMException e) {
			//e.printStackTrace();
			_logger.error("Schema element form error",e);
			errors.add("stylesheet", new ActionMessage(e.getErrorCode()));
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("fail");
		}

		httpServletRequest.getSession().setAttribute("stylesheet.outputtype", seHolder);
		return actionMapping.findForward("success");

	}
}
