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
import org.apache.struts.validator.DynaValidatorForm;

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

		if(Utils.isNullStr(url)){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.conversion.selectSource"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("error");
		}
		if(!Utils.isURL(url)){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.conversion.url.malformed"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("error");
		}


		try {
			String schema = null;
				ArrayList valid;
				if (schema == null) { // schema defined in header
					valid = validate(url, ticket);
				} else {
					valid = validateSchema(url, schema, ticket);
				}
				httpServletRequest.setAttribute("conversion.valid", valid);
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


	private ArrayList validate(String url, String ticket) throws DCMException {
		try {
			ValidationService v = new ValidationService(true);
			v.setTrustedMode(false);
	    	v.setTicket(ticket);
			v.validate(url);
			return v.getErrorList();
		} catch (DCMException dcme) {
			throw dcme;
		} catch (Exception e) {
			throw new DCMException(BusinessConstants.EXCEPTION_VALIDATION_ERROR);
		}
	}


	private ArrayList validateSchema(String url, String schema, String ticket) throws DCMException {
		try {
			ValidationService v = new ValidationService(true);
			v.setTrustedMode(false);
	    	v.setTicket(ticket);
			v.validateSchema(url, schema);
			v.printList();
			return v.getErrorList();

		} catch (DCMException dcme) {
			throw dcme;
		} catch (Exception e) {
			throw new DCMException(BusinessConstants.EXCEPTION_VALIDATION_ERROR);
		}
	}

	private String processFormStr(String arg) {
		String result=null;
		if(arg!=null) {
			if(!arg.trim().equalsIgnoreCase("")) {
				result=arg.trim();
			}
		}
		return result;
	}

}
