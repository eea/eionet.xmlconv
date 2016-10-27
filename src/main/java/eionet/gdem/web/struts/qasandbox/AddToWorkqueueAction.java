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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.dcm.business.WorkqueueManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SearchCRSandboxAction Add selected scripts into workqueue.
 *
 * @author Enriko Käsper, Tieto Estonia
 */

public class AddToWorkqueueAction extends Action {
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(AddToWorkqueueAction.class);

    private static WorkqueueManager workqueueManager = new WorkqueueManager();
    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionErrors errors = new ActionErrors();
        ActionMessages messages = new ActionMessages();

        QASandboxForm cForm = (QASandboxForm) actionForm;
        String sourceUrl = cForm.getSourceUrl();
        String content = cForm.getScriptContent();
        String scriptType = cForm.getScriptType();
        String schemaUrl = cForm.getSchemaUrl();

        if (Utils.isNullStr(sourceUrl)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.missingUrl"));
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }
        if (Utils.isNullStr(content) && !cForm.isShowScripts()) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.missingContent"));
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }
        if (Utils.isNullStr(schemaUrl) && cForm.isShowScripts()) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.error.qasandbox.missingSchemaUrl"));
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }
        if (!Utils.isURL(sourceUrl)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.notUrl"));
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }
        try {
            String userName = (String) httpServletRequest.getSession().getAttribute("user");

            if (cForm.isShowScripts()) {
                List<String> jobIds = workqueueManager.addSchemaScriptsToWorkqueue(userName, sourceUrl, schemaUrl);
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("message.qasandbox.jobsAdded", jobIds.toString()));
            } else {
                String jobId = workqueueManager.addQAScriptToWorkqueue(userName, sourceUrl, content, scriptType);
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("message.qasandbox.jobAdded", jobId));
            }
        } catch (DCMException e) {
            LOGGER.error("Error saving script content", e);
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        } catch (Exception e) {
            LOGGER.error("Error saving script content", e);
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }

        saveMessages(httpServletRequest, messages);
        return actionMapping.findForward("success");
    }

}
