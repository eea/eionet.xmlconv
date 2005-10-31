package eionet.gdem.web.struts.schema;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class EditUplSchemaAction extends Action {

	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();		

		EditUplSchemaForm form=(EditUplSchemaForm)actionForm;
		String schemaId=form.getIdSchema();		 
		String description = form.getDescription();
		
		if (isCancelled(httpServletRequest)){						
			return actionMapping.findForward("success");
		}		
		
		String user = (String)httpServletRequest.getSession().getAttribute("user");

		try{
			SchemaManager sm = new SchemaManager();
			//sm.uplUpdate( user, schemaId, description);
			sm.updateUplSchema( user, schemaId, description);
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.schema.updated"));
		}catch(DCMException e){			
			e.printStackTrace();
			_logger.error(e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
		}
        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);						
		
        return actionMapping.findForward("success");
	}		
}
