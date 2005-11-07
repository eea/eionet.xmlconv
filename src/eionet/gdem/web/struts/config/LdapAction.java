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

package eionet.gdem.web.struts.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.DynaValidatorForm;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.conf.DcmProperties;
import eionet.gdem.dcm.conf.LdapTest;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.SecurityUtil;

public class LdapAction extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();


	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();

		DynaValidatorForm form = (DynaValidatorForm) actionForm;
		String url = (String) form.get("url");
		String context = (String) form.get("context");
		String userDir = (String) form.get("userDir");
		String attrUid = (String) form.get("attrUid");

		String user = (String) httpServletRequest.getSession().getAttribute("user");

		try {

			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_CONFIG_PATH, "u")) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.autorization.config.update"));
				httpServletRequest.getSession().setAttribute("dcm.errors", errors);
				return actionMapping.findForward("success");
			}

			if (url == null || url.equals("")) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.config.ldap.url.validation"));
				httpServletRequest.getSession().setAttribute("dcm.errors", errors);
				return actionMapping.findForward("success");
			}

			LdapTest lt = new LdapTest(url);
			if (!lt.test()) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.editParam.ldap.testFailed"));
				httpServletRequest.getSession().setAttribute("dcm.errors", errors);
				return actionMapping.findForward("success");
			}

			DcmProperties dcmProp = new DcmProperties();

			dcmProp.setLdapParams(url, context, userDir, attrUid);
		} catch (DCMException e) {
			e.printStackTrace();
			_logger.error(e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
			saveErrors(httpServletRequest, errors);
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error(e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.exception.unknown"));
			saveErrors(httpServletRequest, errors);
		}
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.editParam.ldap.saved"));

		httpServletRequest.getSession().setAttribute("dcm.errors", errors);
		httpServletRequest.getSession().setAttribute("dcm.messages", messages);
		return actionMapping.findForward("success");
	}

}
