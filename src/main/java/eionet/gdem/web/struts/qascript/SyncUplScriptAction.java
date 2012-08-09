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
 * Agency.  Portions created by TripleDev are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Enriko Käsper, TripleDev
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

import eionet.gdem.dcm.business.QAScriptManager;

/**
 * @author Kaido Laine
 */

public class SyncUplScriptAction extends Action {

    /** */
    private static final Log LOGGER = LogFactory.getLog(SyncUplScriptAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        SyncUplScriptForm form = (SyncUplScriptForm) actionForm;

        String scriptId = form.getScriptId();
        String scriptFileName = form.getFileName();
        String url = form.getUrl();

        httpServletRequest.setAttribute("scriptId", scriptId);
        String user_name = (String) httpServletRequest.getSession().getAttribute("user");

        if (isCancelled(httpServletRequest)) {
            return actionMapping.findForward("success");
        }

        try {
            QAScriptManager qm = new QAScriptManager();

            qm.replaceScriptFromRemoteFile(user_name, url, scriptFileName);
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.uplScript.cached"));

        } catch (Exception e) {
            // e.printStackTrace();
            LOGGER.error("Unable to sync local script", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage()));
        }
        saveMessages(httpServletRequest.getSession(), messages);
        saveErrors(httpServletRequest.getSession(), errors);

        // saveMessages(httpServletRequest,messages);
        // saveErrors(httpServletRequest,errors);

        return actionMapping.findForward("success");
    }
}
