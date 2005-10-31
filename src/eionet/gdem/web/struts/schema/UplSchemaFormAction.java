package eionet.gdem.web.struts.schema;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class UplSchemaFormAction  extends Action{

	private static LoggerIF _logger=GDEMServices.getLogger();
	
	   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
			ActionErrors errors = new ActionErrors();
			UplSchemaHolder holder = null;
			
			String user = (String)httpServletRequest.getSession().getAttribute("user");
			
			try{
				SchemaManager sm = new SchemaManager();
				holder = sm.getUplSchemas(user);
				
			}catch(DCMException e){			
				e.printStackTrace();
				_logger.error(e);
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
				saveMessages(httpServletRequest, errors);				
			}
			saveMessages(httpServletRequest, errors);
			
	        httpServletRequest.getSession().setAttribute("schemas.uploaded", holder);
	        return actionMapping.findForward("success");
	    }
}
