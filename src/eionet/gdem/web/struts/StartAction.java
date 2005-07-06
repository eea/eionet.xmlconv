package eionet.gdem.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;



public class StartAction extends Action {
	

	public ActionForward execute(ActionMapping actionMapping,
			ActionForm actionForm, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

		
		
/*		if (httpServletRequest.getParameter("logout")!= null)
		{
			httpServletRequest.getSession().setAttribute("user", null);
			return actionMapping.findForward("home");	//home page
		}
*/
		System.out.println("------------StartAction-----------");
		System.out.println(httpServletRequest.getQueryString());
		
		

	     for (java.util.Enumeration  e =httpServletRequest.getParameterNames();  e.hasMoreElements() ;) {
	         System.out.println(e.nextElement());
	     }

		 
		if (httpServletRequest.getParameter("logout")!= null)
		{
			httpServletRequest.getSession().setAttribute("user", null);
			return actionMapping.findForward("home");	//home page
		}
		 
		
		if (httpServletRequest.getParameter("login")!= null){
			System.out.println("------------login-----------");
			return actionMapping.findForward("loginForm");	//manual login
		}
		/*	
		Object obj = httpServletRequest.getSession().getAttribute("user");

		if (obj != null && obj instanceof User) {
			return actionMapping.findForward("success");	//already logged in			
		}
		
		Cookie c[] = httpServletRequest.getCookies();
		if(c != null){			
			for (int i = 0; i < c.length; i++) {
				if (c[i].getName().compareTo("user") == 0) {
					return actionMapping.findForward("login");	//login with cookie
				}
			}
		}
		return actionMapping.findForward("home");	//home page*/
		
		return actionMapping.findForward("home");	//manual login
	}

}
