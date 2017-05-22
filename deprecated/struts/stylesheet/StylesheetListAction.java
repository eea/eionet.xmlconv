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



import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.exceptions.DCMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stylesheet list action.
 * @author Unknown
 * @author George Sofianos
 */
public class StylesheetListAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(StylesheetListAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionMessages errors = new ActionMessages();

        try {
            httpServletRequest.setAttribute(StylesheetListLoader.STYLESHEET_LIST_ATTR, StylesheetListLoader.getStylesheetList(httpServletRequest));
        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Error getting stylesheet list", e);
            errors.add("schema", new ActionMessage("label.exception.unknown"));
            saveErrors(httpServletRequest, errors);
        }
        return actionMapping.findForward("success");
    }
}
