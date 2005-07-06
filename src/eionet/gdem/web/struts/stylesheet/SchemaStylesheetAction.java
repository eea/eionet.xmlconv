package eionet.gdem.web.struts.stylesheet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class SchemaStylesheetAction extends Action{

	private static LoggerIF _logger=GDEMServices.getLogger();
	
	   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

			StylesheetListHolder st = new StylesheetListHolder();
			ActionErrors errors = new ActionErrors();
			String user_name = (String)httpServletRequest.getSession().getAttribute("user");		
			String schema= (String)httpServletRequest.getParameter("schema");

			System.out.println("-------------SchemaStylesheetAction-  start--------------");
			System.out.println("user="+user_name);
			System.out.println("schema request="+schema);
			System.out.println("schema sessija="+(String)httpServletRequest.getSession().getAttribute("schema"));
			
			if (schema!=null && schema!=""){
				System.out.println("-------------schema is requesta "+schema+"--------------");
				httpServletRequest.getSession().setAttribute("schema", schema);
			}else{
				System.out.println("-------------schema is sesije "+schema+"--------------");
				schema=(String)httpServletRequest.getSession().getAttribute("schema");
			}
			
			try{
				SchemaManager sm = new SchemaManager();
				st =sm.getSchemaStylesheets( schema,user_name);
				
			}catch(DCMException e){			
				System.out.println(e.toString());
				_logger.debug(e.toString());
				errors.add("schema", new ActionError(e.getErrorCode()));
			}
	        saveErrors(httpServletRequest, errors);
			
	        httpServletRequest.getSession().setAttribute("schema.stylesheets", st);
			System.out.println("-------------SchemaStylesheetAction---------------");
			
			
			
	        return actionMapping.findForward("success");
			
			
	    }

}
