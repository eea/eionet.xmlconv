package eionet.gdem.web.struts.stylesheet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class StylesheetDeleteAction extends Action {

	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        
		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();
		String stylesheetId = (String)httpServletRequest.getParameter("stylesheetId");
		String user_name = (String)httpServletRequest.getSession().getAttribute("user");

		httpServletRequest.setAttribute("schema", httpServletRequest.getParameter("schema"));
		
		try{
			StylesheetManager sm = new StylesheetManager();
			sm.delete(user_name, stylesheetId);
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.deleted"));			
		}catch(DCMException e){
			e.printStackTrace();
			_logger.error(e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError(e.getErrorCode()));
		}
		
        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);				
        
        return actionMapping.findForward("success");
    }


}
