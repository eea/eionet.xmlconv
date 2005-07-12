package eionet.gdem.web.struts.conversion;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class ListConvFormAction  extends Action{

	private static LoggerIF _logger=GDEMServices.getLogger();
	
	   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

			
			ActionErrors errors = new ActionErrors();

			System.out.println("-------------ListConvFormAction-  start--------------");
			ArrayList schemas = null;
			
			try{
				SchemaManager sm = new SchemaManager();
				schemas = sm.getSchemas(); 
				
			}catch(DCMException e){			
				System.out.println(e.toString());
				_logger.debug(e.toString());
				errors.add("schema", new ActionError(e.getErrorCode()));
			}
	        saveErrors(httpServletRequest, errors);
			
	        httpServletRequest.getSession().setAttribute("converson.schemas", schemas);
			System.out.println("-------------ListConvFormAction---------------");
			
			
	        return actionMapping.findForward("success");
			
			
	    }


}
