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

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


public class PushTag extends TagSupport {
	//private static final WDSLogger logger = WDSLogger.getLogger(PushTag.class);

	private String url;

	private String label;

	private String level;

	public PushTag() {
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		try {
			this.level = new Integer(level).toString();
		} catch (NumberFormatException e) {
			//logger.error(e);
			this.level = "1";
		}
	}

	public int doEndTag() throws JspException {
		BreadCrumbs breadcrumbs = JspUtils.getBreadCrumbs(pageContext);
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();

		String uri = this.url;
		if (this.url == null) {
			uri = JspUtils.getUri(request);
		} else {
			uri = request.getContextPath() + uri;
		}

		String referer = request.getHeader("Referer");

		// TODO
		// check if referer is from the same machine
		// ...

		// strip out the url part
		if (referer != null) {
			try {
				URL url = new URL(referer);
				referer = url.getFile();
			} catch (MalformedURLException murle) {
				referer = null;
			}
		}

		breadcrumbs.addToTrail(referer, new BreadCrumb(uri, this.label),
				new Integer(level).intValue());

		return EVAL_PAGE;
	}

}
