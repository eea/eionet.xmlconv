package eionet.gdem.web.struts.stylesheet;

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

public class EditStylesheetAction extends Action {

	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		ActionErrors errors = new ActionErrors();
		System.out.println("-------------EditStylesheetAction--- start ------------");
		
		if (isCancelled(httpServletRequest)){
			System.out.println("-------------EditStylesheetAction--- is cancelled ------------");
			return actionMapping.findForward("success");
		}
		
		
		StylesheetForm form=(StylesheetForm)actionForm;
		String desc= form.getDescription();
		String schema=form.getSchema();
		String type=form.getOutputtype();
		FormFile xslFile=form.getXslfile();
		String stylesheetId=form.getStylesheetId();
		String user = (String)httpServletRequest.getSession().getAttribute("user");
		httpServletRequest.getSession().setAttribute("schema", schema);
		
		try{
			StylesheetManager st = new StylesheetManager();
			
			st.update( user,stylesheetId, schema,xslFile, type, desc);
			errors.add("stylesheet", new ActionError("label.stylesheet.updated"));
		}catch(DCMException e){			
			System.out.println(e.toString());
			_logger.debug(e.toString());
			errors.add("stylesheet", new ActionError(e.getErrorCode()));			
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("fail");
		}
        saveErrors(httpServletRequest, errors);
		
		System.out.println("-------------EditStylesheetAction---------------");
        return actionMapping.findForward("success");


	
	}
		
}
