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
 *    Original code: Nedeljko Pavlovic (ED) 
 */

package eionet.gdem.web.tags;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;


import eionet.gdem.Properties;
import eionet.gdem.utils.cache.MemoryCache;
import eionet.gdem.utils.xml.XSLTransformer;

public class UIRendererTag extends TagSupport {

	private static String UI_XSL;
	private static String UI_DEFINITION;
	public static XSLTransformer transform = new XSLTransformer();
	protected static MemoryCache MemCache;
	private final static Object mutex = new Object();

	static {
		synchronized (mutex) {
			MemCache = new MemoryCache(100, 10);
			UI_DEFINITION=Properties.uiFolder + File.separatorChar + "UITemplate.xml";
			UI_XSL=Properties.uiFolder + File.separatorChar + "UITemplate.xsl"; 
		}
	}

	private String id;
	private String enableJs;


	public UIRendererTag() {
	}


	public void setParent(Tag parent) {
	}


	public Tag getParent() {
		return null;
	}


	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @param enableJs The enableJs to set.
	 */
	public void setEnableJs(String enableJs) {
		this.enableJs = enableJs;
	}


	public synchronized static void invalidateCache() {
		MemCache.clearCache();
	}


	public int doStartTag() throws JspException {
		Map parameters = new HashMap();
		parameters.put("templateselect", id);
		parameters.put("cellclick", enableJs);
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		parameters.put("galleryPath", request.getContextPath() + "/images/gallery/");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String content=(String) MemCache.getContent(id);
		if (enableJs != null || content == null) {
			try {
				transform.transform("UITemplate.xsl", new FileInputStream(UI_XSL), new FileInputStream(UI_DEFINITION), baos, parameters);
				content=baos.toString("UTF-8");
				if (enableJs == null) {
					MemCache.put(id, content, Integer.MAX_VALUE);
				} 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			print(content);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return EVAL_BODY_INCLUDE;
	}


	/**
	 * doEndTag method
	 * @throws JspException
	 * @return int
	 */
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}


	public void release() {
	}


	/**
	 * Prints Object to output stream
	 * @param toPrint Object
	 * @throws JspException
	 */
	protected void print(Object toPrint) throws JspException {
		try {
			pageContext.getOut().write(toPrint.toString());
		} catch (IOException ioe) {
			throw new JspException(ioe.getMessage()); // CHANGED FOR TEST //
		}
	}

}
