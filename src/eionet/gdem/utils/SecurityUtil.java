/**
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
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.gdem.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.tee.uit.security.AppUser;
import com.tee.uit.security.AccessController;
import com.tee.uit.security.AccessControlListIF;

import edu.yale.its.tp.cas.client.filter.CASFilter;
import eionet.gdem.GDEMException;
import eionet.gdem.web.struts.login.AfterCASLoginAction;


/**
 * This is a class containing some utility methods for keeping
 * security.
 *
 * @author Enriko Käsper
 */
public class SecurityUtil {
    

    /**
    * Returns current user, or 'null', if the current session
    * does not have user attached to it.
    */
    public static final AppUser getUser(HttpServletRequest request, String attrName) {
        

        HttpSession session = request.getSession();
        AppUser user = session==null ? null : (AppUser)session.getAttribute(attrName);
        
        if (user==null){
        	String casUserName = (String)session.getAttribute(CASFilter.CAS_FILTER_USER);
        	if (casUserName!=null){
        		user = new CASUser(casUserName);
				session.setAttribute(attrName, user);
				session.setAttribute("user", user.getUserName());        	
			}
        }
        else if (user instanceof CASUser){
        	String casUserName = (String)session.getAttribute(CASFilter.CAS_FILTER_USER);
        	if (casUserName==null){
        		user = null;
        		session.removeAttribute(attrName);
				session.removeAttribute("user");        	
        	}
        	else if (!casUserName.equals(user.getUserName())){
        		user = new CASUser(casUserName);
				session.setAttribute(attrName, user);
				session.setAttribute("user", user.getUserName());        	
        	}
        }
        
        if (user != null)
            return user;
        else 
            return null;
    }
    /**
     * 
     */
    public static boolean hasPerm(String usr, String aclPath, String prm)
    														throws Exception{
    	if (!aclPath.startsWith("/")) return false;
    	
    	boolean has = false;
		AccessControlListIF acl = null;
		int i = aclPath.indexOf("/", 1);
		while (i!=-1 && !has){
			String subPath = aclPath.substring(0,i);
			try{
				acl = AccessController.getAcl(subPath);
			}
			catch (Exception e){
				acl = null;
			}
			
			if (acl!=null)
				has = acl.checkPermission(usr, prm);
			
			i = aclPath.indexOf("/", i+1);
		}
		
		if (!has){
			try{
				acl = AccessController.getAcl(aclPath);
			}
			catch (Exception e){
				acl = null;
			}
			
			if (acl!=null)
				has = acl.checkPermission(usr, prm);
		}
    	
    	return has;
    }

	/**
	 * 
	 * @param request
	 * @return
	 * @throws GDEMException 
	 */
	public static String getLoginURL(HttpServletRequest request) throws GDEMException {
		
		String result = "/do/login";
		
		String casLoginUrl = request.getSession().getServletContext().getInitParameter(CASFilter.LOGIN_INIT_PARAM);
		if (casLoginUrl!=null){

			String afterLoginUrl = getRealRequestURL(request);
			request.getSession().setAttribute(AfterCASLoginAction.AFTER_LOGIN_ATTR_NAME, afterLoginUrl);

			StringBuffer loginUrl = new StringBuffer(casLoginUrl);
			loginUrl.append("?service=");
			try {
				// + request.getScheme() + "://" + SERVER_NAME + request.getContextPath() + "/login";
				loginUrl.append(URLEncoder.encode(getUrlWithContextPath(request) + "/do/afterLogin", "UTF-8"));
				result = loginUrl.toString();
			}
			catch (UnsupportedEncodingException e) {
				throw new GDEMException(e.toString(), e);
			}
		}
		
		return result;
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws GDEMException 
	 */
	public static String getLogoutURL(HttpServletRequest request) throws GDEMException{


		String result = "/do/start";
		
		String casLoginUrl = request.getSession().getServletContext().getInitParameter(CASFilter.LOGIN_INIT_PARAM);
		if (casLoginUrl!=null){
			
			StringBuffer buf = new StringBuffer(casLoginUrl.replaceFirst("/login", "/logout"));
			try {
				buf.append("?url=").append(URLEncoder.encode(getUrlWithContextPath(request), "UTF-8"));
				result = buf.toString();
			}
			catch (UnsupportedEncodingException e) {
				throw new GDEMException(e.toString(), e);
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	private static String getUrlWithContextPath(HttpServletRequest request){
		
		StringBuffer url = new StringBuffer(request.getScheme());
		url.append("://").append(request.getServerName());
		if (request.getServerPort()>0)
			url.append(":").append(request.getServerPort());
		url.append(request.getContextPath());
		return url.toString();
	}    
	private static String getRealRequestURL(HttpServletRequest request){
		
		HttpServletRequest tmpRequest = request;
		while (tmpRequest instanceof HttpServletRequestWrapper) {
			tmpRequest = (HttpServletRequest) ((HttpServletRequestWrapper)
					tmpRequest).getRequest();
		}
		StringBuffer url = tmpRequest.getRequestURL();

		if (tmpRequest.getQueryString()!=null)
			url.append("?").append(tmpRequest.getQueryString());
		
		return url.toString();
	}
}
class CASUser extends AppUser {

	public CASUser(String userName){
		this.authenticatedUserName = userName;
	}
	private String authenticatedUserName ;

	public String getUserName(){
		return authenticatedUserName; 
	}
}