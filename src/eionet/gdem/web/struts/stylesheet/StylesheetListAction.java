package eionet.gdem.web.struts.stylesheet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;


public class StylesheetListAction extends Action {
	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

		StylesheetListHolder st = new StylesheetListHolder();
		ActionMessages errors = new ActionMessages();
		String user_name = (String)httpServletRequest.getSession().getAttribute("user");		
		
		try{
			SchemaManager sm = new SchemaManager();
			st =sm.getSchemas(user_name);
			
		}catch(DCMException e){			
			System.out.println(e.toString());
			_logger.debug(e.toString());
			errors.add("schema", new ActionError("label.exception.unknown"));
			saveErrors(httpServletRequest, errors);			
		}
        httpServletRequest.getSession().setAttribute("stylesheet.stylesheetList", st);
        return actionMapping.findForward("success");
    }
}
	

