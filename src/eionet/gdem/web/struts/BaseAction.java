package eionet.gdem.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.utils.SecurityUtil;

public class BaseAction extends Action {

    public static final String KEY_REQDTO = "dto";

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	return null;
    }
    
    protected boolean checkPermission(HttpServletRequest request, String acl, String permission) throws Exception {
    	String username=(String) request.getSession().getAttribute("user");
    	boolean result = username!=null && SecurityUtil.hasPerm(username, "/" + acl, permission);
    	return result;
    }
    
    protected String processFormStr(String arg) {
		String result=null;
		if(arg!=null) {
			if(!arg.trim().equalsIgnoreCase("")) {
				result=arg.trim();
			}
		}
		return result;
	}
    
    

}
