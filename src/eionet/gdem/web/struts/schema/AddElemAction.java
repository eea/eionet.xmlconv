package eionet.gdem.web.struts.schema;

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
import org.apache.struts.upload.FormFile;


import eionet.gdem.dcm.business.RootElemManager;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class AddElemAction extends Action {

	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();		
		SchemaElemForm form=(SchemaElemForm)actionForm;
				
		String elem= form.getElemName();
		String namespace=form.getNamespace();
		String schemaId=form.getSchemaId();
		
		String user = (String)httpServletRequest.getSession().getAttribute("user");

		
		if(elem == null || elem.equals("") || namespace == null || namespace.equals("")){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.elem.validation"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);						
			return actionMapping.findForward("success");
		}
		
		
		try{
			RootElemManager rm = new RootElemManager();
			rm.add( user, schemaId,elem, namespace);
			form.setElemName("");
			form.setNamespace("");
			
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.elem.inserted"));
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
