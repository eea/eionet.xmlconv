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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.RedirectingActionForward;
import org.apache.struts.actions.LookupDispatchAction;
import org.apache.struts.upload.FormFile;

import eionet.gdem.Constants;
import eionet.gdem.dcm.business.QAScriptManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Enriko Käsper, Tieto Estonia EditQAScriptAction
 */

public class EditQAScriptAction extends LookupDispatchAction {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(EditQAScriptAction.class);

    /**
     * The method uploads the file from user's filesystem to the repository. Saves all the other changes made onthe form execpt the
     * file source in textarea
     * @param actionMapping Action mapping
     * @param actionForm Action form
     * @param httpServletRequest Request
     * @param httpServletResponse Response
     * @return Action forward
     */
    public ActionForward upload(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        QAScriptForm form = (QAScriptForm) actionForm;
        String scriptId = form.getScriptId();
        String schemaId = form.getSchemaId();
        String shortName = form.getShortName();
        String desc = form.getDescription();
        String schema = form.getSchema();
        String resultType = form.getResultType();
        String scriptType = form.getScriptType();
        String curFileName = form.getFileName();
        FormFile content = form.getScriptFile();
        String upperLimit = form.getUpperLimit();
        String url = form.getUrl();


        String user = (String) httpServletRequest.getSession().getAttribute("user");

        httpServletRequest.setAttribute("scriptId", scriptId);

        if (isCancelled(httpServletRequest)) {
            return findForward(actionMapping, "success", scriptId);
        }

        try {
            QAScriptManager qm = new QAScriptManager();
            qm.update(user, scriptId, shortName, schemaId, resultType, desc, scriptType, curFileName, content, upperLimit, url);

            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.qascript.updated"));

            // clear qascript list in cache
            QAScriptListLoader.reloadList(httpServletRequest);
        } catch (DCMException e) {
            LOGGER.error("Edit QA script error", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
        }

        if (!errors.isEmpty()) {
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("fail");
        }
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);
        httpServletRequest.setAttribute("schema", schema);
        return findForward(actionMapping, "success", scriptId);
    }

    /**
     * The method saves all the changes made on the form. Saves also modifications made to the file source textarea
     * @param actionMapping Action mapping
     * @param actionForm Action form
     * @param httpServletRequest Request
     * @param httpServletResponse Response
     * @return Action forward
     */
    public ActionForward save(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        QAScriptForm form = (QAScriptForm) actionForm;
        String scriptId = form.getScriptId();
        String schemaId = form.getSchemaId();
        String shortName = form.getShortName();
        String desc = form.getDescription();
        String schema = form.getSchema();
        String resultType = form.getResultType();
        String scriptType = form.getScriptType();
        String curFileName = form.getFileName();
        String scriptContent = form.getScriptContent();
        String upperLimit = form.getUpperLimit();
        String url = form.getUrl();
        String checksum = form.getChecksum();
        boolean active = form.getActive();

        boolean updateContent = false;
        String newChecksum = null;

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        httpServletRequest.setAttribute("scriptId", scriptId);

        if (isCancelled(httpServletRequest)) {
            return findForward(actionMapping, "success", scriptId);
        }

        if (!Utils.isNullStr(curFileName) && !Utils.isNullStr(scriptContent)
                && scriptContent.indexOf(Constants.FILEREAD_EXCEPTION) == -1) {

            // compare checksums
            try {
                newChecksum = Utils.getChecksumFromString(scriptContent);
            } catch (Exception e) {
                LOGGER.error("unable to create checksum");
            }
            if (checksum == null) {
                checksum = "";
            }
            if (newChecksum == null) {
                newChecksum = "";
            }

            updateContent = !checksum.equals(newChecksum);
        }

        // Zip result type can only be selected for FME scripts
        if (!XQScript.SCRIPT_LANG_FME.equals(scriptType) && XQScript.SCRIPT_RESULTTYPE_ZIP.equals(resultType)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.qascript.zip.validation"));
            saveErrors(httpServletRequest.getSession(), errors);
        }

        // upper limit between 0 and 10Gb
        if (upperLimit == null || !Utils.isNum(upperLimit) || Integer.parseInt(upperLimit) <= 0
                || Integer.parseInt(upperLimit) > 10000) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.qascript.upperlimit.validation"));
            saveErrors(httpServletRequest.getSession(), errors);
        }

        if (errors.isEmpty()) {
            try {
                QAScriptManager qm = new QAScriptManager();
                qm.update(user, scriptId, shortName, schemaId, resultType, desc, scriptType, curFileName, upperLimit,
                        url, scriptContent, updateContent);
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.qascript.updated"));
                qm.activateDeactivate(user, scriptId, active);
                // clear qascript list in cache
                QAScriptListLoader.reloadList(httpServletRequest);
            } catch (DCMException e) {
                LOGGER.error("Edit QA script error", e);
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            }
        }

        if (!errors.isEmpty()) {
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("fail");
        }
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);
        httpServletRequest.setAttribute("schema", schema);

        return findForward(actionMapping, "success", scriptId);
    }

    /**
     * Cancel action
     * @param actionMapping Action mapping
     * @param actionForm Action form
     * @param httpServletRequest Request
     * @param httpServletResponse Response
     * @return Action forward
     */
    public ActionForward cancel(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        return actionMapping.findForward("success");
    }

    @Override
    protected Map<String, String> getKeyMethodMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("label.qascript.save", "save");
        map.put("label.qascript.upload", "upload");
        return map;
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
        path.append("?scriptId=" + scriptId);
        forward = new RedirectingActionForward(path.toString());
        return forward;
    }
}
