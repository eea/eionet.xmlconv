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
import javax.servlet.http.HttpSession;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.HashMap;

import com.tee.uit.security.AppUser;
import com.tee.uit.security.AccessController;
import com.tee.uit.security.AccessControlListIF;


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
    public static final AppUser getUser(HttpServletRequest servReq, String attrName) {
        
        AppUser user = null;
              
        HttpSession httpSession = servReq.getSession(false);
        if (httpSession != null) {
            user = (AppUser)httpSession.getAttribute(attrName);
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
    
}
