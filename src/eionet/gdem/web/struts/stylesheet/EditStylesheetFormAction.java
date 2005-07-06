package eionet.gdem.web.struts.stylesheet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class EditStylesheetFormAction  extends Action {

	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		ActionErrors errors = new ActionErrors();
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

			System.out.println("stylesheet.outputtypeSel="+stylesheet.getType());
			httpServletRequest.getSession().setAttribute("stylesheet.outputtypeSel", stylesheet.getType());
			
			
			System.out.println("stylesheetId-----"+stylesheet.getConvId());
			
		}catch(DCMException e){			
			System.out.println(e.toString());
			_logger.debug(e.toString());
			errors.add("stylesheet", new ActionError(e.getErrorCode()));
		}
        saveErrors(httpServletRequest, errors);
		httpServletRequest.getSession().setAttribute("stylesheet.outputtype", ctHolder);
		
		System.out.println("-------------EditStylesheetFormAction---------------");
        return actionMapping.findForward("success");


	
	}


}
