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
import org.apache.struts.upload.FormFile;

import eionet.gdem.dcm.business.UplXmlFileManager;
import eionet.gdem.exceptions.DCMException;

/**
 * Action for adding XML files into reporitory
 *
 * @author Enriko Käsper (TietoEnator)
 *
 */
public class AddUplXmlFileAction extends Action {

    /** */
    private static final Log LOGGER = LogFactory.getLog(AddUplXmlFileAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        UplXmlFileForm form = (UplXmlFileForm) actionForm;

        FormFile xmlfile = form.getXmlfile();
        String title = form.getTitle();

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        if (isCancelled(httpServletRequest)) {

            return actionMapping.findForward("success");
        }

        if (xmlfile == null || xmlfile.getFileSize() == 0) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.uplXmlFile.validation"));
            httpServletRequest.getSession().setAttribute("dcm.errors", errors);
            return actionMapping.findForward("fail");
        }

        /*
         * IXmlCtx x = new XmlContext(); try { x.setWellFormednessChecking(); x.checkFromInputStream(new
         * ByteArrayInputStream(xmlfile.getFileData())); } catch (Exception e) { errors.add(ActionMessages.GLOBAL_MESSAGE, new
         * ActionMessage("label.uplXmlFile.error.notvalid")); httpServletRequest.getSession().setAttribute("dcm.errors", errors);
         * return actionMapping.findForward("fail"); }
         */

        try {
            UplXmlFileManager fm = new UplXmlFileManager();
            fm.addUplXmlFile(user, xmlfile, title);
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.uplXmlFile.inserted"));
        } catch (DCMException e) {
            LOGGER.error("Error adding upload XML file", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
        }
        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);

        return actionMapping.findForward("success");
    }
}
