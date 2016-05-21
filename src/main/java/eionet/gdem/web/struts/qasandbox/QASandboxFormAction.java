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

package eionet.gdem.web.struts.qasandbox;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.exceptions.DCMException;
import eionet.gdem.web.struts.qascript.QAScriptListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * QASandboxFormAction Open sandbox form. Optionally load the form from session.
 *
 * @author Enriko Käsper, Tieto Estonia
 */

public class QASandboxFormAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(QASandboxFormAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionErrors errors = new ActionErrors();

        // reset the form in the session
        QASandboxForm cForm = (QASandboxForm) actionForm;

        boolean resetForm = true;
        if (httpServletRequest.getParameter("reset") != null) {
            resetForm = !"false".equals(httpServletRequest.getParameter("reset"));
        }
        if (resetForm) {
            cForm.resetAll(actionMapping, httpServletRequest);
        }

        try {
            httpServletRequest.setAttribute(QAScriptListLoader.QASCRIPT_LIST_ATTR, QAScriptListLoader.getList(httpServletRequest));
        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("QA Sandbox form error", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            saveMessages(httpServletRequest, errors);
        }

        saveErrors(httpServletRequest, errors);
        return actionMapping.findForward("success");
    }
}
