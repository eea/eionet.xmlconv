
package eionet.gdem.ssr;

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
    public static final AppUser getUser(HttpServletRequest servReq) {
        
        AppUser user = null;
              
        HttpSession httpSession = servReq.getSession(false);
        if (httpSession != null) {
            user = (AppUser)httpSession.getAttribute(Names.USER_ATT);
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
