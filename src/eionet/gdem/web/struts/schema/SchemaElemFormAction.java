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

public class SchemaElemFormAction  extends Action {

	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		ActionMessages errors = new ActionMessages();
		SchemaElemForm form=(SchemaElemForm)actionForm;
		String schemaId= (String)httpServletRequest.getParameter("schemaId");
		String user = (String)httpServletRequest.getSession().getAttribute("user");
		
		if (schemaId!=null && schemaId!=""){
			httpServletRequest.getSession().setAttribute("schemaId", schemaId);
		}else{
			schemaId=(String)httpServletRequest.getSession().getAttribute("schemaId");
		}

		
		SchemaElemHolder seHolder = new SchemaElemHolder();
		
		try{
			SchemaManager sm = new SchemaManager();
			seHolder = sm.getSchemaElems(user,schemaId);
			form.setSchema(seHolder.getSchema().getSchema());
			form.setDescription(seHolder.getSchema().getDescription());
			form.setSchemaId(schemaId);
			form.setDtdId(seHolder.getSchema().getDtdPublicId());
			
			httpServletRequest.getSession().setAttribute("schema.rootElemets", seHolder);
						
		}catch(DCMException e){			
			e.printStackTrace();
			_logger.error(e);
			errors.add("stylesheet", new ActionMessage(e.getErrorCode()));
			saveErrors(httpServletRequest, errors);
		}
        
		httpServletRequest.getSession().setAttribute("stylesheet.outputtype", seHolder);		
        return actionMapping.findForward("success");
	
	}
}
