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

import eionet.gdem.dcm.business.RootElemManager;
import eionet.gdem.exceptions.DCMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add element action class.
 * @author Unknown
 * @author George Sofianos
 */
public class AddElemAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(AddElemAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        SchemaElemForm form = (SchemaElemForm) actionForm;

        String elem = form.getElemName();
        String namespace = form.getNamespace();
        String schemaId = form.getSchemaId();

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        if (elem == null || elem.equals("") || namespace == null || namespace.equals("")) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.elem.validation"));
            httpServletRequest.getSession().setAttribute("dcm.errors", errors);
            return actionMapping.findForward("success");
        }

        try {
            RootElemManager rm = new RootElemManager();
            rm.add(user, schemaId, elem, namespace);
            form.setElemName("");
            form.setNamespace("");

            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.elem.inserted"));
        } catch (DCMException e) {
            LOGGER.error("Error adding root element", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
        }
        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);

        return actionMapping.findForward("success");
    }
}
