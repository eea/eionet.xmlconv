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
 *    Original code: Dusan Popovic (ED) 
 */

package eionet.gdem.web.tags.breadcrumbs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

class JspUtils {

	public static BreadCrumbs getBreadCrumbs(PageContext pageContext) {
		HttpSession session = pageContext.getSession();
		BreadCrumbs breadcrumbs = (BreadCrumbs) session
				.getAttribute("com.eurodyn.web.tags.breadcrumbs");
		if (breadcrumbs == null) {
			breadcrumbs = new BreadCrumbs();
			session.setAttribute(
					"com.eurodyn.web.tags.breadcrumbs",
					breadcrumbs);
		}
		return breadcrumbs;
	}

	public static String getUri(HttpServletRequest request) {
		String uri;
		Object requestUri = request
				.getAttribute("javax.servlet.forward.request_uri");
		if (requestUri != null) {
			uri = requestUri.toString();
			Object queryString = request
					.getAttribute("javax.servlet.forward.query_string");
			if (queryString != null) {
				uri += "?" + queryString;
			}
		} else {
			uri = request.getRequestURI();
			if (request.getQueryString() != null) {
				uri += "?" + request.getQueryString();
			}
		}
		return uri;
	}

}
