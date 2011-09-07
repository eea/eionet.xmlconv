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
import org.apache.struts.upload.FormFile;

import eionet.gdem.dcm.business.UplXmlFileManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

/**
 * Action for editing XML file metadata
 * 
 * @author Enriko Käsper (TietoEnator)
 * 
 */
public class EditUplXmlFileAction extends Action {

    private static LoggerIF _logger = GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        EditUplXmlFileForm form = (EditUplXmlFileForm) actionForm;
        String xmlfileId = form.getXmlfileId();
        String title = form.getTitle();
        FormFile xmlfile = form.getXmlFile();
        String xmlFileName = form.getXmlFileName();

        if (isCancelled(httpServletRequest)) {
            return actionMapping.findForward("success");
        }

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            UplXmlFileManager fm = new UplXmlFileManager();
            fm.updateUplXmlFile(user, xmlfileId, title, xmlFileName, xmlfile);
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.uplXmlFile.updated"));
        } catch (DCMException e) {
            e.printStackTrace();
            _logger.error("Error editing uploaded XML file", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
        }
        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);

        return actionMapping.findForward("success");
    }
}
