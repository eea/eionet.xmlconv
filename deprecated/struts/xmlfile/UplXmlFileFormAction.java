/*
 * Created on 20.11.2007
 */
package eionet.gdem.web.struts.xmlfile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.dcm.business.UplXmlFileManager;
import eionet.gdem.exceptions.DCMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action loading the list of XML files.
 *
 * @author Enriko Käsper (TietoEnator)
 *
 */
public class UplXmlFileFormAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(UplXmlFileFormAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionErrors errors = new ActionErrors();
        UplXmlFileHolder holder = null;

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            UplXmlFileManager fm = new UplXmlFileManager();
            holder = fm.getUplXmlFiles(user);

        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Uploaded XML file form error", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            saveMessages(httpServletRequest, errors);
        }
        saveMessages(httpServletRequest, errors);

        httpServletRequest.setAttribute("xmlfiles.uploaded", holder);
        return actionMapping.findForward("success");
    }
}
