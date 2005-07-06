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

package com.eurodyn.web.tags;

import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import com.tee.uit.security.AccessControlListIF;
import com.tee.uit.security.AccessController;
import com.tee.uit.security.SignOnException;


public class HasRole extends ConditionalTagSupport{

	private String username;
	private String role;
	private String acl;


	public HasRole (){
	}
    
    /**
     * @return
     */
    public String getUsername() {
    	return username;
    }
    

    /**
     * @param strUri
     */
    public void setUsername(String strUri) {
		username = strUri;
        
    }
    
	public String getRole() {
		return role;
	}
	

	public void setRole(String role) {
		this.role = role;
	}
	/**
     * allow or not to display jsp content;depends on user's roles (Principals).
     * @return true if tag displays content when user has got the specified role(principal); false otherwise
	 * @see javax.servlet.jsp.jstl.core.ConditionalTagSupport#condition()
	 */
	protected boolean condition() throws JspTagException {
		String u = (String)pageContext.getAttribute(username);
		
		if (u != null && hasPerm(u, acl, role)){
           return true;
              
        }
		else{
           return false;
		}        
	}

	public boolean hasPerm(String usr, String aclPath, String prm){
		if (!aclPath.startsWith("/"))
			return false;

		boolean has = false;
		AccessControlListIF acl = null;
		int i = aclPath.length() <= 1 ? -1 : aclPath.indexOf("/", 1); // not
																		// forgetting
																		// root
																		// path
																		// ("/")
		
		try {
			while (i != -1 && !has) {
				String subPath = aclPath.substring(0, i);
				try {
					acl = AccessController.getAcl(subPath);
				} catch (Exception e) {
					acl = null;
				}

				if (acl != null)
					has = acl.checkPermission(usr, prm);

				i = aclPath.indexOf("/", i + 1);
			}
			if (!has) {
				try {
					acl = AccessController.getAcl(aclPath);
				} catch (Exception e) {
					acl = null;
				}

				if (acl != null)
					has = acl.checkPermission(usr, prm);
			}
		} catch (SignOnException e) {
			return false;
		}

		return has;
	}

	public String getAcl() {
		return acl;
	}
	

	public void setAcl(String acl) {
		this.acl = acl;
	}
	

	
}
