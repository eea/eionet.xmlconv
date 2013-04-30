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
import org.apache.struts.action.RedirectingActionForward;

import eionet.gdem.dcm.business.QAScriptManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.web.struts.schema.SchemaElemForm;

/**
 * @author Enriko Käsper, Tieto Estonia SchemaValidationFormAction
 */

public class SaveSchemaValidationAction extends Action {

    /** */
    private static final Log LOGGER = LogFactory.getLog(SaveSchemaValidationAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        SchemaElemForm form = (SchemaElemForm) actionForm;
        String schemaId = form.getSchemaId();
        boolean validate = form.isDoValidation();
        boolean blocker = form.isBlockerValidation();

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

    private ActionForward findForward(ActionMapping actionMapping, String f, String scriptId) {
        ActionForward forward = actionMapping.findForward(f);
        StringBuffer path = new StringBuffer(forward.getPath());
        path.append("?schemaId=" + scriptId);
        forward = new RedirectingActionForward(path.toString());
        return forward;
    }
}
