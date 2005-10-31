package eionet.gdem.web.struts.stylesheet;

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

public class SchemaStylesheetAction extends Action{

	private static LoggerIF _logger=GDEMServices.getLogger();
	
	   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

			StylesheetListHolder st = new StylesheetListHolder();
			ActionMessages messages = new ActionMessages();
			String user_name = (String)httpServletRequest.getSession().getAttribute("user");		
			String schema= (String)httpServletRequest.getParameter("schema");
		
			/*if (schema!=null && schema!=""){
				httpServletRequest.getSession().setAttribute("schema", schema);
			}else{
				schema=(String)httpServletRequest.getSession().getAttribute("schema");
			}

			*/
			
			if (schema==null || schema.equals("")){
				schema= (String)httpServletRequest.getAttribute("schema");
			}
			
			
			if(schema==null || schema.equals("")){
				return actionMapping.findForward("home");
			}
			
			httpServletRequest.setAttribute("schema",schema);
			
			try{
				SchemaManager sm = new SchemaManager();
				st =sm.getSchemaStylesheets( schema,user_name);
				
			}catch(DCMException e){			
				e.printStackTrace();
				_logger.error(e);
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
			}
	        saveErrors(httpServletRequest, messages);
			
	        httpServletRequest.getSession().setAttribute("schema.stylesheets", st);
	        return actionMapping.findForward("success");
	    }
}
