package eionet.gdem.web.struts.schema;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;


import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class EditSchemaAction extends Action {

	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		ActionErrors errors = new ActionErrors();
		SchemaElemForm form=(SchemaElemForm)actionForm;
		String schemaId=form.getSchemaId();
		String schema=form.getSchema(); 
		String description = form.getDescription();
		String dtdId=form.getDtdId();
		
		String user = (String)httpServletRequest.getSession().getAttribute("user");
		//httpServletRequest.getSession().setAttribute("schema", schema);
		
		try{
			SchemaManager sm = new SchemaManager();
			sm.update( user, schemaId, schema, description, dtdId);
			errors.add("schema", new ActionError("label.schema.inserted"));
		}catch(DCMException e){			
			System.out.println(e.toString());
			_logger.debug(e.toString());
			errors.add("schema", new ActionError(e.getErrorCode()));
		}
    
        saveErrors(httpServletRequest, errors);
		
		System.out.println("-------------EditSchemaAction---------------");
        return actionMapping.findForward("success");


	
	}
		
}
