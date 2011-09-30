/*
 * Created on 20.11.2007
 */
package eionet.gdem.web.struts.xmlfile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.dcm.business.UplXmlFileManager;
import eionet.gdem.exceptions.DCMException;

/**
 * Action for deleting XML files from reporitory
 *
 * @author Enriko Käsper (TietoEnator)
 *
 */
public class DeleteUplXmlFileAction extends Action {

    /** */
    private static final Log LOGGER = LogFactory.getLog(DeleteUplXmlFileAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        String xmlfileId = httpServletRequest.getParameter("xmlfileId");
        String user_name = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            UplXmlFileManager fm = new UplXmlFileManager();
            fm.deleteUplXmlFile(user_name, xmlfileId);
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.uplXmlFile.deleted"));
        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Error deleting XML file", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
        }
        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);

        return actionMapping.findForward("success");
    }
}
