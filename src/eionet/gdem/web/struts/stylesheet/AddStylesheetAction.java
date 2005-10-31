package eionet.gdem.web.struts.stylesheet;

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

public class AddStylesheetAction extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();


	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

		StylesheetForm form = (StylesheetForm) actionForm;
		String desc = form.getDescription();
		String schema = form.getSchema();
		String type = form.getOutputtype();
		FormFile xslFile = form.getXslfile();
		String user = (String) httpServletRequest.getSession().getAttribute("user");
		//httpServletRequest.getSession().setAttribute("schema", schema);
		httpServletRequest.setAttribute("schema", schema);

		if (isCancelled(httpServletRequest)) {
			return actionMapping.findForward("success");
		}

		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();

		if (xslFile == null || xslFile.getFileSize() == 0) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.validation"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("fail");
		}

		if (schema == null || schema.equals("")) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.schema.validation"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("fail");
		}

		try {
			StylesheetManager st = new StylesheetManager();
			st.add(user, schema, xslFile, type, desc);
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.inserted"));
		} catch (DCMException e) {
			e.printStackTrace();
			_logger.error(e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
		}
		httpServletRequest.getSession().setAttribute("dcm.errors", errors);
		httpServletRequest.getSession().setAttribute("dcm.messages", messages);

		return actionMapping.findForward("success");
	}

}
