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
 * The Original Code is Web Dashboards Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Istvan Alfeldi (ED)
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

import eionet.gdem.dcm.business.QAScriptManager;
import eionet.gdem.dto.QAScript;
import eionet.gdem.exceptions.DCMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * QA script form action class.
 * @author Unknown
 * @author George Sofianos
 */
public class QAScriptFormAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(QAScriptFormAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionMessages errors = new ActionMessages();

        QAScriptForm form = (QAScriptForm) actionForm;
        String scriptId = httpServletRequest.getParameter("scriptId");

        if (scriptId == null || scriptId.equals("")) {
            scriptId = (String) httpServletRequest.getAttribute("scriptId");
        } else {
            httpServletRequest.getSession().setAttribute("scriptId", scriptId);
        }
        if (scriptId == null || scriptId.equals("")) {
            scriptId = (String) httpServletRequest.getSession().getAttribute("scriptId");
        }


        try {
            QAScriptManager qm = new QAScriptManager();
            QAScript qaScript = qm.getQAScript(scriptId);
            form.setScriptId(qaScript.getScriptId());
            form.setDescription(qaScript.getDescription());
            form.setShortName(qaScript.getShortName());
            form.setFileName(qaScript.getFileName());
            form.setFilePath(qaScript.getFilePath());
            form.setSchema(qaScript.getSchema());
            form.setSchemaId(qaScript.getSchemaId());
            form.setResultType(qaScript.getResultType());
            form.setScriptType(qaScript.getScriptType());
            form.setModified(qaScript.getModified());
            form.setChecksum(qaScript.getChecksum());
            form.setScriptContent(qaScript.getScriptContent());
            form.setUpperLimit(qaScript.getUpperLimit());
            form.setUrl(qaScript.getUrl());
            form.setActive(qaScript.isActive());

            httpServletRequest.setAttribute(QAScriptListLoader.QASCRIPT_LIST_ATTR, QAScriptListLoader.getList(httpServletRequest));

        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("QA Script form error", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            saveErrors(httpServletRequest, errors);
        }
        return actionMapping.findForward("success");
    }
}
