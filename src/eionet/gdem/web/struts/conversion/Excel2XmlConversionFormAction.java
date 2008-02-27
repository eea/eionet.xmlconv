/*
 * Created on 26.02.2008
 */
package eionet.gdem.web.struts.conversion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * Excel2XmlConversionFormAction
 */

public class Excel2XmlConversionFormAction extends Action {
	private static LoggerIF _logger = GDEMServices.getLogger();
	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		return actionMapping.findForward("success");
	}
}
