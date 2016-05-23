package eionet.gdem.web.struts.qascript;

import eionet.gdem.dcm.business.QAScriptManager;
import eionet.gdem.exceptions.DCMException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.RedirectingActionForward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deactivate QA script action class.
 * @author eworx-alk
 */
public class DeactivateQAScriptAction extends Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateQAScriptAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        QAScriptForm form = (QAScriptForm) actionForm;
        String scriptId = form.getScriptId();
        if (scriptId == null || scriptId.length() == 0) {
            scriptId = httpServletRequest.getParameter("scriptId");
        }
        
        String schemaId = form.getSchemaId();
        if (schemaId == null || schemaId.length() == 0) {
            schemaId = httpServletRequest.getParameter("schemaId");
        }


        String user = (String) httpServletRequest.getSession().getAttribute("user");
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        
        try {
            QAScriptManager qm = new QAScriptManager();
            qm.activateDeactivate(user, scriptId, false);
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.qascript.deactivated"));
            // clear qascript list in cache
            QAScriptListLoader.reloadList(httpServletRequest);
        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Error deactivating QA script", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
        }
        // saveErrors(httpServletRequest, errors);

        saveErrors(httpServletRequest.getSession(), errors);
        saveMessages(httpServletRequest.getSession(), messages);

        return findForward(actionMapping, "success", schemaId);
    }

    /**
     * Finds forward
     * @param actionMapping Action mapping
     * @param f F
     * @param schemaId Schema Id
     * @return Action forward
     */
    private ActionForward findForward(ActionMapping actionMapping, String f, String schemaId) {
        ActionForward forward = actionMapping.findForward(f);
        StringBuffer path = new StringBuffer(forward.getPath());
        path.append("?schemaId=" + schemaId);
        forward = new RedirectingActionForward(path.toString());
        return forward;
    }
}
