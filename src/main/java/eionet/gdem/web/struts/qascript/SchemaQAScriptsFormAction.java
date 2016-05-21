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

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Enriko Käsper, Tieto Estonia SchemaQAScriptsFormAction
 */

public class SchemaQAScriptsFormAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaQAScriptsFormAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        QAScriptListHolder st = new QAScriptListHolder();
        ActionMessages messages = new ActionMessages();

        String schemaId = httpServletRequest.getParameter("schemaId");

        if (schemaId == null || schemaId.equals("")) {
            schemaId = (String) httpServletRequest.getAttribute("schemaId");
        }

        if (schemaId == null || schemaId.equals("")) {
            return actionMapping.findForward("fail");
        }

        httpServletRequest.setAttribute("schemaId", schemaId);

        try {
            SchemaManager sm = new SchemaManager();
            st = sm.getSchemasWithQAScripts( schemaId);
            httpServletRequest.setAttribute(QAScriptListLoader.QASCRIPT_LIST_ATTR, QAScriptListLoader.getList(httpServletRequest));
            httpServletRequest.setAttribute("schema.qascripts", st);

        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Error getting schema QA scripts", e);
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
        }
        saveErrors(httpServletRequest, messages);

        return actionMapping.findForward("success");
    }
}
