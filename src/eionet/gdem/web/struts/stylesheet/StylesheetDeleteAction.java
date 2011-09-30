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

package eionet.gdem.web.struts.stylesheet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.exceptions.DCMException;

public class StylesheetDeleteAction extends Action {

    /** */
    private static final Log LOGGER = LogFactory.getLog(StylesheetDeleteAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        String stylesheetId = httpServletRequest.getParameter("conversionId");
        String user_name = (String) httpServletRequest.getSession().getAttribute("user");

        httpServletRequest.setAttribute("schema", httpServletRequest.getParameter("schema"));

        try {
            StylesheetManager sm = new StylesheetManager();
            sm.delete(user_name, stylesheetId);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.deleted"));
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Error deleting stylesheet", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
        }

        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);

        return actionMapping.findForward("success");
    }

}
