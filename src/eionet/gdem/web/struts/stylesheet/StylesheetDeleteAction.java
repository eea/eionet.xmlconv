package eionet.gdem.web.struts.stylesheet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class StylesheetDeleteAction extends Action {

	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        
		ActionErrors errors = new ActionErrors();
		String stylesheetId = (String)httpServletRequest.getParameter("stylesheetId");

		String user_name = (String)httpServletRequest.getSession().getAttribute("user");		
		
		
		System.out.println("stylesheetId="+stylesheetId);
		System.out.println("user="+user_name);
		
		
		
				
		
		try{
			StylesheetManager sm = new StylesheetManager();
			sm.delete(user_name, stylesheetId);
			errors.add("stylesheet", new ActionError("label.stylesheet.deleted"));
		}catch(DCMException e){			
			System.out.println(e.toString());
			_logger.debug(e.toString());
			errors.add("stylesheet", new ActionError(e.getErrorCode()));
		}
		
//		httpServletRequest.s .setParameter("schema","yuyuuggre");
		
		
        saveErrors(httpServletRequest, errors);
		
		
		
		
		System.out.println("-------------StylesheetDeleteAction---------------");
        return actionMapping.findForward("success");
    }


}
