package eionet.gdem.web.struts.schema;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.Properties;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dto.UplSchema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class EditUplSchemaFormAction  extends Action {

	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		
		ActionMessages errors = new ActionMessages();
        
		EditUplSchemaForm form=(EditUplSchemaForm)actionForm;
		String schemaId= (String)httpServletRequest.getParameter("schemaId");
		
		System.out.println("-------schemaId---------" +schemaId);
		
		try{
			SchemaManager sm = new SchemaManager();
			UplSchema schema = sm.getUplSchemasById(schemaId);

			System.out.println("-------id---------" +schema.getId());
			
			form.setIdSchema(schema.getId());
			form.setSchema(httpServletRequest.getContextPath() + "/schema/" + schema.getSchema());
			
			form.setDescription(schema.getDescription());
			
			System.out.println("-------description---------" +schema.getDescription());
			
		}catch(DCMException e){			
			e.printStackTrace();
			_logger.error(e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
			saveErrors(httpServletRequest, errors);
		}
		
        return actionMapping.findForward("success");
	}
}
