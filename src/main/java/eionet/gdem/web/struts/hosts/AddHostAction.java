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
 *    Original code: Nedeljko Pavlovic (ED)
 */

package eionet.gdem.web.struts.hosts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.DynaValidatorForm;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.web.struts.BaseAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add host action class.
 * @author Unknown
 * @author George Sofianos
 */
public class AddHostAction extends BaseAction {
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(AddHostAction.class);

    /**
     * Purpose of this action is to forward user to Add host form and clean up form bean that might be filled up in previous edit
     * actions.
     */
    @Override
    public ActionForward execute(ActionMapping map, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse httpServletResponse) {
        ActionMessages errors = new ActionMessages();
        DynaValidatorForm hostForm = (DynaValidatorForm) actionForm;
        hostForm.getMap().clear();
        try {
            if (!checkPermission(request, Names.ACL_HOST_PATH, "i")) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.inoperm",
                        translate(map, request, "label.hosts")));
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.exception.unknown"));
        }
        if (errors.size() > 0) {
            saveErrors(request, errors);
            return map.findForward("fail");
        }
        return map.findForward("success");
    }

}
