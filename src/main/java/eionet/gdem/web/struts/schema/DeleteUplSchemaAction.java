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

package eionet.gdem.web.struts.schema;

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
import eionet.gdem.web.struts.qascript.QAScriptListLoader;
import eionet.gdem.web.struts.stylesheet.StylesheetListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteUplSchemaAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteUplSchemaAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        SchemaElemForm form = (SchemaElemForm) actionForm;

        String schemaId = form.getSchemaId();

        String forward = "success";
        String user_name = (String) httpServletRequest.getSession().getAttribute("user");
        boolean deleteSchema = httpServletRequest.getParameter("deleteSchema") != null;

        try {
            SchemaManager sm = new SchemaManager();
            int schemaDeleted = sm.deleteUplSchema(user_name, schemaId, deleteSchema);
            if (schemaDeleted == 2) {
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.uplSchema.deleted"));
            }

            if (deleteSchema && (schemaDeleted == 1 || schemaDeleted == 3)) {
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.schema.deleted"));
            }

            if (deleteSchema && (schemaDeleted == 0 || schemaDeleted == 2)) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.uplSchema.notdeleted"));
            }
            if (!deleteSchema) {
                httpServletRequest.setAttribute("schemaId", schemaId);
                forward = "success_deletefile";
                // clear qascript list in cache
                QAScriptListLoader.reloadList(httpServletRequest);
                StylesheetListLoader.reloadStylesheetList(httpServletRequest);
                StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
            }
        } catch (DCMException e) {
            // e.printStackTrace();
            LOGGER.error("Error deleting root schema", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            forward = "fail";
        }
        saveMessages(httpServletRequest.getSession(), messages);
        saveErrors(httpServletRequest.getSession(), errors);

        saveMessages(httpServletRequest, messages);
        saveErrors(httpServletRequest, errors);

        return actionMapping.findForward(forward);
    }
}
