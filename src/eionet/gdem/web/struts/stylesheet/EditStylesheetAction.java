package eionet.gdem.web.struts.stylesheet;

import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;
import eionet.gdem.utils.xml.XmlException;

public class EditStylesheetAction extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();


	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();

		StylesheetForm form = (StylesheetForm) actionForm;
		String desc = form.getDescription();
		String schema = form.getSchema();
		String type = form.getOutputtype();
		FormFile xslFile = form.getXslfile();
		String stylesheetId = form.getStylesheetId();
		String user = (String) httpServletRequest.getSession().getAttribute("user");

		if (isCancelled(httpServletRequest)) {
			return actionMapping.findForward("success");
		}
		
		
		if (schema == null || schema.equals("")) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.schema.validation"));
		} else if (xslFile == null || xslFile.getFileSize() == 0) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.validation"));
		} else {
			try {
				IXmlCtx x = new XmlContext();
				x.setWellFormednessChecking();
				x.checkFromInputStream(new ByteArrayInputStream(xslFile.getFileData()));			
			} catch (Exception e) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.error.notvalid"));
			}
		}

		if(errors.isEmpty()) {
			try {
				StylesheetManager st = new StylesheetManager();
				st.update(user, stylesheetId, schema, xslFile, type, desc);
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.updated"));
			}catch (DCMException e) {
				_logger.error(e);
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
			}
		}
		
		if(!errors.isEmpty()) {
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("fail");
		}
		httpServletRequest.getSession().setAttribute("dcm.messages", messages);
		httpServletRequest.setAttribute("schema", schema);
		return actionMapping.findForward("success");
	}
}
