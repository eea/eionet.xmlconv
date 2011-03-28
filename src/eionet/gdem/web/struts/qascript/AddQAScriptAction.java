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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.RedirectingActionForward;
import org.apache.struts.upload.FormFile;

import eionet.gdem.dcm.business.QAScriptManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;

/**
 * @author Enriko Käsper, Tieto Estonia
 * AddQAScriptAction
 */

public class AddQAScriptAction  extends Action {

    private static LoggerIF _logger = GDEMServices.getLogger();


    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        QAScriptForm form = (QAScriptForm) actionForm;
        String scriptId = form.getScriptId();
        String schemaId = form.getSchemaId();
        String shortName = form.getShortName();
        String desc = form.getDescription();
        String schema = form.getSchema();
        String resultType = form.getResultType();
        String scriptType = form.getScriptType();
        FormFile scriptFile = form.getScriptFile();
        String upperLimit = form.getUpperLimit();

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        httpServletRequest.setAttribute("schemaId", schemaId);

        if (isCancelled(httpServletRequest)) {
            if(schema!=null)
                return findForward(actionMapping, "cancel", schemaId);
            else
                return actionMapping.findForward("list");
        }

        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        if (scriptFile == null || scriptFile.getFileSize() == 0) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.qascript.file.validation"));
            saveErrors(httpServletRequest.getSession(), errors);
        }

        if (schema == null || schema.equals("")) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.qascript.schema.validation"));
            saveErrors(httpServletRequest.getSession(), errors);
        }

        //upper limit between 0 and 10Gb
        if (upperLimit == null || !Utils.isNum(upperLimit) || Integer.parseInt(upperLimit) <= 0 || Integer.parseInt(upperLimit) > 10000) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.qascript.upperlimit.validation"));
            saveErrors(httpServletRequest.getSession(), errors);
        }
        if(errors.size()>0){
            return actionMapping.findForward("fail");
        }

        try {
            QAScriptManager qm = new QAScriptManager();
            qm.add(user, shortName, schemaId, schema, resultType, desc, scriptType, scriptFile, upperLimit);
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.qascript.inserted"));
            //clear qascript list in session
            QAScriptListLoader.loadQAScriptList(httpServletRequest, true);
        } catch (DCMException e) {
            e.printStackTrace();
            _logger.error("Add QA Script error", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            saveErrors(httpServletRequest.getSession(), errors);
            return actionMapping.findForward("fail");
        }
        saveErrors(httpServletRequest.getSession(), errors);
        saveMessages(httpServletRequest.getSession(), messages);


        return findForward(actionMapping, "success", schemaId);
    }
    private ActionForward findForward(ActionMapping actionMapping, String f, String schemaId){
        ActionForward forward = actionMapping.findForward(f);
         StringBuffer path = new StringBuffer(forward.getPath());
         path.append("?schemaId=" + schemaId);
        forward = new RedirectingActionForward(path.toString());
        return forward;
    }
}
