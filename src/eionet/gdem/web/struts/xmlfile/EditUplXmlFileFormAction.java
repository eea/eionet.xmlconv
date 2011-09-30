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

import eionet.gdem.Properties;
import eionet.gdem.dcm.business.UplXmlFileManager;
import eionet.gdem.dto.UplXmlFile;
import eionet.gdem.exceptions.DCMException;

/**
 * Action for opening Edit XML file metadata form
 *
 * @author Enriko Käsper (TietoEnator)
 *
 */

public class EditUplXmlFileFormAction extends Action {

    /** */
    private static final Log LOGGER = LogFactory.getLog(EditUplXmlFileFormAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionMessages errors = new ActionMessages();

        EditUplXmlFileForm form = (EditUplXmlFileForm) actionForm;
        String xmlfileId = httpServletRequest.getParameter("xmlfileId");

        try {
            UplXmlFileManager fm = new UplXmlFileManager();
            UplXmlFile xmlfile = fm.getUplXmlFileById(xmlfileId);

            form.setXmlfileId(xmlfile.getId());
            form.setXmlFileName(xmlfile.getFileName());
            form.setXmlFilePath(httpServletRequest.getContextPath() + "/" + Properties.xmlfileFolder + "/");

            form.setTitle(xmlfile.getTitle());
            form.setLastModified(xmlfile.getLastModified());

        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Error editing uploaded XML file", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            saveErrors(httpServletRequest, errors);
        }

        return actionMapping.findForward("success");
    }
}
