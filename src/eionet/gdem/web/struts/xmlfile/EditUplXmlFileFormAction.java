/*
 * Created on 20.11.2007
 */
package eionet.gdem.web.struts.xmlfile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.Properties;
import eionet.gdem.dcm.business.UplXmlFileManager;
import eionet.gdem.dto.UplXmlFile;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

/**
 * Action for opening Edit XML file metadata form  
 * 
 * @author Enriko Käsper (TietoEnator)
 *
 */

public class EditUplXmlFileFormAction extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();


	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

		ActionMessages errors = new ActionMessages();

		EditUplXmlFileForm form = (EditUplXmlFileForm) actionForm;
		String xmlfileId = (String) httpServletRequest.getParameter("xmlfileId");

		try {
			UplXmlFileManager fm = new UplXmlFileManager();
			UplXmlFile xmlfile = fm.getUplXmlFileById(xmlfileId);

			form.setXmlfileId(xmlfile.getId());
			form.setXmlfile(httpServletRequest.getContextPath() + "/" + Properties.xmlfileFolder+ "/" + xmlfile.getFileName());

			form.setTitle(xmlfile.getTitle());

		} catch (DCMException e) {
			e.printStackTrace();
			_logger.error("Error editing uploaded XML file", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
			saveErrors(httpServletRequest, errors);
		}

		return actionMapping.findForward("success");
	}
}
