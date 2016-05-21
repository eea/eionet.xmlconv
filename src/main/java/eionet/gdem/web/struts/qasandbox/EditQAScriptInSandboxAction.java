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

import eionet.gdem.dcm.business.QAScriptManager;
import eionet.gdem.dto.QAScript;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.qascript.QAScriptListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EditQAScriptInSandboxAction Open selected QA script content and allow to edit it.
 *
 * @author Enriko Käsper, Tieto Estonia
 *
 */

public class EditQAScriptInSandboxAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(EditQAScriptInSandboxAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionErrors errors = new ActionErrors();

        // reset the form in the session
        QASandboxForm cForm = (QASandboxForm) actionForm;

        String scriptIdParam = null;
        if (httpServletRequest.getParameter("scriptId") != null) {
            scriptIdParam = httpServletRequest.getParameter("scriptId");
        }
        boolean reset = false;
        // request comes from Schema Queries page
        if (httpServletRequest.getParameter("reset") != null) {
            reset = "true".equals(httpServletRequest.getParameter("reset"));
        }
        if (Utils.isNullStr(scriptIdParam)) {

            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.missingId"));
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }

        try {
            httpServletRequest.setAttribute(QAScriptListLoader.QASCRIPT_LIST_ATTR, QAScriptListLoader.getList(httpServletRequest));
            // reset field values
            if (reset) {
                cForm.setSourceUrl("");
                if (cForm.getSchema() != null) {
                    Schema schema = cForm.getSchema();
                    schema.setCrfiles(null);
                    cForm.setSchema(schema);
                }
            }
            cForm.setShowScripts(false);

            // write a new script
            if ("0".equals(scriptIdParam)) {
                cForm.setScriptId(scriptIdParam);
                cForm.setScriptContent("");
                cForm.setScriptType(XQScript.SCRIPT_LANG_XQUERY1);
                return actionMapping.findForward("success");
            }
            QAScriptManager qm = new QAScriptManager();
            QAScript script = qm.getQAScript(scriptIdParam);

            cForm.setScriptId(scriptIdParam);
            cForm.setScriptContent(script.getScriptContent());
            cForm.setScriptType(script.getScriptType());

            cForm.setSchemaId(script.getSchemaId());
            cForm.setSchemaUrl(script.getSchema());
            Schema schema = cForm.getSchema();
            if (schema == null) {
                schema = new Schema();
                schema.setSchema(script.getSchema());
                schema.setId(script.getSchemaId());
                cForm.setSchema(schema);
            }
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
