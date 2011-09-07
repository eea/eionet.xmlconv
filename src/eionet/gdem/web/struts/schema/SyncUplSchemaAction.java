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
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

/**
 * @author Enriko Käsper, Tieto Estonia SyncUplSchemaAction
 */

public class SyncUplSchemaAction extends Action {

    private static LoggerIF _logger = GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        SyncUplSchemaForm form = (SyncUplSchemaForm) actionForm;

        String schemaId = form.getSchemaId();
        String schemaFile = form.getUplSchemaFileName();
        String schemaUrl = form.getSchemaUrl();
        String uplSchemaId = form.getUplSchemaId();

        httpServletRequest.setAttribute("schemaId", schemaId);
        String user_name = (String) httpServletRequest.getSession().getAttribute("user");

        if (isCancelled(httpServletRequest)) {
            return actionMapping.findForward("success");
        }

        try {
            SchemaManager sm = new SchemaManager();
            sm.storeRemoteSchema(user_name, schemaUrl, schemaFile, schemaId, uplSchemaId);

            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.uplSchema.cached"));

        } catch (DCMException e) {
            // e.printStackTrace();
            _logger.error("Unable to sync local schema", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
        }
        saveMessages(httpServletRequest.getSession(), messages);
        saveErrors(httpServletRequest.getSession(), errors);

        // saveMessages(httpServletRequest,messages);
        // saveErrors(httpServletRequest,errors);

        return actionMapping.findForward("success");
    }
}
