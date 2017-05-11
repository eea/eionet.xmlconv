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
 * Enriko Käsper, TripleDev
 */

package eionet.gdem.web.struts.qascript;

import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kaido Laine
 */

public class DiffUplScriptAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiffUplScriptAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        QAScriptForm form = (QAScriptForm) actionForm;

        String schemaId = form.getSchemaId();
        //String uplSchemaId = form.getUplSchemaId();
        String scriptFile = form.getFileName();
        String scriptUrl = form.getUrl();
        String scriptId = form.getScriptId();
        String forward = "success";

        SyncUplScriptForm syncForm = new SyncUplScriptForm();

        String user_name = (String) httpServletRequest.getSession().getAttribute("user");

        try {
//            scriptFile = Properties.queriesFolder + File.separator + scriptFile;
//            SchemaManager sm = new SchemaManager();
//            byte[] remoteScript = sm.downloadRemoteSchema(scriptUrl);
            byte[] remoteScript = Utils.downloadRemoteFile(scriptUrl);

            String result = Utils.diffRemoteFile(remoteScript, Properties.queriesFolder + File.separator + scriptFile);

            if (!Utils.isNullStr(result)) {
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(result));
            }

            if (!BusinessConstants.WARNING_FILES_IDENTICAL.equals(result) || result.equals("")) {
                forward = "warning";
                syncForm.setScriptId(scriptId);
                syncForm.setUrl(scriptUrl);
                syncForm.setFileName(scriptFile);

                try {
                    syncForm.setScriptFile(new String(remoteScript, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    syncForm.setScriptFile(new String(remoteScript));
                    e.printStackTrace();
                }
                httpServletRequest.setAttribute("SyncUplScriptForm", syncForm);
            }

        } catch (DCMException e) {
            LOGGER.error("Unable to diff schemas", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            forward = "fail";
        }
        httpServletRequest.setAttribute("scriptId", scriptId);

        saveMessages(httpServletRequest.getSession(), messages);
        saveErrors(httpServletRequest.getSession(), errors);

        // saveMessages(httpServletRequest,messages);
        // saveErrors(httpServletRequest,errors);

        return actionMapping.findForward(forward);
    }
}
