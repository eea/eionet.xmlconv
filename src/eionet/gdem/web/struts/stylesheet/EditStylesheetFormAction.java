package eionet.gdem.web.struts.stylesheet;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class EditStylesheetFormAction  extends Action {

	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		
		ActionMessages errors = new ActionMessages();
        
		StylesheetForm form=(StylesheetForm)actionForm;
		String stylesheetId= (String)httpServletRequest.getParameter("stylesheetId");
		ConvTypeHolder ctHolder = new ConvTypeHolder();

		try{
			StylesheetManager st = new StylesheetManager();
			Stylesheet stylesheet=st.getStylesheet(stylesheetId);
			form.setDescription(stylesheet.getXsl_descr());
			form.setOutputtype(stylesheet.getType());
			form.setSchema(stylesheet.getSchema());
			form.setStylesheetId(stylesheet.getConvId());
			form.setXsl(stylesheet.getXsl());

			ctHolder =st.getConvTypes();

			httpServletRequest.getSession().setAttribute("stylesheet.outputtypeSel", stylesheet.getType());
			
			SchemaManager schema = new SchemaManager();
			ArrayList schemas = schema.getDDSchemas(); 
			
			httpServletRequest.getSession().setAttribute("stylesheet.DDSchemas", schemas);
			
		}catch(DCMException e){			
			e.printStackTrace();
			_logger.error(e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
			saveErrors(httpServletRequest, errors);
		}
		httpServletRequest.getSession().setAttribute("stylesheet.outputtype", ctHolder);
		
        return actionMapping.findForward("success");
	}
}
