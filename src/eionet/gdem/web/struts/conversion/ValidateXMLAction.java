/*
 * Created on 16.04.2008
 */
package eionet.gdem.web.struts.conversion;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.ValidationService;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ValidateXMLAction
 */

public class ValidateXMLAction  extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		String ticket = (String) httpServletRequest.getSession().getAttribute(Names.TICKET_ATT);
		ActionErrors errors = new ActionErrors();

		ConversionForm cForm = (ConversionForm) actionForm;
		String url = cForm.getUrl();
		String schema = cForm.getSchemaUrl();

		if(Utils.isNullStr(url)){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.conversion.selectSource"));
			//httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			saveErrors(httpServletRequest,errors);
			return actionMapping.findForward("error");
		}
		if(!Utils.isURL(url)){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED));
			//httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			saveErrors(httpServletRequest,errors);
			return actionMapping.findForward("error");
		}


		try {
			ArrayList valid;
			String validatedSchema = null;
			String originalSchema = null;
			try {
				ValidationService v = new ValidationService(true);
				v.setTrustedMode(false);
				v.setTicket(ticket);
				if (schema == null)  // schema defined in header
					v.validate(url);
				else
					v.validateSchema(url, schema);
				valid = v.getErrorList();
				validatedSchema = v.getValidatedSchemaURL();
				originalSchema = v.getOriginalSchema();
			} catch (DCMException dcme) {
				throw dcme;
			} catch (Exception e) {
				throw new DCMException(BusinessConstants.EXCEPTION_VALIDATION_ERROR);
			}
			httpServletRequest.setAttribute("conversion.valid", valid);
			httpServletRequest.setAttribute("conversion.originalSchema", originalSchema);
			if(!originalSchema.equals(validatedSchema))
				httpServletRequest.setAttribute("conversion.validatedSchema", validatedSchema);
		} catch (DCMException e) {
			e.printStackTrace();
			_logger.error("Error validating xml",e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("error");
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("Error validating xml",e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(BusinessConstants.EXCEPTION_GENERAL));
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("error");
		}
		return actionMapping.findForward("success");
	}

}
