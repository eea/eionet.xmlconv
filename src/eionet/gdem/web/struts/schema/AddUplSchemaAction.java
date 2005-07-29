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

public class AddUplSchemaAction extends Action {

	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();		
		UplSchemaForm form=(UplSchemaForm)actionForm;
				
		FormFile schema= form.getSchema();
		
		String user = (String)httpServletRequest.getSession().getAttribute("user");

		if (isCancelled(httpServletRequest) || schema.getFileName() == null ||  schema.getFileName().equals("")){
			
			return actionMapping.findForward("success");
		}
		
		
		try{
			SchemaManager sm = new SchemaManager();
			sm.addUplSchema(user, schema);			
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.uplSchema.inserted"));
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
