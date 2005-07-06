package eionet.gdem.web.struts.schema;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.dcm.business.RootElemManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class DeleteRootElemAction extends Action {

	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        
		//StylesheetListHolder st = new StylesheetListHolder();
		ActionErrors errors = new ActionErrors();
		String elemId = (String)httpServletRequest.getParameter("elemId");

		String user_name = (String)httpServletRequest.getSession().getAttribute("user");		
		
		////////////////////////////

		 //SaveHandler.handleSchemas(httpServletRequest,Names.XSD_DEL_ACTION);		
		
		////////////////////////////		
		
		System.out.println("elemId="+elemId);
		System.out.println("user="+user_name);
		
		
		try{
			RootElemManager rm = new RootElemManager();
			rm.delete( user_name, elemId);
			errors.add("elem", new ActionError("label.elem.deleted"));
		}catch(DCMException e){			
			System.out.println(e.toString());
			_logger.debug(e.toString());
			errors.add("elem", new ActionError(e.getErrorCode()));
		}
        saveErrors(httpServletRequest, errors);
		
		System.out.println("-------------DeleteRootElemAction---------------");
        return actionMapping.findForward("success");
    }

}
