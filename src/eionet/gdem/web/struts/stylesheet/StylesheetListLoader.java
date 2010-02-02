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

package eionet.gdem.web.struts.stylesheet;

import javax.servlet.http.HttpServletRequest;

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

/**
 * Loads stylesheet list and stores it in session
 * 
 * @author Enriko Käsper, Tieto Estonia
 * StylesheetListLoader
 */

public class StylesheetListLoader {
	
	public final static String STYLESHEET_LIST_ATTR = "stylesheet.stylesheetList";

	private static LoggerIF _logger = GDEMServices.getLogger();

	public static StylesheetListHolder loadStylesheetList(HttpServletRequest httpServletRequest, boolean reload, String type) throws DCMException{

		
		Object st = httpServletRequest.getSession().getAttribute(STYLESHEET_LIST_ATTR);
		if(st==null || !(st instanceof StylesheetListHolder) || reload){
			st = new StylesheetListHolder();

			String user_name = (String) httpServletRequest.getSession().getAttribute("user");
			try {
				SchemaManager sm = new SchemaManager();
				st = sm.getSchemas(user_name,type);
			} catch (DCMException e) {
				e.printStackTrace();
				_logger.error("Error getting stylesheet list", e);
				throw e;
			}
			httpServletRequest.getSession().setAttribute(STYLESHEET_LIST_ATTR, st);
		}

		return (StylesheetListHolder)st;
	}
	public static void clearList(HttpServletRequest httpServletRequest){
		httpServletRequest.getSession().removeAttribute(STYLESHEET_LIST_ATTR);		
	}

}
