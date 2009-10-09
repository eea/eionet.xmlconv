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

package eionet.gdem.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionMessages;
import org.apache.struts.tiles.TilesRequestProcessor;

public class RequestProcessor extends TilesRequestProcessor {


	public RequestProcessor() {
		super();
	}


	/**
	 * Preprocess every action that is called from struts framework
	 * 
	 */
	public boolean processPreprocess(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("servletPath", request.getServletPath());

		// Remove messages from session and add to page context
		ActionMessages errors = (ActionMessages) request.getSession().getAttribute("dcm.errors");
		if (errors != null) {
			request.getSession().setAttribute("dcm.errors", null);
			if (!errors.isEmpty()) {
				request.setAttribute("dcm.errors", errors);
			}
		}
		ActionMessages messages = (ActionMessages) request.getSession().getAttribute("dcm.messages");
		if (messages != null) {
			request.getSession().setAttribute("dcm.messages", null);
			if (!messages.isEmpty()) request.setAttribute("dcm.messages", messages);
		}
		return true;
	}


//	protected ActionForward processActionPerform(HttpServletRequest request, HttpServletResponse response, Action action, ActionForm form, ActionMapping mapping) throws IOException, ServletException {
//		String path = request.getPathInfo();
//		String query = request.getQueryString();
//		if (query != null && query.length() > 0) path += "?" + query;
//
//		boolean loggedIn = false;
//		Object objUser = request.getSession().getAttribute("user");
//
//		if (objUser == null && (path.indexOf("/editUI") == 0 || path.indexOf("/ldapForm") == 0 || path.indexOf("/dbForm") == 0 || path.indexOf("/addUplSchemaForm") == 0 || path.indexOf("/addStylesheetForm") == 0)) {
//			String casLoginUrl = getServletContext().getInitParameter("edu.yale.its.tp.cas.client.filter.loginUrl") + "?service=" + request.getRequestURL();
//			response.sendRedirect("/do/login");
//		}
//
//		
//		
//		return super.processActionPerform(request, response, action, form, mapping);
//	}
//

}
