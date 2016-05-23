/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is XMLCONV.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.web.struts.qascript;

import eionet.gdem.dcm.business.QAScriptManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.web.struts.schema.SchemaElemForm;


import org.apache.struts.action.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Enriko Käsper, Tieto Estonia SchemaValidationFormAction
 */

public class SaveSchemaValidationAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveSchemaValidationAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        SchemaElemForm form = (SchemaElemForm) actionForm;
        String schemaId = form.getSchemaId();
        boolean validate = form.isDoValidation();
        boolean blocker = form.isBlocker();

        String user = (String) httpServletRequest.getSession().getAttribute("user");
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        httpServletRequest.setAttribute("schemaId", schemaId);

        try {
            QAScriptManager qm = new QAScriptManager();
            qm.updateSchemaValidation(user, schemaId, validate, blocker);
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.qascript.validation.updated"));
        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Error updateing schema validation", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
        }

        saveErrors(httpServletRequest.getSession(), errors);
        saveMessages(httpServletRequest.getSession(), messages);
        return findForward(actionMapping, "success", schemaId);
    }

    /**
     * Finds forward
     * @param actionMapping Action mapping
     * @param f F
     * @param scriptId Script Id
     * @return Action forward
     */
    private ActionForward findForward(ActionMapping actionMapping, String f, String scriptId) {
        ActionForward forward = actionMapping.findForward(f);
        StringBuffer path = new StringBuffer(forward.getPath());
        path.append("?schemaId=" + scriptId);
        forward = new RedirectingActionForward(path.toString());
        return forward;
    }
}
